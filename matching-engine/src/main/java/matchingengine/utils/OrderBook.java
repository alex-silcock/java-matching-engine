package matchingengine.utils;
import javax.sound.midi.SysexMessage;
import java.util.*;
import java.sql.Timestamp;


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

    public String getInstrument() {
        return this.ticker;
    }

    public Order getBestBidOrder() {
        if (!this.bids.isEmpty()) {
            return this.bids.first();
        } else {
            return null;
        }
    }
    public Order getBestOfferOrder() {
        if (!this.asks.isEmpty()) {
            return this.asks.first();
        } else {
            return null;
        }
    }
    public double getBestBid() {
        try {
            return this.bids.first().getOrderPrice();
        } catch (NoSuchElementException e) {
            return -1;
        }
    }

    public double getBestOffer() {
        try {
            return this.asks.first().getOrderPrice();
        } catch (NoSuchElementException e) {
            return -1;
        }
    }

    public double getMidPrice() {
        double bestBid = this.getBestBid();
        double bestOffer = this.getBestOffer();

        if (bestBid == -1 && bestOffer == -1) {
            return -1;
        } else if (bestBid == -1) {
            return bestOffer;
        } else if (bestOffer == -1) {
            return bestBid;
        } else {
            return (bestBid + bestOffer) / 2;
        }
    }

    public ArrayList<Order> add(Order incomingOrder) {

        if (incomingOrder == null) {return null ;}
        ArrayList<Order> ordersTraded = new ArrayList<>();

        if (Objects.equals(incomingOrder.getSide(), "BUY")) {
            double bestOffer = this.getBestOffer();
            Order bestOfferOrder = this.getBestOfferOrder();
            double incomingOrderPrice = incomingOrder.getOrderPrice();

            if (bestOfferOrder == null || incomingOrderPrice < bestOffer) {
                this.bids.add(incomingOrder);

            } else if (incomingOrderPrice >= bestOffer) {
                double quantityLeftToTrade = incomingOrder.getRemainingQuantity();
                Order headAskOrder = this.asks.first();

                while (quantityLeftToTrade > 0) {
                    double headAskOrderQty = headAskOrder.getRemainingQuantity();

                    if (quantityLeftToTrade >= headAskOrderQty) {
                        ordersTraded.add(this.asks.pollFirst());
                        quantityLeftToTrade -= headAskOrderQty;
                        incomingOrder.setQuantity(quantityLeftToTrade);

                        bestOfferOrder = this.getBestOfferOrder();
                        if (bestOfferOrder != null) {
                            headAskOrder = bestOfferOrder;
                        } else {
                            this.bids.add(incomingOrder);
                            return ordersTraded;
                        }
                    } else {
                        ordersTraded.add(this.asks.first());
                        double newQty = headAskOrder.getRemainingQuantity() - incomingOrder.getRemainingQuantity();
                        headAskOrder.setQuantity(newQty);
                        quantityLeftToTrade = 0;
                    }

                }

            }
        }
        else {
            double bestBid = this.getBestBid();
            double incomingOrderPrice = incomingOrder.getOrderPrice();

            if (bestBid == -1 || incomingOrderPrice > bestBid) {
                this.asks.add(incomingOrder);

            } else if (incomingOrderPrice <= bestBid) {
                double quantityLeftToTrade = incomingOrder.getRemainingQuantity();
                Order headBidOrder = this.bids.first();

                while (quantityLeftToTrade > 0) {
                    double headBidOrderQty = headBidOrder.getRemainingQuantity();

                    if (quantityLeftToTrade >= headBidOrderQty) {
                        ordersTraded.add(this.bids.pollFirst());
                        quantityLeftToTrade -= headBidOrderQty;
                        incomingOrder.setQuantity(quantityLeftToTrade);

                        Order bestBidOrder = this.getBestBidOrder();
                        if (bestBidOrder != null) {
                            headBidOrder = bestBidOrder;
                        } else {
                            this.asks.add(incomingOrder);
                            return ordersTraded;
                        }
                    } else {
                        ordersTraded.add(headBidOrder);
                        double newQty = headBidOrder.getRemainingQuantity() - incomingOrder.getRemainingQuantity();
                        headBidOrder.setQuantity(newQty);
                        quantityLeftToTrade = 0;
                    }

                }

            }
        }
        return ordersTraded;
    }

    public void printBook() {

        NavigableSet<Order> reverseAsks = this.asks.descendingSet();
        for (Order o : reverseAsks) {
            System.out.println("Side: SELL - Order Received At: " + o.getOrderReceivedTime().toString() + " - Order Size " + o.getOrderSize() + " - Order Price: " + o.getOrderPrice());
        }
//        NavigableSet<Order> reverseBids = this.bids.descendingSet();
        for (Order o : this.bids) {
            System.out.println("Side: BUY  - Order Received At: "  + o.getOrderReceivedTime().toString() + " - Order Size " + o.getOrderSize() + " - Order Price: " + o.getOrderPrice());
        }
        System.out.println();
        System.out.println("Mid Price: " + this.getMidPrice());
        System.out.println();
    }

    // public static void main (String[] args) {
    //     OrderBook book = new OrderBook("AAPL");
    //     book.add(new Order("AAPL", 3, "SELL", 2));
    //     book.add(new Order("AAPL", 6, "BUY", 1.5));
    //     book.add(new Order("AAPL", 2, "SELL", 1.5));
    //     book.add(new Order("AAPL", 1.5, "SELL", 1.5));
    //     book.add(new Order("AAPL", 2, "BUY", 1.5));
    //     book.printBook();
    // }
}
