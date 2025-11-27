package matchingengine.utils;

import org.agrona.concurrent.UnsafeBuffer;
import java.time.LocalDateTime;
import lombok.Getter;


@Getter
public class OrderMessage {
    private LocalDateTime orderReceivedTime;
    // TODO - move orderId here

    public int encode(UnsafeBuffer buffer, int offset) {return -1;}

    public void setOrderReceivedTime() {
        this.orderReceivedTime = LocalDateTime.now();
    }
}