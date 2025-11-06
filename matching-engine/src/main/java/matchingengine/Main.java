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
            int sentCount = 0;

            for (int i=0;i<100;i++) {
                double qty = min + rand.nextFloat() * (max - min);
                double price = min + rand.nextFloat() * (max - min);
                String side = Math.random() < 0.5 ? "BUY" : "SELL";

                qty = Math.round(qty * 100.0) / 100.0;
                price = Math.round(price * 100.0) / 100.0;

                Order order = new Order("AAPL", qty, side, price);
                out.writeObject(order);
                out.flush();
                Object response = in.readObject();
                Thread.sleep(1);

                if (i % 100 == 0) {
                    sentCount += 100;
                    System.out.println(String.format("Sent %d orders", sentCount));
                }
            }

            // while (true) {
            //     try {
            //         Thread.sleep(1000);
            //     } catch (InterruptedException e) {
            //         break;
            //     }
            // }
            


        } catch (Exception e){
            return;
        }
    }
}