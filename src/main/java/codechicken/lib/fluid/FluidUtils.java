package codechicken.lib.fluid;

import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class FluidUtils {

    @CapabilityInject (IFluidHandler.class)
    public static final Capability<IFluidHandler> FLUID_HANDLER = null;

    public static int B = FluidAttributes.BUCKET_VOLUME;
    public static FluidStack water = new FluidStack(Fluids.WATER, 1000);
    public static FluidStack lava = new FluidStack(Fluids.LAVA, 1000);

    public static int getLuminosity(FluidStack stack, double density) {
        if (stack.isEmpty()) {
            return 0;
        }
        Fluid fluid = stack.getFluid();
        FluidAttributes attributes = fluid.getAttributes();
        int light = attributes.getLuminosity(stack);
        if (attributes.isGaseous()) {
            light = (int) (light * density);
        }
        return light;
    }
}
