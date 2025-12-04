package matchingengine.utils;
import matchingengine.utils.OrderMessage;

import baseline.OrderCancelEncoder;
import baseline.OrderCancelDecoder;

import org.agrona.concurrent.UnsafeBuffer;

import lombok.Getter;

@Getter
public class OrderCancel extends OrderMessage{
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