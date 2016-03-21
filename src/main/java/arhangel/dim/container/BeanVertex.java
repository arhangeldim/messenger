package arhangel.dim.container;

/**
 * Вершина графа, которая содержит бин
 */
public class BeanVertex {
    private Bean bean;
    private int searchCheck;

    public BeanVertex() {}

    public BeanVertex(Bean bean) {
        this.bean = bean;
        this.searchCheck = 0;
    }

    public Bean getBean() {
        return bean;
    }

    public int getSearchCheck() {
        return this.searchCheck;
    }

    public void setSearchCheck(int searchCheck) {
        this.searchCheck = searchCheck;
    }

    public void setBean(Bean bean) {
        this.bean = bean;
    }

    public String toString() {
        return this.getBean().toString();
    }


}
