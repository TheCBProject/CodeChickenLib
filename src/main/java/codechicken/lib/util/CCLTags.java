package codechicken.lib.util;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;

/**
 * Created by covers1624 on 13/12/20.
 */
public class CCLTags {

    public static class Blocks {
        public static Tag<Block> WOOL = forge("wool");
        public static Tag<Block> WOOL_WHITE = forge("wool/white");
        public static Tag<Block> WOOL_ORANGE = forge("wool/orange");
        public static Tag<Block> WOOL_MAGENTA = forge("wool/magenta");
        public static Tag<Block> WOOL_LIGHT_BLUE = forge("wool/light_blue");
        public static Tag<Block> WOOL_YELLOW = forge("wool/yellow");
        public static Tag<Block> WOOL_LIME = forge("wool/lime");
        public static Tag<Block> WOOL_PINK = forge("wool/pink");
        public static Tag<Block> WOOL_GRAY = forge("wool/gray");
        public static Tag<Block> WOOL_LIGHT_GRAY = forge("wool/light_gray");
        public static Tag<Block> WOOL_CYAN = forge("wool/cyan");
        public static Tag<Block> WOOL_PURPLE = forge("wool/purple");
        public static Tag<Block> WOOL_BLUE = forge("wool/blue");
        public static Tag<Block> WOOL_BROWN = forge("wool/brown");
        public static Tag<Block> WOOL_GREEN = forge("wool/green");
        public static Tag<Block> WOOL_RED = forge("wool/red");
        public static Tag<Block> WOOL_BLACK = forge("wool/black");

        private static Tag<Block> forge(String path) {
            return new BlockTags.Wrapper(new ResourceLocation("forge", path));
        }
    }

    public static class Items {
        public static Tag<Item> WOOL = forge("wool");
        public static Tag<Item> WOOL_WHITE = forge("wool/white");
        public static Tag<Item> WOOL_ORANGE = forge("wool/orange");
        public static Tag<Item> WOOL_MAGENTA = forge("wool/magenta");
        public static Tag<Item> WOOL_LIGHT_BLUE = forge("wool/light_blue");
        public static Tag<Item> WOOL_YELLOW = forge("wool/yellow");
        public static Tag<Item> WOOL_LIME = forge("wool/lime");
        public static Tag<Item> WOOL_PINK = forge("wool/pink");
        public static Tag<Item> WOOL_GRAY = forge("wool/gray");
        public static Tag<Item> WOOL_LIGHT_GRAY = forge("wool/light_gray");
        public static Tag<Item> WOOL_CYAN = forge("wool/cyan");
        public static Tag<Item> WOOL_PURPLE = forge("wool/purple");
        public static Tag<Item> WOOL_BLUE = forge("wool/blue");
        public static Tag<Item> WOOL_BROWN = forge("wool/brown");
        public static Tag<Item> WOOL_GREEN = forge("wool/green");
        public static Tag<Item> WOOL_RED = forge("wool/red");
        public static Tag<Item> WOOL_BLACK = forge("wool/black");

        private static Tag<Item> forge(String path) {
            return new ItemTags.Wrapper(new ResourceLocation("forge", path));
        }
    }
}
