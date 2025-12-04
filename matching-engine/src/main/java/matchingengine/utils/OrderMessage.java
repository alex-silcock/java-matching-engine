package matchingengine.utils;

import org.agrona.concurrent.UnsafeBuffer;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;


@Getter @Setter
public class OrderMessage {
    private LocalDateTime orderReceivedTime;
    protected long orderId;

    public int encode(UnsafeBuffer buffer, int offset) {return -1;}

    public void setOrderReceivedTime() {
        this.orderReceivedTime = LocalDateTime.now();
    }
}