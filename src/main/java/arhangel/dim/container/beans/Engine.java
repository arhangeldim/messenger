package arhangel.dim.container.beans;

/**
 *
 */
public class Engine {
    private int power;

    public Engine() {
    }

    public int getPower() {
        return power;
    }

    // changed primitive parameter type of setter to wrapper one because
    // it is easier to convert String parameter to primitive wrapper
    public void setPower(Integer power) {
        this.power = power;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        Engine engine = (Engine) obj;

        return power == engine.power;

    }

    @Override
    public int hashCode() {
        return power;
    }

    @Override
    public String toString() {
        return "Engine{" +
                "power=" + power +
                '}';
    }
}
