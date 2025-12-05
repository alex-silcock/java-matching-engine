package matchingengine.utils;
import java.util.*;
import baseline.OrderSide;


public class OrderBook {
    private final String ticker;
    private final NavigableSet<Order> asks;
    private final NavigableSet<Order> bids;

    public OrderBook(String ticker) {
        this.ticker = ticker;
        this.asks = new TreeSet<>();
        this.bids = new TreeSet<>();
    }

    public NavigableSet<Order> getBids() {
        return this.bids;
    }

    public NavigableSet<Order> getAsks() {
        return this.asks;
    }

    public Order getBestBidOrder() {
        if (this.bids.isEmpty()) return null;
        return this.bids.first();
    }

    public Order getBestOfferOrder() {
        if (this.asks.isEmpty()) return null;
        return this.asks.first();
    }

    public double getBestBid() {
        try {
            return this.bids.first().getPrice();
        } catch (NoSuchElementException e) {
            return -1;
        }
    }

    public double getBestOffer() {
        try {
            return this.asks.first().getPrice();
        } catch (NoSuchElementException e) {
            return -1;
        }
    }

    public double getMidPrice() {
        double bestBid = this.getBestBid();
        double bestOffer = this.getBestOffer();

        if ((bestBid != -1) && (bestOffer != -1)) {
            return (bestBid + bestOffer) / 2;
        }
        return -1;
    }

    public void cancel(OrderCancel orderCancel) { // should just take id and cancel that
        long orderId = orderCancel.getOrderId();
        Order toRemove = null;

        for (Order order : this.bids) {
            if (order.getOrderId() == orderId) {
                toRemove = order;
            }
        }
        if (toRemove != null) {
            this.bids.remove(toRemove);
            return;
        }

        for (Order order : this.asks) {
            if (order.getOrderId() == orderId) {
                toRemove = order;
            }
        }
        this.asks.remove(toRemove);
        return;
    }


    private Order skipStpfOrders(Order bestOrder, String stpfId, double orderPrice, OrderSide side) {
        if (stpfId.equals("XXXXXX")) return bestOrder;

        while (bestOrder != null && stpfId.equals(bestOrder.getStpfId())) {
            if (side == OrderSide.BUY) {
                bestOrder = this.asks.higher(bestOrder);
                if (bestOrder == null || orderPrice < bestOrder.getPrice()) return null;
            } else {
                bestOrder = this.bids.higher(bestOrder);
                if (bestOrder == null || orderPrice > bestOrder.getPrice()) return null;
            }
        }
        return bestOrder;
    } 

    private int handleSell(Order incomingOrder, List<Order> fills) {
        // First case - incomingOrder doesn't cross the spread, or no offers in the book
        Order bestBidOrder = this.getBestBidOrder();
        double orderPrice = incomingOrder.getPrice();
        String stpfId = incomingOrder.getStpfId();

        if (bestBidOrder == null || orderPrice > bestBidOrder.getPrice()) {
            this.asks.add(incomingOrder);
            return 0;
        }

        // Interlude - skip matching stpfid orders
        bestBidOrder = this.skipStpfOrders(bestBidOrder, stpfId, orderPrice, OrderSide.BUY);
        // if no possible trades, then for now add the order to the book
        if (bestBidOrder == null) {
            this.asks.add(incomingOrder);
            return 0;
        }

        // Now, the order will have crossed the spread
        double qtyToTrade = incomingOrder.getQty();

        while (qtyToTrade > 0) {
            double bestBidOrderQty = bestBidOrder.getQty();
            // First case - the incoming order size >= resting offer order
            // therefore the resting order is fully consumed, and added as a fill
            // qtyToTrade is updated and the incomingOrder then looks for the next order
            // to trade with
            if (qtyToTrade >= bestBidOrderQty) {
                this.bids.remove(bestBidOrder);
                fills.add(bestBidOrder);
                qtyToTrade -= bestBidOrderQty;
                incomingOrder.setQty(qtyToTrade);
                bestBidOrder = this.getBestBidOrder();
            } 
            // Second case - incoming order is going to be fully filled by a resting offer
            else if (qtyToTrade < bestBidOrderQty) {
                bestBidOrder.reduceQty(qtyToTrade);
                Order copy = new Order(this.ticker, qtyToTrade, OrderSide.SELL, orderPrice, stpfId, incomingOrder.getStpfInstruction());
                copy.orderId = bestBidOrder.orderId;
                fills.add(copy);
                qtyToTrade = 0;
                return fills.size();
            }

            // Find the next tradeable order
            bestBidOrder = this.skipStpfOrders(bestBidOrder, stpfId, orderPrice, OrderSide.BUY);
            if (bestBidOrder == null || orderPrice > bestBidOrder.getPrice()) {
                this.asks.add(incomingOrder);
                return fills.size();
            }
        }

        return fills.size();
    }

    private int handleBuy(Order incomingOrder, List<Order> fills) {
        // First case - incomingOrder doesn't cross the spread, or no offers in the book
        Order bestOfferOrder = this.getBestOfferOrder();
        double orderPrice = incomingOrder.getPrice();
        String stpfId = incomingOrder.getStpfId();

        if (bestOfferOrder == null || orderPrice < bestOfferOrder.getPrice()) {
            this.bids.add(incomingOrder);
            return 0;
        }

        // Interlude - skip matching stpfid orders
        bestOfferOrder = this.skipStpfOrders(bestOfferOrder, stpfId, orderPrice, OrderSide.BUY);
        // if no possible trades, then for now add the order to the book
        if (bestOfferOrder == null) {
            this.bids.add(incomingOrder);
            return 0;
        }

        // Now, the order will have crossed the spread
        double qtyToTrade = incomingOrder.getQty();

        while (qtyToTrade > 0) {
            double bestOfferOrderQty = bestOfferOrder.getQty();
            // First case - the incoming order size >= resting offer order
            // therefore the resting order is fully consumed, and added as a fill
            // qtyToTrade is updated and the incomingOrder then looks for the next order
            // to trade with
            if (qtyToTrade >= bestOfferOrderQty) {
                this.asks.remove(bestOfferOrder);
                fills.add(bestOfferOrder);
                qtyToTrade -= bestOfferOrderQty;
                incomingOrder.setQty(qtyToTrade);
                bestOfferOrder = this.getBestOfferOrder();
            } 
            // Second case - incoming order is going to be fully filled by a resting offer
            else if (qtyToTrade < bestOfferOrderQty) {
                bestOfferOrder.reduceQty(qtyToTrade);
                Order copy = new Order(this.ticker, qtyToTrade, OrderSide.SELL, orderPrice, stpfId, incomingOrder.getStpfInstruction());
                copy.orderId = bestOfferOrder.orderId;
                fills.add(copy);
                qtyToTrade = 0;
                return fills.size();
            }

            // Find the next tradeable order
            bestOfferOrder = this.skipStpfOrders(bestOfferOrder, stpfId, orderPrice, OrderSide.BUY);
            if (bestOfferOrder == null || orderPrice < bestOfferOrder.getPrice()) {
                this.bids.add(incomingOrder);
                return fills.size();
            }
        }

        return fills.size();
    }


    public int add(Order incomingOrder, List<Order> fills) {
        fills.clear();
        OrderSide side = incomingOrder.getSide();
        return side == OrderSide.BUY ? this.handleBuy(incomingOrder, fills) : this.handleSell(incomingOrder, fills);
    }
}