package arhangel.dim.lections.collections;

import java.util.Collection;
import java.util.Iterator;

/**
 * Created by valeriyasin on 3/18/16.
 */
public class MyStack<E> implements Stack {

    final int capacity = 1024;
    int size;
    E[] data = (E[])new Object[capacity];


    @Override
    public void push(Object element) throws StackException {
        if (size == capacity) {
            throw new StackException("capacity reached");
        } else {
            data[size] = (E)element;
            size++;
        }
    }

    @Override
    public E pop() throws StackException {
        if (size == 0) {
            throw new StackException("no elements");
        }
        size--;
        return data[size];
    }

    public E peek() {
        return data[size];
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return (size == 0);
    }

    public boolean isFull() {
        return (size == capacity);
    }

    @Override
    public void pushAll(Collection src) throws StackException {

    }

    @Override
    public void popAll(Collection dst) throws StackException {

    }

    @Override
    public Iterator iterator() {
        return null;
    }

//    public class MyStackIterator<E> implements Iterator<Object> {
//        private int cur;
//        int size;
//
//        public MyStackIterator() {
//            cur = 0;
//        }
//
//        @Override
//        public E next() {
//            return (E)data[++cur];
//        }
//    }
}


