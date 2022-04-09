package codechicken.lib.internal.mixin;

import codechicken.lib.render.shader.CCShaderInstance;
import com.mojang.blaze3d.shaders.Program;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.io.IOException;

import static net.covers1624.quack.util.SneakyUtils.unsafeCast;

/**
 * Created by covers1624 on 9/4/22.
 */
@Mixin (ShaderInstance.class)
public abstract class ShaderInstanceMixin {

    private ShaderInstance self() {
        return unsafeCast(this);
    }

    @Redirect (
            method = "<init>(Lnet/minecraft/server/packs/resources/ResourceProvider;Lnet/minecraft/resources/ResourceLocation;Lcom/mojang/blaze3d/vertex/VertexFormat;)V",
            at = @At (
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/ShaderInstance;getOrCreate(Lnet/minecraft/server/packs/resources/ResourceProvider;Lcom/mojang/blaze3d/shaders/Program$Type;Ljava/lang/String;)Lcom/mojang/blaze3d/shaders/Program;",
                    ordinal = 0
            )
    )
    private Program onGetOrCreate(ResourceProvider provider, Program.Type type, String s) throws IOException {
        if (self() instanceof CCShaderInstance instance) {
            return instance.compileProgram(provider, type, new ResourceLocation(s));
        }

        return callGetOrCreate(provider, type, s);
    }

    @Invoker
    public abstract Program callGetOrCreate(ResourceProvider provider, Program.Type type, String s);
}
