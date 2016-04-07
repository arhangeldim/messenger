package arhangel.dim.ioc.beans;

/**
 * Created by olegchuikin on 12/03/16.
 */
public class Gear {
    private int count;

    public Gear() {
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "Gear:\n  count:" + count + "\n";
    }
}
