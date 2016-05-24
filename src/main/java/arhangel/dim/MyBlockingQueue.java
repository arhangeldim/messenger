package arhangel.dim;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by thefacetakt on 19.04.16.
 */

public class MyBlockingQueue<T> {
    private Queue<T> queue;

    private final int maxQueueSize;

    public MyBlockingQueue(int maxSize) {
        queue = new LinkedList<>();
        maxQueueSize = maxSize;
    }

    public boolean offer(T item) {
        synchronized (queue) {
            if (queue.size() == maxQueueSize) {
                return false;
            }
            queue.add(item);
            queue.notifyAll();
            return true;
        }
    }

    public void put(T item) throws InterruptedException {
        synchronized (queue) {
            while (queue.size() == maxQueueSize) {
                queue.wait();
            }
            queue.add(item);
            queue.notifyAll();
        }
    }

    public T take() throws InterruptedException {
        synchronized (queue) {
            while (queue.isEmpty()) {
                queue.wait();
            }
            T result = queue.remove();
            queue.notifyAll();
            return result;
        }
    }

    public T poll(long timeout) throws InterruptedException {
        long timeLeft = timeout;
        synchronized (queue) {
            while (timeLeft > 0 && queue.isEmpty()) {
                long currentTime = System.currentTimeMillis();
                queue.wait(timeLeft);
                timeLeft -= (System.currentTimeMillis() - currentTime);
            }
            if (timeLeft <= 0) {
                return null;
            }
            T result = queue.remove();
            queue.notifyAll();
            return result;
        }
    }

    public boolean isEmpty() {
        synchronized (queue) {
            return queue.isEmpty();
        }
    }
}
