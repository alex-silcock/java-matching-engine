package utils;
import java.util.*;

public class OrderBook {
    private final String ticker;
    private final TreeSet<Order> asks;
    private final TreeSet<Order> bids;

    public OrderBook(String ticker) {
        this.ticker = ticker;
        this.asks = new TreeSet<>();
        this.bids = new TreeSet<>(Comparator.reverseOrder());
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
    public ArrayList<Order> add(Order order) {
        if (order == null) {return new ArrayList<Order>();}

        ArrayList<Order> ordersTraded = new ArrayList<Order>();

        if (Objects.equals(order.getSide(), "BUY")) {
            double bestOffer = this.getBestOffer();
            double incomingOrderPrice = order.getOrderPrice();

            if (bestOffer == -1 || incomingOrderPrice < bestOffer) {
                this.bids.add(order);

            } else if (incomingOrderPrice >= bestOffer) {
                double quantityLeftToTrade = order.getRemainingQuantity();
                Order headAskOrder = this.asks.first();

                while (quantityLeftToTrade > 0) {
                    double headAskOrderQty = headAskOrder.getRemainingQuantity();

                    if (quantityLeftToTrade >= headAskOrderQty) {
                        ordersTraded.add(this.asks.pollFirst());
                        quantityLeftToTrade -= headAskOrderQty;
                        order.setQuantity(quantityLeftToTrade);
                        if (this.getBestOffer() != -1) {
                            headAskOrder = this.asks.first();
                        }
                    } else {
                        ordersTraded.add(this.asks.first());
                        double newQty = headAskOrder.getRemainingQuantity() - order.getRemainingQuantity();
                        headAskOrder.setQuantity(newQty);
                        quantityLeftToTrade = 0;
                    }

                }

            }
        }
        else {
            this.asks.add(order);
        }
        return ordersTraded;
    }

    public void printBook() {
        for (Order o : this.asks) {
            System.out.println("Side: SELL - Order Time: " + o.getOrderTime().toString() + " - Order Price: " + o.getOrderPrice() + " - Order Size " + o.getOrderSize());
        }
        for (Order o : this.bids) {
            System.out.println("Side: BUY - Order Time: " + o.getOrderTime().toString() + " - Order Price: " + o.getOrderPrice() + " - Order Size " + o.getOrderSize());
        }
        System.out.println();
    }


    public static void main (String[] args) {
        OrderBook book = new OrderBook("123");
        String a = book.getInstrument();
        book.getBestBidOrder();
        book.add(new Order("AAPL", 5, "SELL", 1));
        book.add(new Order("AAPL", 1, "BUY", 0.5));
        book.add(new Order("AAPL", 1, "BUY", 1));
        book.printBook();
    }
}
