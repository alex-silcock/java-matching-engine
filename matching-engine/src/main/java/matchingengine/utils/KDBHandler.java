package matchingengine.utils;
import com.kx.c;

public class KDBHandler {

    public enum KDBTarget {
        TP(5010), RDB(5011);

        private final int port;

        KDBTarget(int port) {
            this.port = port;
        }

        public int getPort() {
            return this.port;
        }
    }

    private static c kdbConn;
    private static KDBTarget target;

    public KDBHandler(KDBTarget target) {
        this.target = target;
        try {
            this.kdbConn = new c("localhost", target.getPort());
            System.out.println(String.format("Connected to KDB on port %d", target.getPort()));
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
        KDBHandler kh = new KDBHandler(KDBTarget.RDB);
        System.out.println(kh.query(".l2[`AAPL]"));
    }
}