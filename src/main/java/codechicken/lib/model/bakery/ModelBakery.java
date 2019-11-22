package codechicken.lib.model.bakery;

import codechicken.lib.internal.CCLLog;
import codechicken.lib.internal.proxy.ProxyClient;
import codechicken.lib.model.ModelRegistryHelper;
import codechicken.lib.model.bakedmodels.ModelProperties;
import codechicken.lib.model.bakedmodels.ModelProperties.PerspectiveProperties;
import codechicken.lib.model.bakedmodels.PerspectiveAwareBakedModel;
import codechicken.lib.model.bakedmodels.PerspectiveAwareLayeredModel;
import codechicken.lib.model.bakery.generation.IBlockBakery;
import codechicken.lib.model.bakery.generation.IItemBakery;
import codechicken.lib.model.bakery.generation.ILayeredBlockBakery;
import codechicken.lib.model.bakery.generation.ISimpleBlockBakery;
import codechicken.lib.model.bakery.key.IBlockStateKeyGenerator;
import codechicken.lib.model.bakery.key.IItemStackKeyGenerator;
import codechicken.lib.render.buffer.BakingVertexBuffer;
import codechicken.lib.texture.TextureUtils;
import codechicken.lib.util.ResourceUtils;
import codechicken.lib.util.TransformUtils;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.resource.VanillaResourceType;
import org.apache.logging.log4j.Level;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Created by covers1624 on 25/10/2016.
 */
@OnlyIn (Dist.CLIENT)
public class ModelBakery {

    private static boolean DEBUG = Boolean.parseBoolean(System.getProperty("ccl.debugBakeryLogging"));
    private static boolean FORCE_BLOCK_REBAKE = Boolean.parseBoolean(System.getProperty("ccl.debugForceBlockModelRebake"));
    private static boolean FORCE_ITEM_REBAKE = Boolean.parseBoolean(System.getProperty("ccl.debugForceItemModelRebake"));

    private static Cache<String, IBakedModel> keyModelCache = CacheBuilder.newBuilder().expireAfterAccess(30, TimeUnit.MINUTES).build();

    private static Map<Item, IItemStackKeyGenerator> itemKeyGeneratorMap = Collections.synchronizedMap(new HashMap<>());
    private static Map<Block, IBlockStateKeyGenerator> blockKeyGeneratorMap = Collections.synchronizedMap(new HashMap<>());
    private static IBakedModel missingModel;

    public static final IBlockStateKeyGenerator defaultBlockKeyGenerator = (state, data) -> state.toString();

    public static final IItemStackKeyGenerator defaultItemKeyGenerator = stack -> stack.getItem().getRegistryName().toString() + "|" + stack.getDamage();

    public static void init() {
        ResourceUtils.registerReloadListener((resourceManager, p) -> {
            if (p.test(VanillaResourceType.MODELS)) {
                nukeModelCache();
            }
        });
        ProxyClient.modelHelper.registerCallback(event -> missingModel = ModelLoaderRegistry.getMissingModel().bake(event.getModelLoader(), TextureUtils::getTexture, TransformUtils.DEFAULT_BLOCK, DefaultVertexFormats.ITEM));
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
            throw new IllegalArgumentException("Unable to register IBlockStateKeyGenerator as one is already registered for block:" + block.getRegistryName());
        }
        blockKeyGeneratorMap.put(block, generator);
    }

    public static void registerItemKeyGenerator(Item item, IItemStackKeyGenerator generator) {
        if (itemKeyGeneratorMap.containsKey(item)) {
            throw new IllegalArgumentException("Unable to register IItemStackKeyGenerator as one is already registered for item: " + item.getRegistryName());
        }
        itemKeyGeneratorMap.put(item, generator);
    }

    //    @SuppressWarnings ("deprecation")
    //    public static IBlockState handleExtendedState(IExtendedBlockState state, IBlockAccess world, BlockPos pos) {
    //        Block block = state.getBlock();
    //
    //        if (block instanceof IBakeryProvider) {
    //            IBakery bakery = ((IBakeryProvider) block).getBakery();
    //            if (bakery instanceof IBlockBakery) {
    //                return ((IBlockBakery) bakery).handleState(state, world, pos);
    //            }
    //            throw new IllegalStateException("ModelBakery.handleExtendedState called for block that implements IBakeryProvider but does not return a IBlockBakery in IBakeryProvider.getBakery()!");
    //        }
    //        return state;
    //    }

    public static IBakedModel getCachedItemModel(ItemStack stack) {
        IBakedModel model;
        IItemStackKeyGenerator generator = getKeyGenerator(stack.getItem());
        String key = generator.generateKey(stack);
        model = keyModelCache.getIfPresent(key);
        if (model == null || FORCE_ITEM_REBAKE) {
            try {
                model = timeModelGeneration(ModelBakery::generateItemModel, stack, "ITEM: " + key);
            } catch (Throwable t) {
                CCLLog.errorOnce(t, "ItemBaking", "Fatal exception thrown whilst baking item model for: " + stack);
                BakingVertexBuffer buffer = BakingVertexBuffer.create();
                if (buffer.isDrawing) {
                    buffer.finishDrawing();
                    buffer.reset();
                }
                return missingModel;
            }
            if (model != missingModel) {
                keyModelCache.put(key, model);
            }
        }
        return model;

    }

    public static IBakedModel generateItemModel(ItemStack stack) {
        Item item = stack.getItem();
        if (item instanceof IBakeryProvider) {

            IItemBakery bakery = (IItemBakery) ((IBakeryProvider) item).getBakery();

            List<BakedQuad> generalQuads = new LinkedList<>();
            Map<Direction, List<BakedQuad>> faceQuads = new HashMap<>();
            generalQuads.addAll(bakery.bakeItemQuads(null, stack));

            for (Direction face : Direction.BY_INDEX) {
                List<BakedQuad> quads = new LinkedList<>();

                quads.addAll(bakery.bakeItemQuads(face, stack));

                faceQuads.put(face, quads);
            }

            PerspectiveProperties properties = bakery.getModelProperties(stack);
            return new PerspectiveAwareBakedModel(faceQuads, generalQuads, properties);
        }
        return missingModel;
    }

    public static IBakedModel getCachedModel(BlockState state, IModelData data) {
        if (state == null) {
            return missingModel;
        }
        //        if (state.getUnlistedProperties().containsKey(ModelErrorStateProperty.ERROR_STATE)) {
        //            ErrorState errorState = state.getValue(ModelErrorStateProperty.ERROR_STATE);
        //            if (errorState == null) {
        //                CCLLog.logOncePerTick(Level.FATAL, "A CCL controlled model has been improperly handled by someone and will NOT be rendered. No more information available.");
        //                return missingModel;
        //            }
        //            if (errorState.hasErrored()) {
        //                CCLLog.logOncePerTick(Level.ERROR, "A CCL controlled model has reported an error and will NOT be rendered: \n" + errorState.getReason());
        //                return missingModel;
        //            }
        //        }
        IBakedModel model;
        IBlockStateKeyGenerator keyGenerator = getKeyGenerator(state.getBlock());
        String key = keyGenerator.generateKey(state, data);
        model = keyModelCache.getIfPresent(key);
        if (model == null || FORCE_BLOCK_REBAKE) {
            try {
                model = timeModelGeneration(ModelBakery::generateModel, state, data, "BLOCK: " + key);
            } catch (Throwable t) {
                CCLLog.errorOnce(t, "BlockBaking", "Fatal exception thrown whilst baking block model for: " + state);
                BakingVertexBuffer buffer = BakingVertexBuffer.create();
                if (buffer.isDrawing) {
                    buffer.finishDrawing();
                    buffer.reset();
                }
                return missingModel;
            }
            if (model != missingModel) {
                keyModelCache.put(key, model);
            }
        }
        return model;
    }

    public static IBakedModel generateModel(BlockState state, IModelData data) {
        if (state.getBlock() instanceof IBakeryProvider) {
            IBlockBakery bakery = (IBlockBakery) ((IBakeryProvider) state.getBlock()).getBakery();
            if (bakery instanceof ISimpleBlockBakery) {
                ISimpleBlockBakery simpleBakery = (ISimpleBlockBakery) bakery;
                List<BakedQuad> generalQuads = new LinkedList<>();
                Map<Direction, List<BakedQuad>> faceQuads = new HashMap<>();
                generalQuads.addAll(simpleBakery.bakeQuads(null, state, data));

                for (Direction face : Direction.BY_INDEX) {
                    List<BakedQuad> quads = new LinkedList<>();

                    quads.addAll(simpleBakery.bakeQuads(face, state, data));

                    faceQuads.put(face, quads);
                }
                ModelProperties properties = new ModelProperties(true, true, null);
                return new PerspectiveAwareBakedModel(faceQuads, generalQuads, TransformUtils.DEFAULT_BLOCK, properties);
            }
            if (bakery instanceof ILayeredBlockBakery) {
                ILayeredBlockBakery layeredBakery = (ILayeredBlockBakery) bakery;
                Map<BlockRenderLayer, Map<Direction, List<BakedQuad>>> layerFaceQuadMap = new HashMap<>();
                Map<BlockRenderLayer, List<BakedQuad>> layerGeneralQuads = new HashMap<>();
                for (BlockRenderLayer layer : BlockRenderLayer.values()) {
                    if (state.getBlock().canRenderInLayer(state, layer)) {
                        LinkedList<BakedQuad> quads = new LinkedList<>();
                        quads.addAll(layeredBakery.bakeLayerFace(null, layer, state, data));
                        layerGeneralQuads.put(layer, quads);
                    }
                }

                for (BlockRenderLayer layer : BlockRenderLayer.values()) {
                    if (state.getBlock().canRenderInLayer(state, layer)) {
                        Map<Direction, List<BakedQuad>> faceQuadMap = new HashMap<>();
                        for (Direction face : Direction.BY_INDEX) {
                            List<BakedQuad> quads = new LinkedList<>();
                            quads.addAll(layeredBakery.bakeLayerFace(face, layer, state, data));
                            faceQuadMap.put(face, quads);
                        }
                        layerFaceQuadMap.put(layer, faceQuadMap);
                    }
                }
                ModelProperties properties = new ModelProperties(true, true, null);
                return new PerspectiveAwareLayeredModel(layerFaceQuadMap, layerGeneralQuads, new PerspectiveProperties(TransformUtils.DEFAULT_BLOCK, properties), BlockRenderLayer.SOLID);
            }
        }
        return missingModel;
    }

    private static <T, R> R timeModelGeneration(Function<T, R> func, T thing, String logPostfix) {
        if (DEBUG) {
            CCLLog.log(Level.INFO, "Baking Model.. Key: %s", logPostfix);
        }
        long start = System.nanoTime();
        R ret = func.apply(thing);
        long end = System.nanoTime();
        logGenTime(start, end);
        return ret;
    }

    private static <T, U, R> R timeModelGeneration(BiFunction<T, U, R> func, T thing, U thing2, String logPostfix) {
        if (DEBUG) {
            CCLLog.log(Level.INFO, "Baking Model.. Key: %s", logPostfix);
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

            CCLLog.log(Level.INFO, "Baking finished in %s.", s);
        }
    }

    public static void nukeModelCache() {
        keyModelCache.invalidateAll();
    }
}
