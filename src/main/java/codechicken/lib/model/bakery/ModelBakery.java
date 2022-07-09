package codechicken.lib.model.bakery;

import codechicken.lib.model.bakedmodels.ModelProperties;
import codechicken.lib.model.bakedmodels.ModelProperties.PerspectiveProperties;
import codechicken.lib.model.bakedmodels.PerspectiveAwareBakedModel;
import codechicken.lib.model.bakedmodels.PerspectiveAwareLayeredModel;
import codechicken.lib.model.bakery.generation.IBlockBakery;
import codechicken.lib.model.bakery.generation.IItemBakery;
import codechicken.lib.model.bakery.key.IBlockStateKeyGenerator;
import codechicken.lib.model.bakery.key.IItemStackKeyGenerator;
import codechicken.lib.util.LogUtils;
import codechicken.lib.util.ResourceUtils;
import codechicken.lib.util.TransformUtils;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ChunkRenderTypeSet;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Created by covers1624 on 25/10/2016.
 */
@OnlyIn (Dist.CLIENT)
public class ModelBakery {

    private static final Logger logger = LogManager.getLogger();

    private static final boolean DEBUG = Boolean.parseBoolean(System.getProperty("ccl.debugBakeryLogging"));
    private static final boolean FORCE_BLOCK_REBAKE = Boolean.parseBoolean(System.getProperty("ccl.debugForceBlockModelRebake"));
    private static final boolean FORCE_ITEM_REBAKE = Boolean.parseBoolean(System.getProperty("ccl.debugForceItemModelRebake"));

    private static final Cache<String, BakedModel> keyModelCache = CacheBuilder.newBuilder().expireAfterAccess(30, TimeUnit.MINUTES).build();

    private static final ModelProperties DEFAULT = ModelProperties.builder()
            .withAO(true)
            .withGui3D(true)
            .build();
    private static final PerspectiveProperties DEFAULT_PERSPECTIVE = DEFAULT.toBuilder()
            .withTransforms(TransformUtils.DEFAULT_BLOCK)
            .build();

    private static final Map<Item, IItemStackKeyGenerator> itemKeyGeneratorMap = Collections.synchronizedMap(new HashMap<>());
    private static final Map<Block, IBlockStateKeyGenerator> blockKeyGeneratorMap = Collections.synchronizedMap(new HashMap<>());
    private static BakedModel missingModel;

    public static final IBlockStateKeyGenerator defaultBlockKeyGenerator = (state, data) -> state.toString();

    public static final IItemStackKeyGenerator defaultItemKeyGenerator = stack -> ForgeRegistries.ITEMS.getKey(stack.getItem()).toString() + "|" + stack.getDamageValue();

    public static void init() {
        ResourceUtils.registerReloadListener(e -> nukeModelCache());
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ModelBakery::onModelBake);
    }

    private static void onModelBake(ModelEvent.BakingCompleted event) {
        missingModel = event.getModelManager().getMissingModel();
    }

    public static IBlockStateKeyGenerator getKeyGenerator(Block block) {
        if (blockKeyGeneratorMap.containsKey(block)) {
            return blockKeyGeneratorMap.get(block);
        }
        return defaultBlockKeyGenerator;
    }

    public static IItemStackKeyGenerator getKeyGenerator(Item item) {
        if (itemKeyGeneratorMap.containsKey(item)) {
            return itemKeyGeneratorMap.get(item);
        }
        return defaultItemKeyGenerator;
    }

    public static void registerBlockKeyGenerator(Block block, IBlockStateKeyGenerator generator) {
        if (blockKeyGeneratorMap.containsKey(block)) {
            throw new IllegalArgumentException("Unable to register IBlockStateKeyGenerator as one is already registered for block:" + ForgeRegistries.BLOCKS.getKey(block));
        }
        blockKeyGeneratorMap.put(block, generator);
    }

    public static void registerItemKeyGenerator(Item item, IItemStackKeyGenerator generator) {
        if (itemKeyGeneratorMap.containsKey(item)) {
            throw new IllegalArgumentException("Unable to register IItemStackKeyGenerator as one is already registered for item: " + ForgeRegistries.ITEMS.getKey(item));
        }
        itemKeyGeneratorMap.put(item, generator);
    }

    public static BakedModel getCachedItemModel(ItemStack stack) {
        BakedModel model;
        IItemStackKeyGenerator generator = getKeyGenerator(stack.getItem());
        String key = generator.generateKey(stack);
        model = keyModelCache.getIfPresent(key);
        if (model == null || FORCE_ITEM_REBAKE) {
            try {
                model = timeModelGeneration(ModelBakery::generateItemModel, stack, "ITEM: " + key);
            } catch (Throwable t) {
                LogUtils.errorOnce(logger, t, "ItemBaking", "Fatal exception thrown whilst baking item model for: " + stack);
                return missingModel;
            }
            if (model != missingModel) {
                keyModelCache.put(key, model);
            }
        }
        return model;

    }

    public static BakedModel generateItemModel(ItemStack stack) {
        Item item = stack.getItem();
        if (item instanceof IBakeryProvider bakeryProvider) {

            IItemBakery bakery = (IItemBakery) bakeryProvider.getBakery();

            List<BakedQuad> unculledQuads = List.copyOf(bakery.bakeItemQuads(null, stack));

            Map<Direction, List<BakedQuad>> faceQuads = new HashMap<>();
            for (Direction face : Direction.BY_3D_DATA) {
                faceQuads.put(face, List.copyOf(bakery.bakeItemQuads(face, stack)));
            }

            PerspectiveProperties properties = bakery.getModelProperties(stack);
            return new PerspectiveAwareBakedModel(faceQuads, unculledQuads, properties);
        }
        return missingModel;
    }

    public static BakedModel getCachedModel(BlockState state, ModelData data) {
        if (state == null) {
            return missingModel;
        }
        BakedModel model;
        IBlockStateKeyGenerator keyGenerator = getKeyGenerator(state.getBlock());
        String key = keyGenerator.generateKey(state, data);
        model = keyModelCache.getIfPresent(key);
        if (model == null || FORCE_BLOCK_REBAKE) {
            try {
                model = timeModelGeneration(ModelBakery::generateModel, state, data, "BLOCK: " + key);
            } catch (Throwable t) {
                LogUtils.errorOnce(logger, t, "BlockBaking", "Fatal exception thrown whilst baking block model for: " + state);
                return missingModel;
            }
            if (model != missingModel) {
                keyModelCache.put(key, model);
            }
        }
        return model;
    }

    public static BakedModel generateModel(BlockState state, ModelData data) {
        if (state.getBlock() instanceof IBakeryProvider bakeryProvider) {
            IBlockBakery bakery = (IBlockBakery) bakeryProvider.getBakery();
            ChunkRenderTypeSet layers = bakery.getBlockRenderLayers();

            Map<RenderType, List<BakedQuad>> unculledQuads = new HashMap<>();
            Map<RenderType, Map<Direction, List<BakedQuad>>> culledQuads = new HashMap<>();
            for (RenderType layer : layers) {
                unculledQuads.put(layer, List.copyOf(bakery.bakeFace(null, layer, state, data)));

                Map<Direction, List<BakedQuad>> faceQuadMap = new HashMap<>();
                for (Direction face : Direction.BY_3D_DATA) {
                    faceQuadMap.put(face, List.copyOf(bakery.bakeFace(face, layer, state, data)));
                }
                culledQuads.put(layer, faceQuadMap);

            }
            return new PerspectiveAwareLayeredModel(culledQuads, unculledQuads, DEFAULT_PERSPECTIVE, RenderType.solid());
        }
        return missingModel;
    }

    private static <T, R> R timeModelGeneration(Function<T, R> func, T thing, String logPostfix) {
        if (DEBUG) {
            logger.info("Baking Model.. Key: {}", logPostfix);
        }
        long start = System.nanoTime();
        R ret = func.apply(thing);
        long end = System.nanoTime();
        logGenTime(start, end);
        return ret;
    }

    private static <T, U, R> R timeModelGeneration(BiFunction<T, U, R> func, T thing, U thing2, String logPostfix) {
        if (DEBUG) {
            logger.info("Baking Model.. Key: {}", logPostfix);
        }
        long start = System.nanoTime();
        R ret = func.apply(thing, thing2);
        long end = System.nanoTime();
        logGenTime(start, end);
        return ret;
    }

    private static void logGenTime(long start, long end) {
        if (DEBUG) {
            long delta = end - start;
            long millis = TimeUnit.NANOSECONDS.toMillis(delta);
            String s;
            if (millis >= 5) {
                s = millis + "ms";
            } else {
                s = delta + "ns";
            }

            logger.info("Baking finished in {}.", s);
        }
    }

    public static void nukeModelCache() {
        keyModelCache.invalidateAll();
    }
}
