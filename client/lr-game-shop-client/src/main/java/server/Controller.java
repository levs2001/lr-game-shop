package server;

import client.ICommonInfoDAO;
import client.ICountryInfosDAO;
import client.SearchGamesCountryResult;
import domain.CommonInfo;
import domain.CountryInfo;
import domain.GameRow;
import domain.LanguageCode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

    private final ICommonInfoDAO commonInfoDAO;
    private final ICountryInfosDAO countryInfosDAO;

    Controller(ICommonInfoDAO commonInfoDAO, ICountryInfosDAO countryInfosDAO) {
        this.commonInfoDAO = commonInfoDAO;
        this.countryInfosDAO = countryInfosDAO;
    }

    @PostMapping(SHOP_PREFIX + "add-game/")
    ResponseEntity<String> addGame(@RequestParam GameRow game) {
        log.info("Adding game {}", game.commonInfo().gameId());
        // TODO: Risk of inconsistency.
        // TODO: Check create status.
        commonInfoDAO.add(game.commonInfo());
        countryInfosDAO.add(game.commonInfo().gameId(), game.countryInfos());
        return new ResponseEntity<>(String.format("Game %d added to shop.", game.commonInfo().gameId()), HttpStatus.CREATED);
    }

    @GetMapping(SHOP_PREFIX + "game/")
    ResponseEntity<GameRow> getGame(@RequestParam long id) {
        log.info("Get game {}", id);
        CommonInfo commonInfo = commonInfoDAO.get(id);
        if (commonInfo != null) {
            Map<LanguageCode, CountryInfo> countryInfos = countryInfosDAO.get(id);
            return new ResponseEntity<>(new GameRow(commonInfo, countryInfos), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping(SHOP_PREFIX + "search/")
    ResponseEntity<List<GameRow>> searchByTitle(@RequestParam String title, @RequestParam int limit) {
        log.info("Search by title {} with limit {}", title, limit);

        SearchGamesCountryResult games = countryInfosDAO.searchByTitle(title, limit);

        Set<Long> gameIds = games.gameIds();
        if (gameIds.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        List<GameRow> result = new ArrayList<>(gameIds.size());
        for (long gameId : gameIds) {
            CommonInfo commonInfo = commonInfoDAO.get(gameId);
            result.add(new GameRow(commonInfo, games.getCountryInfos(gameId)));
        }

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping(SHOP_PREFIX + "search/")
    ResponseEntity<List<GameRow>> searchByDeveloper(@RequestParam String developer, @RequestParam int limit) {
        log.info("Search by developer {} with limit {}", developer, limit);
        List<CommonInfo> games = commonInfoDAO.searchByDeveloper(developer, limit);
        return addRegionInfo(games);
    }

    @GetMapping(SHOP_PREFIX + "search/")
    ResponseEntity<List<GameRow>> searchByPublisher(@RequestParam String publisher, @RequestParam int limit) {
        log.info("Search by publisher {} with limit {}", publisher, limit);
        List<CommonInfo> games = commonInfoDAO.searchByPublisher(publisher, limit);
        return addRegionInfo(games);
    }

    private ResponseEntity<List<GameRow>> addRegionInfo(List<CommonInfo> games) {
        if (games.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        List<GameRow> result = new ArrayList<>(games.size());
        for (CommonInfo game : games) {
            Map<LanguageCode, CountryInfo> countryInfos = countryInfosDAO.get(game.gameId());
            result.add(new GameRow(game, countryInfos));
        }

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PutMapping(SHOP_PREFIX + "update-game/")
    ResponseEntity<List<GameRow>> updateGame(@RequestParam GameRow game) {
        log.info("Update game {}", game.commonInfo().gameId());
        // TODO: Check that game presented, if not presented we will not update
        // TODO: Not transactional
        deleteGame(game.commonInfo().gameId());
        addGame(game);
        return null;
    }

    @DeleteMapping(SHOP_PREFIX + "delete-game/")
    ResponseEntity<Long> deleteGame(@RequestParam long id) {
        log.info("Delete game {}", id);
        commonInfoDAO.delete(id);
        countryInfosDAO.delete(id);
        return null;
    }
}
