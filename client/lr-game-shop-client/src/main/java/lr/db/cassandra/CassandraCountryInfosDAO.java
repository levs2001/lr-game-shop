package lr.db.cassandra;

import static com.datastax.oss.driver.api.querybuilder.QueryBuilder.deleteFrom;
import static com.datastax.oss.driver.api.querybuilder.QueryBuilder.insertInto;
import static com.datastax.oss.driver.api.querybuilder.QueryBuilder.literal;
import static com.datastax.oss.driver.api.querybuilder.QueryBuilder.selectFrom;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import lr.db.ICountryInfosDAO;
import lr.db.SearchGamesCountryResult;
import lr.domain.CountryCode;
import lr.domain.CountryInfo;
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

    private final CqlSession session;

    public CassandraCountryInfosDAO(CqlSession session) {
        this.session = session;

        initTable();
    }

    // TODO: что по апдейтам и добавлению, когда уже существует
    @Override
    public void add(long gameId, Map<CountryCode, CountryInfo> countryInfos) {
        for (var entry : countryInfos.entrySet()) {
            var countryField = entry.getValue();
            session.execute(
                insertInto(KEYSPACE, TABLE)
                    .value(C_GAME_ID, literal(gameId))
                    .value(C_LANGUAGE_CODE, literal(entry.getKey().name()))
                    .value(C_TITLE, literal(countryField.title()))
                    .value(C_DESCRIPTION, literal(countryField.description()))
                    .value(C_PRICE, literal(countryField.price()))
                    .build()
            );
        }
    }

    @Override
    public Map<CountryCode, CountryInfo> get(long gameId) {
        ResultSet resultSet = session.execute(
            selectFrom(KEYSPACE, TABLE).all().whereColumn(C_GAME_ID).isEqualTo(literal(gameId)).build()
        );

        Map<CountryCode, CountryInfo> result = new EnumMap<>(CountryCode.class);
        resultSet.forEach(r -> putCountryEntry(result, r));
        return result;
    }

    @Override
    public SearchGamesCountryResult searchByTitle(CountryCode countryCode, String title, int limit) {
        ResultSet resultSet = session.execute(
            selectFrom(KEYSPACE, TABLE).all()
                .whereColumn(C_TITLE).isEqualTo(literal(title))
                .whereColumn(C_LANGUAGE_CODE).isEqualTo(literal(countryCode.name()))
                .limit(limit)
                .allowFiltering()
                .build()
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
            deleteFrom(KEYSPACE, TABLE).whereColumn(C_GAME_ID).isEqualTo(literal(gameId)).build()
        );
    }

    @Override
    public long getAllGameCountryInfosCount() {
        return session.execute(
            selectFrom(KEYSPACE, TABLE).countAll().build()
        ).one().getLong("count");
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

