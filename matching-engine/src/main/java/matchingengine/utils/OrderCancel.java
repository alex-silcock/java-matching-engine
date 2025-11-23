package matchingengine.utils;
import matchingengine.utils.OrderMessage;

public class OrderCancel implements OrderMessage{
    private static long orderId;

    public OrderCancel(long orderId) {
        this.orderId = orderId;
    }
}