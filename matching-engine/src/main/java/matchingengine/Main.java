package matchingengine;
import matchingengine.utils.OrderBook;
import matchingengine.utils.Order;
import matchingengine.utils.MarketListener;
import java.io.*;
import java.net.*;
import java.util.Random;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        int port = 1234;

        try (Socket socket = new Socket("localhost", port)) {
            System.out.println("[Main] Connected to server");
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            int max = 20;
            int min = 1;
            Random rand = new Random();
            List<Order> orders = new ArrayList<>();

            for (int i=0;i<1000;i++) {
                double qty = min + rand.nextFloat() * (max - min);
                double price = min + rand.nextFloat() * (max - min);
                String side = Math.random() < 0.5 ? "BUY" : "SELL";

                qty = Math.round(qty * 100.0) / 100.0;
                price = Math.round(price * 100.0) / 100.0;

                Order order = new Order("AAPL", qty, side, price);
                out.writeObject(order);
                out.flush();
                Object response = in.readObject();
                Thread.sleep(200);
            }


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