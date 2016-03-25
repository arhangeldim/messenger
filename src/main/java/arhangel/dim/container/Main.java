package arhangel.dim.container;

import arhangel.dim.container.beans.Car;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
        Container container = new Container("test.xml");
        Car car = (Car) container.getByName("carBean");
        System.out.println(car.getEngine().getPower());
    }
}