package client.cassandra;

import client.ICountryInfosDAO;
import client.SearchGamesCountryResult;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import domain.CountryInfo;
import domain.LanguageCode;
import java.util.EnumMap;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class CassandraCountryInfosDAO implements ICountryInfosDAO {
    private static final String KEYSPACE = "game_shop";
    private static final String TABLE = "country_infos";

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
    public void add(long gameId, Map<LanguageCode, CountryInfo> countryInfos) {
        for (var entry : countryInfos.entrySet()) {
            var countryField = entry.getValue();
            session.execute(
                QueryBuilder.insertInto(KEYSPACE, TABLE).values(ALL_COLUMNS, new Object[] {
                    gameId, entry.getKey(), countryField.title(), countryField.description(), countryField.price()
                })
            );
        }
    }

    @Override
    public Map<LanguageCode, CountryInfo> get(long gameId) {
        ResultSet resultSet = session.execute(
            QueryBuilder.select(ALL_COLUMNS).from(KEYSPACE, TABLE).where(QueryBuilder.eq(C_GAME_ID, gameId))
        );

        Map<LanguageCode, CountryInfo> result = new EnumMap<>(LanguageCode.class);
        resultSet.forEach(r ->
            result.put(
                LanguageCode.valueOf(r.getString(C_LANGUAGE_CODE)),
                new CountryInfo(r.getString(C_TITLE), r.getString(C_DESCRIPTION), r.getDouble(C_PRICE))
            )
        );
        return result;
    }

    @Override
    public SearchGamesCountryResult searchByTitle(String title, int limit) {
        // TODO
    }

    @Override
    public void delete(long gameId) {
        session.execute(
            QueryBuilder.delete().from(KEYSPACE, TABLE).where(QueryBuilder.eq(C_GAME_ID, gameId))
        );
    }

    private void initTable() {
        session.execute(String.format("""
            CREATE TABLE IF NOT EXISTS %s.%s (
                %s UUID,
                %s TEXT,
                %s TEXT,
                %s TEXT,
                %s DOUBLE,
                PRIMARY KEY ((game_id), language_code)
            );
            """, KEYSPACE, TABLE, C_GAME_ID, C_LANGUAGE_CODE, C_TITLE, C_DESCRIPTION, C_PRICE));
    }
}

