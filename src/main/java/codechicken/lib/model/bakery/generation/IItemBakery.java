package codechicken.lib.model.bakery.generation;

import codechicken.lib.model.bakedmodels.ModelProperties.PerspectiveProperties;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by covers1624 on 12/02/2017.
 * This IS used for ItemBlocks and Items. For ItemBlock implementation, Implement IBakeryProvider on your block and return an instance of this class.
 */
public interface IItemBakery extends IBakery {

    /**
     * Used to actually generate quads for your ItemStack based on the face being requested.
     * <p>
     * Face may be null!
     * Treat a null face as "general" quads, Item Rendering doesn't have any sense of "faces" this is more so a fall over
     * of blocks having face quads. It is fine to have all your quads in the "general" face, but Recommended against for debugging.
     *
     * @param face  The face quads are requested for.
     * @param stack The stack!
     * @return The quads for the layer, May be an empty list. Never null.
     */
    @Nonnull
    @OnlyIn (Dist.CLIENT)
    List<BakedQuad> bakeItemQuads(@Nullable Direction face, ItemStack stack);

    /**
     * Using this allows you to change the way your model appears. You are able to override Gui3d and such.
     * Including Transforms.
     *
     * @return The properties to use for the model.
     */
    @OnlyIn (Dist.CLIENT)
    default PerspectiveProperties getModelProperties(ItemStack stack) {
        return stack.getItem() instanceof BlockItem ? PerspectiveProperties.DEFAULT_BLOCK : PerspectiveProperties.DEFAULT_ITEM;
    }
}
