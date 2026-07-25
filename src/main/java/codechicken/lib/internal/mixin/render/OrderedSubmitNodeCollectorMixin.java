package codechicken.lib.internal.mixin.render;

import codechicken.lib.render.extension.CCOrderedSubmitNodeCollectorExtension;
import net.minecraft.client.renderer.OrderedSubmitNodeCollector;
import org.spongepowered.asm.mixin.Mixin;

/**
 * Created by covers1624 on 11/2/25.
 */
@Mixin (OrderedSubmitNodeCollector.class)
public interface OrderedSubmitNodeCollectorMixin extends CCOrderedSubmitNodeCollectorExtension {
}
