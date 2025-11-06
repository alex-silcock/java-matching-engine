package matchingengine.utils;
import java.util.*;
import java.io.*;
import com.kx.c;

public class L2BookWrapper {
    private static c kdbConn;

    public L2BookWrapper() {}

    static {
        try {
            kdbConn = new c("localhost", 5011);
            System.out.println("Connected to RDB on port 5011");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public c.Flip fetchFromRDB(String query) {
        c.Flip flip = null;
        try {
            flip = (c.Flip)kdbConn.k(query);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flip;
    }

    public static void main(String[] args) {
        L2BookWrapper bw = new L2BookWrapper();
        try{
            String query = """
                `side`price xdesc 0!select sum qty by side, price: 0.5 xbar price from orders
            """;
            
            c.Flip flip = bw.fetchFromRDB(query);
            while(true) {
            for(int col=0;col<flip.x.length;col++)
                System.out.print((col>0?",":"")+flip.x[col]);
                System.out.println();
        
            for(int row=0;row<c.n(flip.y[0]);row++){
                for(int col=0;col<flip.x.length;col++)
                    System.out.print((col>0?",":"")+c.at(flip.y[col],row));
                    System.out.println();
            }
            Thread.sleep(3000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
      
}