package lr.server;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import java.util.List;
import lr.domain.CountryCode;
import lr.domain.GameRow;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("/game-shop")
public interface IGameShopController {
    @Operation(summary = "Add new game")
    @PostMapping("add-game/")
    ResponseEntity<String> addGame(@Parameter(description = "Full information about game") @RequestBody GameRow game);

    @Operation(summary = "Find game by id")
    @GetMapping("game/")
    ResponseEntity<GameRow> getGame(@RequestParam long id);

    @Operation(
        summary = "Search game by title with specific language code",
        description = "Only specific for this language game info will be returned"
    )
    @GetMapping("search/title/")
    ResponseEntity<List<GameRow>> searchByTitle(@RequestParam CountryCode countryCode, @RequestParam String title, @RequestParam int limit);

    @Operation(
        summary = "Search by developer", description = "Note that developer name is one for all languages"
    )
    @GetMapping("search/developer/")
    ResponseEntity<List<GameRow>> searchByDeveloper(@RequestParam String developer, @RequestParam int limit);

    @Operation(
        summary = "Search by publisher", description = "Note that publisher name is one for all languages"
    )
    @GetMapping("search/publisher/")
    ResponseEntity<List<GameRow>> searchByPublisher(@RequestParam String publisher, @RequestParam int limit);

    @Operation(summary = "Update game", description = "Game with current id will be deleted and then added new one")
    @PutMapping("update-game/")
    ResponseEntity<String> updateGame(@Parameter(description = "Full information about game") @RequestBody GameRow game);

    @Operation(summary = "Delete game with current id")
    @DeleteMapping("delete-game/")
    ResponseEntity<Long> deleteGame(@RequestParam long id);

    @Operation(summary = "Get games count")
    @GetMapping("games/count/")
    ResponseEntity<Long> getGamesCount();

    @Operation(summary = "Get all game versions in all countries count")
    @GetMapping("games/countries/")
    ResponseEntity<Long> getAllGamesCountriesCount();
}
