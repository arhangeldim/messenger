package arhangel.dim.container;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by valeriyasin on 3/15/16.
 */


public class GraphBuilder {
    private Map<String, Bean> beanStrings = new HashMap<>();
    private Map<Bean, BeanVertex> beanVertices = new HashMap<>();

    public BeanGraph buildGraph(List<Bean> beans) {
        BeanGraph gr = new BeanGraph();
        for (Bean bean : beans) {
            //BeanStrings.put(bean.getName(), bean);
            BeanVertex beanVertex = gr.addVertex(bean);
            //processVertex(beanVertex, gr);
            beanVertices.put(bean, beanVertex);
            beanStrings.put(bean.getName(), bean);
        }

        Iterator it = beanVertices.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry beanEntry = (Map.Entry)it.next();
            BeanVertex beanVertex = (BeanVertex)(beanEntry).getValue();
            Bean bean = beanVertex.getBean();
            Map<String, Property> properties = bean.getProperties();
            Iterator pit = properties.entrySet().iterator();
            while (pit.hasNext()) {
                Map.Entry property = (Map.Entry) pit.next();
                if (((Property) (property).getValue()).getType() == ValueType.REF) {
                    Bean propertyBean = beanStrings.get(((Property) (property).getValue()).getValue());
                    BeanVertex childBeanVertex = beanVertices.get(propertyBean);
                    gr.addEdge(beanVertex, childBeanVertex);
                }
            }
        }
        return gr;
    }

    public void processVertex(BeanVertex beanVertex, BeanGraph gr) {
        Bean bean = beanVertex.getBean();
        Map<String, Property> properties = bean.getProperties();
        Iterator it = properties.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry property = (Map.Entry)it.next();
            if (((Property)(property).getValue()).getType() == ValueType.REF) {
                Bean propertyBean = beanStrings.get(((Property)(property).getValue()).getValue());
                BeanVertex childBeanVertex = gr.addVertex(propertyBean);
                processVertex(childBeanVertex, gr);
                gr.addEdge(beanVertex, childBeanVertex);
            }
        }
    }
}
