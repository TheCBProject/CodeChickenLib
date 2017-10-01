package codechicken.lib.configuration;

import java.util.List;

/**
 * Created by covers1624 on 18/07/2017.
 */
public interface IConfigValue {

    boolean getBoolean();

    String getString();

    int getInt();

    int getHex();

    double getDouble();

    //
    IConfigValue setDefaultBoolean(boolean value);

    IConfigValue setDefaultString(String value);

    IConfigValue setDefaultInt(int value);

    IConfigValue setDefaultHex(int value);

    IConfigValue setDefaultDouble(double value);

    //
    IConfigValue setBoolean(boolean value);

    IConfigValue setString(String value);

    IConfigValue setInt(int value);

    IConfigValue setHex(int value);

    IConfigValue setDouble(double value);

    //
    List<Boolean> getBooleanList();

    List<String> getStringList();

    List<Integer> getIntList();

    List<Integer> getHexList();

    List<Double> getDoubleList();

    //
    IConfigValue setDefaultBooleanList(List<Boolean> value);

    IConfigValue setDefaultStringList(List<String> value);

    IConfigValue setDefaultIntList(List<Integer> value);

    IConfigValue setDefaultHexList(List<Integer> value);

    IConfigValue setDefaultDoubleList(List<Double> value);

    //
    IConfigValue setBooleanList(List<Boolean> value);

    IConfigValue setStringList(List<String> value);

    IConfigValue setIntList(List<Integer> value);

    IConfigValue setHexList(List<Integer> value);

    IConfigValue setDoubleList(List<Double> value);

}
