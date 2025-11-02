package matchingengine.utils;
import matchingengine.utils.Order;
import matchingengine.utils.OrderBook;
import java.io.*;
import java.net.*;
import com.kx.c;

public class MarketListener {
    // TODO - MarketListener class - check in ticker, now we need a listener to listen to the orders and create a new OrderBook if book doesn't exist

    private final int port;
    public OrderBook orderBook;
    private static c kdbConn;
    // public L2BookWrapper l2Wrapper;

    public MarketListener(int port) {
        this.port = port;
        this.orderBook = new OrderBook("AAPL");
        // this.l2Wrapper = new L2BookWrapper(orderBook);
    }

    static {
        try {
            kdbConn = new c("localhost", 5010);
            System.out.println("Connected to TP on port 5010");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void publishToTp(String table, Object[] row) {
        try {
            kdbConn.ks(".u.upd", table, row);
        } catch (Exception e) {
            e.printStackTrace();
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

    private void handleClient(Socket socket) {
        try (ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())) {
            
            Object obj;
            while ((obj = in.readObject()) != null) {

                if (obj instanceof Order order) {
                    System.out.println("[MarketListener] Received: " + order);
                    order.setOrderReceivedTime();
                    orderBook.add(order);
                    // this.orderBook.printBook();
                    Object[] tpObj = new Object[] {
                        // System.currentTimeMillis(),
                        new c.Timespan(),
                        order.getTicker(),
                        order.getSide().charAt(0),
                        order.getOrderPrice(),
                        order.getRemainingQuantity()
                    };
                    publishToTp("trades", tpObj);

                    out.writeObject("ACK: " + order);
                    out.flush();
                }

            }

        } catch (EOFException e) {
            System.out.println("[MarketListener] Client Disconnected");
        } catch (IOException | ClassNotFoundException e) {
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
