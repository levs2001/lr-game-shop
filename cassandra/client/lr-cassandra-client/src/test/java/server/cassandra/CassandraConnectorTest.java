package server.cassandra;

import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;

import static org.junit.jupiter.api.Assertions.assertEquals;


class CassandraConnectorTest {
    private static KeyspaceRepository schemaRepository;
    private static Session session;

    @BeforeAll
    public static void connect() {
        CassandraConnector client = new CassandraConnector();
        client.connect("127.0.0.1", 9042);
        session = client.getSession();
        schemaRepository = new KeyspaceRepository(session);
    }

    @Test
    public void whenCreatingAKeyspace_thenCreated() {
        String keyspaceName = "library";
        schemaRepository.createKeyspace(keyspaceName, "SimpleStrategy", 1);

        ResultSet result =
                session.execute("SELECT * FROM system_schema.keyspaces;");

        List<String> matchedKeyspaces = result.all()
                .stream()
                .filter(r -> r.getString(0).equals(keyspaceName.toLowerCase()))
                .map(r -> r.getString(0)).toList();

        assertEquals(matchedKeyspaces.size(), 1);
        assertEquals(matchedKeyspaces.get(0), keyspaceName.toLowerCase());
    }
}