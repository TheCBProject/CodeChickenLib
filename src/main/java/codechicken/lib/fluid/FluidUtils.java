package codechicken.lib.fluid;

import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;

public class FluidUtils {

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
