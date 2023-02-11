package codechicken.lib.block.component;

import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.ApiStatus;

/**
 * Created by covers1624 on 22/7/22.
 */
@ApiStatus.Experimental
public abstract class PropertyComponent<V extends Comparable<V>> extends StateAwareComponent {

    public final Property<V> property;
    public final V defaultValue;

    protected PropertyComponent(Property<V> property, V defaultValue) {
        this.property = property;
        this.defaultValue = defaultValue;
    }
}
