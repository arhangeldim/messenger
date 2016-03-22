package arhangel.dim.lections.threads.queueu;

/**
 *
 */
public interface BlockingQueue<E> {

    /**
     *
     * @param e the element to add
     */
    void put(E e) throws InterruptedException;

    /**
     *
     * @return the head element
     * @throws InterruptedException
     */
    E take() throws InterruptedException;
}
