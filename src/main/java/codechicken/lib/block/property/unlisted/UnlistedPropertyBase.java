package codechicken.lib.block.property.unlisted;

import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fluids.FluidStack;

/**
 * Created by covers1624 on 30/10/2016.
 */
public abstract class UnlistedPropertyBase<V> implements IUnlistedProperty<V> {

    protected final String name;

    public UnlistedPropertyBase(String name){
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isValid(V value) {
        return true;
    }
}
