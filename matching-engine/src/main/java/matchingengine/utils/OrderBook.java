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

    public void cancel(OrderCancel orderCancel) {
        long orderId = orderCancel.getOrderId();
        Order toRemove = null;
        boolean removed = false;
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

    public ArrayList<Order> add(Order incomingOrder) {

        if (incomingOrder == null) {return null ;}
        ArrayList<Order> ordersTraded = new ArrayList<>();

        if (incomingOrder.getSide() == OrderSide.BUY) {
            double bestOffer = this.getBestOffer();
            Order bestOfferOrder = this.getBestOfferOrder();
            double incomingOrderPrice = incomingOrder.getPrice();

            if (bestOfferOrder == null || incomingOrderPrice < bestOffer) {
                this.bids.add(incomingOrder);

            } else if (incomingOrderPrice >= bestOffer) {
                double quantityLeftToTrade = incomingOrder.getQty();
                Order headAskOrder = this.asks.first();

                while (quantityLeftToTrade > 0) {
                    double headAskOrderQty = headAskOrder.getQty();

                    if (quantityLeftToTrade >= headAskOrderQty) {
                        ordersTraded.add(this.asks.pollFirst());
                        quantityLeftToTrade -= headAskOrderQty;
                        incomingOrder.setQty(quantityLeftToTrade);

                        bestOfferOrder = this.getBestOfferOrder();
                        if (bestOfferOrder != null) {
                            headAskOrder = bestOfferOrder;
                        } else {
                            this.bids.add(incomingOrder);
                            return ordersTraded;
                        }
                    } else {
                        ordersTraded.add(this.asks.first());
                        double newQty = headAskOrder.getQty() - incomingOrder.getQty();
                        headAskOrder.setQty(newQty);
                        quantityLeftToTrade = 0;
                    }

                }

            }
        }
        else {
            double bestBid = this.getBestBid();
            double incomingOrderPrice = incomingOrder.getPrice();

            if (bestBid == -1 || incomingOrderPrice > bestBid) {
                this.asks.add(incomingOrder);

            } else if (incomingOrderPrice <= bestBid) {
                double quantityLeftToTrade = incomingOrder.getQty();
                Order headBidOrder = this.bids.first();

                while (quantityLeftToTrade > 0) {
                    double headBidOrderQty = headBidOrder.getQty();

                    if (quantityLeftToTrade >= headBidOrderQty) {
                        ordersTraded.add(this.bids.pollFirst());
                        quantityLeftToTrade -= headBidOrderQty;
                        incomingOrder.setQty(quantityLeftToTrade);

                        Order bestBidOrder = this.getBestBidOrder();
                        if (bestBidOrder != null) {
                            headBidOrder = bestBidOrder;
                        } else {
                            this.asks.add(incomingOrder);
                            return ordersTraded;
                        }
                    } else {
                        ordersTraded.add(headBidOrder);
                        double newQty = headBidOrder.getQty() - incomingOrder.getQty();
                        headBidOrder.setQty(newQty);
                        quantityLeftToTrade = 0;
                    }

                }

            }
        }
        return ordersTraded;
    }
}
