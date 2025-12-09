package matchingengine.utils;

import org.agrona.concurrent.UnsafeBuffer;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;


@Getter @Setter
public abstract class OrderMessage{
    private LocalDateTime orderReceivedTime;
    private long orderId;

    public abstract int encode(UnsafeBuffer buffer, int offset);

    public void setOrderReceivedTime() {
        this.orderReceivedTime = LocalDateTime.now();
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public long getOrderId() {
        return this.orderId;
    }
}