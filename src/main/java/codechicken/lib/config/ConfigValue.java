package codechicken.lib.config;

import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;

/**
 * Represents a {@link ConfigTag} with a value
 * of a specific type.
 * <p>
 * Created by covers1624 on 17/4/22.
 */
public interface ConfigValue extends ConfigTag {

    /**
     * Gets the type of the {@link ConfigValue}.
     *
     * @return The {@link ValueType} of this tag.
     */
    ValueType getType();

    /**
     * Gets the {@code boolean} value of this tag.
     * <p>
     * No attempt to convert between tag types is made,
     * this must be called on a tag which has a non {@link ValueType#UNKNOWN} type.
     *
     * @return The boolean value or the default.
     * @throws IllegalStateException If the {@link #getType()} is not {@link ValueType#BOOLEAN},
     *                               or in the event of a missing value, no default is set.
     */
    boolean getBoolean();

    /**
     * Gets the {@link String} value of this tag.
     * <p>
     * No attempt to convert between tag types is made,
     * this must be called on a tag which has a non {@link ValueType#UNKNOWN} type.
     *
     * @return The String value or the default.
     * @throws IllegalStateException If the {@link #getType()} is not {@link ValueType#STRING},
     *                               or in the event of a missing value, no default is set.
     */
    String getString();

    /**
     * Gets the {@code int} value of this tag.
     * <p>
     * No attempt to convert between tag types is made,
     * this must be called on a tag which has a non {@link ValueType#UNKNOWN} type.
     *
     * @return The int value or the default.
     * @throws IllegalStateException If the {@link #getType()} is not {@link ValueType#INT},
     *                               or in the event of a missing value, no default is set.
     */
    int getInt();

    /**
     * Gets the {@code long} value of this tag.
     * <p>
     * No attempt to convert between tag types is made,
     * this must be called on a tag which has a non {@link ValueType#UNKNOWN} type.
     *
     * @return The long value or the default.
     * @throws IllegalStateException If the {@link #getType()} is not {@link ValueType#LONG},
     *                               or in the event of a missing value, no default is set.
     */
    long getLong();

    /**
     * Gets the {@code int} value stored as a Hex String of this tag.
     * <p>
     * No attempt to convert between tag types is made,
     * this must be called on a tag which has a non {@link ValueType#UNKNOWN} type.
     *
     * @return The int value or the default.
     * @throws IllegalStateException If the {@link #getType()} is not {@link ValueType#HEX},
     *                               or in the event of a missing value, no default is set.
     */
    int getHex();

    /**
     * Gets the {@code double} value of this tag.
     * <p>
     * No attempt to convert between tag types is made,
     * this must be called on a tag which has a non {@link ValueType#UNKNOWN} type.
     *
     * @return The double value or the default.
     * @throws IllegalStateException If the {@link #getType()} is not {@link ValueType#DOUBLE},
     *                               or in the event of a missing value, no default is set.
     */
    double getDouble();

    /**
     * Sets the {@code boolean} value of this tag.
     * <p>
     * If the tag has an {@link ValueType#UNKNOWN} value type,
     * this function will set it appropriately.
     *
     * @param value The {@code boolean} value.
     * @return The same {@link ConfigValue}.
     * @throws IllegalStateException If the {@link #getType()} is not {@link ValueType#UNKNOWN}
     *                               or {@link ValueType#BOOLEAN}.
     */
    ConfigValue setBoolean(boolean value);

    /**
     * Sets the {@link String} value of this tag.
     * <p>
     * If the tag has an {@link ValueType#UNKNOWN} value type,
     * this function will set it appropriately.
     *
     * @param value The {@link String} value.
     * @return The same {@link ConfigValue}.
     * @throws IllegalStateException If the {@link #getType()} is not {@link ValueType#UNKNOWN}
     *                               or {@link ValueType#STRING}.
     */
    ConfigValue setString(String value);

    /**
     * Sets the {@code int} value of this tag.
     * <p>
     * If the tag has an {@link ValueType#UNKNOWN} value type,
     * this function will set it appropriately.
     *
     * @param value The {@code int} value.
     * @return The same {@link ConfigValue}.
     * @throws IllegalStateException If the {@link #getType()} is not {@link ValueType#UNKNOWN}
     *                               or {@link ValueType#INT}.
     */
    ConfigValue setInt(int value);

    /**
     * Sets the {@code long} value of this tag.
     * <p>
     * If the tag has an {@link ValueType#UNKNOWN} value type,
     * this function will set it appropriately.
     *
     * @param value The {@code long} value.
     * @return The same {@link ConfigValue}.
     * @throws IllegalStateException If the {@link #getType()} is not {@link ValueType#UNKNOWN}
     *                               or {@link ValueType#LONG}.
     */
    ConfigValue setLong(long value);

    /**
     * Sets the {@code int} value represented as a hex String of this tag.
     * <p>
     * If the tag has an {@link ValueType#UNKNOWN} value type,
     * this function will set it appropriately.
     *
     * @param value The {@code int} value.
     * @return The same {@link ConfigValue}.
     * @throws IllegalStateException If the {@link #getType()} is not {@link ValueType#UNKNOWN}
     *                               or {@link ValueType#HEX}.
     */
    ConfigValue setHex(int value);

    /**
     * Sets the {@code double} value of this tag.
     * <p>
     * If the tag has an {@link ValueType#UNKNOWN} value type,
     * this function will set it appropriately.
     *
     * @param value The {@code double} value.
     * @return The same {@link ConfigValue}.
     * @throws IllegalStateException If the {@link #getType()} is not {@link ValueType#UNKNOWN}
     *                               or {@link ValueType#DOUBLE}.
     */
    ConfigValue setDouble(double value);

    /**
     * Gets the default {@code boolean} value of this tag.
     * <p>
     * No attempt to convert between tag types is made,
     * this must be called on a tag which has a non {@link ValueType#UNKNOWN} type.
     *
     * @return The default boolean value.
     * @throws IllegalStateException If the {@link #getType()} is not {@link ValueType#BOOLEAN},
     *                               or when no default is set.
     */
    boolean getDefaultBoolean();

    /**
     * Gets the default {@link String} value of this tag.
     * <p>
     * No attempt to convert between tag types is made,
     * this must be called on a tag which has a non {@link ValueType#UNKNOWN} type.
     *
     * @return The default {@link String} value.
     * @throws IllegalStateException If the {@link #getType()} is not {@link ValueType#STRING},
     *                               or when no default is set.
     */
    String getDefaultString();

    /**
     * Gets the default {@code int} value of this tag.
     * <p>
     * No attempt to convert between tag types is made,
     * this must be called on a tag which has a non {@link ValueType#UNKNOWN} type.
     *
     * @return The default int value.
     * @throws IllegalStateException If the {@link #getType()} is not {@link ValueType#INT},
     *                               or when no default is set.
     */
    int getDefaultInt();

    /**
     * Gets the default {@code long} value of this tag.
     * <p>
     * No attempt to convert between tag types is made,
     * this must be called on a tag which has a non {@link ValueType#UNKNOWN} type.
     *
     * @return The default long value.
     * @throws IllegalStateException If the {@link #getType()} is not {@link ValueType#LONG},
     *                               or when no default is set.
     */
    long getDefaultLong();

    /**
     * Gets the default {@code int} value stored as a Hex String of this tag.
     * <p>
     * No attempt to convert between tag types is made,
     * this must be called on a tag which has a non {@link ValueType#UNKNOWN} type.
     *
     * @return The default int value.
     * @throws IllegalStateException If the {@link #getType()} is not {@link ValueType#HEX},
     *                               or when no default is set.
     */
    int getDefaultHex();

    /**
     * Gets the default {@code double} value of this tag.
     * <p>
     * No attempt to convert between tag types is made,
     * this must be called on a tag which has a non {@link ValueType#UNKNOWN} type.
     *
     * @return The default double value.
     * @throws IllegalStateException If the {@link #getType()} is not {@link ValueType#DOUBLE},
     *                               or when no default is set.
     */
    double getDefaultDouble();

    /**
     * Sets the default {@code boolean} value of this tag.
     * <p>
     * If the tag has an {@link ValueType#UNKNOWN} value type,
     * this function will set it appropriately.
     *
     * @param value The {@code boolean} value.
     * @return The same {@link ConfigValue}.
     * @throws IllegalStateException If the {@link #getType()} is not {@link ValueType#UNKNOWN}
     *                               or {@link ValueType#BOOLEAN}.
     */
    ConfigValue setDefaultBoolean(boolean value);

    /**
     * Sets the default {@link String} value of this tag.
     * <p>
     * If the tag has an {@link ValueType#UNKNOWN} value type,
     * this function will set it appropriately.
     *
     * @param value The {@link String} value.
     * @return The same {@link ConfigValue}.
     * @throws IllegalStateException If the {@link #getType()} is not {@link ValueType#UNKNOWN}
     *                               or {@link ValueType#STRING}.
     */
    ConfigValue setDefaultString(String value);

    /**
     * Sets the default {@code int} value of this tag.
     * <p>
     * If the tag has an {@link ValueType#UNKNOWN} value type,
     * this function will set it appropriately.
     *
     * @param value The {@code int} value.
     * @return The same {@link ConfigValue}.
     * @throws IllegalStateException If the {@link #getType()} is not {@link ValueType#UNKNOWN}
     *                               or {@link ValueType#INT}.
     */
    ConfigValue setDefaultInt(int value);

    /**
     * Sets the default {@code long} value of this tag.
     * <p>
     * If the tag has an {@link ValueType#UNKNOWN} value type,
     * this function will set it appropriately.
     *
     * @param value The {@code long} value.
     * @return The same {@link ConfigValue}.
     * @throws IllegalStateException If the {@link #getType()} is not {@link ValueType#UNKNOWN}
     *                               or {@link ValueType#LONG}.
     */
    ConfigValue setDefaultLong(long value);

    /**
     * Sets the default {@code int} value represented as a hex String of this tag.
     * <p>
     * If the tag has an {@link ValueType#UNKNOWN} value type,
     * this function will set it appropriately.
     *
     * @param value The {@code int} value.
     * @return The same {@link ConfigValue}.
     * @throws IllegalStateException If the {@link #getType()} is not {@link ValueType#UNKNOWN}
     *                               or {@link ValueType#HEX}.
     */
    ConfigValue setDefaultHex(int value);

    /**
     * Sets the default {@code double} value of this tag.
     * <p>
     * If the tag has an {@link ValueType#UNKNOWN} value type,
     * this function will set it appropriately.
     *
     * @param value The {@code double} value.
     * @return The same {@link ConfigValue}.
     * @throws IllegalStateException If the {@link #getType()} is not {@link ValueType#UNKNOWN}
     *                               or {@link ValueType#DOUBLE}.
     */
    ConfigValue setDefaultDouble(double value);

    /**
     * Set the {@link Restriction} for this {@link ConfigValue}.
     * <p>
     * A restriction is just a {@link Predicate}, and can be used for anything,
     * but is most commonly used for int/float value ranges on tags.
     * <p>
     * If the restriction is violated, either by the tag being
     * loaded from disk, or via a set method, it will be reset to default.
     * <p>
     * TODO, in the future, provide a function to choose what to do when a value
     *  is invalid, allow the value to be altered (clamped), removed, or reset entirely to default.
     * It is invalid for the restriction to not accept the tag's default value,
     * if the restriction does not, it will throw an {@link IllegalArgumentException} either
     * when the restriction is set, or the default is set, whichever comes last.
     * <p>
     * See {@link Restriction} for more information.
     *
     * @param restriction The restriction to set.
     * @return The same {@link ConfigValue}.
     * @see Restriction
     */
    ConfigValue setRestriction(Restriction restriction);

    /**
     * Gets the {@link Restriction} predicate for this tag.
     *
     * @return The {@link Restriction}
     */
    @Nullable
    Restriction getRestriction();

    /**
     * {@inheritDoc}
     */
    @Override
    ConfigValue syncTagToClient();

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
     * @return The same {@link ConfigValue}.
     * @see ConfigCallback
     */
    ConfigValue onSync(ConfigCallback<ConfigValue> callback);

    /**
     * {@inheritDoc}
     */
    @Override
    ConfigValue setComment(String comment);

    /**
     * {@inheritDoc}
     */
    @Override
    ConfigValue setComment(String... comment);

    /**
     * {@inheritDoc}
     */
    @Override
    ConfigValue setComment(List<String> comment);
}
