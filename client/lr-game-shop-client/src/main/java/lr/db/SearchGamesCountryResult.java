package lr.db;

import lr.domain.CountryInfo;
import lr.domain.CountryCode;
import java.util.Map;
import java.util.Set;

/**
 * @param results gameId -> countryInfos
 */
public record SearchGamesCountryResult(Map<Long, Map<CountryCode, CountryInfo>> results) {
    public Set<Long> gameIds() {
        return results.keySet();
    }

    public Map<CountryCode, CountryInfo> getCountryInfos(long gameId) {
        return results.get(gameId);
    }
}
