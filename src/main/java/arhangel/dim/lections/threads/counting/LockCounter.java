package arhangel.dim.lections.threads.counting;

/**
 * реализация счетчика через лок
 */
public class LockCounter implements Counter {

    private long counter;

    public synchronized long inc() {
        return counter++;
    }

    public long incUnsafe() {
        return counter++;
    }


}
