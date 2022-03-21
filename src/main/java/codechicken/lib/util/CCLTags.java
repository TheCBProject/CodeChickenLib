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

        public static TagKey<Block> WOOL = forge("wool");
        public static TagKey<Block> WOOL_WHITE = forge("wool/white");
        public static TagKey<Block> WOOL_ORANGE = forge("wool/orange");
        public static TagKey<Block> WOOL_MAGENTA = forge("wool/magenta");
        public static TagKey<Block> WOOL_LIGHT_BLUE = forge("wool/light_blue");
        public static TagKey<Block> WOOL_YELLOW = forge("wool/yellow");
        public static TagKey<Block> WOOL_LIME = forge("wool/lime");
        public static TagKey<Block> WOOL_PINK = forge("wool/pink");
        public static TagKey<Block> WOOL_GRAY = forge("wool/gray");
        public static TagKey<Block> WOOL_LIGHT_GRAY = forge("wool/light_gray");
        public static TagKey<Block> WOOL_CYAN = forge("wool/cyan");
        public static TagKey<Block> WOOL_PURPLE = forge("wool/purple");
        public static TagKey<Block> WOOL_BLUE = forge("wool/blue");
        public static TagKey<Block> WOOL_BROWN = forge("wool/brown");
        public static TagKey<Block> WOOL_GREEN = forge("wool/green");
        public static TagKey<Block> WOOL_RED = forge("wool/red");
        public static TagKey<Block> WOOL_BLACK = forge("wool/black");

        private static TagKey<Block> forge(String path) {
            return BlockTags.create(new ResourceLocation("forge", path));
        }
    }

    public static class Items {

        public static TagKey<Item> WOOL = forge("wool");
        public static TagKey<Item> WOOL_WHITE = forge("wool/white");
        public static TagKey<Item> WOOL_ORANGE = forge("wool/orange");
        public static TagKey<Item> WOOL_MAGENTA = forge("wool/magenta");
        public static TagKey<Item> WOOL_LIGHT_BLUE = forge("wool/light_blue");
        public static TagKey<Item> WOOL_YELLOW = forge("wool/yellow");
        public static TagKey<Item> WOOL_LIME = forge("wool/lime");
        public static TagKey<Item> WOOL_PINK = forge("wool/pink");
        public static TagKey<Item> WOOL_GRAY = forge("wool/gray");
        public static TagKey<Item> WOOL_LIGHT_GRAY = forge("wool/light_gray");
        public static TagKey<Item> WOOL_CYAN = forge("wool/cyan");
        public static TagKey<Item> WOOL_PURPLE = forge("wool/purple");
        public static TagKey<Item> WOOL_BLUE = forge("wool/blue");
        public static TagKey<Item> WOOL_BROWN = forge("wool/brown");
        public static TagKey<Item> WOOL_GREEN = forge("wool/green");
        public static TagKey<Item> WOOL_RED = forge("wool/red");
        public static TagKey<Item> WOOL_BLACK = forge("wool/black");

        private static TagKey<Item> forge(String path) {
            return ItemTags.create(new ResourceLocation("forge", path));
        }
    }
}
