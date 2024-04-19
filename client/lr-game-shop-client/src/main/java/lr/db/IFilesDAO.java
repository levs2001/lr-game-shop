package lr.db;

import java.io.InputStream;

public interface IFilesDAO {
    boolean upload(String filename, byte[] file);

    InputStream download(String filename);
}
