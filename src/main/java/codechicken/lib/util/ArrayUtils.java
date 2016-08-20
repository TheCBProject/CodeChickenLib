package codechicken.lib.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by covers1624 on 3/27/2016.
 */
public class ArrayUtils {

    /**
     * Converts an String array to lowercase.
     *
     * @param array Array to convert.
     * @return Converted array.
     */
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
     * @param array Array to convert.
     * @return Map of values.
     */
    public static Map<String, String> convertKeyValueArrayToMap(String[] array) {
        HashMap<String, String> map = new HashMap<String, String>();
        for (String entry : array) {
            String[] split = entry.split("=");
            map.put(split[0], split[1]);
        }
        return map;
    }

    /**
     * Checks if a map contains all keys passed in.
     *
     * @param map  Map to check.
     * @param keys Keys that must exist.
     * @return False if fail.
     */
    public static boolean containsKeys(Map<String, String> map, Object... keys) {
        for (Object object : keys) {
            if (!map.containsKey(object)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Adds the value at the first null index in the array.
     *
     * @param array Array to add to.
     * @param value Value to add.
     * @param <T>   Type of value.
     */
    public static <T> void addToArrayFirstNull(T[] array, T value) {
        int nullIndex = -1;
        for (int i = 0; i < array.length; i++) {
            T v = array[i];
            if (v == null) {
                nullIndex = i;
                break;
            }
        }
        if (nullIndex == -1) {//We aren't able to expand it as we are using generics.
            throw new RuntimeException("Unable to add to array as it is full! Expand it before adding to it!");
        }
        array[nullIndex] = value;
    }

    public static <T> List<T> addAllNoNull(T[] array, List<T> list) {
        for (T value : array){
            if (value != null){
                list.add(value);
            }
        }
        return list;
    }

    public static <T> boolean isEmpty(T[] array) {
        for (T value : array) {
            if (value != null) {
                return false;
            }
        }
        return true;
    }

    public static <T> int countNoNull(T[] array){
        int counter = 0;
        for (T value : array) {
            if (value != null) {
                counter++;
            }
        }
        return counter;
    }
}
