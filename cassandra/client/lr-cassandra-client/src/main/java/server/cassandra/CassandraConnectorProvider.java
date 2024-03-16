package server.cassandra;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class CassandraConnectorProvider {
    private static final Logger log = LoggerFactory.getLogger(CassandraConnectorProvider.class);

    @Bean
    public CassandraConnector cassandraConnector(@Value("${cassandra.node.address}") String address,
                                                 @Value("${cassandra.node.port}") int port) {
        log.info("Trying to connect cluster: node {}:{}", address, port);
        CassandraConnector connector = new CassandraConnector();
        connector.connect(address, port);
        log.info("Node connected {}:{}", address, port);

        return connector;
    }
}
