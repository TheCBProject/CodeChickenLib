package codechicken.lib.fluid;

import codechicken.lib.inventory.InventoryUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class FluidUtils {
    public static int B = Fluid.BUCKET_VOLUME;
    public static FluidStack water = new FluidStack(FluidRegistry.WATER, 1000);
    public static FluidStack lava = new FluidStack(FluidRegistry.LAVA, 1000);

    @Deprecated//FluidUtil
    public static boolean fillTankWithContainer(IFluidHandler tank, EntityPlayer player) {
        ItemStack stack = player.getHeldItem(EnumHand.MAIN_HAND);
        FluidStack liquid = FluidUtil.getFluidContained(stack);

        if (liquid == null) {
            return false;
        }

        if (tank.fill(liquid, false) != liquid.amount && !player.capabilities.isCreativeMode) {
            return false;
        }

        tank.fill(liquid, true);

        if (!player.capabilities.isCreativeMode) {
            InventoryUtils.consumeItem(player.inventory, player.inventory.currentItem);
        }

        player.inventoryContainer.detectAndSendChanges();
        return true;
    }

    @Deprecated//FluidUtil
    public static boolean emptyTankIntoContainer(IFluidHandler tank, EntityPlayer player, FluidStack tankLiquid) {
        /*ItemStack stack = player.getHeldItem(EnumHand.MAIN_HAND);

        if (!FluidContainerRegistry.isEmptyContainer(stack)) {
            return false;
        }

        ItemStack filled = FluidContainerRegistry.fillFluidContainer(tankLiquid, stack);
        FluidStack liquid = FluidContainerRegistry.getFluidForFilledItem(filled);

        if (liquid == null || filled == null) {
            return false;
        }

        tank.drain(liquid.amount, true);

        if (!player.capabilities.isCreativeMode) {
            if (stack.stackSize == 1) {
                player.inventory.setInventorySlotContents(player.inventory.currentItem, filled);
            } else if (player.inventory.addItemStackToInventory(filled)) {
                stack.stackSize--;
            } else {
                return false;
            }
        }

        player.inventoryContainer.detectAndSendChanges();
        return true;*/
        return false;
    }

    public static FluidStack copy(FluidStack liquid, int quantity) {
        liquid = liquid.copy();
        liquid.amount = quantity;
        return liquid;
    }

    public static FluidStack read(NBTTagCompound tag) {
        FluidStack stack = FluidStack.loadFluidStackFromNBT(tag);
        return stack != null ? stack : new FluidStack(new Fluid("none", null, null), 0);
    }

    public static NBTTagCompound write(FluidStack fluid, NBTTagCompound tag) {
        return fluid == null || fluid.getFluid() == null ? new NBTTagCompound() : fluid.writeToNBT(new NBTTagCompound());
    }

    public static int getLuminosity(FluidStack stack, double density) {
        Fluid fluid = stack.getFluid();
        if (fluid == null) {
            return 0;
        }
        int light = fluid.getLuminosity(stack);
        if (fluid.isGaseous()) {
            light = (int) (light * density);
        }
        return light;
    }

    public static FluidStack emptyFluid() {
        return new FluidStack(water, 0);
    }
}
