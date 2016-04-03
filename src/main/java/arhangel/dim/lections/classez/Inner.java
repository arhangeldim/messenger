package arhangel.dim.lections.classez;

/**
 * Created by r.kildiev on 29.03.2016.
 */
class Inner {
    Inner2 inner = new Inner2();

    interface HelloWorld {
        public void greet();
    }

    void anonymous() {
        HelloWorld greeting = new HelloWorld() {
            public void greet() {
                System.out.println("Hello World");
            }
        };
        System.out.println(greeting.getClass().getName());
    }

    class Inner2 {
        int b = 0;

                void localMethod() {
            /*private*/
                    class Local {
                        //                static String field = "";
                        int localInt = 0;
                    }
            Local local = new Local();
        }
    }

}
