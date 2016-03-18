package arhangel.dim.lections.collections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

@SuppressWarnings("ALL")
public class StackImpl<E> implements Stack<E> {
    private Object[] stack;
    private int cur = 0;

    public static void main(String[] args) throws Exception {
        List<Integer> ls = new ArrayList<>();
        ls.add(1);
        ls.add(2);
        ls.add(3);
        ls.add(4);
        ls.add(5);
        Stack<Integer> st = new StackImpl<>(5);
        st.pushAll(ls);

        for (Integer i : st) {
            System.out.println(i);
        }

        ls.clear();
        st.popAll(ls);


        for (Integer i : ls) {
            System.out.println(i);
        }

        st.push(1);

        st.push(2);
        st.push(3);
        st.push(4);
        st.push(5);

        //st.push(1000);
        for (Integer i : st) {
            i = 10;
            System.out.println(i);
        }

        st.pop();
        st.pop();
        st.pop();

        //st.push(4);
        //st.push(5);
        //st.push(5);
        //st.push(5);
        for (Integer i : st) {
            System.out.println(i);
        }
        st.pop();
        st.pop();
        //st.pop();

    }

    public StackImpl(int size) {
        stack = new Object[size];
    }

    @Override
    public void push(E element) throws StackException {
        if (cur == stack.length) {
            throw new StackException("Stack overflow");
        }
        stack[cur++] = element;
    }

    @Override
    public E pop() throws StackException {
        if (cur == 0) {
            throw new StackException("Stack underflow");
        }
        return (E) stack[--cur];
    }

    @Override
    public E peek() throws StackException {
        if (cur == 0) {
            throw new StackException("Stack underflow");
        }
        return (E) stack[cur - 1];
    }

    @Override
    public int getSize() {
        return cur;
    }

    @Override
    public boolean isEmpty() {
        return (cur == 0);
    }

    @Override
    public boolean isFull() {
        return (cur == stack.length);
    }

    @Override
    public void pushAll(Collection<E> src) throws StackException {
        if (src.size() > (stack.length - cur)) {
            throw new StackException("Stack overflow");
        }
        for (E srcObj : src) {
            push(srcObj);
        }
    }

    @Override
    public void popAll(Collection<E> dst) throws StackException {
        if (cur == 0) {
            throw new StackException("Stack underflow");
        }

        while (cur != 0) {
            dst.add(pop());
        }
    }

    public class StackIterator<T> implements Iterator<T> {
        private int curr = cur;

        @Override
        public boolean hasNext() {
            return (curr != 0);
        }

        @Override
        public T next() {
            return (T) stack[--curr];
        }

        public StackIterator() {
            this.curr = cur;
        }
    }

    @Override
    public Iterator<E> iterator() {
        return new StackIterator<>();
    }
}
