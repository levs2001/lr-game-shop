package lr.server;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import lr.domain.CommonInfo;
import lr.domain.CountryInfo;
import lr.domain.GameRow;
import lr.domain.CountryCode;

public class GameRowsForTest {
    private final Map<Long, GameRow> gameMap = Map.of(
        1000L,
        new GameRow(
            CommonInfo.create(1000L, "Dev", "Pub", new Date(), "Anime game", "PC", 1.0),
            Map.of(
                CountryCode.EN, CountryInfo.create("Super anime kato", "Low price anime game", 100),
                CountryCode.RU, CountryInfo.create("Супер аниме като", "Аниме игра по низкой цене", 700)
            )
        ),

        1001L,
        new GameRow(
            CommonInfo.create(1001L, "Iogan", "Kreo", new Date(), "Action", "Xbox", 5.0),
            Map.of(
                CountryCode.EN, CountryInfo.create("Kruassan", "Game about kruassan", 11),
                CountryCode.RU, CountryInfo.create("Круассан", "Игра про круассан", 110)
            )
        ),

        1002L,
        new GameRow(
            CommonInfo.create(1002L, "Hurakami", "Kasio", new Date(), "Karate", "Play Station", 1.0),
            Map.of(
                CountryCode.EN, CountryInfo.create("Naruto warrior", "Game about naruto", 100),
                CountryCode.RU, CountryInfo.create("Наруто воин", "Игра про наруто", 700)
            )
        ),


        // У 1003 и 1004 одинаковые названия для проверки поиска.
        1003L,
        new GameRow(
            CommonInfo.create(1003L, "GSC", "Kosh", new Date(), "Shooter", "PC", 10.0),
            Map.of(
                CountryCode.EN, CountryInfo.create("Stalker", "About Chernobil", 10),
                CountryCode.RU, CountryInfo.create("Сталкер", "Про Чернобыльскую зону отчуждения", 500)
            )
        ),

        1004L,
        new GameRow(
            CommonInfo.create(1004L, "Krivorog", "Gfa", new Date(), "Novella", "Mobile", 10.0),
            Map.of(
                CountryCode.EN, CountryInfo.create("Stalker", "Game from film", 1),
                CountryCode.RU, CountryInfo.create("Сталкер", "Игра по фильму", 10)
            )
        )

    );

    private final Set<Long> allIds;
    private final Collection<GameRow> allGames;
    private final long countriesInfosCount;

    public GameRowsForTest() {
        allIds = gameMap.keySet();
        allGames = gameMap.values();

        long countriesInfosCount = 0;
        for (var game : allGames) {
            countriesInfosCount += game.countryInfos().size();
        }
        this.countriesInfosCount = countriesInfosCount;
    }

    public Set<Long> getAllIds() {
        return allIds;
    }

    public Collection<GameRow> getAllGames() {
        return allGames;
    }

    public long getCountriesInfosCount() {
        return countriesInfosCount;
    }

    public GameRow get(long id) {
        return gameMap.get(id);
    }
}
