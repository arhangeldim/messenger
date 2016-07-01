package arhangel.dim.lections.threads.queueu;

import java.util.LinkedList;

/**
 *
 */
public class ListBlockingQueue<E> implements BlockingQueue<E> {

    public static final int DEFAULT_CAPACITY = 10;

    private int capacity;
    private LinkedList<E> list = new LinkedList<>();

    public ListBlockingQueue() {
        capacity = DEFAULT_CAPACITY;
    }

    public ListBlockingQueue(int capacity) {
        this.capacity = capacity;
    }

    @Override
    public void put(E elem) throws InterruptedException {

        /*
         Внутри критической секции пытаемся поместить элемент в очередь
         Если очередь полная isFull==true то блокируемся на wait() пока условие не будет выполнено
         */
    }

    @Override
    public E take() throws InterruptedException {

        /*
         Внутри критической секции пытаемся достать элемент из очереди
         Если очередь пустая isEmpty == true то блокируемся на wait() пока условие не будет выполнено
         */
        return null;
    }

    private boolean isFull() {
        return list.size() == capacity;
    }

    private boolean isEmpty() {
        return list.size() == 0;
    }

}
