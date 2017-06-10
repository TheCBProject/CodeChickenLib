package codechicken.lib.render.item.entity;

import net.minecraft.entity.item.EntityItem;

/**
 * Interface for IBakedModels to receive a pre callback for EntityItem rendering.
 */
public interface IEntityItemPreRenderCallback {

    /**
     * Called before the item is rendered.
     * Use this to setup additional stuff based on the entity.
     *
     * @param item The EntityItem.
     */
    void preEntityRender(EntityItem item);
}
