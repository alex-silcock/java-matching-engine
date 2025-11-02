package matchingengine.utils;
import java.util.*;

public class L2BookWrapper {
    private final OrderBook orderBook;
    private final NavigableMap<Double, Double> asks;
    private final NavigableMap<Double, Double> bids;

    public L2BookWrapper(OrderBook orderBook) {
        this.orderBook = orderBook;
        // Define asks and bids as an implementation of the NaviableMap interface
        this.asks = new TreeMap<>(Collections.reverseOrder());
        this.bids = new TreeMap<>();
    }

    public void buildFromL3() {

        asks.clear();
        bids.clear();

        for (Order bid : orderBook.getBids()) {
            double price = bid.getOrderPrice();
            double roundedPrice = Math.round(price / 0.1) * 0.1;
            bids.merge(roundedPrice, bid.getRemainingQuantity(), Double::sum);
        }

        for (Order ask : orderBook.getAsks()) {
            double price = ask.getOrderPrice();
            double roundedPrice = Math.round(price / 0.1) * 0.1;
            asks.merge(roundedPrice, ask.getRemainingQuantity(), Double::sum);
        }
    }    
}