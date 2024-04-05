package client.mongo;

import client.ICommonInfoDAO;
import domain.CommonInfo;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class MongoCommonInfoDAO implements ICommonInfoDAO {

    @Override
    public void add(CommonInfo info) {

    }

    @Override
    public CommonInfo get(long gameId) {
        return null;
    }

    @Override
    public List<CommonInfo> searchByTitle(String title, int limit) {
        return null;
    }

    @Override
    public List<CommonInfo> searchByDeveloper(String developer, int limit) {
        return null;
    }

    @Override
    public List<CommonInfo> searchByPublisher(String publisher, int limit) {
        return null;
    }
}
