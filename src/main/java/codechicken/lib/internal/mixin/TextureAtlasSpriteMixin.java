package codechicken.lib.internal.mixin;

import codechicken.lib.internal.hook.ICCAtlasSprite;
import codechicken.lib.texture.IProceduralTextureCallback;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import org.spongepowered.asm.mixin.Mixin;

import javax.annotation.Nullable;

/**
 * Interface mixin which embeds a getter and setter for an {@link IProceduralTextureCallback} instance.
 *
 * @author KitsuneAlex
 * @since 12/04/2021
 */
@Mixin(TextureAtlasSprite.class)
public abstract class TextureAtlasSpriteMixin implements ICCAtlasSprite {

    private IProceduralTextureCallback proceduralCallback;

    @Override
    public void setProceduralCallback(@Nullable IProceduralTextureCallback callback) {
        proceduralCallback = callback;
    }

    @Nullable
    @Override
    public IProceduralTextureCallback getProceduralCallback() {
        return proceduralCallback;
    }

}
