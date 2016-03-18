package arhangel.dim.container;

/**
 * Вершина графа, которая содержит бин
 */
public class BeanVertex {
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }

        BeanVertex that = (BeanVertex) other;

        return bean.equals(that.bean);

    }

    @Override
    public int hashCode() {
        return bean.hashCode();
    }

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
