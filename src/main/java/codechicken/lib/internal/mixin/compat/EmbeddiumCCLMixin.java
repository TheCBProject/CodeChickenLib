package codechicken.lib.internal.mixin.compat;

import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import org.embeddedt.embeddium.compat.ccl.CCLCompat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Pseudo;

/**
 * Created by covers1624 on 2/7/25.
 */
@Pseudo
@Mixin (CCLCompat.class)
abstract class EmbeddiumCCLMixin {

    /**
     * @author covers1624
     * @reason Its broken and CCL ships its own.
     */
    @Overwrite
    public static void onClientSetup(FMLClientSetupEvent event) {
    }
}
