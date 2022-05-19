package codechicken.lib.config;

import codechicken.lib.data.MCDataInput;
import codechicken.lib.data.MCDataOutput;
import net.minecraft.resources.ResourceLocation;
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
     * Sets this tag as requiring syncing to the client.
     * <p>
     * Register your root tag via {@link ConfigSyncManager#registerSync(ResourceLocation, ConfigTag)}.
     */
    ConfigTag syncTagToClient();

    /**
     * Checks if this tag or any of its children require client sync.
     *
     * @return If client sync is required.
     */
    boolean requiresClientSync();

    /**
     * Manually trigger all registered {@link ConfigCallback}'s in the tree.
     *
     * @see ConfigValueList#onSync(ConfigCallback)
     * @see ConfigCategory#onSync(ConfigCallback)
     * @see ConfigValue#onSync(ConfigCallback)
     */
    default void forceSync() {
        runSync(ConfigCallback.Reason.MANUAL);
    }

    /**
     * Triggers all registered {@link ConfigCallback}'s in the tree with
     * the specified reason.
     * <p>
     * This method should probably not be called by anyone else.
     *
     * @param reason The reason.
     */
    void runSync(ConfigCallback.Reason reason);

    /**
     * Checks if this tags is a synthetic network tag.
     * <p>
     * Synthetic network tags will exist on the client
     * when some form of dynamic config structure is synced to
     * the client.
     *
     * @return If the tag is synthetic.
     */
    boolean isNetworkTag();

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

    /**
     * Write this tag to a {@link MCDataOutput}.
     * Only categories and/or tags which have {@link #syncTagToClient()} set
     * will be written.
     *
     * @param out The output stream.
     */
    void write(MCDataOutput out);

    /**
     * Read this tag from a {@link MCDataInput}.
     * All tags and values will be read and inserted into the tree.
     * <p>
     * If a tag does not already exist in the tree, one will be added and marked
     * as a 'network tag', {@link #isNetworkTag()}.
     * <p>
     * All tags with a network value will be reset to default when {@link #resetFromNetwork()}
     * is called, whilst any 'network only' tags will be deleted.
     *
     * @param in The input stream.
     */
    void read(MCDataInput in);

    /**
     * Resets all network tags back to their original value.
     * <p>
     * Whilst a tag is set from the network, it is effectively immutable and may not be set.
     * <p>
     * Any tags which are 'network only' are deleted during this operation.
     */
    void resetFromNetwork();
}
