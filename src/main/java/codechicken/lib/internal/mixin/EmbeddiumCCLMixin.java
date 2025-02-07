package codechicken.lib.internal.mixin;

import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import org.embeddedt.embeddium.compat.ccl.CCLCompat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

/**
 * Created by covers1624 on 2/7/25.
 */
@Mixin (CCLCompat.class)
public class EmbeddiumCCLMixin {

    /**
     * @author covers1624
     * @reason Its broken and CCL ships its own.
     */
    @Overwrite
    public static void onClientSetup(FMLClientSetupEvent event) {
    }
}
