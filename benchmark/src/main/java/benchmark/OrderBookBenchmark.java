/*
 * Copyright (c) 2014, Oracle America, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 *  * Neither the name of Oracle nor the names of its contributors may be used
 *    to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */

package benchmark;

import java.util.Random;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Warmup;

import matchingengine.utils.OrderBook;
import matchingengine.utils.Order;

import baseline.OrderSide;
import baseline.STPFInstruction;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;


@Fork(value = 2)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
@Threads(3)
@State(Scope.Thread)
public class OrderBookBenchmark {

  private OrderBook orderBook;
  private ArrayList<Order> orders;
  private ArrayList<Order> fills;
  private String[] stpfIds;

  @Setup(Level.Iteration)
  public void setup() {
    orderBook = new OrderBook("AAPL");
    orders = new ArrayList<Order>(250_000);
    fills = new ArrayList<Order>(250_000);
    stpfIds = new String[250_000];

    int min = 1;
    int max = 20;

    int min2 = 10000;
    int max2 = 99999;
    int totalOrders = 250_000;
    STPFInstruction stpfInstruction = STPFInstruction.RRO;

    for (int i = 0; i < totalOrders; i++) {
      double id = min + ThreadLocalRandom.current().nextDouble() * (max - min);
      stpfIds[i] = "A" + id;
    }

    for (int i = 0; i < totalOrders; i++) {
      double qty = min + ThreadLocalRandom.current().nextDouble() * (max - min);
      double price = min + ThreadLocalRandom.current().nextDouble() * (max - min);
      OrderSide side = ThreadLocalRandom.current().nextDouble() < 0.5 ? OrderSide.BUY : OrderSide.SELL;

      qty = Math.round(qty * 100.0) / 100.0;
      price = Math.round(price * 100.0) / 100.0;

      Order order = new Order("AAPL", 1, side, 1, stpfIds[i], STPFInstruction.RRO);
      order.setOrderReceivedTime();
      orders.add(order);
    }
  }

  @Benchmark
  @BenchmarkMode(Mode.AverageTime)
  @OutputTimeUnit(TimeUnit.MILLISECONDS)
  public void addOrders() {
    for (Order order : orders) {
      orderBook.add(order, fills);
    }
  }

  @Benchmark
  @BenchmarkMode(Mode.Throughput)
  @OutputTimeUnit(TimeUnit.SECONDS)
  public void addSingleOrder() {
    Order order = new Order("AAPL", 1, OrderSide.BUY, 1, "XXXXXX", STPFInstruction.RRO);
    order.setOrderReceivedTime();
    orderBook.add(order, fills);
  }

  public static void main(String[] args) throws Exception {
    org.openjdk.jmh.Main.main(args);
  }
}