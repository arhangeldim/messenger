package arhangel.dim.lections.collections;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * 7
 */
public class Demo {

    // SAM - Single Abstract Method
    static class MyPredicate implements Predicate<Integer> {
        @Override
        public boolean test(Integer integer) {
            return integer % 2 != 0;
        }
    }

    static class MyConsumer<T> implements Consumer<T> {
        @Override
        public void accept(T item) {
            System.out.println("# " + item);
        }
    }

    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7);

        // Filter with predicate
        final int skip = 3;
        // skip = 4;
        numbers.stream().filter(new MyPredicate()).forEach(new MyConsumer<>());

        numbers
                .stream()
                .filter((val) -> val != skip)
                .forEach(System.out::println);


        // Map

        int sum = numbers.stream()
                .map((val) -> val * val)
                .reduce(0, (val1, val2) -> val1 + val2);
        System.out.println("sum: " + sum);


        List<String> myList =
                Arrays.asList("a1", "a2", "b1", "c2", "c1");

        myList.stream()
                .filter(s -> s.startsWith("c"))
                .map(String::toUpperCase)
                .sorted()
                .forEach(System.out::println);

    }
}
