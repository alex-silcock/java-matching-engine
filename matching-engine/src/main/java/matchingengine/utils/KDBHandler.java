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

    private final c kdbConn;
    private final KDBTarget target;

    public KDBHandler(KDBTarget target) {
        this.target = target;
        c temp = null;
        try {
            temp = new c("localhost", target.getPort());
            System.out.println("Connected to KDB on port ");
            System.out.println(target.getPort());
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.kdbConn = temp;
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
}