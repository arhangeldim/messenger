package arhangel.dim.container;

import java.util.List;

/**
 * Вершина графа, которая содержит бин
 */
public class BeanVertex {
    private Bean bean;
    private int search_check;

    public BeanVertex() {};
    public BeanVertex(Bean bean) {
        this.bean = bean; this.search_check = 0;
    }

    public Bean getBean() {
        return bean;
    }

    public int getSearch_check() {return this.search_check;}

    public void setSearch_check(int search_check) {this.search_check = search_check;}

    public void setBean(Bean bean) {
        this.bean = bean;
    }

    public String toString() {
        return this.getBean().toString();
    }


}
