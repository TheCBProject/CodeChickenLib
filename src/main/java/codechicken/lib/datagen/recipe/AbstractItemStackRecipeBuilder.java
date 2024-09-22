package codechicken.lib.datagen.recipe;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;

/**
 * Created by covers1624 on 28/12/20.
 */
public abstract class AbstractItemStackRecipeBuilder<T extends AbstractRecipeBuilder<ItemStack, T>> extends AbstractRecipeBuilder<ItemStack, T> {

    protected AbstractItemStackRecipeBuilder(ResourceLocation id, ItemStack result) {
        super(id, result);
    }

    @Override
    public abstract Recipe<?> _build();
}
