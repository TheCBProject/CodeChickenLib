package codechicken.lib.datagen.recipe;

import net.minecraft.core.HolderGetter;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;

/**
 * Created by covers1624 on 28/12/20.
 */
public abstract class AbstractItemStackRecipeBuilder<T extends AbstractRecipeBuilder<ItemStack, T>> extends AbstractRecipeBuilder<ItemStack, T> {

    protected AbstractItemStackRecipeBuilder(Identifier id, HolderGetter<Item> items, ItemStack result) {
        super(id, items, result);
    }

    @Override
    public abstract Recipe<?> _build();
}
