package codechicken.lib.internal.mixin;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Credit to jozufozu and Flywheel for this mixin <3
 * <p>
 * Created by covers1624 on 1/1/22.
 */
@Mixin (LevelRenderer.class)
public abstract class LevelRendererMixin {

    @Inject (
            method = "renderLevel",
            at = @At (
                    value = "INVOKE",
                    ordinal = 1,
                    target = "Lnet/minecraft/client/renderer/PostChain;process(F)V"
            )
    )
    private void disableTransparencyShaderDepth(PoseStack p1, float p2, long p3, boolean p4, Camera p5, GameRenderer p6, LightTexture p7, Matrix4f p8, CallbackInfo ci) {
        GlStateManager._depthMask(false);
    }
}
