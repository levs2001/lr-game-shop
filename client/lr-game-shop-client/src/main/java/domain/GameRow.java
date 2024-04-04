package domain;

import java.util.Date;
import java.util.EnumMap;

// TODO: genre и platform можно сделать enum.
public record GameRow(long gameId, String developer, String publisher, Date releaseDate, String genre, String platform, int rating,
                      EnumMap<LanguageCode, CountryField> countryFields) {
}
