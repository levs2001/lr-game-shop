package lr.db.cassandra;

import com.datastax.oss.driver.api.core.CqlSession;
import jakarta.annotation.PreDestroy;
import java.net.InetSocketAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CassandraConnectorProvider {
    private static final Logger log = LoggerFactory.getLogger(CassandraConnectorProvider.class);
    private static final String LOCAL_DC_NAME = "datacenter1";
    private CqlSession session;

    // TODO: Connect to all cluster, not to one host.
    @Bean
    public CqlSession cassandraSession(
        @Value("${cassandra.node.addresses}") String[] addresses,
        @Value("${cassandra.node.port}") int port) {
        log.info("Trying to connect cluster: node {}:{}", addresses, port);
        var sessionB = CqlSession.builder();
        for (String adress : addresses) {
            sessionB.addContactPoint(new InetSocketAddress(adress, port));
        }
        session = sessionB.withLocalDatacenter(LOCAL_DC_NAME).build();

        log.info("Cassandra connected {}:{}", addresses, port);

        return session;
    }

    @PreDestroy
    public void close() {
        session.close();
    }
}

