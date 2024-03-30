package utils;
import java.util.*;

public class OrderBook {
    private final String ticker;
    private final TreeSet bids;
    private final TreeSet asks;

    public OrderBook(String ticker) {
        this.ticker = ticker;
        this.bids = new TreeSet<>();
        this.asks = new TreeSet<>();
    }

    public String getInstrument() {
        return this.ticker;
    }

    public static void add(Order order) {
        // check in ticker, now we need a listener to listen to the orders and create a new OrderBook if book doesn't
        // exist

        return;
    }

    public static void main (String args[]) {
        OrderBook book = new OrderBook("123");
        String a = book.getInstrument();

        Order order = new Order("AAPL", 1, "BUY");
        OrderBook.add(order);
    }
}
