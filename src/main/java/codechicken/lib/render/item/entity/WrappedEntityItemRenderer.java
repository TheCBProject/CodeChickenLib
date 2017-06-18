package codechicken.lib.render.item.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderEntityItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;

/**
 * Wrapper for {@link IEntityItemPreRenderCallback}
 * Created by covers1624 on 14/04/2017.
 */
public class WrappedEntityItemRenderer extends RenderEntityItem {

    private Render<EntityItem> wrapped;

    public WrappedEntityItemRenderer(RenderManager renderManagerIn, Render<EntityItem> wrapped) {
        super(renderManagerIn, Minecraft.getMinecraft().getRenderItem());
        this.wrapped = wrapped;
    }

    @Override
    public void doRender(EntityItem entity, double x, double y, double z, float entityYaw, float partialTicks) {
        ItemStack stack = entity.getItem();
        RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();
        IBakedModel model = renderItem.getItemModelMesher().getItemModel(stack);
        if (model instanceof IEntityItemPreRenderCallback) {
            ((IEntityItemPreRenderCallback) model).preEntityRender(entity);
        }
        wrapped.doRender(entity, x, y, z, entityYaw, partialTicks);
    }
}
