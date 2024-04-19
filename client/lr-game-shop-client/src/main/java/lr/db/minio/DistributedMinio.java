package lr.db.minio;

import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Replication already done on minio level. But we need this client to use another minio server, if some is fallen.
 */
@Service
public class DistributedMinio implements IS3Base {
    private static final Logger log = LoggerFactory.getLogger(DistributedMinio.class);
    private final MinioClient[] clients;

    public DistributedMinio(
        @Value("${minio.endpoints}") String[] endpoints,
        @Value("${minio.access.key}") String accessKey,
        @Value("${minio.secret.key}") String secretKey
    ) {
        clients = createClients(endpoints, accessKey, secretKey);
    }

    @Override
    public boolean bucketExists(String bucketName) {
        try {
            // This operation used only in the begining of app initialization.
            // In the begining all minio nodes should be correct. So we use the first only.
            return clients[0].bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        } catch (Exception e) {
            log.error("Cant check bucket existence.", e);
            return false;
        }
    }

    @Override
    public boolean makeBucket(String bucket) {
        try {
            // This operation used only in the begining of app initialization.
            // In the begining all minio nodes should be correct. So we use the first only.
            clients[0].makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
        } catch (Exception e) {
            log.error("Cant make bucket.", e);
            return false;
        }
        return true;
    }

    @Override
    public boolean putObject(String bucket, String objectName, byte[] object) {
        MinioClient client = getClient(bucket);
        if (client == null) {
            log.error("Cant put object {}. All clients are not working for {}", objectName, bucket);
            return false;
        }

        try {
            client.putObject(
                PutObjectArgs.builder()
                    .bucket(bucket)
                    .object(objectName)
                    .stream(new ByteArrayInputStream(object), object.length, -1)
                    .build()
            );
        } catch (Exception e) {
            log.error("Error during image {} uploading.", objectName, e);
            return false;
        }

        return true;
    }

    @Override
    public InputStream getObject(String bucket, String objectName) {
        MinioClient client = getClient(bucket);
        if (client == null) {
            log.error("Cant get object {}. All clients are not working for {}", objectName, bucket);
            return null;
        }

        try {
            return client.getObject(GetObjectArgs.builder().bucket(bucket).object(objectName).build());
        } catch (Exception e) {
            log.error("Error. Can't get object {}.", objectName, e);
        }

        return null;
    }

    /**
     * Looks for working client for this bucket
     *
     * @return null if no working client of this bucket, client otherwise.
     */
    private MinioClient getClient(String bucket) {
        // TODO: Если операция по проверке нерабочего клиента будет медленной, то запоминать последнего рабочего клиента.
        for (MinioClient client : clients) {
            if (isWorkingClient(client, bucket)) {
                return client;
            }
        }

        return null;
    }

    private boolean isWorkingClient(MinioClient client, String bucket) {
        try {
            return client.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
        } catch (Exception e) {
            log.warn("Client {} is unhealthy", client);
            return false;
        }
    }

    private static MinioClient[] createClients(String[] endpoints, String accessKey, String secretKey) {
        MinioClient[] clients = new MinioClient[endpoints.length];
        for (int i = 0; i < endpoints.length; i++) {
            clients[i] = createClient(endpoints[i], accessKey, secretKey);
        }

        return clients;
    }

    private static MinioClient createClient(String endpoint, String accessKey, String secretKey) {
        return MinioClient.builder()
            .endpoint(endpoint)
            .credentials(accessKey, secretKey)
            .build();
    }
}
