package codechicken.lib.render.buffer;

import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

/**
 * Created by covers1624 on 4/23/20.
 */
public interface ISpriteAwareVertexBuilder extends IVertexBuilder {

    void sprite(TextureAtlasSprite sprite);

}
