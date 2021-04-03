package codechicken.lib.util;

import codechicken.lib.vec.Vector3;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Created by covers1624 on 6/30/2016.
 */
public class ItemUtils {

    public static boolean isPlayerHolding(LivingEntity entity, Predicate<Item> predicate) {
        for (Hand hand : Hand.values()) {
            ItemStack stack = entity.getItemInHand(hand);
            if (!stack.isEmpty()) {
                if (predicate.test(stack.getItem())) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isPlayerHoldingSomething(PlayerEntity player) {
        return !player.getMainHandItem().isEmpty() || !player.getOffhandItem().isEmpty();
    }

    @Nonnull
    public static ItemStack getHeldStack(PlayerEntity player) {
        ItemStack stack = player.getMainHandItem();
        if (stack.isEmpty()) {
            stack = player.getOffhandItem();
        }
        return stack;
    }

    /**
     * Drops an item with basic default random velocity.
     */
    public static void dropItem(ItemStack stack, World world, Vector3 dropLocation) {
        ItemEntity item = new ItemEntity(world, dropLocation.x, dropLocation.y, dropLocation.z, stack);
        item.setDeltaMovement(world.random.nextGaussian() * 0.05, world.random.nextGaussian() * 0.05 + 0.2F, world.random.nextGaussian() * 0.05);
        world.addFreshEntity(item);
    }

    /**
     * Drops an item in the world at the given BlockPos
     *
     * @param world    World to drop the item.
     * @param pos      Location to drop item.
     * @param stack    ItemStack to drop.
     * @param velocity The velocity to add.
     */
    public static void dropItem(World world, BlockPos pos, @Nonnull ItemStack stack, double velocity) {
        double xVelocity = world.random.nextFloat() * velocity + (1.0D - velocity) * 0.5D;
        double yVelocity = world.random.nextFloat() * velocity + (1.0D - velocity) * 0.5D;
        double zVelocity = world.random.nextFloat() * velocity + (1.0D - velocity) * 0.5D;
        ItemEntity entityItem = new ItemEntity(world, pos.getX() + xVelocity, pos.getY() + yVelocity, pos.getZ() + zVelocity, stack);
        entityItem.setPickUpDelay(10);
        world.addFreshEntity(entityItem);
    }

    /**
     * Drops an item in the world at the given BlockPos
     *
     * @param world World to drop the item.
     * @param pos   Location to drop item.
     * @param stack ItemStack to drop.
     */
    public static void dropItem(World world, BlockPos pos, @Nonnull ItemStack stack) {
        dropItem(world, pos, stack, 0.7D);
    }

    /**
     * Drops all the items in an IInventory on the ground.
     *
     * @param world     World to drop the item.
     * @param pos       Position to drop item.
     * @param inventory IInventory to drop.
     */
    public static void dropInventory(World world, BlockPos pos, IInventory inventory) {
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (!stack.isEmpty() && stack.getCount() > 0) {
                dropItem(world, pos, stack);
            }
        }
    }

    /**
     * Copy's an ItemStack.
     *
     * @param stack    Stack to copy.
     * @param quantity Size of the new stack.
     * @return The new stack.
     */
    public static ItemStack copyStack(@Nonnull ItemStack stack, int quantity) {
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        }

        stack = stack.copy();
        stack.setCount(quantity);
        return stack;
    }

    /**
     * Ejects an item with .3 velocity in the given direction.
     *
     * @param world World to spawn the item.
     * @param pos   Location for item to spawn.
     * @param stack Stack to spawn.
     * @param dir   Direction to shoot.
     */
    public static void ejectItem(World world, BlockPos pos, @Nonnull ItemStack stack, Direction dir) {
        pos.relative(dir);
        ItemEntity entity = new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, stack);
        entity.setDeltaMovement(Vector3d.atLowerCornerOf(dir.getNormal()).scale(0.3));
        entity.setPickUpDelay(10);
        world.addFreshEntity(entity);
    }

    /**
     * Ejects an list of items with .3 velocity in the given direction.
     *
     * @param world  World to spawn the item.
     * @param pos    Location for item to spawn.
     * @param stacks Stack to spawn.
     * @param dir    Direction to shoot.
     */
    public static void ejectItems(World world, BlockPos pos, List<ItemStack> stacks, Direction dir) {
        for (ItemStack stack : stacks) {
            ejectItem(world, pos, stack, dir);
        }
    }

    /**
     * Gets the burn time for a given ItemStack.
     * Will return 0 if there is no burn time on the item.
     *
     * @param itemStack Stack to get Burn time on.
     * @return Burn time for the Stack.
     */
    @Deprecated
    public static int getBurnTime(@Nonnull ItemStack itemStack) {
        return ForgeHooks.getBurnTime(itemStack);
    }

    /**
     * Compares an ItemStack, Useful for comparators.
     *
     * @param stack1 First Stack.
     * @param stack2 Second Stack.
     * @return Returns the difference.
     */
    public static int compareItemStack(@Nonnull ItemStack stack1, @Nonnull ItemStack stack2) {
        int itemStack1ID = Item.getId(stack1.getItem());
        int itemStack2ID = Item.getId(stack1.getItem());
        return itemStack1ID != itemStack2ID ? itemStack1ID - itemStack2ID : (stack1.getDamageValue() == stack2.getDamageValue() ? 0 : stack1.getDamageValue() - stack2.getDamageValue());
    }

    /**
     * @param stack1 The {@link ItemStack} being compared.
     * @param stack2 The {@link ItemStack} to compare to.
     * @return whether the two items are the same in terms of damage and itemID.
     */
    public static boolean areStacksSameType(@Nonnull ItemStack stack1, @Nonnull ItemStack stack2) {
        return !stack1.isEmpty() && !stack2.isEmpty() && (stack1.getItem() == stack2.getItem() && (stack2.getDamageValue() == stack1.getDamageValue()) && ItemStack.tagMatches(stack2, stack1));
    }

    public static boolean tagsMatch(Item item1, Item item2) {
        Set<ResourceLocation> tag1 = item1.getTags();
        Set<ResourceLocation> tag2 = item2.getTags();
        return tag1.stream().anyMatch(tag2::contains);
    }

    public static boolean areStacksSameOrTagged(ItemStack stack1, ItemStack stack2) {
        return areStacksSameType(stack1, stack2) || tagsMatch(stack1.getItem(), stack2.getItem());
    }
}
