package codechicken.lib.render.item.map;

import net.minecraft.world.item.ItemStack;

/**
 * Used to handle rendering FirstPerson items the same as a map.
 * <p>
 * Created by covers1624 on 15/02/2017.
 */
public interface IMapRenderer {

    /**
     * If the IMapRenderer should handle rendering the data from this map.
     *
     * @param stack   The ItemStack to render map data from.
     * @param inFrame If the ItemStack is inside an ItemFrame.
     * @return Should this IMapRenderer handle with this context.
     */
    boolean shouldHandle(ItemStack stack, boolean inFrame);

    /**
     * If this is called you are expected to do something.
     * This is ONLY called if shouldHandle returns true.
     *
     * @param stack   The ItemStack to render the map data from.
     * @param inFrame If the ItemStack is inside an ItemFrame.
     */
    void renderMap(ItemStack stack, boolean inFrame);

}
