package codechicken.lib.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

/**
 * Created by covers1624 on 13/12/20.
 */
public class CCLTags {

    public static class Blocks {

        public static TagKey<Block> WOOLS = common("wools");
        public static TagKey<Block> WOOLS_WHITE = common("wools/white");
        public static TagKey<Block> WOOLS_ORANGE = common("wools/orange");
        public static TagKey<Block> WOOLS_MAGENTA = common("wools/magenta");
        public static TagKey<Block> WOOLS_LIGHT_BLUE = common("wools/light_blue");
        public static TagKey<Block> WOOLS_YELLOW = common("wools/yellow");
        public static TagKey<Block> WOOLS_LIME = common("wools/lime");
        public static TagKey<Block> WOOLS_PINK = common("wools/pink");
        public static TagKey<Block> WOOLS_GRAY = common("wools/gray");
        public static TagKey<Block> WOOLS_LIGHT_GRAY = common("wools/light_gray");
        public static TagKey<Block> WOOLS_CYAN = common("wools/cyan");
        public static TagKey<Block> WOOLS_PURPLE = common("wools/purple");
        public static TagKey<Block> WOOLS_BLUE = common("wools/blue");
        public static TagKey<Block> WOOLS_BROWN = common("wools/brown");
        public static TagKey<Block> WOOLS_GREEN = common("wools/green");
        public static TagKey<Block> WOOLS_RED = common("wools/red");
        public static TagKey<Block> WOOLS_BLACK = common("wools/black");

        private static TagKey<Block> common(String path) {
            return BlockTags.create(ResourceLocation.fromNamespaceAndPath("c", path));
        }
    }

    public static class Items {

        public static TagKey<Item> WOOLS = common("wools");
        public static TagKey<Item> WOOLS_WHITE = common("wools/white");
        public static TagKey<Item> WOOLS_ORANGE = common("wools/orange");
        public static TagKey<Item> WOOLS_MAGENTA = common("wools/magenta");
        public static TagKey<Item> WOOLS_LIGHT_BLUE = common("wools/light_blue");
        public static TagKey<Item> WOOLS_YELLOW = common("wools/yellow");
        public static TagKey<Item> WOOLS_LIME = common("wools/lime");
        public static TagKey<Item> WOOLS_PINK = common("wools/pink");
        public static TagKey<Item> WOOLS_GRAY = common("wools/gray");
        public static TagKey<Item> WOOLS_LIGHT_GRAY = common("wools/light_gray");
        public static TagKey<Item> WOOLS_CYAN = common("wools/cyan");
        public static TagKey<Item> WOOLS_PURPLE = common("wools/purple");
        public static TagKey<Item> WOOLS_BLUE = common("wools/blue");
        public static TagKey<Item> WOOLS_BROWN = common("wools/brown");
        public static TagKey<Item> WOOLS_GREEN = common("wools/green");
        public static TagKey<Item> WOOLS_RED = common("wools/red");
        public static TagKey<Item> WOOLS_BLACK = common("wools/black");

        private static TagKey<Item> common(String path) {
            return ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", path));
        }
    }
}
