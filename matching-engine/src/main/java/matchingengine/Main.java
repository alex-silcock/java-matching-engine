package matchingengine;

import baseline.OrderEncoder;
import baseline.OrderCancelEncoder;
import baseline.OrderSide;
import baseline.MessageHeaderEncoder;
import baseline.STPFInstruction;

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
        OrderCancelEncoder cancelEncoder = new OrderCancelEncoder();
        UnsafeBuffer buffer = new UnsafeBuffer(ByteBuffer.allocateDirect(36));
        UnsafeBuffer bufferCancel = new UnsafeBuffer(ByteBuffer.allocateDirect(16));

        try (Socket socket = new Socket("localhost", port)) {
            System.out.println("[Main] Connected to server");
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            out.flush();
            DataInputStream in = new DataInputStream(socket.getInputStream());

            MessageHeaderEncoder header = new MessageHeaderEncoder();

            int min = 1;
            int max = 20;
            int totalOrders = 250;
            String stpf_id = "A12345";
            STPFInstruction stpfInstruction = STPFInstruction.RRO;

            for (int i = 0; i < totalOrders; i++) {
                double qty = min + ThreadLocalRandom.current().nextDouble() * (max - min);
                double price = min + ThreadLocalRandom.current().nextDouble() * (max - min);
                OrderSide side = ThreadLocalRandom.current().nextDouble() < 0.5 ? OrderSide.BUY : OrderSide.SELL;

                qty = Math.round(qty * 100.0) / 100.0;
                price = Math.round(price * 100.0) / 100.0;
                
                encoder.wrapAndApplyHeader(buffer, 0, header)
                    .ticker("AAPL") // 4 bytes
                    .qty(qty) // 8 bytes
                    .side(side) // 1 byte
                    .price(price) // 8 bytes
                    .stpfId(stpf_id) // 6 bytes
                    .stpfInstruction(stpfInstruction); // 1 byte
                    
                int len = header.ENCODED_LENGTH + encoder.encodedLength(); // 8 bytes header + 28 bytes message
                byte[] bytes = new byte[len];
                buffer.getBytes(0, bytes);

                out.writeInt(len);
                out.write(bytes);
                out.flush();

                Long ack = in.readLong();
                System.out.println("[Main] ACK: " + ack);

                // cancelEncoder.wrapAndApplyHeader(bufferCancel, 0, header)
                //     .orderId(ack);
                
                // int lenC = header.ENCODED_LENGTH + cancelEncoder.encodedLength(); // 8 bytes header + 8 bytes message
                // byte[] bytesC = new byte[lenC];
                // bufferCancel.getBytes(0, bytesC);                

                // out.writeInt(lenC);
                // out.write(bytesC);
                
                // out.flush();

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
        // new Thread(m::sendOrders, "sendOrders2").start();
    }
}