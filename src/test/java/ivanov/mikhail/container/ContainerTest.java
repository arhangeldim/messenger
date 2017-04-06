package ivanov.mikhail.container;

import org.junit.BeforeClass;
import org.junit.Test;

import  org.junit.Assert;

import ivanov.mikhail.container.beans.Car;
import ivanov.mikhail.container.beans.Engine;
import ivanov.mikhail.container.beans.Gear;

/**
 *
 */
public class ContainerTest {

    private static Container container;

    private static Car expectedCar;
    private static Gear expectedGear;
    private static Engine expectedEngine;

    @BeforeClass
    public static void init() {
        try {
            container = new Container("config.xml");
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
        }
        Assert.assertTrue(container != null);

        expectedEngine = new Engine();
        expectedEngine.setPower(200);

        expectedGear = new Gear();
        expectedGear.setCount(6);

        expectedCar = new Car();
        expectedCar.setEngine(expectedEngine);
        expectedCar.setGear(expectedGear);

    }

    @Test
    public void testGetByName() throws Exception {
        Car car = (Car) container.getByName("carBean");
        Assert.assertTrue(car != null);
        Assert.assertEquals(expectedCar, car);
    }

    @Test
    public void testGetByClass() throws Exception {
        Car car = (Car) container.getByClass("Car");
        Assert.assertTrue(car != null);
        Assert.assertEquals(expectedCar, car);
    }
}
