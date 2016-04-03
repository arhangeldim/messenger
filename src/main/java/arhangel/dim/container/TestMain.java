package arhangel.dim.container;

import arhangel.dim.container.beans.Car;

public class TestMain {
    public static void main(String[] args) {
        try {
            Container container = new Container("config.xml");
            Car car = (Car) container.getByClass("arhangel.dim.container.beans.Car");
            System.out.println(car.getEngine().getPower());
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
}
