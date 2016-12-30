package codechicken.lib.model.blockbakery;

import codechicken.lib.block.property.unlisted.UnlistedMapProperty;

/**
 * Created by covers1624 on 26/12/2016.
 */
public class BlockBakeryProperties {
    public static final UnlistedMapProperty LAYER_FACE_SPRITE_MAP;

    static {
        LAYER_FACE_SPRITE_MAP = new UnlistedMapProperty("layer_face_sprite");
    }
}
