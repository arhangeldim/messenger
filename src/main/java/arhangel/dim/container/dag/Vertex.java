package arhangel.dim.container.dag;

/**
 * Представление вершины графа
 */
public class Vertex<V> {

    public enum State {
        DEFAULT,
        MARKED,
        VISITED
    }

    private V value;
    private State state = State.DEFAULT;

    public Vertex(V value) {
        this.value = value;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "Vertex{" +
                "value=" + value +
                ", state=" + state +
                '}';
    }
}
