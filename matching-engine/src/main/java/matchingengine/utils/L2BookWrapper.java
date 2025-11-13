package matchingengine.utils;
import java.util.*;
import java.io.*;
import com.kx.c;
import matchingengine.utils.KDBHandler;

public class L2BookWrapper {
    private static KDBHandler kh;
    public L2BookWrapper() {
        this.kh = new KDBHandler(KDBHandler.KDBTarget.RDB);
    }

    public static void main(String[] args) {
        L2BookWrapper bw = new L2BookWrapper();
        try{
            String query = ".l2[`AAPL]";
            
            c.Flip flip = bw.kh.query(query);
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