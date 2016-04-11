package arhangel.dim.container.beans;

import java.util.StringJoiner;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by thefacetakt on 22.03.16.
 */

public class test {
    static public void foo(A a) {
        System.out.println("A");
    }

    static private void foo(B b) {
        System.out.println("B");
    }

    static protected void foo(C c) {
        System.out.println("C");
    }

    public static void main(String[] args)

    }

    public interface C {
    }

    static class B extends A {
    }

    static class A implements C {
    }

}



