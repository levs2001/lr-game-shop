package server;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;
import server.cassandra.CassandraConnector;

@RestController
public class CassandraController {
    private static final Logger log = LoggerFactory.getLogger(CassandraController.class);
    private final CassandraConnector cassandraConnector;

    public CassandraController(CassandraConnector cassandraConnector) {
        this.cassandraConnector = cassandraConnector;
    }

    // TODO: такая штука работать не будет из-за некорректных символов в запросе
    @GetMapping(value = "/cassandra/game-shop")
    ResponseEntity<List<String>> query(@RequestParam String query) {
        log.info("query. query: {}", query);
        Session session = cassandraConnector.getSession();
        ResultSet result = session.execute(query);
        log.info("Result set: {}", result);

        return new ResponseEntity<>(result.all().stream().map(r -> r.getString(0)).toList(), HttpStatus.OK);
    }

    // Just for test
    @GetMapping(value = "/cassandra/game-shop/id")
    ResponseEntity<Long> id(@RequestParam long id) {
        log.info("id. id: {}", id);
        return new ResponseEntity<>(id, HttpStatus.OK);
    }

    // Just for test
    @GetMapping(value = "/time")
    ResponseEntity<Long> time() {
        return new ResponseEntity<>(System.currentTimeMillis(), HttpStatus.OK);
    }
}
