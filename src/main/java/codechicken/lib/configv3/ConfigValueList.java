package codechicken.lib.configv3;

import it.unimi.dsi.fastutil.booleans.BooleanList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.longs.LongList;

import java.util.List;

/**
 * Created by covers1624 on 18/4/22.
 */
public interface ConfigValueList extends ConfigTag {

    ValueType getType();

    BooleanList getBooleans();

    List<String> getStrings();

    IntList getInts();

    LongList getLongs();

    IntList getHexs();

    DoubleList getDoubles();

    ConfigValueList setBooleans(List<Boolean> values);

    ConfigValueList setStrings(List<String> values);

    ConfigValueList setInts(List<Integer> values);

    ConfigValueList setLongs(List<Long> values);

    ConfigValueList setHexs(List<Integer> values);

    ConfigValueList setDoubles(List<Double> values);

    BooleanList getDefaultBooleans();

    List<String> getDefaultStrings();

    IntList getDefaultInts();

    LongList getDefaultLongs();

    IntList getDefaultHexs();

    DoubleList getDefaultDoubles();

    ConfigValueList setDefaultBooleans(List<Boolean> values);

    ConfigValueList setDefaultStrings(List<String> values);

    ConfigValueList setDefaultInts(List<Integer> values);

    ConfigValueList setDefaultLongs(List<Long> values);

    ConfigValueList setDefaultHexs(List<Integer> values);

    ConfigValueList setDefaultDoubles(List<Double> values);

    ConfigValueList onSync(ConfigCallback<ConfigValueList> callback);

    @Override
    ConfigValueList setComment(String comment);

    @Override
    ConfigValueList setComment(String... comment);

    @Override
    ConfigValueList setComment(List<String> comment);
}
