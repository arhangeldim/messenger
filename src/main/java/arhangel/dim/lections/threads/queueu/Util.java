package arhangel.dim.lections.threads.queueu;

import java.io.IOException;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 */
public class Util {

    // утилитки для чтения файла. На каждый узел (файл) вызывается consumer
    static void readDir(String path, Consumer<Path> reader) {
        Path dir = Paths.get(path);
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
            for (Path file : stream) {
                if (file.toFile().isDirectory()) {
                    readDir(file.toAbsolutePath().toString(), reader);
                } else {
                    reader.accept(file);
                }
            }
        } catch (IOException | DirectoryIteratorException e) {
            System.err.println(e);
        }
    }

    static byte[] readContent(Path file) throws IOException {
        return Files.readAllBytes(file);
    }

    static boolean search(String line, Pattern pattern) {
        Matcher matcher = pattern.matcher(line);
        boolean result = matcher.find();
        return result;
    }

    public static void sleepQuietly(TimeUnit unit, int timeout) {
        try {
            unit.sleep(timeout);
        } catch (InterruptedException e) {
            //
        }
    }
}
