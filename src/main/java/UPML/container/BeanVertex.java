package UPML.container;

import arhangel.dim.container.Bean;

/**
 * Вершина графа, которая содержит бин
 */
public class BeanVertex {
    private Bean bean;

    public BeanVertex(Bean bean) {
        this.bean = bean;
    }

    public Bean getBean() {
        return bean;
    }

    public void setBean(Bean bean) {
        this.bean = bean;
    }
}
