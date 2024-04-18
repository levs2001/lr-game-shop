package lr.client.mongo;

import static java.util.Arrays.asList;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;

import com.mongodb.ConnectionString;
import com.mongodb.DBObjectCodecProvider;
import com.mongodb.DBRefCodecProvider;
import com.mongodb.DocumentToDBRefTransformer;
import com.mongodb.Jep395RecordCodecProvider;
import com.mongodb.KotlinCodecProvider;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.gridfs.codecs.GridFSFileCodecProvider;
import com.mongodb.client.model.geojson.codecs.GeoJsonCodecProvider;
import com.mongodb.client.model.mql.ExpressionCodecProvider;
import jakarta.annotation.PreDestroy;
import java.util.List;
import org.bson.codecs.BsonCodecProvider;
import org.bson.codecs.BsonValueCodecProvider;
import org.bson.codecs.CollectionCodecProvider;
import org.bson.codecs.DocumentCodecProvider;
import org.bson.codecs.EnumCodecProvider;
import org.bson.codecs.IterableCodecProvider;
import org.bson.codecs.JsonObjectCodecProvider;
import org.bson.codecs.MapCodecProvider;
import org.bson.codecs.ValueCodecProvider;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.jsr310.Jsr310CodecProvider;
import org.bson.codecs.record.RecordCodecProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MongoClientProvider {
    private static final Logger log = LoggerFactory.getLogger(MongoClientProvider.class);
    private MongoClient mongoClient;

    @Bean
    public MongoClient mongoClient(@Value("${mongo.uri}") String uri) {
        log.info("Trying to connect mongo cluster with uri: {}", uri);
        ConnectionString connectionString = new ConnectionString(uri);
        CodecRegistry codecRegistry = CodecRegistries.fromRegistries(fromProviders(codecProviders()));
        MongoClientSettings clientSettings = MongoClientSettings.builder()
            .applyConnectionString(connectionString)
            .codecRegistry(codecRegistry)
            .build();

        mongoClient = MongoClients.create(clientSettings);
        log.info("Mongo connected. {}", uri);
        return mongoClient;
    }

    private static List<CodecProvider> codecProviders() {
        return asList(
            new ValueCodecProvider(),
            new BsonValueCodecProvider(),
            new DBRefCodecProvider(),
            new DBObjectCodecProvider(),
            new DocumentCodecProvider(new DocumentToDBRefTransformer()),
            new CollectionCodecProvider(new DocumentToDBRefTransformer()),
            new IterableCodecProvider(new DocumentToDBRefTransformer()),
            new MapCodecProvider(new DocumentToDBRefTransformer()),
            new GeoJsonCodecProvider(),
            new GridFSFileCodecProvider(),
            new Jsr310CodecProvider(),
            new JsonObjectCodecProvider(),
            new BsonCodecProvider(),
            new EnumCodecProvider(),
            new ExpressionCodecProvider(),
            new KotlinCodecProvider(),
            new RecordCodecProvider()
        );
    }

    @PreDestroy
    public void close() {
        mongoClient.close();
    }
}
