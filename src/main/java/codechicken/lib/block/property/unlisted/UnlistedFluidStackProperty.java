package codechicken.lib.block.property.unlisted;

import net.minecraftforge.fluids.FluidStack;

/**
 * Created by covers1624 on 30/10/2016.
 */
public class UnlistedFluidStackProperty extends UnlistedPropertyBase<FluidStack>{

    public UnlistedFluidStackProperty(String name) {
        super(name);
    }

    @Override
    public Class<FluidStack> getType() {
        return FluidStack.class;
    }

    @Override
    public String valueToString(FluidStack value) {
        return value.toString();
    }
}
