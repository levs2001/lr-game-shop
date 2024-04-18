package lr.client;

import lr.domain.CountryInfo;
import lr.domain.CountryCode;
import java.util.Map;

public interface ICountryInfosDAO {
    // TODO: return smth to know about creating status.
    void add(long gameId, Map<CountryCode, CountryInfo> countryInfos);

    Map<CountryCode, CountryInfo> get(long gameId);

    SearchGamesCountryResult searchByTitle(CountryCode countryCode, String title, int limit);

    void delete(long gameId);
}
