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
    @Test
    public void simpleSpreadCross2() {
        OrderBook book = new OrderBook("AAPL");
        book.add(new Order("AAPL", 1.5, "BUY", 1));
        book.add(new Order("AAPL", 5, "SELL", 1));
        double bestOfferSize = book.getBestOfferOrder().getRemainingQuantity();
        assertEquals(3.5, bestOfferSize, 0.000001d);
    }

    @Test
    public void simpleSpreadCross3() {
        OrderBook book = new OrderBook("AAPL");
        book.add(new Order("AAPL", 3, "SELL", 2));
        book.add(new Order("AAPL", 6, "BUY", 1.5));
        book.add(new Order("AAPL", 2, "SELL", 1.5));
        book.add(new Order("AAPL", 1.5, "SELL", 1.5));
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        book.add(new Order("AAPL", 2, "BUY", 1.5));
        book.printBook();
        double bestOfferSize = book.getBestOfferOrder().getRemainingQuantity();
        double bestBidSize = book.getBestBidOrder().getRemainingQuantity();
        assertEquals(3, bestOfferSize, 0.000001d);
        assertEquals(2.0, book.getBestOfferOrder().getOrderPrice(), 0.000001d);
        assertEquals(2.5, bestBidSize, 0.000001d);
        assertEquals(2.0, book.getBestOfferOrder().getOrderPrice(), 0.000001d);
    }
}
