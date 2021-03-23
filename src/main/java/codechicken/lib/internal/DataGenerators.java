package codechicken.lib.internal;

import codechicken.lib.util.CCLTags;
import net.minecraft.block.Blocks;
import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.ItemTagsProvider;
import net.minecraft.item.Items;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;

import static codechicken.lib.CodeChickenLib.MOD_ID;

/**
 * Created by covers1624 on 13/12/20.
 */
@Mod.EventBusSubscriber (modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators {

    @SubscribeEvent
    public static void gatherDataGenerators(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();
        ExistingFileHelper files = event.getExistingFileHelper();
        if (event.includeServer()) {
            BlockTags blockTagsProvider = new BlockTags(gen, files);
            gen.addProvider(blockTagsProvider);
            gen.addProvider(new ItemTags(gen, blockTagsProvider, files));
        }
    }

    private static class BlockTags extends BlockTagsProvider {

        public BlockTags(DataGenerator generatorIn, ExistingFileHelper files) {
            super(generatorIn, MOD_ID, files);
        }

        @Override
        protected void addTags() {
            tag(CCLTags.Blocks.WOOL)
                    .addTag(CCLTags.Blocks.WOOL_WHITE)
                    .addTag(CCLTags.Blocks.WOOL_ORANGE)
                    .addTag(CCLTags.Blocks.WOOL_MAGENTA)
                    .addTag(CCLTags.Blocks.WOOL_LIGHT_BLUE)
                    .addTag(CCLTags.Blocks.WOOL_YELLOW)
                    .addTag(CCLTags.Blocks.WOOL_LIME)
                    .addTag(CCLTags.Blocks.WOOL_PINK)
                    .addTag(CCLTags.Blocks.WOOL_GRAY)
                    .addTag(CCLTags.Blocks.WOOL_LIGHT_GRAY)
                    .addTag(CCLTags.Blocks.WOOL_CYAN)
                    .addTag(CCLTags.Blocks.WOOL_PURPLE)
                    .addTag(CCLTags.Blocks.WOOL_BLUE)
                    .addTag(CCLTags.Blocks.WOOL_BROWN)
                    .addTag(CCLTags.Blocks.WOOL_GREEN)
                    .addTag(CCLTags.Blocks.WOOL_RED)
                    .addTag(CCLTags.Blocks.WOOL_BLACK);
            tag(CCLTags.Blocks.WOOL_WHITE).add(Blocks.WHITE_WOOL);
            tag(CCLTags.Blocks.WOOL_ORANGE).add(Blocks.ORANGE_WOOL);
            tag(CCLTags.Blocks.WOOL_MAGENTA).add(Blocks.MAGENTA_WOOL);
            tag(CCLTags.Blocks.WOOL_LIGHT_BLUE).add(Blocks.LIGHT_BLUE_WOOL);
            tag(CCLTags.Blocks.WOOL_YELLOW).add(Blocks.YELLOW_WOOL);
            tag(CCLTags.Blocks.WOOL_LIME).add(Blocks.LIME_WOOL);
            tag(CCLTags.Blocks.WOOL_PINK).add(Blocks.PINK_WOOL);
            tag(CCLTags.Blocks.WOOL_GRAY).add(Blocks.GRAY_WOOL);
            tag(CCLTags.Blocks.WOOL_LIGHT_GRAY).add(Blocks.LIGHT_GRAY_WOOL);
            tag(CCLTags.Blocks.WOOL_CYAN).add(Blocks.CYAN_WOOL);
            tag(CCLTags.Blocks.WOOL_PURPLE).add(Blocks.PURPLE_WOOL);
            tag(CCLTags.Blocks.WOOL_BLUE).add(Blocks.BLUE_WOOL);
            tag(CCLTags.Blocks.WOOL_BROWN).add(Blocks.BROWN_WOOL);
            tag(CCLTags.Blocks.WOOL_GREEN).add(Blocks.GREEN_WOOL);
            tag(CCLTags.Blocks.WOOL_RED).add(Blocks.RED_WOOL);
            tag(CCLTags.Blocks.WOOL_BLACK).add(Blocks.BLACK_WOOL);
        }

        @Override
        public String getName() {
            return "CodeChickenLib Block tags.";
        }
    }

    private static class ItemTags extends ItemTagsProvider {

        public ItemTags(DataGenerator generatorIn, BlockTags blockTagProvider, ExistingFileHelper files) {
            super(generatorIn, blockTagProvider, MOD_ID, files);
        }

        @Override
        protected void addTags() {
            tag(CCLTags.Items.WOOL)
                    .addTag(CCLTags.Items.WOOL_WHITE)
                    .addTag(CCLTags.Items.WOOL_ORANGE)
                    .addTag(CCLTags.Items.WOOL_MAGENTA)
                    .addTag(CCLTags.Items.WOOL_LIGHT_BLUE)
                    .addTag(CCLTags.Items.WOOL_YELLOW)
                    .addTag(CCLTags.Items.WOOL_LIME)
                    .addTag(CCLTags.Items.WOOL_PINK)
                    .addTag(CCLTags.Items.WOOL_GRAY)
                    .addTag(CCLTags.Items.WOOL_LIGHT_GRAY)
                    .addTag(CCLTags.Items.WOOL_CYAN)
                    .addTag(CCLTags.Items.WOOL_PURPLE)
                    .addTag(CCLTags.Items.WOOL_BLUE)
                    .addTag(CCLTags.Items.WOOL_BROWN)
                    .addTag(CCLTags.Items.WOOL_GREEN)
                    .addTag(CCLTags.Items.WOOL_RED)
                    .addTag(CCLTags.Items.WOOL_BLACK);
            tag(CCLTags.Items.WOOL_WHITE).add(Items.WHITE_WOOL);
            tag(CCLTags.Items.WOOL_ORANGE).add(Items.ORANGE_WOOL);
            tag(CCLTags.Items.WOOL_MAGENTA).add(Items.MAGENTA_WOOL);
            tag(CCLTags.Items.WOOL_LIGHT_BLUE).add(Items.LIGHT_BLUE_WOOL);
            tag(CCLTags.Items.WOOL_YELLOW).add(Items.YELLOW_WOOL);
            tag(CCLTags.Items.WOOL_LIME).add(Items.LIME_WOOL);
            tag(CCLTags.Items.WOOL_PINK).add(Items.PINK_WOOL);
            tag(CCLTags.Items.WOOL_GRAY).add(Items.GRAY_WOOL);
            tag(CCLTags.Items.WOOL_LIGHT_GRAY).add(Items.LIGHT_GRAY_WOOL);
            tag(CCLTags.Items.WOOL_CYAN).add(Items.CYAN_WOOL);
            tag(CCLTags.Items.WOOL_PURPLE).add(Items.PURPLE_WOOL);
            tag(CCLTags.Items.WOOL_BLUE).add(Items.BLUE_WOOL);
            tag(CCLTags.Items.WOOL_BROWN).add(Items.BROWN_WOOL);
            tag(CCLTags.Items.WOOL_GREEN).add(Items.GREEN_WOOL);
            tag(CCLTags.Items.WOOL_RED).add(Items.RED_WOOL);
            tag(CCLTags.Items.WOOL_BLACK).add(Items.BLACK_WOOL);
        }

        @Override
        public String getName() {
            return "CodeChickenLib Item tags.";
        }
    }
}
