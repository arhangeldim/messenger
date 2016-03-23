package arhangel.dim.lections.collections;

import java.util.Collection;
import java.util.Iterator;

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
    void pushAll(E[] src) throws StackException;

    void popAll(E[] dst) throws StackException;
}

class MyStack<E> implements Stack<E> {

    private E[] massiv;


    private int tail;
    private int length;

    public MyStack(int length) {
        Object[] arr = new Object[length];
        this.length = length;
        massiv = (E[]) arr;
    }

    @Override
    public  void push(E element) throws StackException {

        if (this.tail < this.length) {
            this.massiv[this.tail + 1] = E;
            this.tail++;
        }
    }

    @Override
    public E pop() throws StackException {

        return this.massiv[this.tail--];
    }

    @Override
    public E peek() {
        return this.massiv[this.tail];
    }

    @Override
    public int getSize() {
        return this.tail;
    }

    @Override
     public boolean isEmpty() {
        if (this.tail > 0) return false;
        else return true;
    }

    @Override
    public boolean isFull() {
        if (this.tail == this.length) return true;
        else return false;
    }

    @Override
    public void pushAll(E[] src) throws StackException {
        for (E t : src) {
            this.push(t);
        }
    }

    @Override
    public void popAll(E[] dst) throws StackException {
        int i = 0;
        for (E t : this.massiv) {
            dst[i] = t;
        }
    }

    @Override
    public Iterator<E> iterator() {

        return iter;
    }


    private class MyIterator<E> implements Iterator<E> {
        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public E next() {
            return null;
        }
    }

    public static void main(String[] args) {
        Stack<Integer> stack = new MyStack<>(10);

        Iterator<Integer> iter =stack.iterator();
        while (iter.hasNext()) {
            System.out.println(iter.next());
        }
    }

}