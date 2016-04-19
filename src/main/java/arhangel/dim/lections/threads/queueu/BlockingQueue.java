package arhangel.dim.lections.threads.queueu;

/**
 *
 */
public interface BlockingQueue<E> {

    /**
     * @param elem the element to add
     */
    void put(E elem) throws InterruptedException;

    /**
     * @return the head element
     */
    E take() throws InterruptedException;
}
