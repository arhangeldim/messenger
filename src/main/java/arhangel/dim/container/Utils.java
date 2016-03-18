package arhangel.dim.container;

public class Utils {

    public static String capitalize(String string) {
        string = string.toLowerCase();
        string = Character.toString(string.charAt(0)).toUpperCase() + string.substring(1);
        return string;
    }
}
