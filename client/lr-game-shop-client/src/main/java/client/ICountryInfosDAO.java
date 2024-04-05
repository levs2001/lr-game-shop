package client;

import domain.CountryInfo;
import domain.LanguageCode;
import java.util.Map;

public interface ICountryInfosDAO {
    // TODO: return smth to know about creating status.
    void add(long gameId, Map<LanguageCode, CountryInfo> countryInfos);

    Map<LanguageCode, CountryInfo> get(long gameId);

    SearchGamesCountryResult searchByTitle(String title, int limit);

    void delete(long gameId);
}
