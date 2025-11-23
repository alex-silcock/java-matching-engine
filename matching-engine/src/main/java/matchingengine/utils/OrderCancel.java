package matchingengine.utils;
import matchingengine.utils.OrderMessage;

import baseline.OrderCancelEncoder;
import baseline.OrderCancelDecoder;

import org.agrona.concurrent.UnsafeBuffer;


public class OrderCancel extends OrderMessage{
    private static long orderId;

    public OrderCancel(long orderId) {
        this.orderId = orderId;
    }

    public int encode(UnsafeBuffer buffer, int offset) {
        OrderCancelEncoder encoder = new OrderCancelEncoder();
        encoder.wrap(buffer, offset);
        encoder.orderId(orderId);
        return encoder.encodedLength();
    }

    public static OrderCancel decode(OrderCancelDecoder decoder) {
        long orderId = decoder.orderId();
        OrderCancel orderCancel = new OrderCancel(orderId);
        return orderCancel;
    }
}