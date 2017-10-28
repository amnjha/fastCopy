package code.project.fastCopy.copyStrategies;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;

/**
 * @author aman.jha
 */
public interface Copy {
    void copyFile(File source, File target);

    static void close(Closeable closable) {
        if (closable != null) {
            try {
                closable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
