package codechicken.lib.config;

import it.unimi.dsi.fastutil.booleans.BooleanList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.longs.LongList;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;

/**
 * Created by covers1624 on 18/4/22.
 */
public interface ConfigValueList extends ConfigTag {

    /**
     * Gets the type of the {@link ConfigValueList}.
     *
     * @return The {@link ValueType} of this tag.
     */
    ValueType getType();

    /**
     * Gets the {@link BooleanList} values stored in this tag.
     * <p>
     * No attempt to convert between tag types is made,
     * this must be called on a tag which has a non {@link ValueType#UNKNOWN} type.
     *
     * @return The boolean value or the default.
     * @throws IllegalStateException If the {@link #getType()} is not {@link ValueType#BOOLEAN},
     *                               or in the event of a missing value, no default is set.
     */
    BooleanList getBooleans();

    /**
     * Gets the {@link List} of {@link String} values stored in this tag.
     * <p>
     * No attempt to convert between tag types is made,
     * this must be called on a tag which has a non {@link ValueType#UNKNOWN} type.
     *
     * @return The String value or the default.
     * @throws IllegalStateException If the {@link #getType()} is not {@link ValueType#STRING},
     *                               or in the event of a missing value, no default is set.
     */
    List<String> getStrings();

    /**
     * Gets the {@link IntList} values stored in this tag.
     * <p>
     * No attempt to convert between tag types is made,
     * this must be called on a tag which has a non {@link ValueType#UNKNOWN} type.
     *
     * @return The int value or the default.
     * @throws IllegalStateException If the {@link #getType()} is not {@link ValueType#INT},
     *                               or in the event of a missing value, no default is set.
     */
    IntList getInts();

    /**
     * Gets the {@link LongList} value stored in this tag.
     * <p>
     * No attempt to convert between tag types is made,
     * this must be called on a tag which has a non {@link ValueType#UNKNOWN} type.
     *
     * @return The long value or the default.
     * @throws IllegalStateException If the {@link #getType()} is not {@link ValueType#LONG},
     *                               or in the event of a missing value, no default is set.
     */
    LongList getLongs();

    /**
     * Gets the {@link IntList} values stored as a Hex strings stored in this tag.
     * <p>
     * No attempt to convert between tag types is made,
     * this must be called on a tag which has a non {@link ValueType#UNKNOWN} type.
     *
     * @return The int value or the default.
     * @throws IllegalStateException If the {@link #getType()} is not {@link ValueType#HEX},
     *                               or in the event of a missing value, no default is set.
     */
    IntList getHexs();

    /**
     * Gets the {@link DoubleList} values stored in this tag.
     * <p>
     * No attempt to convert between tag types is made,
     * this must be called on a tag which has a non {@link ValueType#UNKNOWN} type.
     *
     * @return The double value or the default.
     * @throws IllegalStateException If the {@link #getType()} is not {@link ValueType#DOUBLE},
     *                               or in the event of a missing value, no default is set.
     */
    DoubleList getDoubles();

    /**
     * Sets the {@link List} of {@link Boolean} values stored in this tag.
     * <p>
     * If the tag has an {@link ValueType#UNKNOWN} value type,
     * this function will set it appropriately.
     *
     * @param values The {@link List} of {@link Boolean} values.
     * @return The same {@link ConfigValueList}.
     * @throws IllegalStateException If the {@link #getType()} is not {@link ValueType#UNKNOWN}
     *                               or {@link ValueType#BOOLEAN}.
     */
    ConfigValueList setBooleans(List<Boolean> values);

    /**
     * Sets the {@link List} of {@link String} values stored in this tag.
     * <p>
     * If the tag has an {@link ValueType#UNKNOWN} value type,
     * this function will set it appropriately.
     *
     * @param values The {@link List} of {@link String} values.
     * @return The same {@link ConfigValueList}.
     * @throws IllegalStateException If the {@link #getType()} is not {@link ValueType#UNKNOWN}
     *                               or {@link ValueType#STRING}.
     */
    ConfigValueList setStrings(List<String> values);

    /**
     * Sets the {@link List} of {@link Integer} values stored in this tag.
     * <p>
     * If the tag has an {@link ValueType#UNKNOWN} value type,
     * this function will set it appropriately.
     *
     * @param values The {@link List} of {@link Integer} values.
     * @return The same {@link ConfigValueList}.
     * @throws IllegalStateException If the {@link #getType()} is not {@link ValueType#UNKNOWN}
     *                               or {@link ValueType#INT}.
     */
    ConfigValueList setInts(List<Integer> values);

    /**
     * Sets the {@link List} of {@link Long} values stored in this tag.
     * <p>
     * If the tag has an {@link ValueType#UNKNOWN} value type,
     * this function will set it appropriately.
     *
     * @param values The {@link List} of {@link Long} values.
     * @return The same {@link ConfigValueList}.
     * @throws IllegalStateException If the {@link #getType()} is not {@link ValueType#UNKNOWN}
     *                               or {@link ValueType#LONG}.
     */
    ConfigValueList setLongs(List<Long> values);

    /**
     * Sets the {@link List} of {@link Integer} values represented as hex Strings stored in this tag.
     * <p>
     * If the tag has an {@link ValueType#UNKNOWN} value type,
     * this function will set it appropriately.
     *
     * @param values The {@link List} of {@link Integer} values.
     * @return The same {@link ConfigValueList}.
     * @throws IllegalStateException If the {@link #getType()} is not {@link ValueType#UNKNOWN}
     *                               or {@link ValueType#HEX}.
     */
    ConfigValueList setHexs(List<Integer> values);

    /**
     * Sets the {@link List} of {@link Double} values stored in this tag.
     * <p>
     * If the tag has an {@link ValueType#UNKNOWN} value type,
     * this function will set it appropriately.
     *
     * @param values The {@link List} of {@link Double} values.
     * @return The same {@link ConfigValueList}.
     * @throws IllegalStateException If the {@link #getType()} is not {@link ValueType#UNKNOWN}
     *                               or {@link ValueType#DOUBLE}.
     */
    ConfigValueList setDoubles(List<Double> values);

    /**
     * Gets the default {@link BooleanList} values stored in this tag.
     * <p>
     * No attempt to convert between tag types is made,
     * this must be called on a tag which has a non {@link ValueType#UNKNOWN} type.
     *
     * @return The default {@link BooleanList} values.
     * @throws IllegalStateException If the {@link #getType()} is not {@link ValueType#BOOLEAN},
     *                               or when no default is set.
     */
    BooleanList getDefaultBooleans();

    /**
     * Gets the default {@link List} of {@link String} values stored in this tag.
     * <p>
     * No attempt to convert between tag types is made,
     * this must be called on a tag which has a non {@link ValueType#UNKNOWN} type.
     *
     * @return The default {@link List} of {@link String} values.
     * @throws IllegalStateException If the {@link #getType()} is not {@link ValueType#STRING},
     *                               or when no default is set.
     */
    List<String> getDefaultStrings();

    /**
     * Gets the default {@link IntList} values stored in this tag.
     * <p>
     * No attempt to convert between tag types is made,
     * this must be called on a tag which has a non {@link ValueType#UNKNOWN} type.
     *
     * @return The default {@link LongList} values.
     * @throws IllegalStateException If the {@link #getType()} is not {@link ValueType#INT},
     *                               or when no default is set.
     */
    IntList getDefaultInts();

    /**
     * Gets the default {@link LongList} values stored in this tag.
     * <p>
     * No attempt to convert between tag types is made,
     * this must be called on a tag which has a non {@link ValueType#UNKNOWN} type.
     *
     * @return The default {@link LongList} values.
     * @throws IllegalStateException If the {@link #getType()} is not {@link ValueType#LONG},
     *                               or when no default is set.
     */
    LongList getDefaultLongs();

    /**
     * Gets the default {@link IntList} values represented as Hex strings stored in this tag.
     * <p>
     * No attempt to convert between tag types is made,
     * this must be called on a tag which has a non {@link ValueType#UNKNOWN} type.
     *
     * @return The default {@link IntList} values.
     * @throws IllegalStateException If the {@link #getType()} is not {@link ValueType#HEX},
     *                               or when no default is set.
     */
    IntList getDefaultHexs();

    /**
     * Gets the default {@link DoubleList} values stored in this tag.
     * <p>
     * No attempt to convert between tag types is made,
     * this must be called on a tag which has a non {@link ValueType#UNKNOWN} type.
     *
     * @return The default {@link DoubleList} values.
     * @throws IllegalStateException If the {@link #getType()} is not {@link ValueType#DOUBLE},
     *                               or when no default is set.
     */
    DoubleList getDefaultDoubles();

    /**
     * Sets the default {@link List} of {@link Boolean} values stored in this tag.
     * <p>
     * If the tag has an {@link ValueType#UNKNOWN} value type,
     * this function will set it appropriately.
     *
     * @param values The {@link List} of {@link Boolean} values.
     * @return The same {@link ConfigValueList}.
     * @throws IllegalStateException If the {@link #getType()} is not {@link ValueType#UNKNOWN}
     *                               or {@link ValueType#BOOLEAN}.
     */
    ConfigValueList setDefaultBooleans(List<Boolean> values);

    /**
     * Sets the default {@link List} of {@link String} values stored in this tag.
     * <p>
     * If the tag has an {@link ValueType#UNKNOWN} value type,
     * this function will set it appropriately.
     *
     * @param values The {@link List} of {@link String} values.
     * @return The same {@link ConfigValueList}.
     * @throws IllegalStateException If the {@link #getType()} is not {@link ValueType#UNKNOWN}
     *                               or {@link ValueType#STRING}.
     */
    ConfigValueList setDefaultStrings(List<String> values);

    /**
     * Sets the default {@link List} of {@link Integer} values stored in this tag.
     * <p>
     * If the tag has an {@link ValueType#UNKNOWN} value type,
     * this function will set it appropriately.
     *
     * @param values The {@link List} of {@link Integer} values.
     * @return The same {@link ConfigValueList}.
     * @throws IllegalStateException If the {@link #getType()} is not {@link ValueType#UNKNOWN}
     *                               or {@link ValueType#INT}.
     */
    ConfigValueList setDefaultInts(List<Integer> values);

    /**
     * Sets the default {@link List} of {@link Long} values stored in this tag.
     * <p>
     * If the tag has an {@link ValueType#UNKNOWN} value type,
     * this function will set it appropriately.
     *
     * @param values The {@link List} of {@link Long} values.
     * @return The same {@link ConfigValueList}.
     * @throws IllegalStateException If the {@link #getType()} is not {@link ValueType#UNKNOWN}
     *                               or {@link ValueType#LONG}.
     */
    ConfigValueList setDefaultLongs(List<Long> values);

    /**
     * Sets the default {@link List} of {@link Integer} values represented as hex Strings stored in this tag.
     * <p>
     * If the tag has an {@link ValueType#UNKNOWN} value type,
     * this function will set it appropriately.
     *
     * @param values The {@link List} of {@link Integer} values.
     * @return The same {@link ConfigValueList}.
     * @throws IllegalStateException If the {@link #getType()} is not {@link ValueType#UNKNOWN}
     *                               or {@link ValueType#HEX}.
     */
    ConfigValueList setDefaultHexs(List<Integer> values);

    /**
     * Sets the default {@link List} of {@link Double} values stored in this tag.
     * <p>
     * If the tag has an {@link ValueType#UNKNOWN} value type,
     * this function will set it appropriately.
     *
     * @param values The {@link List} of {@link Double} values.
     * @return The same {@link ConfigValueList}.
     * @throws IllegalStateException If the {@link #getType()} is not {@link ValueType#UNKNOWN}
     *                               or {@link ValueType#DOUBLE}.
     */
    ConfigValueList setDefaultDoubles(List<Double> values);

    /**
     * // TODO Improve wording for lists.
     * Set the {@link Restriction} for this {@link ConfigValueList}.
     * <p>
     * A restriction is just a {@link Predicate}, and can be used for anything,
     * but is most commonly used for int/float value ranges on tags.
     * <p>
     * If the restriction is violated, either by the tag being
     * loaded from disk, or via a set method, it will be reset to default.
     * <p>
     * TODO, in the future, provide a function to choose what to do when a value
     *  is invalid, allow the value to be altered (clamped), removed, or reset entirely to default.
     * <p>
     * It is invalid for the restriction to not accept the tag's default value,
     * if the restriction does not, it will throw an {@link IllegalArgumentException} either
     * when the restriction is set, or the default is set, whichever comes last.
     * <p>
     * See {@link Restriction} for more information.
     *
     * @param restriction The restriction to set.
     * @return The same {@link ConfigValueList}.
     * @see Restriction
     */
    ConfigValueList setRestriction(ListRestriction restriction);

    /**
     * Gets the {@link Restriction} predicate for this tag.
     *
     * @return The {@link Restriction}
     */
    @Nullable
    ListRestriction getRestriction();

    /**
     * {@inheritDoc}
     */
    @Override
    ConfigValueList syncTagToClient();

    /**
     * Called when this tag, or any children, are modified.
     * <p>
     * This is commonly used for when tags are synced to the client
     * and inversely, reverted when disconnecting.
     * <p>
     * Additionally, one can manually fire all sync callbacks using {@link #forceSync()}
     * <p>
     * See {@link ConfigCallback} for more info.
     *
     * @param callback The {@link ConfigCallback}.
     * @return The same {@link ConfigValueList}.
     * @see ConfigCallback
     */
    ConfigValueList onSync(ConfigCallback<ConfigValueList> callback);

    /**
     * {@inheritDoc}
     */
    @Override
    ConfigValueList setComment(String comment);

    /**
     * {@inheritDoc}
     */
    @Override
    ConfigValueList setComment(String... comment);

    /**
     * {@inheritDoc}
     */
    @Override
    ConfigValueList setComment(List<String> comment);
}
