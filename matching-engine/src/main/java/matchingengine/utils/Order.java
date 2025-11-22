package matchingengine.utils;

import baseline.OrderDecoder;
import baseline.OrderEncoder;
import baseline.OrderSide;
import org.agrona.concurrent.UnsafeBuffer;

import java.util.*;
import java.sql.Timestamp;

public class Order implements Comparable<Order>{
    public final String ticker;
    private double size;
    private long orderId;
    private final OrderSide side;
    private final double price;
    private Timestamp orderReceivedTime;


    public Order(String ticker, double size, OrderSide side, double price) {
        this.ticker = ticker;
        this.size = size;
        this.side = side;
        this.price = price;
    }

    public int encode(UnsafeBuffer buffer, int offset) {
        OrderEncoder encoder = new OrderEncoder();
        encoder.wrap(buffer, offset);
        encoder.ticker(ticker);
        encoder.size(size);
        encoder.side(side);
        encoder.price(price);
        return encoder.encodedLength();
    }

    public static Order decode(OrderDecoder decoder) {
        String ticker = decoder.ticker();
        double size = decoder.size();
        OrderSide side = decoder.side();
        double price = decoder.price();
        Order order = new Order(ticker, size, side, price);
        return order;
    }

    public void setOrderId(long id) {
        this.orderId = id;
    }
    
    public long getOrderId() {
        return this.orderId;
    }

    public String getTicker() {
        return this.ticker;
    }

    public double getRemainingQuantity() {
        return this.size;
    }

    public String getSide() {
        return this.side.toString();
    }

    public double getOrderPrice() {
        return this.price;
    }

    public double getOrderSize() {
        return this.size;
    }

    public void setQuantity(double newQty) {
        this.size = newQty;
    }

    public void setOrderReceivedTime() {
        this.orderReceivedTime = new Timestamp(System.currentTimeMillis());
    }

    public Timestamp getOrderReceivedTime() {
        return this.orderReceivedTime;
    }

    @Override
    public int compareTo(Order o) {
        int priceComparison = Double.compare(this.price, o.price);
        if (priceComparison != 0) {
            if (Objects.equals(o.getSide(), "BUY")) {
                return priceComparison * -1;
            } else {
                return priceComparison;
            }
        }

        int timestampComparison = this.getOrderReceivedTime().compareTo(o.getOrderReceivedTime());
        if (timestampComparison != 0) {
            return timestampComparison;
        }
        // default to return on Snowflake ID
        return Long.compare(this.getOrderId(), o.getOrderId());
    }

    @Override
    public String toString() {
        return String.format("Order[%s %.2f %s %.2f]", ticker, size, side, price);
    }
}
