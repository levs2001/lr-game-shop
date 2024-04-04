package server;

import domain.GameRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {
    private static final Logger log = LoggerFactory.getLogger(Controller.class);
    private static final String SHOP_PREFIX = "/game-shop/";

    @PostMapping(SHOP_PREFIX + "add-game/")
    ResponseEntity<String> addGame(@RequestParam GameRow game) {
        log.info("Adding game {}", game.gameId());

        return new ResponseEntity<>(String.format("Game %d added to shop.", game.gameId()), HttpStatus.CREATED);
    }

    @GetMapping(SHOP_PREFIX + "game/")
    ResponseEntity<GameRow> getGame(@RequestParam long id) {
        log.info("Get game {}", id);

        return null;
    }

    @GetMapping(SHOP_PREFIX + "search/")
    ResponseEntity<GameRow> searchByTitle(@RequestParam String title, @RequestParam int limit) {
        log.info("Search by title {} with limit {}", title, limit);

        return null;
    }

    @GetMapping(SHOP_PREFIX + "search/")
    ResponseEntity<GameRow> searchByDeveloper(@RequestParam String developer, @RequestParam int limit) {
        log.info("Search by developer {} with limit {}", developer, limit);

        return null;
    }

    @GetMapping(SHOP_PREFIX + "search/")
    ResponseEntity<GameRow> searchByPublisher(@RequestParam String publisher, @RequestParam int limit) {
        log.info("Search by publisher {} with limit {}", publisher, limit);

        return null;
    }

    @PutMapping(SHOP_PREFIX + "update-game/")
    ResponseEntity<GameRow> updateGame(@RequestParam GameRow game) {
        log.info("Update game {}", game.gameId());

        // Check that game presented, if not presented we will not update
        return null;
    }

    @DeleteMapping(SHOP_PREFIX + "delete-game/")
    ResponseEntity<Long> deleteGame(@RequestParam long id) {
        log.info("Delete game {}", id);

        return null;
    }
}
