package arhangel.dim.container;

import arhangel.dim.container.beans.Car;

public class TestMain {
    public static void main(String[] args) {
        try {
            Container container = new Container("config.xml");
            Car car = (Car) container.getByName("carBean");
            System.out.println(car.getEngine().getPower());
        } catch (InvalidConfigurationException ex) {
            System.out.println(ex.getMessage());
        }
    }
}
