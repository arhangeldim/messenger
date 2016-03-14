package arhangel.dim.container;

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

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof BeanVertex) {
            return bean.getName().equals(((BeanVertex) obj).getBean().getName());
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return bean.hashCode();
    }
}