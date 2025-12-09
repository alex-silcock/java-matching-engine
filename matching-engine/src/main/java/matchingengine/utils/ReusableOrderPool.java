package matchingengine.utils;

import matchingengine.utils.Order;

import java.util.concurrent.ArrayBlockingQueue;

public class ReusableOrderPool {
    /*
    * The goal of the class is to create N empty Order objects, that can be reused by the Matching Engine
    * to reduce object allocation.
    */

    private final ArrayBlockingQueue<Order> pool;

    public ReusableOrderPool(int size) {
        this.pool = new ArrayBlockingQueue<>(size);

        for (int i = 0; i < size; i++) {
            this.pool.add(new Order());
        }
    }

    public Order take() throws InterruptedException {
        return pool.take();
    }

    public void release(Order order) throws InterruptedException {
        pool.put(order);
    }
}