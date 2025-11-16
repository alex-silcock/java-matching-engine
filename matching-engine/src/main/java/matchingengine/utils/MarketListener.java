package matchingengine.utils;
import matchingengine.utils.Order;
import matchingengine.utils.OrderBook;
import matchingengine.utils.KDBHandler;
import baseline.OrderEncoder;
import baseline.OrderDecoder;
import java.nio.ByteBuffer;
import org.agrona.concurrent.UnsafeBuffer;

import java.io.*;
import java.net.*;
import java.util.*;
import com.kx.c;

public class MarketListener {
    // TODO - MarketListener class - check in ticker, now we need a listener to listen to the orders and create a new OrderBook if book doesn't exist

    private final int port;
    public OrderBook orderBook;
    private static KDBHandler kh;
    private static OrderDecoder decoder;

    public MarketListener(int port) {
        this.port = port;
        this.orderBook = new OrderBook("AAPL");
        this.kh = new KDBHandler(KDBHandler.KDBTarget.TP);
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

    private void handleClient(Socket socket) {
        try (DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream())) {
            
            while (true) {
                int len = in.readInt();
                byte[] bytes = new byte[len];
                in.readFully(bytes);

                UnsafeBuffer buffer = new UnsafeBuffer(bytes);
                OrderDecoder decoder = new OrderDecoder();
                decoder.wrap(buffer, 0, OrderDecoder.BLOCK_LENGTH, OrderDecoder.SCHEMA_VERSION);

                Order order = Order.decode(decoder);
                order.setOrderReceivedTime();
                System.out.println("[MarketListener] Received: " + order);
                    order.setOrderReceivedTime();
                    ArrayList<Order> ordersTraded = orderBook.add(order);
                    
                    Object[] tpObjOrder = new Object[] {
                        new c.Timespan(),
                        order.getTicker(),
                        order.getSide(),
                        order.getOrderPrice(),
                        order.getRemainingQuantity()
                    };
                    kh.publishToTp("orders", tpObjOrder);

                    for (Order trade : ordersTraded) {
                        Object[] tpObjTrade = new Object[] {
                            new c.Timespan(),
                            trade.getTicker(),
                            trade.getOrderPrice(),
                            trade.getRemainingQuantity()
                        };
                        kh.publishToTp("trades", tpObjTrade);
                    }

                    out.writeUTF("ACK: " + order);
                    out.flush();
            }

        } catch (EOFException e) {
            System.out.println("[MarketListener] Client Disconnected");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onOrderReceived(Order order) {
        this.orderBook.add(order);
    }

    public static void main(String[] args) {
        int port = 1234;
        MarketListener listener = new MarketListener(port);
        listener.startListening();
    }

}
