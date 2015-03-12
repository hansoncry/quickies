package ormus.quickies;

import org.h2.jdbcx.JdbcConnectionPool;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.*;

public class RpcEvents {

    public static final String URL = "jdbc:h2:file:./events";

    public RpcEvents() {
    }

    public static void main(String[] args) throws IOException {
        DataSource ds = JdbcConnectionPool.create(URL, "", "");
        DBI dbi = new DBI(ds);
        Handle h = dbi.open();
        h.execute("DROP TABLE IF EXISTS event");
        h.execute("CREATE TABLE IF NOT EXISTS event (event_type VARCHAR(100),event_time BIGINT,event_name VARCHAR(100),action VARCHAR,callback VARCHAR)");

        Path dir = args.length > 0 ? Paths.get(args[0]) : Paths.get("./");
        DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "*log*");
        for (Path file : stream) {
            processFile(file, h);
        }

        h.close();
    }

    private static void processFile(Path file, Handle h) {
        Charset charset = Charset.forName("US-ASCII");
        try (BufferedReader reader = Files.newBufferedReader(file, charset)) {
            String line;
            while ((line = reader.readLine()) != null) {
                // 'RPC-EVENT',1424280209519,'onSuccess','com.nightingale.remote.rpc.action.Practice_getIDs@34c26d26','com.medrium.gwt.client.widgets.shared.CancellableAsyncCallback@488bf4d9'
                int idx = line.indexOf("'RPC-EVENT'");
                if (idx > 0) {
                    String event = line.substring(idx);
                    h.execute("INSERT INTO event VALUES(" + event +")");
                }
            }
        }
        catch (IOException x) {
            x.printStackTrace();
        }
    }
}
