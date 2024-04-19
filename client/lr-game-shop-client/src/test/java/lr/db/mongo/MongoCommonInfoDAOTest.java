package lr.db.mongo;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lr.Application;
import lr.db.ICommonInfoDAO;
import lr.domain.CommonInfo;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(properties = "spring.main.lazy-initialization=true", classes = Application.class)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@TestPropertySource(locations = "classpath:mongo-test.properties")
class MongoCommonInfoDAOTest {
    private final ICommonInfoDAO commonInfoDAO;

    public MongoCommonInfoDAOTest(ICommonInfoDAO commonInfoDAO) {
        this.commonInfoDAO = commonInfoDAO;
    }

    @Test
    public void testAddAndGet() {
        long id = 1;
        CommonInfo expected = CommonInfo.create(id, "Leo", "Mirukawa", new Date(), "action", "pc", 8.5);
        commonInfoDAO.add(expected);
        assertEquals(expected, commonInfoDAO.get(id));

        deleteWithCheck(id);
    }

    @Test
    public void testSearchByDeveloper() {
        List<String> developers = List.of("Mind fish", "Ea", "Luxsoft", "Yacuti", "Korean games");
        Map<String, CommonInfo> ciMap = new HashMap<>();
        for (int id = 0; id < developers.size(); id++) {
            CommonInfo ci = withDeveloper(id, developers.get(id));
            commonInfoDAO.add(ci);
            ciMap.put(ci.developer(), ci);
        }

        for (String dev : ciMap.keySet()) {
            List<CommonInfo> infos = commonInfoDAO.searchByDeveloper(dev, 10);
            assertEquals(1, infos.size());
            assertEquals(ciMap.get(dev), infos.getFirst());
        }

        for (CommonInfo ci : ciMap.values()) {
            deleteWithCheck(ci.gameId());
        }
    }

    @Test
    public void testSearchByPublisher() {
        List<String> publishers = List.of("Publ Mind fish", "Publ Ea", "Publ Luxsoft", "Publ Yacuti", "Publ Korean games");
        Map<String, CommonInfo> ciMap = new HashMap<>();
        for (int id = 0; id < publishers.size(); id++) {
            CommonInfo ci = withPublisher(id, publishers.get(id));
            commonInfoDAO.add(ci);
            ciMap.put(ci.publisher(), ci);
        }

        for (String pub : ciMap.keySet()) {
            List<CommonInfo> infos = commonInfoDAO.searchByPublisher(pub, 10);
            assertEquals(1, infos.size(), pub);
            assertEquals(ciMap.get(pub), infos.getFirst(), pub);
        }

        for (CommonInfo ci : ciMap.values()) {
            deleteWithCheck(ci.gameId());
        }
    }

    @Test
    public void testSearchWithLimit() {
        String oneStr = "One developer or publisher";
        int addN = 20;

        for (int i = 0; i < addN; i++) {
            commonInfoDAO.add(withDeveloper(i, oneStr));
            commonInfoDAO.add(withPublisher(addN + i, oneStr));
        }

        assertEquals(10, commonInfoDAO.searchByPublisher(oneStr, 10).size());
        assertEquals(20, commonInfoDAO.searchByPublisher(oneStr, 40).size());

        assertEquals(10, commonInfoDAO.searchByDeveloper(oneStr, 10).size());
        assertEquals(20, commonInfoDAO.searchByDeveloper(oneStr, 40).size());

        for (int i = 0; i < addN * 2; i++) {
            deleteWithCheck(i);
        }
    }

    private CommonInfo withPublisher(long id, String publisher) {
        return CommonInfo.create(id, "", publisher, new Date(), "", "", 7.3 + id);
    }

    private CommonInfo withDeveloper(long id, String developer) {
        return CommonInfo.create(id, developer, "", new Date(), "", "", 9.0 + id);

    }

    private void deleteWithCheck(long id) {
        commonInfoDAO.delete(id);
        assertNull(commonInfoDAO.get(id));
    }

}