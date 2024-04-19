package lr.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Map;

@Schema(description = "All information about one game")
public record GameRow(
    @Schema(description = "Common information, one for all countries") CommonInfo commonInfo,
    @Schema(description = "Map from country code (RU, EN, FR) to information that dependent on country") Map<CountryCode, CountryInfo> countryInfos
) {
}

