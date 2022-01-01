package codechicken.lib.internal.mixin;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.util.math.vector.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Credit to jozufozu and Flywheel for this mixin <3
 * <p>
 * Created by covers1624 on 1/1/22.
 */
@Mixin (WorldRenderer.class)
public abstract class WorldRendererMixin {

    @Inject (
            method = "renderLevel",
            at = @At (
                    value = "INVOKE",
                    ordinal = 1,
                    target = "Lnet/minecraft/client/shader/ShaderGroup;process(F)V"
            )
    )
    private void disableTransparencyShaderDepth(MatrixStack p1, float p2, long p3, boolean p4, ActiveRenderInfo p5, GameRenderer p6, LightTexture p7, Matrix4f p8, CallbackInfo ci) {
        GlStateManager._depthMask(false);
    }
}
