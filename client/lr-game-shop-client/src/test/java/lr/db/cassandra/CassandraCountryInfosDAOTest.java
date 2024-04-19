package lr.db.cassandra;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lr.Application;
import lr.db.ICountryInfosDAO;
import lr.db.SearchGamesCountryResult;
import lr.domain.CountryInfo;
import lr.domain.CountryCode;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(properties = "spring.main.lazy-initialization=true", classes = Application.class)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@TestPropertySource(locations = "classpath:cassandra-test.properties")
class CassandraCountryInfosDAOTest {
    private final ICountryInfosDAO countryInfosDAO;

    public CassandraCountryInfosDAOTest(ICountryInfosDAO countryInfosDAO) {
        this.countryInfosDAO = countryInfosDAO;
    }

    @Test
    public void testAddAndGet() {
        long gameId = 1;
        var expected = Map.of(
            CountryCode.EN,
            CountryInfo.create("Test game", "Test description", 2.0)
        );
        countryInfosDAO.add(gameId, expected);

        var actual = countryInfosDAO.get(gameId);
        assertEquals(expected, actual);

        // Clear and check deletion
        deleteWithCheck(gameId);
    }

    @Test
    public void testSearch() {
        var titles = List.of("Xcom", "Battlefield", "Super game");
        CountryCode countryCode = CountryCode.EN;
        List<Map<CountryCode, CountryInfo>> countryInfos = new ArrayList<>();
        for (int id = 0; id < titles.size(); id++) {
            var countryInfo = Map.of(
                countryCode,
                CountryInfo.create(titles.get(id), "", 2.0)
            );

            countryInfosDAO.add(id, countryInfo);
            countryInfos.add(countryInfo);
        }

        // Test that we can find all added titles
        for (int testId = 0; testId < titles.size(); testId++) {
            SearchGamesCountryResult result = countryInfosDAO.searchByTitle(countryCode, titles.get(testId), 10);
            assertEquals(1, result.results().size());
            assertEquals(countryInfos.get(testId), result.getCountryInfos(testId));
        }

        // Test that we can't find smth random
        SearchGamesCountryResult result = countryInfosDAO.searchByTitle(countryCode,"Random", 10);
        assertEquals(0, result.results().size());

        // Deleting test data
        for (int testId = 0; testId < titles.size(); testId++) {
            deleteWithCheck(testId);
        }
    }

    @Test
    public void testSearchWithLimit() {
        String oneTitle = "One title";
        CountryCode countryCode = CountryCode.EN;
        for (long id = 0; id < 20; id++) {
            var countryInfo = Map.of(
                countryCode,
                CountryInfo.create(oneTitle, "", 2.0)
            );
            countryInfosDAO.add(id, countryInfo);
        }

        for (int lim = 10; lim <= 20; lim += 2) {
            checkLimit(lim, oneTitle, countryCode);
        }

        for (long id = 0; id < 20; id++) {
            deleteWithCheck(id);
        }
    }

    private void checkLimit(int limit, String oneTitle, CountryCode countryCode) {
        assertEquals(limit, countryInfosDAO.searchByTitle(countryCode, oneTitle, limit).gameIds().size());
    }

    private void deleteWithCheck(long gameId) {
        countryInfosDAO.delete(gameId);
        var actual = countryInfosDAO.get(gameId);
        assertTrue(actual.isEmpty());
    }
}