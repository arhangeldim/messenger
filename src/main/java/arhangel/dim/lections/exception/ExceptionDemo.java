package arhangel.dim.lections.exception;


import java.io.IOException;
import java.sql.Connection;

/**
 *
 */
public class ExceptionDemo {

    public static void convertString(String str) {
        if (str == null) {
            throw new IllegalArgumentException("Arg str must be non-null");
        }
    }

    public static void m1() throws Exception {
        throw new ArithmeticException("Amth");
    }

    public static void m2() throws Exception {
        try {
            m1();
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    public static void main(String[] args) throws Exception {

        try {
            m2();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        //convertString(null);

//        System.out.println("str == null -> " + getSize(null));
//        System.out.println("str != null -> " + getSize("test"));

        //exceptionLost();

    }

    public static int getSize(String str) {
        Connection conn = null;
        try {
            return str.length();
        } catch (Exception e) {
            System.out.println("in catch block");
            //System.exit(0);
            return -1;
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (Exception e) {
                    // do nothing
                }
            }
            return 0;
        }
    }

    public static void exceptionLost() throws Exception {
        try {
            try {
                throw new Exception("a");
            } catch (Exception e) {
                System.out.println("In catch");
                throw e; // a
            } finally {
                System.out.println("In finally block");

//                if (true) {
//                    throw new IOException("b");
//                }
                System.err.println("c");
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
}
