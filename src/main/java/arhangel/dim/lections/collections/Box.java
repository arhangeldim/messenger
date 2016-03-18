package arhangel.dim.lections.collections;

/**
 *
 */
public class Box<T> {
    private T item;

    public Box(T item) {
        this.item = item;
    }

    public T getItem() {
        return item;
    }

    public void setItem(T item) {
        this.item = item;
        // Object - nothing more
    }

    @Override
    public String toString() {
        return "Box{" +
                "item=" + item +
                '}';
    }
}
