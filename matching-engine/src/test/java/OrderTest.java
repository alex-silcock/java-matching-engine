package matchingengine.test;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

import matchingengine.utils.Order;
import matchingengine.utils.OrderBook;
import baseline.OrderSide;
import baseline.STPFInstruction;

import java.util.ArrayList;

public class OrderTest {
    @Test
    public void testBestBid() {
        OrderBook book = new OrderBook("AAPL");
        ArrayList<Order> orders = new ArrayList<Order>();

        orders.add(new Order("AAPL", 1, OrderSide.BUY, 1, "A12345", STPFInstruction.RRO));
        orders.add(new Order("AAPL", 0.5, OrderSide.BUY, 0.5, "A12345", STPFInstruction.RRO));
        orders.add(new Order("AAPL", 5, OrderSide.SELL, 5, "B12345", STPFInstruction.RRO));

        for (Order order : orders) {
            order.setOrderReceivedTime();
            book.add(order);
        }
        double bestBid = book.getBestBid();
        assertEquals(1, bestBid, 0.000001d);
    }
    @Test
    public void testBestOffer() {
        OrderBook book = new OrderBook("AAPL");
        ArrayList<Order> orders = new ArrayList<Order>();

        orders.add(new Order("AAPL", 0.5, OrderSide.BUY, 0.5, "A12345", STPFInstruction.RRO));
        orders.add(new Order("AAPL", 5, OrderSide.SELL, 5, "B12345", STPFInstruction.RRO));

        for (Order order : orders) {
            order.setOrderReceivedTime();
            book.add(order);
        }

        double bestOffer = book.getBestOffer();
        assertEquals(5, bestOffer, 0.000001d);
    }
    @Test
    public void simpleSpreadCross() {
        OrderBook book = new OrderBook("AAPL");

        ArrayList<Order> orders = new ArrayList<Order>();

        orders.add(new Order("AAPL", 5, OrderSide.SELL, 1, "A12345", STPFInstruction.RRO));
        orders.add(new Order("AAPL", 1, OrderSide.BUY, 1, "B12345", STPFInstruction.RRO));

        for (Order order : orders) {
            order.setOrderReceivedTime();
            book.add(order);
        }
        double bestOfferSize = book.getBestOfferOrder().getQty();
        assertEquals(4, bestOfferSize, 0.000001d);
    }
    @Test
    public void simpleSpreadCross2() {
        OrderBook book = new OrderBook("AAPL");
        ArrayList<Order> orders = new ArrayList<Order>();

        orders.add(new Order("AAPL", 1.5, OrderSide.BUY, 1.5, "A12345", STPFInstruction.RRO));
        orders.add(new Order("AAPL", 5, OrderSide.SELL, 1, "B12345", STPFInstruction.RRO));

        for (Order order : orders) {
            order.setOrderReceivedTime();
            book.add(order);
        }
        double bestOfferSize = book.getBestOfferOrder().getQty();
        assertEquals(3.5, bestOfferSize, 0.000001d);
    }

    @Test
    public void simpleSpreadCross3() {
        OrderBook book = new OrderBook("AAPL");
        ArrayList<Order> orders = new ArrayList<Order>();

        orders.add(new Order("AAPL", 3, OrderSide.SELL, 2, "A12345", STPFInstruction.RRO));
        orders.add(new Order("AAPL", 6, OrderSide.BUY, 1.5, "B12345", STPFInstruction.RRO));
        orders.add(new Order("AAPL", 2, OrderSide.SELL, 1.5, "A12345", STPFInstruction.RRO));
        orders.add(new Order("AAPL", 1.5, OrderSide.SELL, 1.5, "A12345", STPFInstruction.RRO));
        orders.add(new Order("AAPL", 2, OrderSide.BUY, 1.5, "B12345", STPFInstruction.RRO));

        for (Order order : orders) {
            order.setOrderReceivedTime();
            book.add(order);
        }

        double bestOfferSize = book.getBestOfferOrder().getQty();
        double bestBidSize = book.getBestBidOrder().getQty();
        assertEquals(3, bestOfferSize, 0.000001d);
        assertEquals(2.0, book.getBestOfferOrder().getPrice(), 0.000001d);
        assertEquals(2.5, bestBidSize, 0.000001d);
        assertEquals(2.0, book.getBestOfferOrder().getPrice(), 0.000001d);
    }

    // TODO - add tests here for self-trading prevention
}
