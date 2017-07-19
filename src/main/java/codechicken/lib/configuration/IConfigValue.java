package codechicken.lib.configuration;

import java.util.List;

/**
 * Created by covers1624 on 18/07/2017.
 */
public interface IConfigValue {

    boolean getBoolean(boolean defaultValue);

    String getString(String defaultValue);

    int getInt(int defaultValue);

    int getHex(int defaultValue);

    double getDouble(double defaultValue);

    IConfigValue setBoolean(boolean value);
    IConfigValue setString(String value);
    IConfigValue setInt(int value);
    IConfigValue setHex(int value);
    IConfigValue setDouble(double value);

    List<Boolean> getBooleanList(List<Boolean> defaultValues);

    List<String> getStringList(List<String> defaultValues);

    List<Integer> getIntList(List<Integer> defaultValues);

    List<Integer> getHexList(List<Integer> defaultValues);

    List<Double> getDoubleList(List<Double> defaultValues);

    IConfigValue setBooleanList(List<Boolean> value);
    IConfigValue setStringList(List<String> value);
    IConfigValue setIntList(List<Integer> value);
    IConfigValue setHexList(List<Integer> value);
    IConfigValue setDoubleList(List<Double> value);

}
