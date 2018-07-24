package codechicken.lib.fluid;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.EmptyFluidHandler;

//TODO, this is messy, why all the null checks there is never null fluids.
public class FluidUtils {

    @CapabilityInject (IFluidHandler.class)
    public static final Capability<IFluidHandler> FLUID_HANDLER = null;

    public static int B = Fluid.BUCKET_VOLUME;
    public static FluidStack water = new FluidStack(FluidRegistry.WATER, 1000);
    public static FluidStack lava = new FluidStack(FluidRegistry.LAVA, 1000);

    public static FluidStack copy(FluidStack liquid, int quantity) {
        liquid = liquid.copy();
        liquid.amount = quantity;
        return liquid;
    }

    public static FluidStack read(NBTTagCompound tag) {
        FluidStack stack = FluidStack.loadFluidStackFromNBT(tag);
        return stack != null ? stack : emptyFluid();
    }

    public static NBTTagCompound write(FluidStack fluid, NBTTagCompound tag) {
        return fluid == null || fluid.getFluid() == null ? tag : fluid.writeToNBT(tag);
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

    //region hasFluidHandler

    /**
     * Checks if the Fluid capability exists on the tile for the specified face.
     * Overloaded methods delegate to this in the end.
     *
     * @param tile The tile.
     * @param face THe face.
     * @return If the tile has the Fluid capability.
     */
    public static boolean hasFluidHandler(TileEntity tile, EnumFacing face) {
        return tile != null && tile.hasCapability(FLUID_HANDLER, face);
    }

    public static boolean hasFluidHandler(TileEntity tile, int face) {
        return hasFluidHandler(tile, EnumFacing.VALUES[face]);
    }

    public static boolean hasFluidHandler(IBlockAccess world, BlockPos pos, EnumFacing face) {
        return hasFluidHandler(world.getTileEntity(pos), face);
    }

    public static boolean hasFluidHandler(IBlockAccess world, BlockPos pos, int face) {
        return hasFluidHandler(world.getTileEntity(pos), face);
    }
    //endregion

    //region getFluidHandler_Raw

    /**
     * Grabs the Fluid capability from the tile.
     * Overloaded methods delegate to this in the end.
     *
     * @param tile The tile.
     * @param face The face.
     * @return The Fluid handler capability.
     */
    public static IFluidHandler getFluidHandler_Raw(TileEntity tile, EnumFacing face) {
        return tile.getCapability(FLUID_HANDLER, face);
    }

    public static IFluidHandler getFluidHandler_Raw(TileEntity tile, int face) {
        return getFluidHandler_Raw(tile, EnumFacing.VALUES[face]);
    }

    public static IFluidHandler getFluidHandler_Raw(IBlockAccess world, BlockPos pos, EnumFacing face) {
        return getFluidHandler_Raw(world.getTileEntity(pos), face);
    }

    public static IFluidHandler getFluidHandler_Raw(IBlockAccess world, BlockPos pos, int face) {
        return getFluidHandler_Raw(world.getTileEntity(pos), face);
    }
    //endregion

    //region getFluidHandlerOr

    /**
     * Gets the Fluid capability for the tile or the default if none.
     * This method guards against tiles specify a cap exists and returning null,
     * in that case the default is returned.
     * Overloaded methods delegate to this in the end.
     *
     * @param tile     The tile.
     * @param face     The face.
     * @param _default The default.
     * @return The Fluid handler or default.
     */
    public static IFluidHandler getFluidHandlerOr(TileEntity tile, EnumFacing face, IFluidHandler _default) {
        IFluidHandler handler = hasFluidHandler(tile, face) ? getFluidHandler_Raw(tile, face) : null;
        return handler != null ? handler : _default;
    }

    public static IFluidHandler getFluidHandlerOr(TileEntity tile, int face, IFluidHandler _default) {
        return hasFluidHandler(tile, face) ? getFluidHandler_Raw(tile, face) : _default;
    }

    public static IFluidHandler getFluidHandlerOr(IBlockAccess world, BlockPos pos, EnumFacing face, IFluidHandler _default) {
        return getFluidHandlerOr(world.getTileEntity(pos), face, _default);
    }

    public static IFluidHandler getFluidHandlerOr(IBlockAccess world, BlockPos pos, int face, IFluidHandler _default) {
        return getFluidHandlerOr(world.getTileEntity(pos), face, _default);
    }
    //endregion

    //region getFluidHandler

    /**
     * Gets the Fluid capability for the tile.
     * Returns null if the cap doesn't exist.
     * Overloaded methods delegate to this in the end.
     *
     * @param tile The tile.
     * @param face The face.
     * @return The Fluid capability or null.
     */
    public static IFluidHandler getFluidHandler(TileEntity tile, EnumFacing face) {
        return getFluidHandlerOr(tile, face, null);
    }

    public static IFluidHandler getFluidHandler(TileEntity tile, int face) {
        return getFluidHandlerOr(tile, face, null);
    }

    public static IFluidHandler getFluidHandler(IBlockAccess world, BlockPos pos, EnumFacing face) {
        return getFluidHandlerOr(world, pos, face, null);
    }

    public static IFluidHandler getFluidHandler(IBlockAccess world, BlockPos pos, int face) {
        return getFluidHandlerOr(world, pos, face, null);
    }
    //endregion

    //region getFluidHandlerOrEmpty

    /**
     * Gets the Fluid capability for the tile or an Empty handler.
     * This method guards against tiles specify a cap exists and returning null,
     * in that case the empty handler is returned.
     * Overloaded methods delegate to this in the end.
     *
     * @param tile The tile.
     * @param face The face.
     * @return The Fluid capability or null.
     */
    public static IFluidHandler getFluidHandlerOrEmpty(TileEntity tile, EnumFacing face) {
        return getFluidHandlerOr(tile, face, EmptyFluidHandler.INSTANCE);
    }

    public static IFluidHandler getFluidHandlerOrEmpty(TileEntity tile, int face) {
        return getFluidHandlerOr(tile, face, EmptyFluidHandler.INSTANCE);
    }

    public static IFluidHandler getFluidHandlerOrEmpty(IBlockAccess world, BlockPos pos, EnumFacing face) {
        return getFluidHandlerOr(world.getTileEntity(pos), face, EmptyFluidHandler.INSTANCE);
    }

    public static IFluidHandler getFluidHandlerOrEmpty(IBlockAccess world, BlockPos pos, int face) {
        return getFluidHandlerOr(world.getTileEntity(pos), face, EmptyFluidHandler.INSTANCE);
    }
    //endregion
}
