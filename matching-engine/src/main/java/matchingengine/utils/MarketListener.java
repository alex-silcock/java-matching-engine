package matchingengine.utils;
import matchingengine.utils.Order;
import matchingengine.utils.OrderBook;
import java.io.*;
import java.net.*;

public class MarketListener {
    // TODO - MarketListener class - check in ticker, now we need a listener to listen to the orders and create a new OrderBook if book doesn't exist

    private final int port;
    public OrderBook orderBook;

    public MarketListener(int port) {
        this.port = port;
        this.orderBook = new OrderBook("AAPL");

    }

    public void startListening() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("[MarketListener] Listening on port " + port);

            try (Socket socket = serverSocket.accept()) {
                System.out.println("[MarketListener] Client connected " + socket.getInetAddress());
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());

                try {
                    Object obj;
                    while ((obj = in.readObject()) != null) {

                        if (obj instanceof Order order) {
                            System.out.println("[MarketListener] Received: " + order);
                            order.setOrderReceivedTime();
                            this.orderBook.add(order);
                            this.orderBook.printBook();
                            out.writeObject("ACK: " + order);
                            out.flush();
                        }

                    }
                } catch (EOFException e) {
                    System.out.println("[MarketListener] Client Disconnected");
                }
            }
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
