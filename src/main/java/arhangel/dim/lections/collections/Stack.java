package arhangel.dim.lections.collections;

import java.util.Collection;

/**
 *
 */
public interface Stack<E> extends Iterable<E> {

    void push(E element) throws StackException;

    E pop() throws StackException;

    E peek();

    int getSize();

    boolean isEmpty();

    boolean isFull();

    // A little bit wrong =( Could you fix it?
    void pushAll(Collection<E> src) throws StackException;

    void popAll(Collection<E> dst) throws StackException;
}
