package client.cassandra;

import com.datastax.driver.core.Session;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class CassandraConnectorProvider {
    private static final Logger log = LoggerFactory.getLogger(CassandraConnectorProvider.class);
    private CassandraConnector connector;

    // TODO: Connect to all cluster, not to one host.
    @Bean
    public Session cassandraSession(
        @Value("${cassandra.node.address}") String address,
        @Value("${cassandra.node.port}") int port) {
        log.info("Trying to connect cluster: node {}:{}", address, port);
        CassandraConnector connector = new CassandraConnector();
        connector.connect(address, port);
        this.connector = connector;
        log.info("Node connected {}:{}", address, port);

        return connector.getSession();
    }

    @PreDestroy
    public void close() {
        connector.close();
    }
}

