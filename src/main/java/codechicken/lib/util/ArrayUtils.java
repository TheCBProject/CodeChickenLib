package codechicken.lib.util;

import java.lang.reflect.Array;
import java.util.*;
import java.util.function.Function;

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
        HashMap<String, String> map = new HashMap<>();
        for (String entry : array) {
            String[] split = entry.split("=");
            map.put(split[0], split[1]);
        }
        return map;
    }

    /**
     * Prefixes a string array with the desired value.
     *
     * @param prefix The prefix to apply.
     * @param list   The list to apply the prefix to.
     * @return The list with the prefix applied.
     */
    public static List<String> prefixStringList(String prefix, List<String> list) {
        List<String> finalList = new ArrayList<>();
        for (String string : list) {
            finalList.add(prefix + string);
        }
        return finalList;
    }

    /**
     * Checks if a map contains all keys passed in.
     *
     * @param map  Map to check.
     * @param keys Keys that must exist.
     * @param <T>  The type of data in the map key.
     * @return False if fail.
     */
    @SafeVarargs
    public static <T> boolean containsKeys(Map<T, ?> map, T... keys) {
        for (T object : keys) {
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
     * @return Returns a new array in the event the input was expanded.
     */
    public static <T> T[] addToArrayFirstNull(T[] array, T value) {
        int nullIndex = -1;
        for (int i = 0; i < array.length; i++) {
            T v = array[i];
            if (v == null) {
                nullIndex = i;
                break;
            }
        }
        if (nullIndex == -1) {
            T[] copy = createNewArray(array, array.length + 1);
            System.arraycopy(array, 0, copy, 0, array.length);
            nullIndex = array.length;
            array = copy;
        }
        array[nullIndex] = value;
        return array;
    }

    /**
     * Adds all elements from the array that are not null to the list.
     *
     * @param array Array to grab from.
     * @param list  List to add to.
     * @param <T>   What we are dealing with.
     * @return The modified list.
     */
    public static <T> List<T> addAllNoNull(T[] array, List<T> list) {
        for (T value : array) {
            if (value != null) {
                list.add(value);
            }
        }
        return list;
    }

    /**
     * Checks if the array is all null.
     *
     * @param array The array to check.
     * @param <T>   What we are dealing with.
     * @return True if the array only contains nulls.
     */
    public static <T> boolean isEmpty(T[] array) {
        for (T value : array) {
            if (value != null) {
                return false;
            }
        }
        return true;
    }

    /**
     * Counts the elements in the array that are not null.
     *
     * @param array The array to check.
     * @param <T>   What we are dealing with.
     * @return The count of non-null objects in the array.
     */
    public static <T> int countNoNull(T[] array) {
        return count(array, Objects::nonNull);
    }

    /**
     * Counts elements in the array that conform to the Function check.
     *
     * @param array The array to check.
     * @param check The Function to apply to each element.
     * @param <T>   What we are dealing with.
     * @return The count.
     */
    public static <T> int count(T[] array, Function<T, Boolean> check) {
        int counter = 0;
        for (T value : array) {
            if (check.apply(value)) {
                counter++;
            }
        }
        return counter;
    }

    /**
     * Fills the array with the specified value.
     * If the value is an instance of Copyable it will call copy.
     *
     * @param array Array to fill.
     * @param value Value to fill with.
     * @param <T>   What we are dealing with.
     */
    @SuppressWarnings ("unchecked")
    public static <T> T[] fill(T[] array, T value) {
        for (int i = 0; i < array.length; i++) {
            T newValue = value;
            if (value instanceof Copyable) {
                newValue = ((Copyable<T>) value).copy();
            }
            array[i] = newValue;
        }
        return array;
    }

    /**
     * Fills the array with the specified value.
     * A Function is used to check if the value should be replaced.
     * If the value is an instance of Copyable it will call copy.
     *
     * @param array Array to fill.
     * @param value Value to fill with.
     * @param check Called to decide if the value should be replaced.
     * @param <T>   What we are dealing with.
     */
    @SuppressWarnings ("unchecked")
    public static <T> void fillArray(T[] array, T value, Function<T, Boolean> check) {
        for (int i = 0; i < array.length; i++) {
            if (check.apply(array[i])) {
                T newValue = value;
                if (value instanceof Copyable) {
                    newValue = ((Copyable<T>) value).copy();
                }
                array[i] = newValue;
            }
        }
    }

    /**
     * Apples the Specified function to the entire array and returns a new List of the result.
     * The input to the function may be null.
     *
     * @param function The function to apply.
     * @param array    The array to apply the function to.
     * @param <I>      The Input generic.
     * @param <O>      The Output generic.
     * @return A list of the output of the specified function.
     */
    public static <I, O> List<O> applyArray(Function<I, O> function, I... array) {
        List<O> finalList = new ArrayList<>();
        for (I i : array) {
            finalList.add(function.apply(i));
        }

        return finalList;
    }

    /**
     * Basically a wrapper for System.arraycopy with support for CCL Copyable's
     *
     * @param src     The source array.
     * @param srcPos  Starting position in the source array.
     * @param dst     The destination array.
     * @param destPos Starting position in the destination array.
     * @param length  The number of elements to copy.
     */
    public static void arrayCopy(Object src, int srcPos, Object dst, int destPos, int length) {
        System.arraycopy(src, srcPos, dst, destPos, length);
        if (dst instanceof Copyable[]) {
            Object[] oa = (Object[]) dst;
            Copyable<Object>[] c = (Copyable[]) dst;
            for (int i = destPos; i < destPos + length; i++) {
                if (c[i] != null) {
                    oa[i] = c[i].copy();
                }
            }
        }
    }

    /**
     * Returns the index of the first occurrence of the specified element in the array.
     * Will return -1 if the element is non existent in the array.
     *
     * @param array  The array to search.
     * @param object Element to find.
     * @param <T>    What we are dealing with.
     * @return The index in the array of the object.
     */
    public static <T> int indexOf(T[] array, T object) {
        if (object == null) {
            for (int i = 0; i < array.length; i++) {
                if (array[i] == null) {
                    return i;
                }
            }
        } else {
            for (int i = 0; i < array.length; i++) {
                if (object.equals(array[i])) {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * Create a new array using the provided array as a template for both type and length.
     *
     * @param array The template.
     * @param <T>   The type.
     * @return The new array.
     */
    @SuppressWarnings ("unchecked")
    public static <T> T[] createNewArray(T[] array) {
        return createNewArray(array, array.length);
    }

    /**
     * Create a new array using the provided array as a template for the type and with the provided length.
     *
     * @param array  The type template.
     * @param length The new array's length.
     * @param <T>    The type.
     * @return The new array.
     */
    @SuppressWarnings ("unchecked")
    public static <T> T[] createNewArray(T[] array, int length) {
        Class<? extends T[]> newType = (Class<? extends T[]>) array.getClass();
        T[] copy;
        //noinspection RedundantCast
        if (newType.equals(Object[].class)) {
            copy = (T[]) new Object[length];
        } else {
            copy = (T[]) newArray(newType.getComponentType(), length);
        }
        return copy;
    }

    /**
     * Creates a new array form its component class.
     *
     * @param arrayClass The component class.
     * @param length     The length.
     * @param <T>        The thing.
     * @return The new array.
     */
    @SuppressWarnings ("unchecked")
    public static <T> T[] newArray(Class<T> arrayClass, int length) {
        return (T[]) Array.newInstance(arrayClass, length);
    }

    /**
     * Rolls the array based on the shift.
     * Positive shift means the array will roll to the right.
     * Negative shift means the array will roll to the left.
     *
     * @param input The input array.
     * @param shift The shift amount.
     * @param <T>   The thing.
     * @return The new array.
     */
    public static <T> T[] rollArray(T[] input, int shift) {
        T[] newArray = createNewArray(input);

        for (int i = 0; i < input.length; i++) {
            int newPos = (i + shift) % input.length;

            if (newPos < 0) {
                newPos += input.length;
            }
            newArray[newPos] = input[i];
        }
        return newArray;
    }

    /**
     * Checks if an array contains any of the specified element.
     *
     * @param input   The input
     * @param element The thing to test against.
     * @param <T>     The thing.
     * @return If the element exists at all.
     */
    public static <T> boolean contains(T[] input, T element) {
        for (T test : input) {
            if (Objects.equals(test, element)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Creates the inverse of an array.
     * If the input array does not contain an element from the allElements array,
     * then it is added to the output.
     *
     * @param input       The input.
     * @param allElements All possible values.
     * @param <T>         The thing.
     * @return The inverse array.
     */
    public static <T> T[] inverse(T[] input, T[] allElements) {
        List<T> list = new LinkedList<>();
        for (T e : allElements) {
            if (!contains(input, e)) {
                list.add(e);
            }
        }

        return list.toArray(createNewArray(input, list.size()));
    }

    /**
     * Checks if the specified array is empty or contains a null entry.
     *
     * @param input The input.
     * @param <T>   The thing.
     * @return If the array is null or contains null.
     */
    public static <T> boolean isNullOrContainsNull(T[] input) {
        if (input != null) {
            for (T t : input) {
                if (t == null) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }

    /**
     * Convert an int array to a list of Integers.
     *
     * @param arr in.
     * @return out.
     */
    public static List<Integer> toList(int[] arr) {
        List<Integer> list = new ArrayList<>();
        for (int i : arr) {
            list.add(i);
        }
        return list;
    }
}
