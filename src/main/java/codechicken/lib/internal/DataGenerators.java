package codechicken.lib.internal;

import codechicken.lib.util.CCLTags;
import net.minecraft.block.Blocks;
import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.ItemTagsProvider;
import net.minecraft.item.Items;
import net.minecraft.tags.ItemTags;
import net.minecraftforge.client.model.generators.ExistingFileHelper;
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
            gen.addProvider(new BlockTags(gen));
            gen.addProvider(new ItemTags(gen));
        }
    }

    private static class BlockTags extends BlockTagsProvider {

        public BlockTags(DataGenerator generatorIn) {
            super(generatorIn);
        }

        @Override
        protected void registerTags() {
            getBuilder(CCLTags.Blocks.WOOL)
                    .add(CCLTags.Blocks.WOOL_WHITE)
                    .add(CCLTags.Blocks.WOOL_ORANGE)
                    .add(CCLTags.Blocks.WOOL_MAGENTA)
                    .add(CCLTags.Blocks.WOOL_LIGHT_BLUE)
                    .add(CCLTags.Blocks.WOOL_YELLOW)
                    .add(CCLTags.Blocks.WOOL_LIME)
                    .add(CCLTags.Blocks.WOOL_PINK)
                    .add(CCLTags.Blocks.WOOL_GRAY)
                    .add(CCLTags.Blocks.WOOL_LIGHT_GRAY)
                    .add(CCLTags.Blocks.WOOL_CYAN)
                    .add(CCLTags.Blocks.WOOL_PURPLE)
                    .add(CCLTags.Blocks.WOOL_BLUE)
                    .add(CCLTags.Blocks.WOOL_BROWN)
                    .add(CCLTags.Blocks.WOOL_GREEN)
                    .add(CCLTags.Blocks.WOOL_RED)
                    .add(CCLTags.Blocks.WOOL_BLACK);
            getBuilder(CCLTags.Blocks.WOOL_WHITE).add(Blocks.WHITE_WOOL);
            getBuilder(CCLTags.Blocks.WOOL_ORANGE).add(Blocks.ORANGE_WOOL);
            getBuilder(CCLTags.Blocks.WOOL_MAGENTA).add(Blocks.MAGENTA_WOOL);
            getBuilder(CCLTags.Blocks.WOOL_LIGHT_BLUE).add(Blocks.LIGHT_BLUE_WOOL);
            getBuilder(CCLTags.Blocks.WOOL_YELLOW).add(Blocks.YELLOW_WOOL);
            getBuilder(CCLTags.Blocks.WOOL_LIME).add(Blocks.LIME_WOOL);
            getBuilder(CCLTags.Blocks.WOOL_PINK).add(Blocks.PINK_WOOL);
            getBuilder(CCLTags.Blocks.WOOL_GRAY).add(Blocks.GRAY_WOOL);
            getBuilder(CCLTags.Blocks.WOOL_LIGHT_GRAY).add(Blocks.LIGHT_GRAY_WOOL);
            getBuilder(CCLTags.Blocks.WOOL_CYAN).add(Blocks.CYAN_WOOL);
            getBuilder(CCLTags.Blocks.WOOL_PURPLE).add(Blocks.PURPLE_WOOL);
            getBuilder(CCLTags.Blocks.WOOL_BLUE).add(Blocks.BLUE_WOOL);
            getBuilder(CCLTags.Blocks.WOOL_BROWN).add(Blocks.BROWN_WOOL);
            getBuilder(CCLTags.Blocks.WOOL_GREEN).add(Blocks.GREEN_WOOL);
            getBuilder(CCLTags.Blocks.WOOL_RED).add(Blocks.RED_WOOL);
            getBuilder(CCLTags.Blocks.WOOL_BLACK).add(Blocks.BLACK_WOOL);
        }

        @Override
        public String getName() {
            return "CodeChickenLib Block tags.";
        }
    }

    private static class ItemTags extends ItemTagsProvider {

        public ItemTags(DataGenerator generatorIn) {
            super(generatorIn);
        }

        @Override
        protected void registerTags() {
            getBuilder(CCLTags.Items.WOOL)
                    .add(CCLTags.Items.WOOL_WHITE)
                    .add(CCLTags.Items.WOOL_ORANGE)
                    .add(CCLTags.Items.WOOL_MAGENTA)
                    .add(CCLTags.Items.WOOL_LIGHT_BLUE)
                    .add(CCLTags.Items.WOOL_YELLOW)
                    .add(CCLTags.Items.WOOL_LIME)
                    .add(CCLTags.Items.WOOL_PINK)
                    .add(CCLTags.Items.WOOL_GRAY)
                    .add(CCLTags.Items.WOOL_LIGHT_GRAY)
                    .add(CCLTags.Items.WOOL_CYAN)
                    .add(CCLTags.Items.WOOL_PURPLE)
                    .add(CCLTags.Items.WOOL_BLUE)
                    .add(CCLTags.Items.WOOL_BROWN)
                    .add(CCLTags.Items.WOOL_GREEN)
                    .add(CCLTags.Items.WOOL_RED)
                    .add(CCLTags.Items.WOOL_BLACK);
            getBuilder(CCLTags.Items.WOOL_WHITE).add(Items.WHITE_WOOL);
            getBuilder(CCLTags.Items.WOOL_ORANGE).add(Items.ORANGE_WOOL);
            getBuilder(CCLTags.Items.WOOL_MAGENTA).add(Items.MAGENTA_WOOL);
            getBuilder(CCLTags.Items.WOOL_LIGHT_BLUE).add(Items.LIGHT_BLUE_WOOL);
            getBuilder(CCLTags.Items.WOOL_YELLOW).add(Items.YELLOW_WOOL);
            getBuilder(CCLTags.Items.WOOL_LIME).add(Items.LIME_WOOL);
            getBuilder(CCLTags.Items.WOOL_PINK).add(Items.PINK_WOOL);
            getBuilder(CCLTags.Items.WOOL_GRAY).add(Items.GRAY_WOOL);
            getBuilder(CCLTags.Items.WOOL_LIGHT_GRAY).add(Items.LIGHT_GRAY_WOOL);
            getBuilder(CCLTags.Items.WOOL_CYAN).add(Items.CYAN_WOOL);
            getBuilder(CCLTags.Items.WOOL_PURPLE).add(Items.PURPLE_WOOL);
            getBuilder(CCLTags.Items.WOOL_BLUE).add(Items.BLUE_WOOL);
            getBuilder(CCLTags.Items.WOOL_BROWN).add(Items.BROWN_WOOL);
            getBuilder(CCLTags.Items.WOOL_GREEN).add(Items.GREEN_WOOL);
            getBuilder(CCLTags.Items.WOOL_RED).add(Items.RED_WOOL);
            getBuilder(CCLTags.Items.WOOL_BLACK).add(Items.BLACK_WOOL);
        }

        @Override
        public String getName() {
            return "CodeChickenLib Item tags.";
        }
    }
}
