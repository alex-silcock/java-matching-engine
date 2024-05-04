package test;
import org.junit.Test;
import utils.Order;
import utils.OrderBook;

import static org.junit.Assert.assertEquals;
public class OrderTest {
    @Test
    public void testBestBid() {
        OrderBook book = new OrderBook("AAPL");
        book.add(new Order("AAPL", 1, "BUY", 1));
        book.add(new Order("AAPL", 0.5, "BUY", 0.5));
        book.add(new Order("AAPL", 5, "SELL", 5));
        double bestBid = book.getBestBid();
        assertEquals(1, bestBid, 0.000001d);
    }
    @Test
    public void testBestOffer() {
        OrderBook book = new OrderBook("AAPL");
        book.add(new Order("AAPL", 0.5, "BUY", 0.5));
        book.add(new Order("AAPL", 5, "SELL", 5));
        double bestOffer = book.getBestOffer();
        assertEquals(5, bestOffer, 0.000001d);
    }
    @Test
    public void simpleSpreadCross() {
        OrderBook book = new OrderBook("AAPL");
        book.add(new Order("AAPL", 5, "SELL", 1));
        book.add(new Order("AAPL", 1, "BUY", 1));
        double bestOfferSize = book.getBestOfferOrder().getRemainingQuantity();
        assertEquals(4, bestOfferSize, 0.000001d);
    }
}
