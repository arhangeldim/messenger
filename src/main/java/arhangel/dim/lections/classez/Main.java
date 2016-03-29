package arhangel.dim.lections.classez;

/**
 * Created by r.kildiev on 29.03.2016.
 */

//Lecture Slides         https://drive.google.com/open?id=0Bz3AMLjdKktjdDl6MXhQTFBIU2M
public class Main {
    public static void main(String[] args) {
        Inner inner = new Inner();
        inner.anonymous();
        Inner.Inner2 innerField = inner.inner;
//        Inner.Inner2 inner2 = new Inner.Inner2();
    }
}
