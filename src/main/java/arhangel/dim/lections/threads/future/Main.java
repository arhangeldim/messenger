package arhangel.dim.lections.threads.future;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import arhangel.dim.lections.threads.queueu.Util;

/**
 *
 */
public class Main {

    static Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws Exception {
        Transformer<Image> transformer = new Transformer<>();
        Image image = Network.loadImage();

        CompletableFuture<Image> transform = CompletableFuture.supplyAsync(() -> {
            log.info("Transforming...");
            Image transformed = transformer.transform(image);
            log.info("Transform finished.");
            return transformed;
        });

        CompletableFuture<Void> store = transform.thenAccept(Network::store);
        store.get();
    }

    static class Network {
        public static Image loadImage() {
            byte[] data = new byte[] {1, 2, 3};
            ImageInfo info = new ImageInfo(100 ,100, "png");
            return new Image(data, info);
        }

        public static ImageInfo store(Image image) {
            log.info("Storing...");
            Util.sleepQuietly(TimeUnit.SECONDS, 2);
            log.info("Store finished.");
            return image.getImageInfo();
        }
    }

}
