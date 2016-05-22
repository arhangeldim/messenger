package arhangel.dim.lections.threads.queueu;

import java.nio.file.Path;

/**
 *
 */
public class QueueItem {

    private String data;
    private Path path;

    public QueueItem(Path path, String data) {
        this.data = data;
        this.path = path;
    }

    public String getData() {
        return data;
    }

    public Path getPath() {
        return path;
    }
}
