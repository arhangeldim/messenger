package arhangel.dim.ioc.beans;

/**
 * Created by olegchuikin on 12/03/16.
 */
public class Car {

    private Engine engine;
    private Gear gear;

    public Car() {
    }

    public Engine getEngine() {
        return engine;
    }

    public void setEngine(Engine engine) {
        this.engine = engine;
    }

    public Gear getGear() {
        return gear;
    }

    public void setGear(Gear gear) {
        this.gear = gear;
    }

    @Override
    public String toString() {
        return "Car:\n  engine:" + engine + "gear:" + gear + "\n";
    }
}
