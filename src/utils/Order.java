package utils;
import java.util.*;
import java.sql.Timestamp;
public class Order implements Comparable<Order>{

    public final String ticker;
    private double size;
    private final Timestamp orderTime;
    private final UUID tradeId;
    private final String side;
    private final double price;


    public Order(String ticker, double size, String side, double price) {
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
        return this.side;
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

    public static void main(String[] args) {
        Order order = new Order("APPL", 1, "BUY", 1);
        Order order2 = new Order("APPL", 2, "BUY", 1);
        System.out.println(order.getOrderTime());
        System.out.println(order2.getOrderTime());
        System.out.println(order.compareTo(order2));
    }
}
