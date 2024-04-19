package lr.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Date;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;

/**
 * Static for all countries.
 */
@Schema(description = "Information about game that is static for all countries")
public record CommonInfo(@BsonId @BsonProperty(C_GAME_ID) long gameId,
                         @BsonProperty(C_DEVELOPER) String developer,
                         @BsonProperty(C_PUBLISHER) String publisher,
                         @BsonProperty(C_RELEASE_DATE) Date releaseDate,
                         @BsonProperty(C_GENRE) String genre,
                         @BsonProperty(C_PLATFORM) String platform,
                         @BsonProperty(C_RATING) double rating) {
    // Don't change id column name, only _id name supported by mongo for id column
    public static final String C_GAME_ID = "_id";
    public static final String C_DEVELOPER = "developer";
    public static final String C_PUBLISHER = "publisher";
    public static final String C_RELEASE_DATE = "release_date";
    public static final String C_GENRE = "genre";
    public static final String C_PLATFORM = "platform";
    public static final String C_RATING = "rating";

    public static CommonInfo create(
        long gameId, String developer, String publisher, Date releaseDate, String genre, String platform, double rating) {
        return new CommonInfo(gameId, developer, publisher, releaseDate, genre, platform, rating);
    }
}
