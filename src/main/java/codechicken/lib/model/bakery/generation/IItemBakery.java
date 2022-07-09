package codechicken.lib.model.bakery.generation;

import codechicken.lib.model.bakedmodels.ModelProperties.PerspectiveProperties;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * A bakery capable of generating quads for an {@link ItemStack}.
 * <p>
 * This is used for regular items and {@link BlockItem}s.
 * <p>
 * Created by covers1624 on 12/02/2017.
 */
public interface IItemBakery extends IBakery {

    /**
     * Used to generate quads for your {@link ItemStack}.
     *
     * @param face  The face quads are requested for.
     * @param stack The stack!
     * @return The quads.
     */
    @OnlyIn (Dist.CLIENT)
    List<BakedQuad> bakeItemQuads(@Nullable Direction face, ItemStack stack);

    /**
     * Return the {@link PerspectiveProperties} for the generated model.
     *
     * @return The {@link PerspectiveProperties} to use.
     */
    @OnlyIn (Dist.CLIENT)
    default PerspectiveProperties getModelProperties(ItemStack stack) {
        return stack.getItem() instanceof BlockItem ? PerspectiveProperties.DEFAULT_BLOCK : PerspectiveProperties.DEFAULT_ITEM;
    }
}
