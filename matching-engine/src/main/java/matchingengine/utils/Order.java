package matchingengine.utils;

import matchingengine.utils.OrderMessage;

import baseline.OrderDecoder;
import baseline.OrderEncoder;
import baseline.OrderSide;
import org.agrona.concurrent.UnsafeBuffer;

import java.util.*;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Order implements Comparable<Order>, OrderMessage{
    public final String ticker;
    private double qty;
    private long orderId;
    private final OrderSide side;
    private final double price;
    private LocalDateTime orderReceivedTime;


    public Order(String ticker, double qty, OrderSide side, double price) {
        this.ticker = ticker;
        this.qty = qty;
        this.side = side;
        this.price = price;
    }

    public int encode(UnsafeBuffer buffer, int offset) {
        OrderEncoder encoder = new OrderEncoder();
        encoder.wrap(buffer, offset);
        encoder.ticker(ticker);
        encoder.qty(qty);
        encoder.side(side);
        encoder.price(price);
        return encoder.encodedLength();
    }

    public static Order decode(OrderDecoder decoder) {
        String ticker = decoder.ticker();
        double qty = decoder.qty();
        OrderSide side = decoder.side();
        double price = decoder.price();
        Order order = new Order(ticker, qty, side, price);
        return order;
    }

    public void setOrderReceivedTime() {
        this.orderReceivedTime = LocalDateTime.now();
    }

    @Override
    public int compareTo(Order order) {
        int priceComparison = Double.compare(this.price, order.getPrice());
        if (priceComparison != 0) {
            if (Objects.equals(order.getSide(), OrderSide.BUY)) {
                return priceComparison * -1;
            } else {
                return priceComparison;
            }
        }

        int timestampComparison = this.getOrderReceivedTime().compareTo(order.getOrderReceivedTime());
        if (timestampComparison != 0) return timestampComparison;
        // default to return on Snowflake ID
        return Long.compare(this.getOrderId(), order.getOrderId());
    }

    @Override
    public String toString() {
        return String.format("Order[%s %.2f %s %.2f]", ticker, qty, side, price);
    }
}
