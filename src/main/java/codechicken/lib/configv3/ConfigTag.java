package codechicken.lib.configv3;

import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Created by covers1624 on 17/4/22.
 */
public interface ConfigTag {

    /**
     * Gets the name of this tag.
     *
     * @return The name.
     */
    String getName();

    /**
     * Gets a descriptive name for this tag.
     * {@code some.tag.name}
     *
     * @return The descriptive name.
     */
    default String getDesc() {
        return (getParent() != null ? getParent().getDesc() + "." : "") + getName();
    }

    /**
     * Get the parent tag.
     * <p>
     * This will be {@code null} for the root tag.
     *
     * @return The parent or {@code null}.
     */
    @Nullable
    ConfigCategory getParent();

    /**
     * Sets the comment for this tag.<br/>
     * Will be split on <code>\n</code>.
     *
     * @param comment The comment line.
     * @return The same config tag.
     */
    ConfigTag setComment(String comment);

    /**
     * Sets the comment for this tag.
     *
     * @param comment The comment lines.
     * @return The same config tag.
     */
    ConfigTag setComment(String... comment);

    /**
     * Sets the comment for this tag.
     *
     * @param comment The comment lines.
     * @return The same config tag.
     */
    ConfigTag setComment(List<String> comment);

    /**
     * Gets the comment for the tag.
     *
     * @return The comment.
     */
    List<String> getComment();

    /**
     * Manually trigger all registered {@link ConfigCallback}'s in the tree.
     *
     * @see ConfigValueList#onSync(ConfigCallback)
     * @see ConfigCategory#onSync(ConfigCallback)
     * @see ConfigValue#onSync(ConfigCallback)
     */
    void forceSync();

    /**
     * Saves the config to disk if any values have changed.
     * <p>
     * This does not save a specific {@link ConfigTag} this
     * gets propagated all the way up the tree to the root
     * {@link ConfigTag} and saves the entire config, if any
     * branches are dirty.
     */
    default void save() {
        if (getParent() != null) {
            getParent().save();
        }
    }

    /**
     * If this {@link ConfigTag} or any of its children
     * are dirty and requires flushing to disk.
     *
     * @return If the branch is dirty.
     */
    boolean isDirty();

    /**
     * Delete this tag from the parent.
     */
    default void delete() {
        if (getParent() != null) {
            getParent().delete(getName());
        }
    }

    /**
     * Reset this tag back to the default.
     * <p>
     * In the event that this is a {@link ConfigCategory} sets all
     * descendant values to their default.
     */
    void reset();

    /**
     * Creates a deep clone of this {@link ConfigTag}.
     * <p>
     * If this is called on a child tag, it will split from its parent.
     * <p>
     * Does not keep callbacks.
     *
     * @return The deep copied tag.
     */
    ConfigTag copy();
}
