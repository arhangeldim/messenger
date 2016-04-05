package arhangel.dim.lections.classez;

/**
 * Created by r.kildiev on 29.03.2016.
 */
class Outer {
    Inner inner = new Inner();

    void anonymous() {

        HelloWorld greeting = /*class $1 implements*/new HelloWorld() {
            public String greet() {
                System.out.println("Hello World");
                return null;
            }
        };
        String result = greeting.greet().substring(3);
    }

    interface HelloWorld {
        public String greet();
    }

    class Inner {
        int innerField = 0;
        int localInt = 1;

        String local = new String();

        public Inner() {

        }

        void localMethod() {
            /*private*/
            class Local {
                int outerLocalInt;
                int localInt = 2;

                //                static String field = "";
                public Local() {
                }
            }
//            int localInt = local.localInt;
            //blah blah code
        }
    }

}
