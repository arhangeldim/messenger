package arhangel.dim.lections.threads.counting;

/**
 *
 */
public class SimpleCounter implements Counter {
    private long val;

    public long inc() {
        return val++;
    }

}
