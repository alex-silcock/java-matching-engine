package matchingengine.utils;
import java.util.*;
import baseline.OrderSide;


public class OrderBook {
    private final String ticker;
    private final NavigableSet<Order> asks;
    private final NavigableSet<Order> bids;

    public OrderBook(String ticker) {
        this.ticker = ticker;
        this.asks = new TreeSet<>();
        this.bids = new TreeSet<>();
    }

    public NavigableSet<Order> getBids() {
        return this.bids;
    }

    public NavigableSet<Order> getAsks() {
        return this.asks;
    }

    public Order getBestBidOrder() {
        if (this.bids.isEmpty()) return null;
        return this.bids.first();
    }

    public Order getBestOfferOrder() {
        if (this.asks.isEmpty()) return null;
        return this.asks.first();
    }

    public double getBestBid() {
        try {
            return this.bids.first().getPrice();
        } catch (NoSuchElementException e) {
            return -1;
        }
    }

    public double getBestOffer() {
        try {
            return this.asks.first().getPrice();
        } catch (NoSuchElementException e) {
            return -1;
        }
    }

    public double getMidPrice() {
        double bestBid = this.getBestBid();
        double bestOffer = this.getBestOffer();

        if ((bestBid != -1) && (bestOffer != -1)) {
            return (bestBid + bestOffer) / 2;
        }
        return -1;
    }

    public void cancel(OrderCancel orderCancel) { // should just take id and cancel that
        long orderId = orderCancel.getOrderId();
        Order toRemove = null;

        for (Order order : this.bids) {
            if (order.getOrderId() == orderId) {
                toRemove = order;
            }
        }
        if (toRemove != null) {
            this.bids.remove(toRemove);
            return;
        }

        for (Order order : this.asks) {
            if (order.getOrderId() == orderId) {
                toRemove = order;
            }
        }
        this.asks.remove(toRemove);
        return;
    }

    public int add(Order incomingOrder, List<Order> fills) {
        fills.clear();

        OrderSide side = incomingOrder.getSide();

        // buy side
        if (side == OrderSide.BUY) {
            // no cross
            Order bestOfferOrder = this.getBestOfferOrder();
            if (bestOfferOrder == null || incomingOrder.getPrice() < bestOfferOrder.getPrice()) {
                this.bids.add(incomingOrder);
                return 0;
            }
            // skip stfp matching orders
            while (bestOfferOrder != null && incomingOrder.getStpfId().equals(bestOfferOrder.getStpfId())) {
                bestOfferOrder = this.asks.higher(bestOfferOrder);
            }

            if (bestOfferOrder == null) {
                this.bids.add(incomingOrder);
                return 0;
            }

            // matching loop
            double quantityLeftToTrade = incomingOrder.getQty();
            double bestOfferOrderQty = bestOfferOrder.getQty();

            while (quantityLeftToTrade > 0) {
                // full consumption
                if (quantityLeftToTrade >= bestOfferOrderQty) {
                    this.asks.remove(bestOfferOrder);
                    fills.add(bestOfferOrder);
                    quantityLeftToTrade -= bestOfferOrderQty;
                    incomingOrder.setQty(quantityLeftToTrade);

                    bestOfferOrder = this.getBestOfferOrder();
                    if (bestOfferOrder == null) {
                        this.bids.add(incomingOrder);
                        return fills.size();
                    }
                } 
                // partial fill - incoming order added to bids
                // improvement here: FOK
                else {
                    fills.add(bestOfferOrder);
                    bestOfferOrder.reduceQty(quantityLeftToTrade);
                    return fills.size();
                }

            }
            return fills.size();
        } else {
            // sell side
            // no cross
            Order bestBidOrder = this.getBestBidOrder();
            if (bestBidOrder == null || incomingOrder.getPrice() > bestBidOrder.getPrice()) {
                this.asks.add(incomingOrder);
                return 0;
            }
            // skip stfp matching orders
            while (bestBidOrder != null && incomingOrder.getStpfId().equals(bestBidOrder.getStpfId())) {
                bestBidOrder = this.asks.higher(bestBidOrder);
            }

            if (bestBidOrder == null) {
                this.asks.add(incomingOrder);
                return 0;
            }

            // matching loop
            double quantityLeftToTrade = incomingOrder.getQty();
            double bestBidOrderQty = bestBidOrder.getQty();

            while (quantityLeftToTrade > 0) {
                // full consumption
                if (quantityLeftToTrade >= bestBidOrderQty) {
                    this.bids.remove(bestBidOrder);
                    fills.add(bestBidOrder);
                    quantityLeftToTrade -= bestBidOrderQty;
                    incomingOrder.setQty(quantityLeftToTrade);

                    bestBidOrder = this.getBestBidOrder();
                    if (bestBidOrder == null) {
                        this.asks.add(incomingOrder);
                        return fills.size();
                    }
                } 
                // partial fill - incoming order added to bids
                // improvement here: FOK
                else {
                    fills.add(bestBidOrder);
                    bestBidOrder.reduceQty(quantityLeftToTrade);
                    return fills.size();
                }

            }
            return fills.size();
        }
    }

    public int add2(Order incomingOrder, List<Order> fills) {
        fills.clear();
        int fillCount = 0;
        if (incomingOrder == null) return 0;

        OrderSide side = incomingOrder.getSide();
        if (side == OrderSide.BUY) {
            double bestOfferPrice = this.getBestOffer();
            Order bestOfferOrder = this.getBestOfferOrder();
            double incomingOrderPrice = incomingOrder.getPrice();

            // incoming buy price is less than the price of the best offer - orders shouldn't cross
            if (bestOfferOrder == null || incomingOrderPrice < bestOfferPrice) {
                System.out.println("B - adding bid as no spread cross");
                this.bids.add(incomingOrder);
                return 0;
            } else if (incomingOrderPrice >= bestOfferPrice) {
                double quantityLeftToTrade = incomingOrder.getQty();

                // walk the book until find an order that can match
                while (incomingOrder.getStpfId().equals(bestOfferOrder.getStpfId())) {
                    bestOfferOrder = this.asks.higher(bestOfferOrder);
                    // for now just add the order
                    if (bestOfferOrder == null) {
                        this.bids.add(incomingOrder);
                        return 0;
                    }
                }

                while (quantityLeftToTrade > 0) {
                    double bestOfferOrderQty = bestOfferOrder.getQty();

                    // incoming order is larger than best offer
                    if (quantityLeftToTrade >= bestOfferOrderQty) {
                        fills.add(bestOfferOrder);
                        fillCount++;
                        this.asks.remove(bestOfferOrder);
                        quantityLeftToTrade -= bestOfferOrderQty;
                        incomingOrder.setQty(quantityLeftToTrade);

                        bestOfferOrder = this.getBestOfferOrder();
                        if (bestOfferOrder == null) {
                            this.bids.add(incomingOrder);
                            return fillCount;
                        }
                    } 
                    // incoming order is smaller than best offer
                    else {
                        fills.add(bestOfferOrder);
                        bestOfferOrder.reduceQty(quantityLeftToTrade);
                        return fillCount;
                    }

                }
            }
            return fillCount;

        } else if (side == OrderSide.SELL) {

            double bestBidPrice = this.getBestBid();
            Order bestBidOrder = this.getBestBidOrder();
            double incomingOrderPrice = incomingOrder.getPrice();

            if (bestBidOrder == null || incomingOrderPrice > bestBidPrice) {
                System.out.println("adding offer to book as no spread cross");
                this.asks.add(incomingOrder);
                return 0;
            } else if (incomingOrderPrice <= bestBidPrice) {
                double quantityLeftToTrade = incomingOrder.getQty();

                // walk the book until find an order that can match
                while (incomingOrder.getStpfId().equals(bestBidOrder.getStpfId())) {
                    System.out.println("S - finding non matching trade");
                    bestBidOrder = this.bids.higher(bestBidOrder);
                    System.out.println(bestBidOrder);
                    // for now just add the order
                    if (bestBidOrder == null) {
                        this.asks.add(incomingOrder);
                        System.out.println("S - null best bid");
                        return 0;
                    }
                }

                while (quantityLeftToTrade > 0) {
                    double bestBidOrderQty = bestBidOrder.getQty();

                    // incoming order is larger than best bid
                    if (quantityLeftToTrade >= bestBidOrderQty) {
                        fills.add(bestBidOrder);
                        fillCount++;
                        this.bids.remove(bestBidOrder);
                        quantityLeftToTrade -= bestBidOrderQty;
                        incomingOrder.setQty(quantityLeftToTrade);

                        bestBidOrder = this.getBestBidOrder();
                        if (bestBidOrder == null) {
                            this.asks.add(incomingOrder);
                            return fillCount;
                        }
                    } 
                    // incoming order is smaller than best bid
                    else {
                        fills.add(bestBidOrder);
                        bestBidOrder.reduceQty(quantityLeftToTrade);
                        return fillCount;
                    }

                }
            }
                return fillCount;

        }
        return fillCount;
    }
}
