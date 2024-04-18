package lr.client;

import lr.domain.CommonInfo;
import java.util.List;

public interface ICommonInfoDAO {
    void add(CommonInfo info);

    CommonInfo get(long gameId);

    List<CommonInfo> searchByDeveloper(String developer, int limit);

    List<CommonInfo> searchByPublisher(String publisher, int limit);

    void delete(long gameId);
}
