package lr.server;

import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lr.client.ICommonInfoDAO;
import lr.client.ICountryInfosDAO;
import lr.client.SearchGamesCountryResult;
import lr.domain.CommonInfo;
import lr.domain.CountryCode;
import lr.domain.CountryInfo;
import lr.domain.GameRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Main game shop controller", description = "Controller for operations with game shop")
@RestController
public class GameShopController implements IGameShopController {
    private static final Logger log = LoggerFactory.getLogger(GameShopController.class);
    private final ICommonInfoDAO commonInfoDAO;
    private final ICountryInfosDAO countryInfosDAO;

    GameShopController(ICommonInfoDAO commonInfoDAO, ICountryInfosDAO countryInfosDAO) {
        this.commonInfoDAO = commonInfoDAO;
        this.countryInfosDAO = countryInfosDAO;
    }

    @Override
    public ResponseEntity<String> addGame(GameRow game) {
        log.info("Adding game {}", game.commonInfo().gameId());
        // TODO: Risk of inconsistency.
        // TODO: Check create status.
        commonInfoDAO.add(game.commonInfo());
        countryInfosDAO.add(game.commonInfo().gameId(), game.countryInfos());
        return new ResponseEntity<>(String.format("Game %d added to shop.", game.commonInfo().gameId()), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<GameRow> getGame(long id) {
        log.info("Get game {}", id);
        CommonInfo commonInfo = commonInfoDAO.get(id);
        if (commonInfo != null) {
            Map<CountryCode, CountryInfo> countryInfos = countryInfosDAO.get(id);
            return new ResponseEntity<>(new GameRow(commonInfo, countryInfos), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @Override
    public ResponseEntity<List<GameRow>> searchByTitle(CountryCode countryCode, String title, int limit) {
        log.info("Search by title {} with limit {}", title, limit);

        SearchGamesCountryResult games = countryInfosDAO.searchByTitle(countryCode, title, limit);

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

    @Override
    public ResponseEntity<List<GameRow>> searchByDeveloper(String developer, int limit) {
        log.info("Search by developer {} with limit {}", developer, limit);
        List<CommonInfo> games = commonInfoDAO.searchByDeveloper(developer, limit);
        return addRegionInfo(games);
    }

    @Override
    public ResponseEntity<List<GameRow>> searchByPublisher(String publisher, int limit) {
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
            Map<CountryCode, CountryInfo> countryInfos = countryInfosDAO.get(game.gameId());
            result.add(new GameRow(game, countryInfos));
        }

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<String> updateGame(GameRow game) {
        log.info("Update game {}", game.commonInfo().gameId());
        // TODO: Check that game presented, if not presented we will not update
        // TODO: Not transactional
        deleteGame(game.commonInfo().gameId());
        addGame(game);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Long> deleteGame(long id) {
        log.info("Delete game {}", id);
        commonInfoDAO.delete(id);
        countryInfosDAO.delete(id);
        return new ResponseEntity<>(id, HttpStatus.OK);
    }
}
