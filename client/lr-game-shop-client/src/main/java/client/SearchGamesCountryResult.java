package client;

import domain.CountryInfo;
import domain.LanguageCode;
import java.util.Map;
import java.util.Set;

/**
 * @param results gameId -> countryInfos
 */
public record SearchGamesCountryResult(Map<Long, Map<LanguageCode, CountryInfo>> results) {
    public Set<Long> gameIds() {
        return results.keySet();
    }

    public Map<LanguageCode, CountryInfo> getCountryInfos(long gameId) {
        return results.get(gameId);
    }
}
