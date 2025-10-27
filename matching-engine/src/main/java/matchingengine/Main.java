package matchingengine;
import matchingengine.utils.OrderBook;
import matchingengine.utils.Order;
import matchingengine.utils.MarketListener;
import java.io.*;
import java.net.*;

public class Main {
    public static void main(String[] args) {

        int port = 1234;
        // new Thread(() -> new MarketListener(port).startListening()).start();

        // try {
        //     Thread.sleep(500);

        // } catch (InterruptedException ignored) {}

        try (Socket socket = new Socket("localhost", port)) {
            System.out.println("[Main] Connected to server");
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            

            Order o1 = new Order("AAPL", 3, "SELL", 2);
            Order o2 = new Order("AAPL", 6, "SELL", 1.5);
            Order o3 = new Order("AAPL", 2, "SELL", 1.5);
            Order o4 = new Order("AAPL", 1.5, "SELL", 1.5);
            Order o5 = new Order("AAPL", 2, "SELL", 1.5);
            
            Order o6 = new Order("AAPL", 10, "BUY", 2);
            Order o7 = new Order("AAPL", 10, "BUY", 2.5);

            Order[] orders = {o1, o2, o3, o4, o5, o6, o7};

            for (Order order : orders) {
                
                out.writeObject(order);
                out.flush();
                Object response = in.readObject();
                System.out.println("[Main] Server response: " + response);
                Thread.sleep(1000);
            }
            // out.writeObject();
            
            



        } catch (Exception e){
            return;
        }




        // OrderBook book = new OrderBook("AAPL");
        // book.add(new Order("AAPL", 3, "SELL", 2));
        // book.add(new Order("AAPL", 6, "SELL", 1.5));
        // book.add(new Order("AAPL", 2, "SELL", 1.5));
        // book.add(new Order("AAPL", 1.5, "SELL", 1.5));
        // book.add(new Order("AAPL", 2, "SELL", 1.5));
        // book.printBook();
        // book.add(new Order("AAPL", 10, "BUY", 2));
        // book.add(new Order("AAPL", 10, "BUY", 2.5));
        // System.out.println("After adding BUY order:");
        // book.printBook();
    }
}