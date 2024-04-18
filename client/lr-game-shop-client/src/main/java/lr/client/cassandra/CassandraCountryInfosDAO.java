package lr.client.cassandra;

import com.datastax.driver.core.Row;
import java.util.HashMap;
import lr.client.ICountryInfosDAO;
import lr.client.SearchGamesCountryResult;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import lr.domain.CountryInfo;
import lr.domain.CountryCode;
import java.util.EnumMap;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class CassandraCountryInfosDAO implements ICountryInfosDAO {
    private static final String KEYSPACE = "game_shop";
    private static final String TABLE = "country_info";

    private static final String C_GAME_ID = "game_id";
    private static final String C_LANGUAGE_CODE = "language_code";
    private static final String C_TITLE = "title";
    private static final String C_DESCRIPTION = "description";
    private static final String C_PRICE = "price";
    private static final String[] ALL_COLUMNS = new String[] {C_GAME_ID, C_LANGUAGE_CODE, C_TITLE, C_DESCRIPTION, C_PRICE};

    private final Session session;

    public CassandraCountryInfosDAO(Session session) {
        this.session = session;

        initTable();
    }

    // TODO: что по апдейтам и добавлению, когда уже существует
    @Override
    public void add(long gameId, Map<CountryCode, CountryInfo> countryInfos) {
        for (var entry : countryInfos.entrySet()) {
            var countryField = entry.getValue();
            session.execute(
                QueryBuilder.insertInto(KEYSPACE, TABLE).values(ALL_COLUMNS, new Object[] {
                    gameId, entry.getKey().name(), countryField.title(), countryField.description(), countryField.price()
                })
            );
        }
    }

    @Override
    public Map<CountryCode, CountryInfo> get(long gameId) {
        ResultSet resultSet = session.execute(
            QueryBuilder.select(ALL_COLUMNS).from(KEYSPACE, TABLE).where(QueryBuilder.eq(C_GAME_ID, gameId))
        );

        Map<CountryCode, CountryInfo> result = new EnumMap<>(CountryCode.class);
        resultSet.forEach(r -> putCountryEntry(result, r));
        return result;
    }

    @Override
    public SearchGamesCountryResult searchByTitle(CountryCode countryCode, String title, int limit) {
        ResultSet resultSet = session.execute(
            QueryBuilder.select(ALL_COLUMNS).from(KEYSPACE, TABLE)
                .where(QueryBuilder.eq(C_TITLE, title)).and(QueryBuilder.eq(C_LANGUAGE_CODE, countryCode.name()))
                .limit(limit).allowFiltering()
        );
        Map<Long, Map<CountryCode, CountryInfo>> results = new HashMap<>();
        resultSet.forEach(r -> putCountryEntry(results.computeIfAbsent(r.getLong(C_GAME_ID), k -> new HashMap<>()), r));

        return new SearchGamesCountryResult(results);
    }

    private void putCountryEntry(Map<CountryCode, CountryInfo> map, Row row) {
        map.put(
            CountryCode.valueOf(row.getString(C_LANGUAGE_CODE)),
            CountryInfo.create(row.getString(C_TITLE), row.getString(C_DESCRIPTION), row.getDouble(C_PRICE))
        );
    }

    @Override
    public void delete(long gameId) {
        session.execute(
            QueryBuilder.delete().from(KEYSPACE, TABLE).where(QueryBuilder.eq(C_GAME_ID, gameId))
        );
    }

    private void initTable() {
        session.execute(String.format("""
            CREATE KEYSPACE IF NOT EXISTS %s WITH REPLICATION = { 'class' : 'SimpleStrategy', 'replication_factor' : '3' };
            """, KEYSPACE));

        session.execute(String.format("""
            CREATE TABLE IF NOT EXISTS %s.%s (
                %s BIGINT,
                %s TEXT,
                %s TEXT,
                %s TEXT,
                %s DOUBLE,
                PRIMARY KEY ((game_id), language_code)
            );
            """, KEYSPACE, TABLE, C_GAME_ID, C_LANGUAGE_CODE, C_TITLE, C_DESCRIPTION, C_PRICE));

        session.execute(String.format("""
            CREATE INDEX IF NOT EXISTS ON %s.%s (  %s );
            """, KEYSPACE, TABLE, C_TITLE));
    }
}

