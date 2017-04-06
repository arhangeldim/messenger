package ivanov.mikhail.lections.objects;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class Polimorf {

    private static Logger log = LoggerFactory.getLogger(Polimorf.class);

    static class Parent {
        void test() {
            System.out.println("parent::test()");
        }

        void testParent() {
            System.out.println("parent::testParent()");
        }
    }

    static class Child extends Parent {
        @Override
        void test() {
            System.out.println("child::test()");
        }

        void testChild() {
            System.out.println("child::testParent()");
        }
    }

    public static void main(String[] args) {
        Parent item = new Child();

        System.out.println("===Parent -> Child===");
        // Late binding - invoke Child impl
        item.test();
        item.testParent();
        // No such method in Parent
        //item.testChild();

        System.out.println("===Child -> Child===");
        Child other = new Child();
        other.test();
        other.testChild();
        other.testParent();

        System.out.println("===Collection===");
        List<Parent> list = new ArrayList<>();
        list.add(new Child());
        list.add(new Parent());

        // Using for loop (old!)
        for (Parent p : list) {
            p.test();
        }

        // Using stream
        //list.stream().forEach(p -> p.test());

        // Using method references
        // list.stream().forEach(Parent::test);


    }
}
