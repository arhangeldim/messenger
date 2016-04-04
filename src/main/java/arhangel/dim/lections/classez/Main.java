package arhangel.dim.lections.classez;

/**
 * Created by r.kildiev on 29.03.2016.
 */

//Lecture Slides         https://drive.google.com/open?id=0Bz3AMLjdKktjdDl6MXhQTFBIU2M
public class Main {
    public static void main(String[] args) {
        Outer outer = new Outer();
        outer.anonymous();
//        Outer.Inner innerClass = outer.inner;
//        System.out.println(innerClass.localInt);
//        Outer.Inner inner2 = new Outer.Inner();

        IntWrapper intWrapper = new IntWrapper();
        intWrapper.setOperation(new IntWrapper.Monad<String>() {
            @Override
            public String apply(String arg) {
                System.out.println("apply String");
                return "++".substring(1);
            }
        })
                .setNum(new IntWrapper.Monad<Integer>() {
                    @Override
                    public Integer apply(Integer arg) {
                        System.out.println("apply Integer");
                        return arg + 1;
                    }
                })
                .calculate(3)
        ;
    }
}
