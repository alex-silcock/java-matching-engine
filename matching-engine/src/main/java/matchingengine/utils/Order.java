package matchingengine.utils;
import java.util.*;
import java.sql.Timestamp;
import java.io.Serializable;

public class Order implements Comparable<Order>, Serializable {

    public enum Side {
        BUY, SELL;

        @Override
        public String toString() {
            return (this == BUY ? "BUY" : "SELL");
        }
    }

    private static final long serialVersionUID = 1L;
    public final String ticker;
    private double size;
    private final Timestamp orderTime;
    private final UUID tradeId;
    private final Side side;
    private final double price;
    private Timestamp orderReceivedTime;


    public Order(String ticker, double size, Side side, double price) {
        this.ticker = ticker;
        this.tradeId = UUID.randomUUID();
        this.orderTime = new Timestamp(System.currentTimeMillis());
        this.size = size;
        this.side = side;
        this.price = price;
    }
    public String getTicker() {
        return this.ticker;
    }

    public double getRemainingQuantity() {
        return this.size;
    }

    public UUID getTradeId() {
        return this.tradeId;
    }

    public String getSide() {
        return this.side.toString();
    }

    public Timestamp getOrderTime() {
        return this.orderTime;
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

        int timestampComparison = this.getOrderTime().compareTo(o.getOrderTime());
        if (timestampComparison != 0) {
            return timestampComparison;
        }
        // default to return on UUID
        return this.getTradeId().compareTo(o.getTradeId());
    }

    @Override
    public String toString() {
        return String.format("Order[%s %.2f %s %.2f]", ticker, size, side, price);
    }
}
