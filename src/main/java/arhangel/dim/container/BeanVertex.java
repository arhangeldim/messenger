package arhangel.dim.container;

/**
 * Вершина графа, которая содержит бин
 */
public class BeanVertex {

    public enum Color {
        WHITE,
        GRAY,
        BLACK
    }

    private Bean bean;
    private Color color;

    public BeanVertex(Bean bean) {
        this.bean = bean;
        this.color = Color.WHITE;
    }

    public Bean getBean() {
        return bean;
    }

    public void setBean(Bean bean) {
        this.bean = bean;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }
}
