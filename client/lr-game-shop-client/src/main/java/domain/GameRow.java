package domain;

import java.util.Map;

public record GameRow(CommonInfo commonInfo,
                      Map<LanguageCode, CountryInfo> countryInfos) {
}

