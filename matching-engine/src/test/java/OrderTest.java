package matchingengine.test;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

import matchingengine.utils.Order;
import matchingengine.utils.OrderBook;
import baseline.OrderSide;
import baseline.STPFInstruction;

import java.util.Arrays;
import java.util.ArrayList;

public class OrderTest {
    @Test
    public void testBestBid() {
        OrderBook book = new OrderBook("AAPL");
        ArrayList<Order> orders = new ArrayList<Order>();
        ArrayList<Order> fills = new ArrayList<Order>();

        orders.add(new Order("AAPL", 1, OrderSide.BUY, 1, "A12345", STPFInstruction.RRO));
        orders.add(new Order("AAPL", 0.5, OrderSide.BUY, 0.5, "A12345", STPFInstruction.RRO));
        orders.add(new Order("AAPL", 5, OrderSide.SELL, 5, "B12345", STPFInstruction.RRO));

        for (Order order : orders) {
            order.setOrderReceivedTime();
            book.add(order, fills);
        }
        double bestBid = book.getBestBid();
        assertEquals(1, bestBid, 0.000001d);
    }
    @Test
    public void testBestOffer() {
        OrderBook book = new OrderBook("AAPL");
        ArrayList<Order> orders = new ArrayList<Order>();
        ArrayList<Order> fills = new ArrayList<Order>();


        orders.add(new Order("AAPL", 1, OrderSide.BUY, 1, "XXXXXX", STPFInstruction.RRO));
        orders.add(new Order("AAPL", 5, OrderSide.SELL, 5, "XXXXXX", STPFInstruction.RRO));

        for (Order order : orders) {
            order.setOrderReceivedTime();
            book.add(order, fills);
        }

        double bestOffer = book.getBestOffer();
        assertEquals(5, bestOffer, 0.000001d);
    }
    @Test
    public void simpleSpreadCross() {
        OrderBook book = new OrderBook("AAPL");

        ArrayList<Order> orders = new ArrayList<Order>();
        ArrayList<Order> fills = new ArrayList<Order>();


        orders.add(new Order("AAPL", 5, OrderSide.SELL, 1, "A12345", STPFInstruction.RRO));
        orders.add(new Order("AAPL", 1, OrderSide.BUY, 1, "B12345", STPFInstruction.RRO));

        for (Order order : orders) {
            order.setOrderReceivedTime();
            book.add(order, fills);
        }
        double bestOfferSize = book.getBestOfferOrder().getQty();
        assertEquals(4, bestOfferSize, 0.000001d);
    }
    @Test
    public void simpleSpreadCross2() {
        OrderBook book = new OrderBook("AAPL");
        ArrayList<Order> orders = new ArrayList<Order>();
        ArrayList<Order> fills = new ArrayList<Order>();


        orders.add(new Order("AAPL", 1.5, OrderSide.BUY, 1.5, "A12345", STPFInstruction.RRO));
        orders.add(new Order("AAPL", 5, OrderSide.SELL, 1, "B12345", STPFInstruction.RRO));

        for (Order order : orders) {
            order.setOrderReceivedTime();
            book.add(order, fills);
        }
        double bestOfferSize = book.getBestOfferOrder().getQty();
        assertEquals(3.5, bestOfferSize, 0.000001d);
    }

    @Test
    public void simpleSpreadCross3() {
        OrderBook book = new OrderBook("AAPL");
        ArrayList<Order> orders = new ArrayList<Order>();
        ArrayList<Order> fills = new ArrayList<Order>();

        orders.add(new Order("AAPL", 3, OrderSide.SELL, 2, "A12345", STPFInstruction.RRO));
        orders.add(new Order("AAPL", 6, OrderSide.BUY, 1.5, "B12345", STPFInstruction.RRO));
        orders.add(new Order("AAPL", 2, OrderSide.SELL, 1.5, "A12345", STPFInstruction.RRO));
        orders.add(new Order("AAPL", 1.5, OrderSide.SELL, 1.5, "A12345", STPFInstruction.RRO));
        orders.add(new Order("AAPL", 2, OrderSide.BUY, 1.5, "B12345", STPFInstruction.RRO));

        for (Order order : orders) {
            order.setOrderReceivedTime();
            book.add(order, fills);
        }

        double bestOfferSize = book.getBestOfferOrder().getQty();
        double bestBidSize = book.getBestBidOrder().getQty();
        assertEquals(3, bestOfferSize, 0.000001d);
        assertEquals(2.0, book.getBestOfferOrder().getPrice(), 0.000001d);
        assertEquals(2.5, bestBidSize, 0.000001d);
        assertEquals(2.0, book.getBestOfferOrder().getPrice(), 0.000001d);
    }

    @Test
    public void simpleSpreadCross4() {
        OrderBook book = new OrderBook("AAPL");
        ArrayList<Order> orders = new ArrayList<Order>();
        ArrayList<Order> fills = new ArrayList<Order>();

        orders.add(new Order("AAPL", 14.95, OrderSide.BUY, 1.95, "XXXXXX", STPFInstruction.RRO));
        orders.add(new Order("AAPL", 12.82, OrderSide.SELL, 4.69, "XXXXXX", STPFInstruction.RRO));
        orders.add(new Order("AAPL", 3.63, OrderSide.BUY, 8.46, "XXXXXX", STPFInstruction.RRO));

        for (Order order : orders) {
            order.setOrderReceivedTime();
            book.add(order, fills);
        }

        double bestOfferSize = book.getBestOfferOrder().getQty();
        assertEquals(9.19, bestOfferSize, 0.000001d);
    }

    @Test
    public void noMatchSameSTPFId() {
        OrderBook book = new OrderBook("AAPL");
        ArrayList<Order> orders = new ArrayList<Order>();
        ArrayList<Order> fills = new ArrayList<Order>();

        orders.add(new Order("AAPL", 3, OrderSide.BUY, 1, "C12345", STPFInstruction.RRO));
        orders.add(new Order("AAPL", 3, OrderSide.SELL, 1, "C12345", STPFInstruction.RRO));

        for (Order order : orders) {
            order.setOrderReceivedTime();
            book.add(order, fills);
        }

        double bestOfferSize = book.getBestOfferOrder().getQty();
        double bestBidSize = book.getBestBidOrder().getQty();
        assertEquals(3, bestOfferSize, 0.000001d);
        assertEquals(1, book.getBestOfferOrder().getPrice(), 0.000001d);
        assertEquals(3, bestBidSize, 0.000001d);
        assertEquals(1, book.getBestBidOrder().getPrice(), 0.000001d);
    }

    @Test
    public void moreOrdersTest() {
        OrderBook book = new OrderBook("AAPL");
        ArrayList<Order> orders = new ArrayList<Order>();
        ArrayList<Order> fills = new ArrayList<Order>();

        orders.add(new Order("AAPL", 11.66, OrderSide.SELL, 2.99  , "XXXXXX", STPFInstruction.RRO));
        orders.add(new Order("AAPL", 11.26, OrderSide.SELL, 12.44 , "XXXXXX", STPFInstruction.RRO));
        orders.add(new Order("AAPL", 4.98 , OrderSide.SELL, 11.02 , "XXXXXX", STPFInstruction.RRO));
        orders.add(new Order("AAPL", 2.76 , OrderSide.BUY , 6.92  , "XXXXXX", STPFInstruction.RRO));

        for (Order order : orders) {
            order.setOrderReceivedTime();
            book.add(order, fills);
        }

        double bestOfferSize = book.getBestOfferOrder().getQty();
        Order bestBid = book.getBestBidOrder();
        assertEquals(8.9, bestOfferSize, 0.000001d);
        assertEquals(null, bestBid);
        
        ArrayList<Order> orders2 = new ArrayList<Order>();
        orders2.add(new Order("AAPL", 17.84, OrderSide.BUY , 0.9   , "XXXXXX", STPFInstruction.RRO));
        orders2.add(new Order("AAPL", 15.01, OrderSide.BUY , 5.86  , "XXXXXX", STPFInstruction.RRO));

        for (Order order : orders2) {
            order.setOrderReceivedTime();
            book.add(order, fills);
        }

        bestOfferSize = book.getBestOfferOrder().getQty();
        double bestBidSize = book.getBestBidOrder().getQty();
        assertEquals(4.98, bestOfferSize, 0.000001d);
        assertEquals(6.11, bestBidSize, 0.000001d);

        // orders.add(new Order("AAPL", 8.72 , OrderSide.SELL, 3.41  , "XXXXXX", STPFInstruction.RRO));
        // orders.add(new Order("AAPL", 10.5 , OrderSide.BUY , 2.51  , "XXXXXX", STPFInstruction.RRO));
        // orders.add(new Order("AAPL", 19.52, OrderSide.SELL, 6.49  , "XXXXXX", STPFInstruction.RRO));
        // orders.add(new Order("AAPL", 12.68, OrderSide.SELL, 2.32  , "XXXXXX", STPFInstruction.RRO));
        // orders.add(new Order("AAPL", 5.82 , OrderSide.SELL, 1.82  , "XXXXXX", STPFInstruction.RRO));
        // orders.add(new Order("AAPL", 16.93, OrderSide.SELL, 4.76  , "XXXXXX", STPFInstruction.RRO));
        // orders.add(new Order("AAPL", 17.55, OrderSide.BUY , 12.06 , "XXXXXX", STPFInstruction.RRO));
        // orders.add(new Order("AAPL", 2.32 , OrderSide.BUY , 15.59 , "XXXXXX", STPFInstruction.RRO));
        // orders.add(new Order("AAPL", 17.19, OrderSide.SELL, 1.56  , "XXXXXX", STPFInstruction.RRO));
        // orders.add(new Order("AAPL", 2.98 , OrderSide.SELL, 17.35 , "XXXXXX", STPFInstruction.RRO));
        // orders.add(new Order("AAPL", 14.24, OrderSide.SELL, 13.95 , "XXXXXX", STPFInstruction.RRO));

        // for (Order order : orders) {
        //     order.setOrderReceivedTime();
        //     book.add(order, fills);
        // }

        // double bestOfferSize = book.getBestOfferOrder().getQty();
        // double bestBidSize = book.getBestBidOrder().getQty();
        // assertEquals(3, bestOfferSize, 0.000001d);
        // assertEquals(1, book.getBestOfferOrder().getPrice(), 0.000001d);
        // assertEquals(3, bestBidSize, 0.000001d);
        // assertEquals(1, book.getBestBidOrder().getPrice(), 0.000001d);
    }
}


