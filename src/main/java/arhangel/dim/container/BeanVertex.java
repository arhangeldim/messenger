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
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof BeanVertex)) {
            return false;
        }

        BeanVertex that = (BeanVertex) object;

        return bean.equals(that.bean);

    }

    @Override
    public int hashCode() {
        return bean.hashCode();
    }
}
