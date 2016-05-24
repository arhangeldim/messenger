package arhangel.dim.ioc.beans;

/**
 * Created by olegchuikin on 12/03/16.
 */
public class Engine {

    private long power;

    public Engine() {
    }

    public long getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }

    @Override
    public String toString() {
        return "Engine:\n  power:" + power + "\n";
    }
}
