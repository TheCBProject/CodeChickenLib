package codechicken.lib.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by covers1624 on 3/27/2016.
 * TODO, Move to CCL.
 */
public class ArrayUtils {

    public static String[] arrayToLowercase(String[] array) {
        String[] copy = new String[array.length];
        for (int i = 0; i < array.length; i++) {
            copy[i] = array[i].toLowerCase();
        }
        return copy;
    }

    /**
     * Converts and array of "key=value" to a map.
     *
     * @param array
     * @return
     */
    public static Map<String, String> convertKeyValueArrayToMap(String[] array) {
        HashMap<String, String> map = new HashMap<String, String>();
        for (String entry : array) {
            String[] split = entry.split("=");
            map.put(split[0], split[1]);
        }
        return map;
    }

    public static boolean containsKeys(Map<String, String> map, String... keys) {
        for (Object object : keys) {
            if (!map.containsKey(object)) {
                return false;
            }
        }
        return true;
    }
}
