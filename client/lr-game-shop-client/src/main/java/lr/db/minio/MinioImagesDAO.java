package lr.db.minio;

import java.io.InputStream;
import lr.db.IFilesDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class MinioImagesDAO implements IFilesDAO {
    private static final Logger log = LoggerFactory.getLogger(MinioImagesDAO.class);
    private static final String BUCKET_NAME = "game-shop-images";
    private final IS3Base db;

    public MinioImagesDAO(IS3Base db) {
        this.db = db;
        initBucket();
    }

    private void initBucket() {
        boolean bucketExists;
        try {
            bucketExists = db.bucketExists(BUCKET_NAME);
        } catch (Exception e) {
            log.error("Can't check if {} bucket exists in minio.", BUCKET_NAME, e);
            throw new RuntimeException(e);
        }

        if (!bucketExists) {
            try {
                if (!db.makeBucket(BUCKET_NAME)) {
                    throw new IllegalStateException("Cant create bucket for shop images");
                }
            } catch (Exception e) {
                log.error("Can't create {} bucket in minio.", BUCKET_NAME, e);
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public boolean upload(String filename, byte[] file) {
        return db.putObject(BUCKET_NAME, filename, file);
    }

    @Override
    public InputStream download(String filename) {
        return db.getObject(BUCKET_NAME, filename);
    }
}
