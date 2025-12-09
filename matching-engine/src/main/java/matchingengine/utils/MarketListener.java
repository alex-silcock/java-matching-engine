package matchingengine.utils;

import baseline.OrderDecoder;
import baseline.MessageHeaderDecoder;
import baseline.OrderCancelDecoder;

import matchingengine.utils.Order;
import matchingengine.utils.OrderCancel;
import matchingengine.utils.OrderBook;
import matchingengine.utils.KDBHandler;
import matchingengine.utils.Snowflake;
import matchingengine.utils.OrderMessage;

import org.agrona.concurrent.UnsafeBuffer;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.lang.IllegalArgumentException;

import com.kx.c;

public class MarketListener {
    // TODO - MarketListener class - check in ticker, now we need a listener to listen to the orders and create a new OrderBook if book doesn't exist

    private final int port;
    private final OrderBook orderBook;
    private final KDBHandler kh;
    private final Snowflake snowflake; // Snowflake ID generator
    private final BlockingQueue<OrderMessage> orderQueue; // thread-safe
    private final ExecutorService executorService;

    public MarketListener(int port) {
        this.port = port;
        this.orderBook = new OrderBook("AAPL");
        this.kh = new KDBHandler(KDBHandler.KDBTarget.TP);
        this.snowflake = new Snowflake(275);
        this.orderQueue = new ArrayBlockingQueue<>(64_000);
        this.executorService = new ThreadPoolExecutor(8, 16, 1, TimeUnit.SECONDS, new ArrayBlockingQueue<>(1024));
    }

    public void readQueue() {
        ArrayList<Order> fills = new ArrayList<Order>(1000); // assume max fills is 64 per order
        Object[] tpObjOrder = new Object[7];
        Object[] tpObjTrade = new Object[5];
        long[] tradeIds = new long[2];
        String[] stpfIds = new String[2];

        while (true) {
            try {
                OrderMessage message = this.orderQueue.take();
                if (message instanceof Order order) {
                    pubOrder(order, tpObjOrder);
                    int fillCount = orderBook.add(order, fills);
                    if (fillCount > 0) {
                        pubTrade(order, fills, tpObjTrade, tradeIds, stpfIds);
                    }
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
            System.out.println("[MarketListener] Listening on port:");
            System.out.println(port);
            while (true) {
                try {
                    Socket socket = serverSocket.accept();
                    System.out.println("[MarketListener] Client connected:");
                    System.out.println(socket.getInetAddress());
                    this.executorService.submit(() -> handleClient(socket));
                } catch (IOException e) {
                    System.out.println("[MarketListener] Could not accept client connection");
                }
            }
        } catch (IOException e) {
            System.out.println("[MarketListener] Couldn't start");
        }
    }

    private void pubOrder(OrderMessage orderMessage, Object[] tpObjOrder) {
        Arrays.fill(tpObjOrder, null);

        if (orderMessage instanceof Order order) {
            tpObjOrder[0] = order.getTicker();
            tpObjOrder[1] = order.getSide().toString();
            tpObjOrder[2] = order.getPrice();
            tpObjOrder[3] = order.getQty();
            tpObjOrder[4] = order.getOrderId();
            tpObjOrder[5] = order.getStpfId();
            tpObjOrder[6] = order.getStpfInstruction().toString();

        } else if (orderMessage instanceof OrderCancel orderCancel) {
            tpObjOrder[0] = null;
            tpObjOrder[1] = "CANCEL";
            tpObjOrder[2] = null;
            tpObjOrder[3] = null;
            tpObjOrder[4] = orderCancel.getOrderId();
            tpObjOrder[5] = null;
            tpObjOrder[6] = null;
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

            tpObjTrade[0] = trade.getTicker();
            tpObjTrade[1] = trade.getPrice();
            tpObjTrade[2] = trade.getQty();
            tpObjTrade[3] = tradeIds;
            tpObjTrade[4] = stpfIds;
            kh.publishToTp("trades", tpObjTrade);
        }
    }

    private void handleClient(Socket socket) {
        int MAX_MESSAGE_SIZE = 64;  // 64 bytes max message length
        MessageHeaderDecoder headerDecoder = new MessageHeaderDecoder();
        OrderDecoder clientDecoder = new OrderDecoder();
        OrderCancelDecoder clientDecoderCancel = new OrderCancelDecoder();
        byte[] bytes = new byte[MAX_MESSAGE_SIZE];
        UnsafeBuffer buffer = new UnsafeBuffer(bytes);

        try (DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream())) {
            
            while (true) {
                int len = in.readInt();
                if (len > MAX_MESSAGE_SIZE) {
                    System.out.println("[MarketListener] Message too big");
                    socket.close();
                    return;
                }

                in.readFully(bytes, 0, len);
                buffer.wrap(bytes, 0, len);
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
                        System.out.println("[MarketListener] Received: ");
                        System.out.println(order);

                        boolean enqueued = false;
                        try {
                            enqueued = orderQueue.offer(order, 50, TimeUnit.MILLISECONDS);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            socket.close();
                        }

                        if (enqueued) out.writeLong(orderId);
                        else {out.writeLong(-1); socket.close();}
                        out.flush();
                        break;

                    case OrderCancelDecoder.TEMPLATE_ID:
                        clientDecoderCancel.wrap(buffer, headerDecoder.ENCODED_LENGTH, clientDecoderCancel.BLOCK_LENGTH, clientDecoderCancel.SCHEMA_VERSION);
                        OrderCancel orderCancel = OrderCancel.decode(clientDecoderCancel);
                        System.out.println("[MarketListener] Received Cancel: ");
                        System.out.println(orderCancel.getOrderId());
                        boolean enqueuedCancel = orderQueue.offer(orderCancel);
                        break;
                    default:
                        System.out.println("Template not found");
                        socket.close();
                        break;
                }
            }

        } catch (EOFException e) {
            System.out.println("[MarketListener] Client Disconnected");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
                return;
            } catch (IOException ignore) {}
        }
    }

    public static void main(String[] args) {
        int port = 1234;
        MarketListener listener = new MarketListener(port);
        new Thread(listener::startListening, "MarketListener").start();
        new Thread(listener::readQueue, "QueueReader").start();
    }
}