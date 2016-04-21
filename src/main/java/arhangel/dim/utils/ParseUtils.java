package arhangel.dim.utils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by olegchuikin on 19/04/16.
 */
public class ParseUtils {

    public static List<Long> stringArrToLongList(String[] strings) {
        return Arrays.asList(strings).stream().map(Long::parseLong).collect(Collectors.toList());
    }

    public static String[] longListToStringArr(List<Long> list) {
        List<String> collect = list.stream().map(Object::toString).collect(Collectors.toList());
        return collect.toArray(new String[collect.size()]);
    }
}
