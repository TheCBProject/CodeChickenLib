package codechicken.lib.internal.mixin;

import codechicken.lib.internal.hook.ICCAtlasSprite;
import codechicken.lib.texture.IProceduralTextureCallback;
import codechicken.lib.texture.IProceduralTextureCallback.ITextureAccessor;
import codechicken.lib.texture.TextureUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.AtlasTexture.SheetData;
import net.minecraft.client.renderer.texture.MipmapGenerator;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.tuple.Pair;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;

/**
 * Mixin which implements efficient caching and ticking for {@link codechicken.lib.texture.IProceduralTextureCallback}.
 *
 * @author KitsuneAlex
 * @since 12/04/2021
 */
@Mixin(AtlasTexture.class)
public class AtlasTextureMixin {

    //@formatter:off
    @Shadow @Final private Map<ResourceLocation, TextureAtlasSprite> texturesByName;
    //@formatter:on

    private final HashSet<Pair<TextureAtlasSprite, ITextureAccessor>> proceduralTextures = new HashSet<>();
    private final Random random = new Random(new Random().nextLong());

    /**
     * Called when the texture atlas is reloaded.
     *
     * @param data The sheet data for the current reload.
     * @param cbi The callback info for this injection.
     */
    @Inject(method = "reload", at = @At("TAIL"))
    private void onReload(@Nonnull SheetData data, @Nonnull CallbackInfo cbi) {
        proceduralTextures.clear();

        for(final TextureAtlasSprite texture : texturesByName.values()) {
            if(((ICCAtlasSprite)texture).getProceduralCallback() != null) {
                proceduralTextures.add(Pair.of(texture, TextureUtils.createAccessor(texture)));
            }
        }
    }

    /**
     * Called when the texture atlas is ticked.
     *
     * @param cbi The callback info for this injection.
     */
    @SuppressWarnings("all")
    @Inject(method = "cycleAnimationFrames", at = @At("TAIL"))
    private void onCycleAnimationFrames(@Nonnull CallbackInfo cbi) {
        if(proceduralTextures.isEmpty()) {
            return;
        }

        final int levels = Minecraft.getInstance().options.mipmapLevels;
        final boolean hasLevels = levels > 0;

        for(final Pair<TextureAtlasSprite, ITextureAccessor> pair : proceduralTextures) {
            final TextureAtlasSprite texture = pair.getLeft();
            final ITextureAccessor accessor = pair.getRight();
            final IProceduralTextureCallback callback = ((ICCAtlasSprite)texture).getProceduralCallback();

            if(!callback.shouldUpdate(random)) {
                continue;
            }

            callback.updateTexture(accessor, random);

            if(hasLevels) { // Re-compute the mipmaps for this texture if needed
                final NativeImage[] mippedTexture = MipmapGenerator.generateMipLevels(texture.mainImage[0], levels);
                System.arraycopy(mippedTexture, 0, texture.mainImage, 0, mippedTexture.length);
            }

            texture.uploadFirstFrame();
        }
    }

}
