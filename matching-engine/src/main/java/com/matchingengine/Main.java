package matchingengine;
import matchingengine.utils.OrderBook;
import matchingengine.utils.Order;
public class Main {
    public static void main(String[] args) {
        OrderBook book = new OrderBook("AAPL");
        book.add(new Order("AAPL", 3, "SELL", 2));
        book.add(new Order("AAPL", 6, "BUY", 1.5));
        book.add(new Order("AAPL", 2, "SELL", 1.5));
        book.add(new Order("AAPL", 1.5, "SELL", 1.5));
        book.add(new Order("AAPL", 2, "BUY", 1.5));
        book.printBook();    
    }
}