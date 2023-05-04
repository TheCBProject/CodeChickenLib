package codechicken.lib.internal.mixin;

import codechicken.lib.internal.ItemFileRenderer;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Created by covers1624 on 27/2/23.
 */
@Mixin (Minecraft.class)
abstract class MinecraftMixin {

    @Inject (
            method = "runTick",
            at = @At (
                    value = "TAIL"
            )
    )
    private void onRunTick(boolean p_91384_, CallbackInfo ci) {
        ItemFileRenderer.tick();
    }
}
