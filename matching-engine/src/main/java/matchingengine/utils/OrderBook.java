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
}
