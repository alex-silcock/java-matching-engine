package matchingengine;
import matchingengine.utils.OrderBook;
import matchingengine.utils.Order;
import matchingengine.utils.MarketListener;
import baseline.OrderEncoder;

import java.io.*;
import java.net.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.ArrayList;
import java.util.List;
import java.nio.ByteBuffer;
import org.agrona.concurrent.UnsafeBuffer;

public class Main {
    public static void main(String[] args) {

        int port = 1234;
        OrderEncoder encoder = new OrderEncoder();
        UnsafeBuffer buffer = new UnsafeBuffer(ByteBuffer.allocateDirect(1024));
        

        try (Socket socket = new Socket("localhost", port)) {
            System.out.println("[Main] Connected to server");
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            out.flush();
            DataInputStream in = new DataInputStream(socket.getInputStream());

            int max = 20;
            int min = 1;
            List<Order> orders = new ArrayList<>();
            int sentCount = 0;

            for (int i=0;i<250;i++) {
                double qty = min + ThreadLocalRandom.current().nextDouble() * (max - min);
                double price = min + ThreadLocalRandom.current().nextDouble() * (max - min);
                baseline.Side side = ThreadLocalRandom.current().nextDouble() < 0.5 ? baseline.Side.BUY : baseline.Side.SELL;

                qty = Math.round(qty * 100.0) / 100.0;
                price = Math.round(price * 100.0) / 100.0;
                
                encoder.wrap(buffer, 0)
                    .ticker("AAPL")
                    .orderTime(System.currentTimeMillis())
                    .size(qty)
                    .side(side)
                    .price(price);
                    
                int len = encoder.encodedLength();
                byte[] bytes = new byte[len];
                buffer.getBytes(0, bytes);
                out.writeInt(len);
                out.write(bytes);
                
                out.flush();
                Thread.sleep(1);

                if (i % 100 == 0) {
                    sentCount += 100;
                    System.out.println(String.format("Sent %d orders", sentCount));
                }
            }
        } catch (Exception e){
            return;
        }
    }
}