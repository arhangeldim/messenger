package arhangel.dim.lections.classez;

/**
 * Created by Rustam on 29.03.2016.
 */
public class IntWrapper {

    Monad<String> operation;
    Monad<Integer> num;

    public IntWrapper setOperation(Monad<String> operation) {
        this.operation = operation;
        return this;
    }

    public IntWrapper setNum(Monad<Integer> num) {
        this.num = num;
        return this;
    }

    public int calculate(int num2) {
        int result = 0;
        operation.apply("asd");
        num.apply(12);
//        switch (operation) {
//            case "+": result = num + num2; break;
//            case "-": result = num - num2; break;
//        }
        return result;
    }

    interface Monad<T> {
        T apply(T arg);
    }
}
