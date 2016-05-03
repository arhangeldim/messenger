package arhangel.dim.lections.threads.queueu;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class ProdConsSample {

    static Logger log = LoggerFactory.getLogger(ProdConsSample.class);

    public static final int COUNT = 5;
    public static final String PATH = "/Users/dmirty/Tools/odkl/odnoklassniki-ejb/";
    public static final Pattern PATTERN = Pattern.compile("PaymentWizard");

    public static void main(String[] args) throws Exception {
        log.info("Starting search...");
        final long start = System.nanoTime();

        for (int i = 0; i < COUNT; i++) {
            long tmp = System.nanoTime();
            // -- One thread impl
            oneThread(PATH, PATTERN);

            // -- Reader -> Searcher (2 threads)
            //producerConsumer(PATH, PATTERN);

            // -- Readers pool
            //pool(PATH, PATTERN);
            log.info("Iteration: " + i + " in " + ((double) (System.nanoTime() - tmp)) / 1e6 + "ms");
        }
        double elapsed = ((double) (System.nanoTime() - start)) / (1e6 * COUNT);

        System.out.println("Completed in " + elapsed + "ms");
    }

    // Все в одном потоке
    public static void oneThread(String path, Pattern pattern) {
        Util.readDir(path, (item) -> {
            try {
                String line = new String(Util.readContent(item));
                boolean result = Util.search(line, pattern);
            } catch (Exception e) {
                e.printStackTrace();
            }

        });
    }

    // Паттерн передачи данных через очередь
    public static void producerConsumer(String path, Pattern pattern) throws Exception {
        // Очередь - нужно шарить очередь между потоком, который читает и который ищет
        BlockingQueue<QueueItem> queue = new ArrayBlockingQueue<>(32);

        // Поток на поиск данных
        SearchWorker<QueueItem> worker = new SearchWorker<>(queue, pattern);
        worker.start();

        // В главном потоке читаем контент
        Util.readDir(path, (item) -> {
            try {
                String line = new String(Util.readContent(item));
                // QueueItem позволяет хранить до информацию
                queue.put(new QueueItem(item, line));
            } catch (Exception e) {
                log.error("Failed to read content.", e);
            }

        });
        // Больше данных нет
        worker.setSetNoTask(true);

        // Ждем, когда воркер все обработает
        worker.join();
    }

    // На чтение работает пул потоков
    public static void pool(String path, Pattern pattern) throws Exception {
        BlockingQueue<QueueItem> queue = new ArrayBlockingQueue<>(32);

        SearchWorker<QueueItem> worker = new SearchWorker<>(queue, pattern);
        worker.start();

        final int threadCount = Runtime.getRuntime().availableProcessors();
        log.info("Running read on " + threadCount + " processors.");

        // Создадим тред-пул на чтение данных с диска
        ExecutorService service = Executors.newFixedThreadPool(threadCount);

        Util.readDir(path, (item) -> {

            // Чтение каждого узла (файла) будет в отдельном потоке
            service.submit(() -> {
                try {
                    String line = new String(Util.readContent(item));
                    queue.put(new QueueItem(item, line));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        });

        // Все данные прочитаны, запрещаем добавление новых задач
        service.shutdown();

        // Ждем окончания работы всех потоков-читателей
        service.awaitTermination(100, TimeUnit.SECONDS);

        // Ждем воркеры
        worker.setSetNoTask(true);
        worker.join();
    }
}
