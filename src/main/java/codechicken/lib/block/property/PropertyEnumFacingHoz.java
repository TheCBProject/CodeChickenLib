package codechicken.lib.block.property;

import com.google.common.collect.Lists;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.util.EnumFacing;

import javax.annotation.Nonnull;
import java.util.Collection;

/**
 * Created by covers1624 on 3/27/2016.
 */
public class PropertyEnumFacingHoz extends PropertyDirection {

    protected PropertyEnumFacingHoz(String name, Collection<EnumFacing> values) {
        super(name, values);
    }

    @Nonnull
    public static PropertyEnumFacingHoz create(@Nonnull String name) {
        return new PropertyEnumFacingHoz(name, Lists.newArrayList(EnumFacing.HORIZONTALS));
    }

}
