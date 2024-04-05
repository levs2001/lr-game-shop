package domain;

import java.util.Date;

/**
 * Static for all countries.
 */
public record CommonInfo(long gameId, String developer, String publisher, Date releaseDate, String genre, String platform, int rating) {
}
