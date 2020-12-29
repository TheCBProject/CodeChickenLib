package codechicken.lib.util;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.Tags.IOptionalNamedTag;

/**
 * Created by covers1624 on 13/12/20.
 */
public class CCLTags {

    public static class Blocks {
        public static IOptionalNamedTag<Block> WOOL = forge("wool");
        public static IOptionalNamedTag<Block> WOOL_WHITE = forge("wool/white");
        public static IOptionalNamedTag<Block> WOOL_ORANGE = forge("wool/orange");
        public static IOptionalNamedTag<Block> WOOL_MAGENTA = forge("wool/magenta");
        public static IOptionalNamedTag<Block> WOOL_LIGHT_BLUE = forge("wool/light_blue");
        public static IOptionalNamedTag<Block> WOOL_YELLOW = forge("wool/yellow");
        public static IOptionalNamedTag<Block> WOOL_LIME = forge("wool/lime");
        public static IOptionalNamedTag<Block> WOOL_PINK = forge("wool/pink");
        public static IOptionalNamedTag<Block> WOOL_GRAY = forge("wool/gray");
        public static IOptionalNamedTag<Block> WOOL_LIGHT_GRAY = forge("wool/light_gray");
        public static IOptionalNamedTag<Block> WOOL_CYAN = forge("wool/cyan");
        public static IOptionalNamedTag<Block> WOOL_PURPLE = forge("wool/purple");
        public static IOptionalNamedTag<Block> WOOL_BLUE = forge("wool/blue");
        public static IOptionalNamedTag<Block> WOOL_BROWN = forge("wool/brown");
        public static IOptionalNamedTag<Block> WOOL_GREEN = forge("wool/green");
        public static IOptionalNamedTag<Block> WOOL_RED = forge("wool/red");
        public static IOptionalNamedTag<Block> WOOL_BLACK = forge("wool/black");

        private static IOptionalNamedTag<Block> forge(String path) {
            return BlockTags.createOptional(new ResourceLocation("forge", path));
        }
    }

    public static class Items {
        public static IOptionalNamedTag<Item> WOOL = forge("wool");
        public static IOptionalNamedTag<Item> WOOL_WHITE = forge("wool/white");
        public static IOptionalNamedTag<Item> WOOL_ORANGE = forge("wool/orange");
        public static IOptionalNamedTag<Item> WOOL_MAGENTA = forge("wool/magenta");
        public static IOptionalNamedTag<Item> WOOL_LIGHT_BLUE = forge("wool/light_blue");
        public static IOptionalNamedTag<Item> WOOL_YELLOW = forge("wool/yellow");
        public static IOptionalNamedTag<Item> WOOL_LIME = forge("wool/lime");
        public static IOptionalNamedTag<Item> WOOL_PINK = forge("wool/pink");
        public static IOptionalNamedTag<Item> WOOL_GRAY = forge("wool/gray");
        public static IOptionalNamedTag<Item> WOOL_LIGHT_GRAY = forge("wool/light_gray");
        public static IOptionalNamedTag<Item> WOOL_CYAN = forge("wool/cyan");
        public static IOptionalNamedTag<Item> WOOL_PURPLE = forge("wool/purple");
        public static IOptionalNamedTag<Item> WOOL_BLUE = forge("wool/blue");
        public static IOptionalNamedTag<Item> WOOL_BROWN = forge("wool/brown");
        public static IOptionalNamedTag<Item> WOOL_GREEN = forge("wool/green");
        public static IOptionalNamedTag<Item> WOOL_RED = forge("wool/red");
        public static IOptionalNamedTag<Item> WOOL_BLACK = forge("wool/black");

        private static IOptionalNamedTag<Item> forge(String path) {
            return ItemTags.createOptional(new ResourceLocation("forge", path));
        }
    }
}
