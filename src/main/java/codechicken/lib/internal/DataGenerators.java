package codechicken.lib.internal;

import codechicken.lib.datagen.LanguageProvider;
import codechicken.lib.util.CCLTags;
import net.covers1624.quack.util.CrashLock;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

import static codechicken.lib.CodeChickenLib.MOD_ID;

/**
 * Created by covers1624 on 13/12/20.
 */
public class DataGenerators {

    private static final CrashLock LOCK = new CrashLock("Already Initialized");

    public static void init(IEventBus modBus) {
        LOCK.lock();
        modBus.addListener(DataGenerators::gatherDataGenerators);
    }

    private static void gatherDataGenerators(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();
        PackOutput output = gen.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        ExistingFileHelper files = event.getExistingFileHelper();
        BlockTags blockTagsProvider = new BlockTags(output, lookupProvider, files);
        gen.addProvider(event.includeServer(), blockTagsProvider);
        gen.addProvider(event.includeServer(), new ItemTags(output, lookupProvider, blockTagsProvider.contentsGetter(), files));
        gen.addProvider(event.includeClient() || event.includeServer(), new LangUS(output, LanguageProvider.getDist(event)));
    }

    private static class BlockTags extends BlockTagsProvider {

        public BlockTags(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper files) {
            super(output, lookupProvider, MOD_ID, files);
        }

        @Override
        protected void addTags(HolderLookup.Provider pProvider) {
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

        public ItemTags(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagLookup<Block>> blockTagProvider, ExistingFileHelper files) {
            super(output, lookupProvider, blockTagProvider, MOD_ID, files);
        }

        @Override
        protected void addTags(HolderLookup.Provider pProvider) {
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

    public static class LangUS extends LanguageProvider {

        public LangUS(PackOutput output, Side side) {
            super(output, MOD_ID, "en_us", side);
        }

        @Override
        protected void addTranslations() {
            addServer("ccl.commands.gc.before", "Before:");
            addServer("ccl.commands.gc.performing", "Performing GC..");
            addServer("ccl.commands.gc.after", "After:");
            addServer("ccl.commands.killall.fail", "Found no entities.");
            addServer("ccl.commands.killall.fail.player", "You cannot kill players with this command.");
            addServer("ccl.commands.killall.success", "Killed %s entities.");
            addServer("ccl.commands.killall.success.line", "Killed %s");
            addServer("ccl.commands.count.fail", "Found no entities.");
            addServer("ccl.commands.count.total", "Found %s entities.");

            addServer("ccl.energy_bar.energy_storage", "Energy Storage");
            addServer("ccl.energy_bar.capacity", "Capacity:");
            addServer("ccl.energy_bar.stored", "Stored:");
            addServer("ccl.energy_bar.rf", "RF");
            addServer("ccl.fluid_tank.fluid_storage", "Fluid Storage");
            addServer("ccl.fluid_tank.capacity", "Capacity:");
            addServer("ccl.fluid_tank.stored", "Stored:");
            addServer("ccl.fluid_tank.mb", "mB");
            addServer("ccl.fluid_tank.contains", "Contains:");
        }
    }
}
