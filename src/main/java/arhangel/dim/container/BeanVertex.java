package arhangel.dim.container;

/**
 * Вершина графа, которая содержит бин
 */
public class BeanVertex {
    private Bean bean;

    enum ColorType {
        BLACK,
        GREY,
        WHITE
    }

    ColorType color;

    public BeanVertex(Bean bean) {
        this.bean = bean;
        this.color = ColorType.BLACK;
    }

    public Bean getBean() {
        return bean;
    }

    public void setBean(Bean bean) {
        this.bean = bean;
    }
}
