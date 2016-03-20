package arhangel.dim.container.beans;

/**
 *
 */
public class Gear {
    private int count;

    public Gear() {
    }

    public int getCount() {
        return count;
    }

    // changed primitive parameter type of setter to wrapper one because
    // it is easier to convert String parameter to primitive wrapper
    public void setCount(Integer count) {
        this.count = count;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        Gear gear = (Gear) obj;

        return count == gear.count;

    }

    @Override
    public int hashCode() {
        return count;
    }

    @Override
    public String toString() {
        return "Gear{" +
                "count=" + count +
                '}';
    }
}
