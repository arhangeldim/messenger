package arhangel.dim.container;

import java.io.File;

public class Utils {

    public static String capitalize(String string) {
        string = string.toLowerCase();
        string = Character.toString(string.charAt(0)).toUpperCase() + string.substring(1);
        return string;
    }


    public static File initFile(String pathToFile) {
        File file = new File(pathToFile);
        if (!file.exists()) {
            String tryPath = System.getProperty("user.dir") + "/" + pathToFile;
            file = new File(tryPath);
        }
        if (!file.exists()) {
            String tryPath = System.getProperty("user.dir").replaceAll("/src", "") + "/" + pathToFile;
            file = new File(tryPath);
        }
        return file;
    }
}
