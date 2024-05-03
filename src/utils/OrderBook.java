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

    public void add(Order order) {
        // TODO - check in ticker, now we need a listener to listen to the orders and create a new OrderBook if book doesn't exist
        if (order == null) {return;}

        if (Objects.equals(order.getSide(), "BUY")) {
            this.bids.add(order);
        }
        else {
            this.asks.add(order);
        }
    }

    public void printBook() {
        for (Order o : this.asks) {
            System.out.println("Side: SELL - Order Time: " + o.getOrderTime().toString() + " - Order Price: " + o.getOrderPrice() + " - Order Size " + o.getOrderSize());
        }
        for (Order o : this.bids) {
            System.out.println("Side: BUY - Order Time: " + o.getOrderTime().toString() + " - Order Price: " + o.getOrderPrice() + " - Order Size " + o.getOrderSize());
        }
    }


    public static void main (String[] args) {
        OrderBook book = new OrderBook("123");
        String a = book.getInstrument();

        book.add(new Order("AAPL", 1, "BUY", 1));
        book.add(new Order("AAPL", 0.5, "BUY", 0.5));
        book.add(new Order("AAPL", 5, "SELL", 5));
        book.printBook();
    }
}
