package codechicken.lib.configuration;

import java.util.List;

/**
 * Created by covers1624 on 18/07/2017.
 */
public interface IConfigValue<E extends IConfigValue> {

    boolean getBoolean();

    String getString();

    int getInt();

    int getHex();

    double getDouble();

    //
    E setDefaultBoolean(boolean value);

    E setDefaultString(String value);

    E setDefaultInt(int value);

    E setDefaultHex(int value);

    E setDefaultDouble(double value);

    //
    E setBoolean(boolean value);

    E setString(String value);

    E setInt(int value);

    E setHex(int value);

    E setDouble(double value);

    //
    List<Boolean> getBooleanList();

    List<String> getStringList();

    List<Integer> getIntList();

    List<Integer> getHexList();

    List<Double> getDoubleList();

    //
    E setDefaultBooleanList(List<Boolean> value);

    E setDefaultStringList(List<String> value);

    E setDefaultIntList(List<Integer> value);

    E setDefaultHexList(List<Integer> value);

    E setDefaultDoubleList(List<Double> value);

    //
    E setBooleanList(List<Boolean> value);

    E setStringList(List<String> value);

    E setIntList(List<Integer> value);

    E setHexList(List<Integer> value);

    E setDoubleList(List<Double> value);

    /**
     * Copies the tag.
     * If called on a child tag, it is split from its parent.
     *
     * @return The copy.
     */
    E copy();

    /**
     * Copies the tag, not really meant for public use.
     * Internally, called with the already copied parent.
     *
     * @param parent The already copied parent.
     * @return The copy.
     */
    E copy(E parent);

    /**
     * Copies the tags value from the provided tag.
     * If called on a Category, all children of the category
     * are called to copy from their pair.
     * If their pair doesnt exist, an exception is thrown.
     *
     * @param other The tag to copy values from.
     * @return This tag.
     */
    E copyFrom(E other);

}
