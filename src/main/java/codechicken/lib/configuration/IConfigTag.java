package codechicken.lib.configuration;

import codechicken.lib.data.MCDataInput;
import codechicken.lib.data.MCDataOutput;
import codechicken.lib.util.ThrowingBiConsumer;

import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

public interface IConfigTag<E extends IConfigTag> extends IConfigValue<E>, ISerializableConfigTag<E> {

    /**
     * If the tag has a parent tag.
     * Basically only a ConfigFile will have no parent.
     *
     * @return IF the tag has a parent.
     */
    boolean hasParent();

    /**
     * Gets the parent tag.
     *
     * @return The parent tag, null if the tag has no parent.
     */
    @Nullable
    E getParent();

    /**
     * A category is defined as a tag that has children.
     * It is invalid for a tag to have children but also have a value.
     *
     * @return If we are a category.
     */
    boolean isCategory();

    /**
     * A value is defined as a tag that has no children and has a value.
     * It is invalid for a tag to have children and a value.
     * A tag with no value and no children is treated as a category.
     *
     * @return If we are a value.
     */
    boolean isValue();

    /**
     * Gets the name of this tag.
     *
     * @return The name.
     */
    String getName();

    /**
     * Returns a name fit for localization based on the tree.
     * //TODO Explain how name is generated.
     *
     * @return The name for localization.
     */
    String getUnlocalizedName();

    /**
     * If this tag is dirty.
     * Tags are marked as dirty when either:
     * Their children are marked as dirty.
     * Or their value is set, via code or GUI.
     *
     * @return If we are dirty.
     */
    boolean isDirty();

    /**
     * Marks the tag as dirty.
     * Calls to save will ONLY write to disk if the tag is dirty.
     *
     * @return The tag.
     */
    E markDirty();

    /**
     * Clears all children from this tag.
     */
    void clear();

    /**
     * Checks to see if this tag has a child with the given name.
     *
     * @param name The name to check.
     * @return If the tag has a child with the given name.
     */
    boolean hasTag(String name);

    /**
     * Gets a tag.
     * Creates one if absent.
     *
     * @param name The name of the tag.
     * @return The tag.
     */
    E getTag(String name);

    /**
     * Gets a tag if one is present.
     *
     * @param name The name of the tag.
     * @return The tag.
     */
    @Nullable
    E getTagIfPresent(String name);

    /**
     * Deletes a child tag if the specified tag exists.
     *
     * @param name The name of the tag to delete.
     * @return This tag.
     */
    E deleteTag(String name);

    /**
     * Returns a list of all child elements for this tag.
     *
     * @return All child elements.
     */
    List<String> getChildNames();

    /**
     * Walks down the tree of tags, the callback will be called
     * for each child category and value.
     *
     * @param consumer The callback.
     */
    void walkTags(Consumer<E> consumer);

    /**
     * Resets the tag to the stored default value.
     * If called on a category, it will reset all children to default.
     *
     * @return This tag.
     */
    E resetToDefault();

    /**
     * Completely arbitrary string settable by the implementor,
     * to define the version of this tag.
     * Can be used for anything.
     *
     * @return The version tag.
     */
    String getTagVersion();

    /**
     * Sets a completely arbitrary version string for this tag.
     * Can be used for anything.
     * This overrides any version set in the tag.
     * It is recommended to read the version when you first get the tag.
     *
     * @param version The new version.
     * @return This tag.
     */
    E setTagVersion(String version);

    /**
     * Returns the type of the tag.
     *
     * @return The type.
     */
    TagType getTagType();

    /**
     * Sets a single line comment above the given tag.
     *
     * @param comment The comment.
     */
    E setComment(String comment);

    /**
     * Sets a MultiLine comment above the given tag.
     *
     * @param lines The lines.
     */
    E setComment(List<String> lines);

    /**
     * Tells the config to save to disk.
     * The tag will ONLY save to disk if it is marked as dirty.
     */
    default void save() {
        if (hasParent()) {
            getParent().save();
        }
    }

    /**
     * Specifies that this config should sync to the client.
     * If you set this, you MUST supply a syncCallback.
     */
    E setSyncToClient();

    /**
     * Sets the callback for a sync event.
     * SyncType should be checked to see if its a manual sync,
     * if so, do all your error checking, otherwise throw SyncExceptions
     * if you encounter an issue syncing.
     *
     * @param consumer The consumer.
     * @return This tag.
     */
    E setSyncCallback(ThrowingBiConsumer<E, SyncType, SyncException> consumer);

    /**
     * This can be used to check if this tag or any of its children
     * require syncing. Its behavior is specific to its type:
     * If its a category, it will ask its child tree if any require syncing
     * and return true if ANY of them want to sync.
     * If its a value, it will return true if marked as requiring syncing.
     *
     * NOTE:
     * Categories will return true even if none of their direct children require syncing,
     * any child in the tree requiring sync will propagate up the tag hierarchy,
     * be sure to ask all children if they need syncing in order to flesh out the sync tree.
     *
     * @return If sync is required.
     */
    boolean requiresSync();

    /**
     * Runs all sync callbacks, in self and children.
     * Use this to run a manual sync on initial load.
     */
    default void runSync() {
        try {
            runSync(SyncType.MANUAL);
        } catch (SyncException e) {
            throw new RuntimeException("Sync exception caught on manual sync.", e);
        }
    }

    /**
     * Called internally to run a sync of a specific type.
     *
     * @param type The sync type.
     * @throws SyncException Can be thrown by a sync callback to specify an unrecoverable issue.
     */
    void runSync(SyncType type) throws SyncException;

    //INTERNAL!!!!!!
    Object getRawValue();

    public static enum TagType {
        BOOLEAN {
            @Override
            public char getChar() {
                return 'B';
            }

            @Override
            public Object copy(Object value) {
                return value;
            }

            @Override
            public Object read(MCDataInput in, TagType listType) {
                return in.readBoolean();
            }

            @Override
            public void write(MCDataOutput out, TagType listType, Object value) {
                out.writeBoolean((Boolean) value);
            }
        },
        STRING {
            @Override
            public char getChar() {
                return 'S';
            }

            @Override
            public Object copy(Object value) {
                return value;
            }

            @Override
            public Object read(MCDataInput in, TagType listType) {
                return in.readString();
            }

            @Override
            public void write(MCDataOutput out, TagType listType, Object value) {
                out.writeString((String) value);
            }
        },
        INT {
            @Override
            public char getChar() {
                return 'I';
            }

            @Override
            public Object copy(Object value) {
                return value;
            }

            @Override
            public Object read(MCDataInput in, TagType listType) {
                return in.readVarInt();
            }

            @Override
            public void write(MCDataOutput out, TagType listType, Object value) {
                out.writeVarInt((Integer) value);
            }
        },
        HEX {
            @Override
            public char getChar() {
                return 'H';
            }

            @Override
            protected String processLine(Object obj) {
                Integer hex = (Integer) obj;
                return "0x" + (Long.toString(((long) hex) << 32 >>> 32, 16)).toUpperCase();
            }

            @Override
            public Object read(MCDataInput in, TagType listType) {
                return INT.read(in, listType);
            }

            @Override
            public void write(MCDataOutput out, TagType listType, Object value) {
                INT.write(out, listType, value);
            }

            @Override
            public Object copy(Object value) {
                return value;
            }
        },
        DOUBLE {
            @Override
            public char getChar() {
                return 'D';
            }

            @Override
            public Object copy(Object value) {
                return value;
            }

            @Override
            public Object read(MCDataInput in, TagType listType) {
                return in.readDouble();
            }

            @Override
            public void write(MCDataOutput out, TagType listType, Object value) {
                out.writeDouble((Double) value);
            }
        },
        LIST {
            @Override
            public char getChar() {
                return '#';//Invalid, this should never be written. So make it a comment.
            }

            @Override
            @SuppressWarnings ("unchecked")
            public Object copy(Object value) {
                return new LinkedList((List) value);
            }

            @Override
            public Object read(MCDataInput in, TagType listType) {
                List list = new LinkedList();
                int num = in.readVarInt();
                for (int i = 0; i < num; i++) {
                    list.add(listType.read(in, listType));
                }
                return list;
            }

            @Override
            public void write(MCDataOutput out, TagType listType, Object value) {
                List list = (List) value;
                out.writeVarInt(list.size());
                for (Object o: list) {
                    listType.write(out, listType, o);
                }
            }
        };

        public abstract char getChar();

        public abstract Object copy(Object value);

        protected String processLine(Object obj) {
            return obj.toString();
        }

        public static TagType fromChar(char c) {
            switch (c) {
                case 'B': {
                    return BOOLEAN;
                }
                case 'S': {
                    return STRING;
                }
                case 'I': {
                    return INT;
                }
                case 'D': {
                    return DOUBLE;
                }
                case 'H': {
                    return HEX;
                }
                default: {
                    return null;
                }
            }
        }

        public abstract Object read(MCDataInput in, TagType listType);

        public abstract void write(MCDataOutput out, TagType listType, Object value);
    }

    /**
     * Used to identify why your sync callback is being called,
     * Manual is only ever fired if you call {@link IConfigTag#runSync}
     * Connect and Disconnect can be used to identify runtime sync and reload,
     * Special actions may need to be taken to rebuild internal data structures.
     * It is recommended to do any initial error checking and resetting to defaults on Manual
     * then just trust the data synced with Connect / Disconnect, if that isn't possible
     * throw a {@link SyncException}
     */
    public static enum SyncType {
        MANUAL,
        CONNECT,
        DISCONNECT
    }

    /**
     * Throw this from a Sync callback to notify the sync pipeline of an unrecoverable issue.
     */
    public static class SyncException extends Exception {

        public SyncException(String reason) {
            super(reason);
        }
    }

}
