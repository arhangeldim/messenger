package arhangel.dim.container.dag;


import arhangel.dim.container.Container;
import arhangel.dim.container.beans.Car;

/**
 *
 */
public class Main {

    public static void main(String[] args) throws Exception {
        Container container = new Container("./config.xml");
        System.out.print(container.toString());
        System.out.print("\ncarBean->gear->count = " + ((Car)container.getByName("carBean")).getGear().getCount());
    }

}
