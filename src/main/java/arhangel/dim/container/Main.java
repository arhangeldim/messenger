package arhangel.dim.container;

import arhangel.dim.container.beans.Car;
import arhangel.dim.core.net.Protocol;
import arhangel.dim.core.net.SerialProtocol;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        BeanXmlReader reader = new BeanXmlReader();
        try {
            List<Bean> beans = reader.parseBeans("/home/spec45as/technotrack/messenger/config.xml");
            for (Bean bean : beans) {
                System.out.println(bean.toString());
            }
            BeanGraph graph = new BeanGraph(reader.parseBeans("/home/spec45as/technotrack/messenger/config.xml"));

            List<BeanVertex> vertices = graph.sortTopologically();
            for (BeanVertex vertex : vertices) {
                System.out.println(vertex.getBean());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Container container = new Container("/home/spec45as/technotrack/messenger/config.xml");
            Car car = (Car) container.getByName("carBean");
            System.out.println(car.getEngine());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
