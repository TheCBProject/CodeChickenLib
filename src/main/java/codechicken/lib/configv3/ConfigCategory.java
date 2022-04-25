package codechicken.lib.configv3;

import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

/**
 * Created by covers1624 on 17/4/22.
 */
public interface ConfigCategory extends ConfigTag {

    /**
     * Checks if this tag has a child with the given name.
     *
     * @param name The name.
     * @return If the child exists.
     */
    boolean has(String name);

    /**
     * Gets the {@link ConfigTag} with the given name, or {@code null}.
     *
     * @param name The name.
     * @return The {@link ConfigTag} or {@code null} if it doesn't exist.
     */
    @Nullable
    ConfigTag findTag(String name);

    /**
     * Gets or creates a {@link ConfigCategory} with the given name.
     *
     * @param name The name.
     * @return The {@link ConfigCategory}.
     * @throws IllegalStateException If a {@link ConfigTag} exists, but it is not a {@link ConfigCategory}.
     */
    ConfigCategory getCategory(String name);

    /**
     * Gets the {@link ConfigCategory} with the given name, or null.
     * <p>
     * This will return {@code null} if the {@link ConfigTag} at the name
     * is not a {@link ConfigCategory} or it does not exist.
     *
     * @param name The name.
     * @return The {@link ConfigCategory} or {@code null}.
     */
    @Nullable
    ConfigCategory findCategory(String name);

    /**
     * Gets or creates a {@link ConfigValue} with the given name.
     *
     * @param name The name.
     * @return The {@link ConfigValue}.
     * @throws IllegalStateException If a {@link ConfigTag} exists, but it is not a {@link ConfigValue}.
     */
    ConfigValue getValue(String name);

    /**
     * Gets the {@link ConfigValue} with the given name, or null.
     * <p>
     * This will return {@code null} if the {@link ConfigTag} at the name
     * is not a {@link ConfigValue} or it does not exist.
     *
     * @param name The name.
     * @return The {@link ConfigValue} or {@code null}
     */
    @Nullable
    ConfigValue findValue(String name);

    /**
     * Gets or creates a {@link ConfigValueList} with the given name.
     *
     * @param name The name.
     * @return The {@link ConfigValueList}.
     * @throws IllegalStateException If a {@link ConfigTag} exists, but it is not a {@link ConfigValueList}.
     */
    ConfigValueList getValueList(String name);

    /**
     * Gets the {@link ConfigValueList} with the given name, or null.
     * <p>
     * This will return {@code null} if the {@link ConfigTag} at the name
     * is not a {@link ConfigValueList} or it does not exist.
     *
     * @param name The name.
     * @return The {@link ConfigValueList} or {@code null}
     */
    @Nullable
    ConfigValueList findValueList(String name);

    /**
     * Gets all child tags contained in this Category.
     *
     * @return All the child tags.
     */
    Collection<ConfigTag> getChildren();

    /**
     * Deletes the tag with the given name.
     * <p>
     * If the tag does not exist, this method does nothing.
     *
     * @param name The name of the tag to delete.
     * @return The same config tag.
     */
    ConfigCategory delete(String name);

    /**
     * Removes all children from this tag.
     */
    void clear();

    @Override
    ConfigCategory setComment(List<String> comment);
}
