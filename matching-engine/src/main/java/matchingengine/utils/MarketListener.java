package matchingengine.utils;

import baseline.OrderDecoder;
import baseline.MessageHeaderDecoder;
import baseline.OrderCancelDecoder;

import matchingengine.utils.Order;
import matchingengine.utils.OrderBook;
import matchingengine.utils.KDBHandler;
import matchingengine.utils.Snowflake;
import matchingengine.utils.OrderMessage;

import org.agrona.concurrent.UnsafeBuffer;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.lang.IllegalArgumentException;

import com.kx.c;

public class MarketListener {
    // TODO - MarketListener class - check in ticker, now we need a listener to listen to the orders and create a new OrderBook if book doesn't exist

    private final int port;
    public OrderBook orderBook;
    private static KDBHandler kh;
    private static Snowflake snowflake; // Snowflake ID generator
    private final BlockingQueue<OrderMessage> orderQueue = new LinkedBlockingQueue<>();

    public MarketListener(int port) {
        this.port = port;
        this.orderBook = new OrderBook("AAPL");
        this.kh = new KDBHandler(KDBHandler.KDBTarget.TP);
        this.snowflake = new Snowflake(275);
    }

    public void readQueue() {
        ArrayList<Order> fills = new ArrayList<Order>(1000); // assume max fills is 1000 per order
        Object[] tpObjOrder = new Object[8];
        Object[] tpObjTrade = new Object[6];
        long[] tradeIds = new long[2];
        String[] stpfIds = new String[2];

        while (true) {
            try {
                OrderMessage message = orderQueue.take();
                if (message instanceof Order order) {
                    int fillCount = orderBook.add(order, fills);
                    pubOrder(order, tpObjOrder);
                    if (fillCount > 0) {pubTrade(order, fills, tpObjTrade, tradeIds, stpfIds);}
                } else if (message instanceof OrderCancel orderCancel) {
                    // pubCancel(orderCancel);
                    orderBook.cancel(orderCancel); // should return true if able to cancel - if order has not been touched
                    pubOrder(orderCancel, tpObjOrder);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void startListening() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("[MarketListener] Listening on port " + port);
            while (true) {
                try {
                    Socket socket = serverSocket.accept();
                    System.out.println("[MarketListener] Client connected " + socket.getInetAddress());
                    new Thread(() -> handleClient(socket)).start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void pubOrder(OrderMessage orderMessage, Object[] tpObjOrder) {
        Arrays.fill(tpObjOrder, null);

        if (orderMessage instanceof Order order) {
            tpObjOrder[0] = new c.Timespan();
            tpObjOrder[1] = order.getTicker();
            tpObjOrder[2] = order.getSide().toString();
            tpObjOrder[3] = order.getPrice();
            tpObjOrder[4] = order.getQty();
            tpObjOrder[5] = order.getOrderId();
            tpObjOrder[6] = order.getStpfId();
            tpObjOrder[7] = order.getStpfInstruction().toString();

        } else if (orderMessage instanceof OrderCancel orderCancel) {
            tpObjOrder[0] = new c.Timespan();
            tpObjOrder[1] = null;
            tpObjOrder[2] = "CANCEL";
            tpObjOrder[3] = null;
            tpObjOrder[4] = null;
            tpObjOrder[5] = orderCancel.getOrderId();
            tpObjOrder[6] = null;
            tpObjOrder[7] = null;
        }
        kh.publishToTp("orders", tpObjOrder);
    }

    private void pubTrade(Order order, ArrayList<Order> ordersTraded, Object[] tpObjTrade, long[] tradeIds, String[] stpfIds) {
        Arrays.fill(tpObjTrade, null);
        Arrays.fill(tradeIds, 0L);
        Arrays.fill(stpfIds, null);

        for (Order trade : ordersTraded) {
            tradeIds[0] = trade.getOrderId();
            tradeIds[1] = order.getOrderId();

            stpfIds[0] = trade.getStpfId();
            stpfIds[1] = order.getStpfId();

            tpObjTrade[0] = new c.Timespan();
            tpObjTrade[1] = trade.getTicker();
            tpObjTrade[2] = trade.getPrice();
            tpObjTrade[3] = trade.getQty();
            tpObjTrade[4] = tradeIds;
            tpObjTrade[5] = stpfIds;
            kh.publishToTp("trades", tpObjTrade);
        }
    }

    private void handleClient(Socket socket) {
        MessageHeaderDecoder headerDecoder = new MessageHeaderDecoder();
        OrderDecoder clientDecoder = new OrderDecoder();
        OrderCancelDecoder clientDecoderCancel = new OrderCancelDecoder();

        try (DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream())) {
            
            while (true) {
                int len = in.readInt();
                byte[] bytes = new byte[len];
                in.readFully(bytes);
                UnsafeBuffer buffer = new UnsafeBuffer(bytes);
                headerDecoder.wrap(buffer, 0); // header starts at 0 offset
                int templateId = headerDecoder.templateId();

                switch (templateId) {
                    case OrderDecoder.TEMPLATE_ID:
                        clientDecoder.wrap(buffer, headerDecoder.ENCODED_LENGTH, clientDecoder.BLOCK_LENGTH, clientDecoder.SCHEMA_VERSION);
                        Order order = Order.decode(clientDecoder);

                        long orderId = snowflake.nextId();
                        order.setOrderId(orderId);
                        order.setOrderReceivedTime();
                        LocalDateTime receivedTime = order.getOrderReceivedTime();
                        System.out.println("[MarketListener] Received: " + order);
                        boolean enqueued = orderQueue.offer(order);
                        /* 
                        * This could become something else SBE encoded for sending ACKS
                        * For example, an enum
                        * 0 (OrderID, Received Time) Success
                        * 1 (-1.    , -1 )           Fail
                        */
                        if (enqueued) {
                            out.writeLong(orderId);
                        } else {
                            out.writeLong((long)-1);
                        }
                        out.flush();
                        break;

                    case OrderCancelDecoder.TEMPLATE_ID:
                        clientDecoderCancel.wrap(buffer, headerDecoder.ENCODED_LENGTH, clientDecoderCancel.BLOCK_LENGTH, clientDecoderCancel.SCHEMA_VERSION);
                        OrderCancel orderCancel = OrderCancel.decode(clientDecoderCancel);
                        System.out.println("[MarketListener] Received Cancel: " + orderCancel.getOrderId());
                        boolean enqueuedCancel = orderQueue.offer(orderCancel);
                        break;
                    default:
                        throw new IllegalArgumentException("Template not found");
                }
            }

        } catch (EOFException e) {
            System.out.println("[MarketListener] Client Disconnected");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        int port = 1234;
        MarketListener listener = new MarketListener(port);
        new Thread(listener::startListening, "MarketListener").start();
        new Thread(listener::readQueue, "QueueReader").start();
    }
}