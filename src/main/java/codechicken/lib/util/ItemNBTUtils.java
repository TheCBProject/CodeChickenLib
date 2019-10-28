package codechicken.lib.util;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;

import javax.annotation.Nonnull;
import java.util.UUID;

/**
 * Created by covers1624 on 6/30/2016.
 */
public class ItemNBTUtils {

    /**
     * Checks if an ItemStack has an NBTTag.
     *
     * @param stack Stack to check.
     * @return If tag is in existence.
     */
    public static boolean hasTag(@Nonnull ItemStack stack) {
        return stack.hasTag();
    }

    /**
     * Sets the tag on an ItemStack.
     *
     * @param stack       Stack to set tag on.
     * @param tagCompound Tag to set on item.
     */
    public static void setTag(@Nonnull ItemStack stack, CompoundNBT tagCompound) {
        stack.setTag(tagCompound);
    }

    /**
     * Gets the NBTTag associated with an ItemStack.
     *
     * @param stack Stack to get tag from.
     * @return Tag from the ItemStack.
     */
    public static CompoundNBT getTag(@Nonnull ItemStack stack) {
        return stack.getTag();
    }

    /**
     * Checks if an NBTTag exists on an item and if not it will create a new one.
     *
     * @param stack Stack to check.
     * @return The Tag on the item.
     */
    public static CompoundNBT validateTagExists(@Nonnull ItemStack stack) {
        if (!hasTag(stack)) {
            setTag(stack, new CompoundNBT());
        }
        return getTag(stack);
    }

    /**
     * Checks if an ItemStack has an NBTTag on an ItemStack with the specified Key and Value.
     *
     * @param stack Stack to check.
     * @param key   Key to check for.
     * @return False if key does not exist or if the tag does not exist.
     */
    public static boolean hasKey(@Nonnull ItemStack stack, String key) {
        return hasTag(stack) && getTag(stack).contains(key);
    }

    /**
     * Checks if an ItemStack has an NBTTag of a specific type and name.
     *
     * @param stack   Stack to check.
     * @param key     Key to check for.
     * @param nbtType Primitive NBT Type.
     * @return False if key does not exist or if the tag does not exist.
     */
    public static boolean hasKey(@Nonnull ItemStack stack, String key, int nbtType) {
        return hasTag(stack) && getTag(stack).contains(key, nbtType);
    }

    /**
     * Removes a key from the ItemStacks NBTTag.
     *
     * @param stack Stack to edit.
     * @param key   Key to remove.
     */
    public static void removeTag(@Nonnull ItemStack stack, String key) {
        if (hasTag(stack)) {
            getTag(stack).remove(key);
        }
    }

    //region Setters

    /**
     * Sets a byte on an ItemStack with the specified Key and Value.
     *
     * @param stack Stack to put.
     * @param key   Key.
     * @param b     Value.
     */
    public static void putByte(@Nonnull ItemStack stack, String key, byte b) {
        validateTagExists(stack);
        getTag(stack).putByte(key, b);
    }

    /**
     * Sets a short on an ItemStack with the specified Key and Value.
     *
     * @param stack Stack to put.
     * @param key   Key.
     * @param s     Value.
     */
    public static void putShort(@Nonnull ItemStack stack, String key, short s) {
        validateTagExists(stack);
        getTag(stack).putShort(key, s);
    }

    /**
     * Sets a int on an ItemStack with the specified Key and Value.
     *
     * @param stack Stack to put.
     * @param key   Key.
     * @param i     Value.
     */
    public static void putInt(@Nonnull ItemStack stack, String key, int i) {
        validateTagExists(stack);
        getTag(stack).putInt(key, i);
    }

    /**
     * Sets a long on an ItemStack with the specified Key and Value.
     *
     * @param stack Stack to put.
     * @param key   Key.
     * @param l     Value.
     */
    public static void putLong(@Nonnull ItemStack stack, String key, long l) {
        validateTagExists(stack);
        getTag(stack).putLong(key, l);
    }

    /**
     * Sets a UUID on an ItemStack with the specified Key and Value.
     *
     * @param stack Stack to put.
     * @param key   Key.
     * @param uuid  Value.
     */
    public static void putUUID(@Nonnull ItemStack stack, String key, UUID uuid) {
        validateTagExists(stack);
        getTag(stack).putUniqueId(key, uuid);
    }

    /**
     * Sets a float on an ItemStack with the specified Key and Value.
     *
     * @param stack Stack to put.
     * @param key   Key.
     * @param f     Value.
     */
    public static void putFloat(@Nonnull ItemStack stack, String key, float f) {
        validateTagExists(stack);
        getTag(stack).putFloat(key, f);
    }

    /**
     * Sets a double on an ItemStack with the specified Key and Value.
     *
     * @param stack Stack to put.
     * @param key   Key.
     * @param d     Value.
     */
    public static void putDouble(@Nonnull ItemStack stack, String key, double d) {
        validateTagExists(stack);
        getTag(stack).putDouble(key, d);
    }

    /**
     * Sets a String on an ItemStack with the specified Key and Value.
     *
     * @param stack Stack to put.
     * @param key   Key.
     * @param s     Value.
     */
    public static void putString(@Nonnull ItemStack stack, String key, String s) {
        validateTagExists(stack);
        getTag(stack).putString(key, s);
    }

    /**
     * Sets a byte array on an ItemStack with the specified Key and Value.
     *
     * @param stack Stack to put.
     * @param key   Key.
     * @param b     Value.
     */
    public static void putByteArray(@Nonnull ItemStack stack, String key, byte[] b) {
        validateTagExists(stack);
        getTag(stack).putByteArray(key, b);
    }

    /**
     * Sets a int array on an ItemStack with the specified Key and Value.
     *
     * @param stack Stack to put.
     * @param key   Key.
     * @param i     Value.
     */
    public static void putIntArray(@Nonnull ItemStack stack, String key, int[] i) {
        validateTagExists(stack);
        getTag(stack).putIntArray(key, i);
    }

    /**
     * Sets a boolean on an ItemStack with the specified Key and Value.
     *
     * @param stack Stack to put.
     * @param key   Key.
     * @param b     Value.
     */
    public static void putBoolean(@Nonnull ItemStack stack, String key, boolean b) {
        validateTagExists(stack);
        getTag(stack).putBoolean(key, b);
    }

    //endregion

    //region Getters

    /**
     * Gets a byte from an ItemStacks NBTTag.
     *
     * @param stack Stack key exists on.
     * @param key   Key for the value.
     * @return Value.
     */
    public static byte getByte(@Nonnull ItemStack stack, String key) {
        validateTagExists(stack);
        return getTag(stack).getByte(key);
    }

    /**
     * Gets a short from an ItemStacks NBTTag.
     *
     * @param stack Stack key exists on.
     * @param key   Key for the value.
     * @return Value.
     */
    public static short getShort(@Nonnull ItemStack stack, String key) {
        validateTagExists(stack);
        return getTag(stack).getShort(key);
    }

    /**
     * Gets a int from an ItemStacks NBTTag.
     *
     * @param stack Stack key exists on.
     * @param key   Key for the value.
     * @return Value.
     */
    public static int getInt(@Nonnull ItemStack stack, String key) {
        validateTagExists(stack);
        return getTag(stack).getInt(key);
    }

    /**
     * Gets a long from an ItemStacks NBTTag.
     *
     * @param stack Stack key exists on.
     * @param key   Key for the value.
     * @return Value.
     */
    public static long getLong(@Nonnull ItemStack stack, String key) {
        validateTagExists(stack);
        return getTag(stack).getLong(key);
    }

    /**
     * Gets a UUID from an ItemStacks NBTTag.
     *
     * @param stack Stack key exists on.
     * @param key   Key for the value.
     * @return Value.
     */
    public static UUID getUUID(@Nonnull ItemStack stack, String key) {
        validateTagExists(stack);
        return getTag(stack).getUniqueId(key);
    }

    /**
     * Gets a float from an ItemStacks NBTTag.
     *
     * @param stack Stack key exists on.
     * @param key   Key for the value.
     * @return Value.
     */
    public static float getFloat(@Nonnull ItemStack stack, String key) {
        validateTagExists(stack);
        return getTag(stack).getFloat(key);
    }

    /**
     * Gets a double from an ItemStacks NBTTag.
     *
     * @param stack Stack key exists on.
     * @param key   Key for the value.
     * @return Value.
     */
    public static double getDouble(@Nonnull ItemStack stack, String key) {
        validateTagExists(stack);
        return getTag(stack).getDouble(key);
    }

    /**
     * Gets a String from an ItemStacks NBTTag.
     *
     * @param stack Stack key exists on.
     * @param key   Key for the value.
     * @return Value.
     */
    public static String getString(@Nonnull ItemStack stack, String key) {
        validateTagExists(stack);
        return getTag(stack).getString(key);
    }

    /**
     * Gets a byte array from an ItemStacks NBTTag.
     *
     * @param stack Stack key exists on.
     * @param key   Key for the value.
     * @return Value.
     */
    public static byte[] getByteArray(@Nonnull ItemStack stack, String key) {
        validateTagExists(stack);
        return getTag(stack).getByteArray(key);
    }

    /**
     * Gets a int array from an ItemStacks NBTTag.
     *
     * @param stack Stack key exists on.
     * @param key   Key for the value.
     * @return Value.
     */
    public static int[] getIntArray(@Nonnull ItemStack stack, String key) {
        validateTagExists(stack);
        return getTag(stack).getIntArray(key);
    }

    /**
     * Gets a boolean from an ItemStacks NBTTag.
     *
     * @param stack Stack key exists on.
     * @param key   Key for the value.
     * @return Value.
     */
    public static boolean getBoolean(@Nonnull ItemStack stack, String key) {
        validateTagExists(stack);
        return getTag(stack).getBoolean(key);
    }

    /**
     * Gets a NBTTagCompound from an ItemStacks NBTTag.
     *
     * @param stack Stack key exists on.
     * @param key   Key for the value.
     * @return Value.
     */
    public static CompoundNBT getCompoundTag(@Nonnull ItemStack stack, String key) {
        validateTagExists(stack);
        return getTag(stack).getCompound(key);
    }

    /**
     * Gets a NBTTagList from an ItemStacks NBTTag.
     *
     * @param stack Stack key exists on.
     * @param key   Key for the value.
     * @param type  Primitive NBT Type the List should be made up of.
     * @return Value.
     */
    public static ListNBT getList(@Nonnull ItemStack stack, String key, int type) {
        validateTagExists(stack);
        return getTag(stack).getList(key, type);
    }

    //endregion

}
