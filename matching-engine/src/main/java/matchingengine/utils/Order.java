package matchingengine.utils;

import matchingengine.utils.OrderMessage;

import baseline.OrderDecoder;
import baseline.OrderEncoder;
import baseline.OrderSide;
import baseline.STPFInstruction;

import org.agrona.concurrent.UnsafeBuffer;

import java.util.*;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Order extends OrderMessage implements Comparable<Order>{
    public final String ticker;
    private double qty;
    private final OrderSide side;
    private final double price;
    private final String STPF_ID;
    private final STPFInstruction STPF_Instruction;

    public Order(String ticker, double qty, OrderSide side, double price, String stpfId, STPFInstruction stpfInstr) {
        this.ticker = ticker;
        this.qty = qty;
        this.side = side;
        this.price = price;
        this.STPF_ID = stpfId;
        this.STPF_Instruction = stpfInstr;
    }

    public String getStpfId() {
        return this.STPF_ID;
    }

    public STPFInstruction getStpfInstruction() {
        return this.STPF_Instruction;
    }

    public void reduceQty(double amount) {
        this.qty -= amount;
    }

    public int encode(UnsafeBuffer buffer, int offset) {
        OrderEncoder encoder = new OrderEncoder();
        encoder.wrap(buffer, offset);
        encoder.ticker(ticker);
        encoder.qty(qty);
        encoder.side(side);
        encoder.price(price);
        encoder.stpfId(STPF_ID);
        encoder.stpfInstruction(STPF_Instruction);
        return encoder.encodedLength();
    }

    public static Order decode(OrderDecoder decoder) {
        String ticker = decoder.ticker();
        double qty = decoder.qty();
        OrderSide side = decoder.side();
        double price = decoder.price();
        String stpfId = decoder.stpfId();
        STPFInstruction stpfInstr = decoder.stpfInstruction();
        Order order = new Order(ticker, qty, side, price, stpfId, stpfInstr);
        return order;
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
        // default to return on Snowflake ID - time ordered
        return Long.compare(this.getOrderId(), order.getOrderId());
    }

    @Override
    public String toString() {
        return String.format("Order[%s %.2f %s %.2f]", ticker, qty, side, price);
    }
}
