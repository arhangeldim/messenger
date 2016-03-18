package arhangel.dim.container;

public class Utils {

    public static String capitalize(String sString){
        sString = sString.toLowerCase();
        sString = Character.toString(sString.charAt(0)).toUpperCase()+sString.substring(1);
        return sString;
    }
}
