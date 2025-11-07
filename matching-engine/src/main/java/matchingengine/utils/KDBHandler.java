package matchingengine.utils;
import com.kx.c;

public class KDBHandler {

    private static c kdbConn;
    private static int port;
    private static String target;

    public KDBHandler(String target) {
        this.target = target;
        this.port = (target == "tp") ? 5010 : 5011;
        try {
            this.kdbConn = new c("localhost", this.port);
            System.out.println(String.format("Connected to KDB on port %d", this.port));
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public c.Flip query(String query) {
        c.Flip flip = null;
        try {
            flip = (c.Flip)kdbConn.k(query);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flip;
    }

    public void publishToTp(String table, Object[] row) {
        try {
            kdbConn.ks(".u.upd", table, row);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main (String[] args) {
        KDBHandler kh = new KDBHandler("rdb");
        System.out.println(kh.query(".l2[`AAPL]"));
    }
}