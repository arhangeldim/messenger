package ivanov.mikhail.container;

/**
 * Вершина графа, которая содержит бин
 */
public class BeanVertex {

    public enum State {
        DEFAULT,
        MARKED,
        VISITED
    }

    private Bean bean;
    private State state = State.DEFAULT;

    public BeanVertex(Bean bean) {
        this.bean = bean;
    }

    public Bean getBean() {
        return bean;
    }

    public void setBean(Bean bean) {
        this.bean = bean;
    }

    public State getState() {
        return state;
    }

    void setState(State state) {
        this.state = state;
    }
}
