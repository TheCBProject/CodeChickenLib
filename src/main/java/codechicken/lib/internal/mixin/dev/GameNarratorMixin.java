package codechicken.lib.internal.mixin.dev;

import com.mojang.text2speech.Narrator;
import net.minecraft.client.GameNarrator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Created by covers1624 on 13/11/23.
 */
@Mixin (GameNarrator.class)
abstract class GameNarratorMixin {

    @Redirect (
            method = "<init>",
            at = @At (
                    value = "INVOKE",
                    target = "Lcom/mojang/text2speech/Narrator;getNarrator()Lcom/mojang/text2speech/Narrator;"
            )
    )
    private Narrator redirectGetNarator() {
        // GO AWAY NARRATOR ERRORS!
        return Narrator.EMPTY;
    }
}
