package arhangel.dim.container;

import arhangel.dim.container.beans.Car;
import arhangel.dim.container.beans.Engine;
import arhangel.dim.container.beans.Gear;
import sun.security.provider.certpath.Vertex;

import java.util.*;

public class Main {
    public static void main(String[] args) throws InvalidConfigurationException, IllegalAccessException, ClassNotFoundException, InstantiationException, NoSuchFieldException {
        BeanXmlReader reader = new BeanXmlReader();

        List<Bean> beanList = reader.parseBeans("/home/philip/messenger/config.xml");
        BeanGraph graph = new BeanGraph(beanList);
        List<Bean> beanListSorted = graph.sortBeans();

        Container container = new Container("config.xml");
        System.out.println(container.objByClassName.toString());
    }

}
