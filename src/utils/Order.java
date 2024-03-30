package utils;
import java.util.*;
import java.sql.Timestamp;
// TODO - Create this order which is a hashset
public class Order {

    public final String ticker;
    private final long size;
    private final Timestamp orderTime;
    private final UUID tradeId;
    private final String side;

    public Order(String ticker, long size, String side) {
        this.ticker = ticker;
        this.tradeId = UUID.randomUUID();
        this.orderTime = new Timestamp(System.currentTimeMillis());
        this.size = size;
        this.side = side;
    }
    public String getTicker() {
        return this.ticker;
    }

    public long getRemainingQuantity() {
        return this.size;
    }

    public UUID getTradeId() {
        return this.tradeId;
    }

    public String getSide() {
        return this.side;
    }

    public Timestamp getOrderTime() {
        return this.orderTime;
    }
    public static void main(String args[]) {
        Order order = new Order("APPL", 1, "BUY");
        System.out.println(order.getRemainingQuantity());
    }
}
