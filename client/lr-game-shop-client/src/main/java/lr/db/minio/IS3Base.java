package lr.db.minio;

import java.io.InputStream;

public interface IS3Base {
    boolean bucketExists(String bucketName);

    boolean makeBucket(String bucket);

    boolean putObject(String bucket, String objectName, byte[] object);

    InputStream getObject(String bucket, String objectName);
}
