package arhangel.dim.lections.threads.future;

import java.util.concurrent.TimeUnit;

import arhangel.dim.lections.threads.queueu.Util;

/**
 *
 */
public class Transformer<T extends Image> {

    public Image transform(Image image) {

        // sleep for 2 seconds - imitate long transformation
        Util.sleepQuietly(TimeUnit.SECONDS, 2);
        return image;
    }
}
