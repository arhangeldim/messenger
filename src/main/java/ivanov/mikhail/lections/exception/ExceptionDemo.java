package ivanov.mikhail.lections.exception;


import java.io.IOException;

/**
 *
 */
public class ExceptionDemo {

    public void convertString(String str) {
        if (str == null) {
            throw new IllegalArgumentException("Arg str must be non-null");
        }
    }

    public static void main(String[] args) {
        //new ExceptionDemo().convertString(null);

        //System.out.println("str == null -> " + getSize(null));
        //System.out.println("str != null -> " + getSize("test"));

        //exceptionLost();

    }

    public static int getSize(String str) {
        try {
            return str.toString().length();
        } catch (Exception e) {
            System.out.println("in catch block");
            return -1;
        } finally {
            return 0;
        }
    }

    public static void exceptionLost() {
        try {
            try {
                throw new Exception("a");
            } finally {
                if (true) {
                    throw new IOException("b");
                }
                System.err.println("c");
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        } catch (Exception e) {
            System.err.println("d");
            System.err.println(e.getMessage());
        }
    }
}
