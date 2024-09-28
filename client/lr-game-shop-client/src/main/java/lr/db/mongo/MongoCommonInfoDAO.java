package lr.db.mongo;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Indexes;
import java.util.ArrayList;
import lr.db.ICommonInfoDAO;
import lr.domain.CommonInfo;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class MongoCommonInfoDAO implements ICommonInfoDAO {
    private static final String DATABASE_NAME = "game_shop";
    private static final String COLLECTION_NAME = "common_info";

    private final MongoCollection<CommonInfo> collection;

    public MongoCommonInfoDAO(MongoClient mongoClient) {
        MongoDatabase database = mongoClient.getDatabase(DATABASE_NAME);
        // getCollection will return proxy collection even if it not exists
        // If collection was not created, will be created after insert.
        collection = database.getCollection(COLLECTION_NAME, CommonInfo.class);

        if (!database.listCollectionNames().into(new ArrayList<>()).contains(COLLECTION_NAME)) {
            database.createCollection(COLLECTION_NAME);
            collection.createIndex(Indexes.text(CommonInfo.C_DEVELOPER));
            collection.createIndex(Indexes.text(CommonInfo.C_PUBLISHER));
        }
    }

    @Override
    public void add(CommonInfo info) {
        collection.insertOne(info);
    }

    @Override
    public CommonInfo get(long gameId) {
        return collection.find(Filters.eq(CommonInfo.C_GAME_ID, gameId)).first();
    }

    @Override
    public List<CommonInfo> searchByDeveloper(String developer, int limit) {
        return searchList(CommonInfo.C_DEVELOPER, developer, limit);
    }

    @Override
    public List<CommonInfo> searchByPublisher(String publisher, int limit) {
        return searchList(CommonInfo.C_PUBLISHER, publisher, limit);
    }

    @Override
    public long getGamesCount() {
        return collection.countDocuments();
    }

    private List<CommonInfo> searchList(String colName, String colValue, int limit) {
        return collection.find(Filters.eq(colName, colValue)).limit(limit).into(new ArrayList<>());
    }

    @Override
    public void delete(long gameId) {
        collection.deleteOne(Filters.eq(CommonInfo.C_GAME_ID, gameId));
    }
}
