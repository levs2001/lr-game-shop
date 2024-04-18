package lr.server;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import lr.Application;
import lr.domain.GameRow;
import lr.domain.CountryCode;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.TestPropertySource;

// TODO: Add tests for another methods.
@SpringBootTest(properties = "spring.main.lazy-initialization=true", classes = Application.class)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@TestPropertySource(locations = {"classpath:cassandra-test.properties", "classpath:mongo-test.properties"})
class GameShopControllerTest {
    private static final GameRowsForTest gameRows = new GameRowsForTest();
    private final IGameShopController controller;

    GameShopControllerTest(IGameShopController controller) {
        this.controller = controller;
    }

    @Test
    public void testAddAndGet() {
        GameRow expected = gameRows.get(1000L);

        var status = controller.addGame(expected);
        assertEquals(HttpStatus.CREATED, status.getStatusCode());

        ResponseEntity<GameRow> answer = controller.getGame(expected.commonInfo().gameId());
        assertEquals(HttpStatus.OK, answer.getStatusCode());
        assertEquals(expected, answer.getBody());

        deleteWithCheck(expected.commonInfo().gameId());
    }

    @Test
    public void testSearchByTitle() {
        for (GameRow game : gameRows.getAllGames()) {
            var status = controller.addGame(game);
            assertEquals(HttpStatus.CREATED, status.getStatusCode());
        }

        CountryCode countryCode = CountryCode.EN;
        long katoId = 1001L;
        var katoActual = searchTitleWithCheck(gameRows.get(katoId).countryInfos().get(countryCode).title(), countryCode);
        // Проверяем что находится одна игра у которой один тайтл
        assertEquals(1, katoActual.size());
        // Я не проверяю на эквивалентность весь GameRow поскольку запрашивается только конкретный язык
        assertEquals(katoId, katoActual.getFirst().commonInfo().gameId());

        // Игры с тайтлом Stalker 2
        List<GameRow> stalker = searchTitleWithCheck(gameRows.get(1003L).countryInfos().get(countryCode).title(), countryCode);
        var idsStalker = stalker.stream().map(r -> r.commonInfo().gameId()).toList();
        assertEquals(List.of(1003L, 1004L), idsStalker);

        for (long id : gameRows.getAllIds()) {
            deleteWithCheck(id);
        }
    }

    public List<GameRow> searchTitleWithCheck(String title, CountryCode code) {
        var res = controller.searchByTitle(code, title, 10);
        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertNotNull(res.getBody());
        return res.getBody();
    }

    public void deleteWithCheck(long gameId) {
        var status = controller.deleteGame(gameId);
        assertEquals(HttpStatus.OK, status.getStatusCode());
        var ans = controller.getGame(gameId);
        assertNull(ans.getBody());
        assertEquals(HttpStatus.NOT_FOUND, ans.getStatusCode());
    }
}