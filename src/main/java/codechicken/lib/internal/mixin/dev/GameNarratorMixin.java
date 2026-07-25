package codechicken.lib.internal.mixin.dev;

import com.mojang.text2speech.Narrator;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Created by covers1624 on 13/11/23.
 */
@Mixin (GameNarrator.class)
abstract class GameNarratorMixin {

    @Redirect (
            method = "Lnet/minecraft/client/GameNarrator;<init>(Lnet/minecraft/client/Minecraft;)V",
            at = @At (
                    value = "INVOKE",
                    target = "Lcom/mojang/text2speech/Narrator;getNarrator()Lcom/mojang/text2speech/Narrator;"
            )
    )
    private Narrator redirectGetNarator(Minecraft mc) {
        // GO AWAY NARRATOR ERRORS!
        return Narrator.EMPTY;
    }
}
