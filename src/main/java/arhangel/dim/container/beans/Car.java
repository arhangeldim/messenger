package arhangel.dim.container.beans;

/**
 *
 */
public class Car {
    private Gear gear;
    private Engine engine;

    public Car() {

    }

    public Gear getGear() {
        return gear;
    }

    public void setGear(Gear gear) {
        this.gear = gear;
    }

    public Engine getEngine() {
        return engine;
    }

    public void setEngine(Engine engine) {
        this.engine = engine;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        Car car = (Car) obj;

        if (gear != null ? !gear.equals(car.gear) : car.gear != null) {
            return false;
        }
        return !(engine != null ? !engine.equals(car.engine) : car.engine != null);

    }

    @Override
    public int hashCode() {
        int result = gear != null ? gear.hashCode() : 0;
        result = 31 * result + (engine != null ? engine.hashCode() : 0);
        return result;
    }
}
