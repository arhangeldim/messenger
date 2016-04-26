package arhangel.dim.lections.threads.queueu;

import java.util.LinkedList;

/**
 * TODO: implements on lesson
 */
public class ListBlockingQueue<E> {

    public static final int DEFAULT_CAPACITY = 10;

    private int capacity;
    private LinkedList<E> list = new LinkedList<>();

    public ListBlockingQueue() {
        capacity = DEFAULT_CAPACITY;
    }

    public ListBlockingQueue(int capacity) {
        this.capacity = capacity;
    }

    public void put(E elem) throws InterruptedException {

    }

    public E take() throws InterruptedException {

        return null;
    }

    private boolean isFull() {
        return list.size() == capacity;
    }

    private boolean isEmpty() {
        return list.size() == 0;
    }

}
