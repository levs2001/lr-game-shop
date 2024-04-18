package lr.client.cassandra;


import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;

public class CassandraConnector {
    private Cluster cluster;
    private Session session;

    /**
     * Адресов может быть много, но порт у всех один, так сделан драйвер.
     */
    public void connect(String[] addresses, Integer port) {
        Cluster.Builder b = Cluster.builder().addContactPoints(addresses);
        if (port != null) {
            b.withPort(port);
        }
        cluster = b.build();

        session = cluster.connect();
    }

    public Session getSession() {
        return this.session;
    }

    public void close() {
        session.close();
        cluster.close();
    }
}