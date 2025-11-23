package matchingengine;

import baseline.OrderEncoder;
import baseline.OrderSide;

import matchingengine.utils.OrderBook;
import matchingengine.utils.Order;
import matchingengine.utils.MarketListener;

import org.agrona.concurrent.UnsafeBuffer;

import java.io.*;
import java.net.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.ArrayList;
import java.util.List;
import java.nio.ByteBuffer;

public class Main {
    public void sendOrders() {
        int port = 1234;
        OrderEncoder encoder = new OrderEncoder();
        UnsafeBuffer buffer = new UnsafeBuffer(ByteBuffer.allocateDirect(1024));

        try (Socket socket = new Socket("localhost", port)) {
            System.out.println("[Main] Connected to server");
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            out.flush();
            DataInputStream in = new DataInputStream(socket.getInputStream());

            int min = 1;
            int max = 20;
            int totalOrders = 250;

            for (int i = 0; i < totalOrders; i++) {
                double qty = min + ThreadLocalRandom.current().nextDouble() * (max - min);
                double price = min + ThreadLocalRandom.current().nextDouble() * (max - min);
                OrderSide side = ThreadLocalRandom.current().nextDouble() < 0.5 ? OrderSide.BUY : OrderSide.SELL;

                qty = Math.round(qty * 100.0) / 100.0;
                price = Math.round(price * 100.0) / 100.0;
                
                encoder.wrap(buffer, 0)
                    .ticker("AAPL")
                    .qty(qty)
                    .side(side)
                    .price(price);
                    
                int len = encoder.encodedLength();
                byte[] bytes = new byte[len];
                buffer.getBytes(0, bytes);

                out.writeInt(len);
                out.write(bytes);
                String ack = in.readUTF();
                
                out.flush();

                if (i % 100 == 0) {
                    System.out.println("Sent " + i + " orders");
                }
            }
        } catch (Exception e){
            return;
        }
    }

    public static void main(String[] args) {
        Main m = new Main();
        new Thread(m::sendOrders, "sendOrders1").start();
        new Thread(m::sendOrders, "sendOrders2").start();
    }
}