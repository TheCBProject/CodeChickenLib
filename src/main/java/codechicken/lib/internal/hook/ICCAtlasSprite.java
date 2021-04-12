package codechicken.lib.internal.hook;

import codechicken.lib.texture.IProceduralTextureCallback;

import javax.annotation.Nullable;

/**
 * @author KitsuneAlex
 * @since 12/04/2021
 */
public interface ICCAtlasSprite {

    void setProceduralCallback(@Nullable IProceduralTextureCallback callback);

    @Nullable
    IProceduralTextureCallback getProceduralCallback();

}
