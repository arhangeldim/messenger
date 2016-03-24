package arhangel.dim.lections.collections;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


public class FunctionalProgramming {

    // Принимает одно значение, возвращает одно значение
    interface Function<R, T> {
        R apply(T val);
    }

    // Принимает элемент и проверяет его на условие
    interface Predicate<T> {
        boolean test(T val);
    }

    // Принимает 2 аргумента, проводит опреацию и возвращает результат
    interface BiFunction<R, U, V> {
        R apply(U val1, V val2);
    }

    // 2 аргумента одного типа
    interface BiOperator<T> extends BiFunction<T, T, T> {
        T apply(T val1, T val2);
    }

    static class Square implements Function<Integer, Integer> {
        public Integer apply(Integer val) {
            return val * val;
        }
    }

    /*
    Применить к каждому элементу коллекции заданную операцию
     */
    static List map(Collection collection, Function functor) {
        return Collections.emptyList();
    }

    /*
    Проверить элементы коллекции на заданное условие.
    Вернуть коллекцию элементов, прошедших фильтр
     */
    static List filter(List list, Predicate predicate) {
        return Collections.emptyList();
    }

    /*
    Последовательно применить операцию ко всем элементам коллекции
    Вернуть одно значение
     */
    static <T> T reduce(List<T> list, T init, BiOperator<T> op) {
        return null;
    }

    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4);

        // Returned 1, 4, 9, 16
        System.out.println("map [^2]: " + numbers + " -> " + map(numbers, new Square()));

        System.out.println("filter [%3]: " + numbers + " -> " + filter(numbers, new Predicate<Integer>() {
            @Override
            public boolean test(Integer val) {
                return val % 3 != 0;
            }
        }));

    }
}
