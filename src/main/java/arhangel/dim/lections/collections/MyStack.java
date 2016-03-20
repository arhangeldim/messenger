package arhangel.dim.lections.collections;

import java.util.Collection;
import java.util.Iterator;

/**
 * Created by vital on 18.03.16.
 */
public class MyStack<E> implements Stack<E> {
    private Object[] objects;
    private int currentSize;
    // Objectc[] ob = new Object[];
    // T item = (T) ob[10];

    MyStack(int size) {
        objects = new Object[size];
        currentSize = 0;
    }

    @Override
    public void push(E element) throws StackException {
        if (isFull()) {
            throw new StackException("push error");
        }
        objects[currentSize++] = element;
//        currentSize++;
    }

    @Override
    public E pop() throws StackException {
        if (isEmpty()) {
            throw new StackException("pop error");
        }
        currentSize--;
        return (E) objects[currentSize];
    }

    @Override
    public E peek() throws StackException {
        if (isEmpty()) {
            throw new StackException("peek error");
        }
        return (E) objects[currentSize - 1];
    }

    @Override
    public int getSize() {
        return currentSize;
    }

    @Override
    public boolean isEmpty() {
        if (currentSize == 0) {
            return false;
        }
        return true;
    }

    @Override
    public boolean isFull() {
        if (objects.length == currentSize) {
            return true;
        }
        return false;
    }

    @Override
    public void pushAll(Collection<E> src) throws StackException {
        for (E elem : src) {
            push(elem);
        }
    }

    @Override
    public void popAll(Collection<E> dst) throws StackException {

    }

    @Override
    public Iterator<E> iterator() {
        return null;
    }
}
