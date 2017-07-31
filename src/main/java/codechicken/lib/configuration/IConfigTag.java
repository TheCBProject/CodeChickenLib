package codechicken.lib.configuration;

import javax.annotation.Nullable;
import java.util.List;

@Deprecated
public interface IConfigTag extends IConfigValue {

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
    IConfigTag getParent();

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
    IConfigTag markDirty();

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
    IConfigTag getTag(String name);

    /**
     * Gets a tag if one is present.
     *
     * @param name The name of the tag.
     * @return The tag.
     */
    @Nullable
    IConfigTag getTagIfPresent(String name);

    /**
     * Deletes a child tag if the specified tag exists.
     *
     * @param name The name of the tag to delete.
     * @return This tag.
     */
    IConfigTag deleteTag(String name);

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
    IConfigTag setTagVersion(String version);

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
    IConfigTag setComment(String comment);

    /**
     * Sets a MultiLine comment above the given tag.
     *
     * @param lines The lines.
     */
    IConfigTag setComment(List<String> lines);

    /**
     * Tells the config to save to disk.
     * The tag will ONLY save to disk if it is marked as dirty.
     */
    default void save() {
        if (hasParent()) {
            getParent().save();
        }
    }

    //INTERNAL!!!!!!
    Object getRawValue();

    public static enum TagType {
        BOOLEAN {
            @Override
            public char getChar() {
                return 'B';
            }
        },
        STRING {
            @Override
            public char getChar() {
                return 'S';
            }
        },
        INT {
            @Override
            public char getChar() {
                return 'I';
            }
        },
        HEX {
            @Override
            public char getChar() {
                return 'H';
            }

            @Override
            protected String processLine(Object obj) {
                if (obj instanceof String) {
                    String line = (String) obj;
                    return "0x" + (line.substring(2).toUpperCase());
                }
                Integer hex = (Integer) obj;
                return "0x" + (Long.toString(((long) hex) << 32 >>> 32, 16)).toUpperCase();
            }
        },
        DOUBLE {
            @Override
            public char getChar() {
                return 'D';
            }
        },
        LIST {
            @Override
            public char getChar() {
                return '#';//Invalid, this should never be written. So make it a comment.
            }
        };

        public abstract char getChar();

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
    }

}
