package codechicken.lib.model.bakery;

import codechicken.lib.internal.CCLLog;
import codechicken.lib.model.BakedModelProperties;
import codechicken.lib.model.ModelRegistryHelper;
import codechicken.lib.model.PerspectiveAwareModelProperties;
import codechicken.lib.model.PlanarFaceBakery;
import codechicken.lib.model.bakedmodels.PerspectiveAwareBakedModel;
import codechicken.lib.model.bakedmodels.PerspectiveAwareLayeredModel;
import codechicken.lib.model.bakery.generation.*;
import codechicken.lib.model.bakery.key.IBlockStateKeyGenerator;
import codechicken.lib.model.bakery.key.IItemStackKeyGenerator;
import codechicken.lib.render.buffer.BakingVertexBuffer;
import codechicken.lib.texture.IItemBlockTextureProvider;
import codechicken.lib.texture.IWorldBlockTextureProvider;
import codechicken.lib.texture.TextureUtils;
import codechicken.lib.util.TransformUtils;
import codechicken.lib.util.VertexDataUtils;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.Level;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

/**
 * Created by covers1624 on 25/10/2016.
 */
@SideOnly (Side.CLIENT)
public class ModelBakery {

    private static boolean DEBUG = Boolean.parseBoolean(System.getProperty("ccl.debugBakeryLogging"));

    private static Cache<String, IBakedModel> keyModelCache = CacheBuilder.newBuilder().expireAfterAccess(30, TimeUnit.MINUTES).build();

    private static Map<Item, IItemStackKeyGenerator> itemKeyGeneratorMap = new HashMap<>();
    private static Map<Block, IBlockStateKeyGenerator> blockKeyGeneratorMap = new HashMap<>();
    private static IBakedModel missingModel;

    public static final IBlockStateKeyGenerator defaultBlockKeyGenerator = state -> {
        if (state.getBlock() instanceof IWorldBlockTextureProvider) {
            Map<BlockRenderLayer, Map<EnumFacing, TextureAtlasSprite>> layerFaceSpriteMap = state.getValue(BlockBakeryProperties.LAYER_FACE_SPRITE_MAP);
            StringBuilder builder = new StringBuilder(state.getBlock().getRegistryName() + ",");
            for (Entry<BlockRenderLayer, Map<EnumFacing, TextureAtlasSprite>> layerEntry : layerFaceSpriteMap.entrySet()) {
                builder.append(layerEntry.getKey().toString()).append(",");
                for (Entry<EnumFacing, TextureAtlasSprite> faceSpriteEntry : layerEntry.getValue().entrySet()) {
                    builder.append(faceSpriteEntry.getKey()).append(",").append(faceSpriteEntry.getValue().getIconName()).append(",");
                }
            }
            return builder.toString();
        }
        return state.getBlock().getRegistryName().toString() + "|" + state.getBlock().getMetaFromState(state);
    };

    public static final IItemStackKeyGenerator defaultItemKeyGenerator = stack -> stack.getItem().getRegistryName().toString() + "|" + stack.getMetadata();

    public static void init() {
        TextureUtils.registerReloadListener(resourceManager -> nukeModelCache());
        ModelRegistryHelper.registerCallback(modelRegistry -> missingModel = ModelLoaderRegistry.getMissingModel().bake(TransformUtils.DEFAULT_BLOCK, DefaultVertexFormats.ITEM, TextureUtils.bakedTextureGetter));
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

    public static IBlockState handleExtendedState(IExtendedBlockState state, IBlockAccess world, BlockPos pos) {
        Block block = state.getBlock();

        if (block instanceof IBakeryProvider) {
            IBakery bakery = ((IBakeryProvider) block).getBakery();
            if (bakery instanceof IBlockBakery) {
                return ((IBlockBakery) bakery).handleState(state, world, pos);
            }
            throw new IllegalStateException("ModelBakery.handleExtendedState called for block that implements IBakeryProvider but does not return a IBlockBakery in IBakeryProvider.getBakery()!");
        } else if (block instanceof IWorldBlockTextureProvider) {
            IWorldBlockTextureProvider provider = ((IWorldBlockTextureProvider) block);
            Map<BlockRenderLayer, Map<EnumFacing, TextureAtlasSprite>> layerFaceSpriteMap = new HashMap<>();
            for (BlockRenderLayer layer : BlockRenderLayer.values()) {
                if (block.canRenderInLayer(state, layer)) {
                    Map<EnumFacing, TextureAtlasSprite> faceSpriteMap = new HashMap<>();
                    for (EnumFacing face : EnumFacing.VALUES) {
                        TextureAtlasSprite sprite = provider.getTexture(face, state, layer, world, pos);
                        if (sprite != null) {
                            faceSpriteMap.put(face, sprite);
                        }
                    }
                    layerFaceSpriteMap.put(layer, faceSpriteMap);
                }
            }
            state = state.withProperty(BlockBakeryProperties.LAYER_FACE_SPRITE_MAP, layerFaceSpriteMap);
        }
        return state;
    }

    public static IBakedModel getCachedItemModel(ItemStack stack) {
        IBakedModel model;
        IItemStackKeyGenerator generator = getKeyGenerator(stack.getItem());
        String key = generator.generateKey(stack);
        model = keyModelCache.getIfPresent(key);
        if (model == null) {
            try {
                model = generateItemModel(stack);
            } catch (Throwable t) {
                CCLLog.errorOnce(t, "ItemBaking", "Fatal exception thrown whilst baking item model for: " + stack);
                BakingVertexBuffer buffer = BakingVertexBuffer.create();
                if (buffer.isDrawing) {
                    buffer.finishDrawing();
                    buffer.reset();
                }
            }
            if (DEBUG) {
                CCLLog.log(Level.INFO, "Baking item model: " + key);
            }
            if (model != missingModel) {
                keyModelCache.put(key, model);
            }
        }
        return model;

    }

    public static IBakedModel generateItemModel(ItemStack stack) {
        Item item = stack.getItem();
        if (item instanceof ItemBlock) {
            Block block = Block.getBlockFromItem(item);
            if (block instanceof IBakeryProvider) {
                IBakery bakery = ((IBakeryProvider) block).getBakery();

                List<BakedQuad> generalQuads = new LinkedList<>();
                Map<EnumFacing, List<BakedQuad>> faceQuads = new HashMap<>();
                generalQuads.addAll(((IItemBakery) bakery).bakeItemQuads(null, stack));

                for (EnumFacing face : EnumFacing.VALUES) {
                    List<BakedQuad> quads = new LinkedList<>();

                    quads.addAll(VertexDataUtils.shadeQuadFaces(((IItemBakery) bakery).bakeItemQuads(face, stack)));

                    faceQuads.put(face, quads);
                }
                PerspectiveAwareModelProperties properties = ((IItemBakery) bakery).getModelProperties(stack);
                return new PerspectiveAwareBakedModel(faceQuads, generalQuads, properties.getModelState(), properties.getProperties());

            } else if (block instanceof IItemBlockTextureProvider) {
                IItemBlockTextureProvider provider = ((IItemBlockTextureProvider) block);
                Map<EnumFacing, List<BakedQuad>> faceQuadMap = new HashMap<>();
                for (EnumFacing face : EnumFacing.VALUES) {
                    List<BakedQuad> faceQuads = new LinkedList<>();

                    faceQuads.addAll(VertexDataUtils.shadeQuadFaces(PlanarFaceBakery.bakeFace(face, provider.getTexture(face, stack), DefaultVertexFormats.ITEM)));

                    faceQuadMap.put(face, faceQuads);
                }
                BakedModelProperties properties = new BakedModelProperties(true, true, null);
                return new PerspectiveAwareBakedModel(faceQuadMap, TransformUtils.DEFAULT_BLOCK, properties);
            }
        } else {
            if (item instanceof IBakeryProvider) {

                IItemBakery bakery = ((IItemBakery) ((IBakeryProvider) item).getBakery());

                List<BakedQuad> generalQuads = new LinkedList<>();
                Map<EnumFacing, List<BakedQuad>> faceQuads = new HashMap<>();
                generalQuads.addAll(bakery.bakeItemQuads(null, stack));

                for (EnumFacing face : EnumFacing.VALUES) {
                    List<BakedQuad> quads = new LinkedList<>();

                    quads.addAll(bakery.bakeItemQuads(face, stack));

                    faceQuads.put(face, quads);
                }

                PerspectiveAwareModelProperties bakeryProperties = bakery.getModelProperties(stack);
                BakedModelProperties properties = bakeryProperties.getProperties();
                return new PerspectiveAwareBakedModel(faceQuads, generalQuads, bakeryProperties.getModelState(), properties);
            }
        }
        return missingModel;
    }

    public static IBakedModel getCachedModel(IExtendedBlockState state) {
        if (state == null) {
            return missingModel;
        }
        IBakedModel model;
        IBlockStateKeyGenerator keyGenerator = getKeyGenerator(state.getBlock());
        String key = keyGenerator.generateKey(state);
        model = keyModelCache.getIfPresent(key);
        if (model == null) {
            try {
                model = generateModel(state);
            } catch (Throwable t) {
                CCLLog.errorOnce(t, "BlockBaking", "Fatal exception thrown whilst baking block model for: " + state);
                BakingVertexBuffer buffer = BakingVertexBuffer.create();
                if (buffer.isDrawing) {
                    buffer.finishDrawing();
                    buffer.reset();
                }
            }
            if (DEBUG) {
                CCLLog.log(Level.INFO, "Baking block model: " + key);
            }
            if (model != missingModel) {
                keyModelCache.put(key, model);
            }
        }
        return model;
    }

    public static IBakedModel generateModel(IExtendedBlockState state) {
        if (state.getBlock() instanceof IBakeryProvider) {
            IBlockBakery bakery = ((IBlockBakery) ((IBakeryProvider) state.getBlock()).getBakery());
            if (bakery instanceof ISimpleBlockBakery) {
                ISimpleBlockBakery simpleBakery = ((ISimpleBlockBakery) bakery);
                List<BakedQuad> generalQuads = new LinkedList<>();
                Map<EnumFacing, List<BakedQuad>> faceQuads = new HashMap<>();
                generalQuads.addAll(simpleBakery.bakeQuads(null, state));

                for (EnumFacing face : EnumFacing.VALUES) {
                    List<BakedQuad> quads = new LinkedList<>();

                    quads.addAll(simpleBakery.bakeQuads(face, state));

                    faceQuads.put(face, quads);
                }
                BakedModelProperties properties = new BakedModelProperties(true, true, null);
                return new PerspectiveAwareBakedModel(faceQuads, generalQuads, TransformUtils.DEFAULT_BLOCK, properties);
            }
            if (bakery instanceof ILayeredBlockBakery) {
                ILayeredBlockBakery layeredBakery = ((ILayeredBlockBakery) bakery);
                Map<BlockRenderLayer, Map<EnumFacing, List<BakedQuad>>> layerFaceQuadMap = new HashMap<>();
                Map<BlockRenderLayer, List<BakedQuad>> layerGeneralQuads = new HashMap<>();
                for (BlockRenderLayer layer : BlockRenderLayer.values()) {
                    if (state.getBlock().canRenderInLayer(state, layer)) {
                        LinkedList<BakedQuad> quads = new LinkedList<>();
                        quads.addAll(layeredBakery.bakeLayerFace(null, layer, state));
                        layerGeneralQuads.put(layer, quads);
                    }
                }

                for (BlockRenderLayer layer : BlockRenderLayer.values()) {
                    if (state.getBlock().canRenderInLayer(state, layer)) {
                        Map<EnumFacing, List<BakedQuad>> faceQuadMap = new HashMap<>();
                        for (EnumFacing face : EnumFacing.VALUES) {
                            List<BakedQuad> quads = new LinkedList<>();
                            quads.addAll(layeredBakery.bakeLayerFace(face, layer, state));
                            faceQuadMap.put(face, quads);
                        }
                        layerFaceQuadMap.put(layer, faceQuadMap);
                    }
                }
                BakedModelProperties properties = new BakedModelProperties(true, true, null);
                return new PerspectiveAwareLayeredModel(layerFaceQuadMap, layerGeneralQuads, TransformUtils.DEFAULT_BLOCK, properties);
            }
        }
        if (state.getBlock() instanceof IWorldBlockTextureProvider) {
            Map<BlockRenderLayer, Map<EnumFacing, List<BakedQuad>>> layerFaceQuadMap = generateLayerFaceQuadMap(state);
            BakedModelProperties properties = new BakedModelProperties(true, true, null);
            return new PerspectiveAwareLayeredModel(layerFaceQuadMap, TransformUtils.DEFAULT_BLOCK, properties);
        }
        return missingModel;
    }

    public static Map<BlockRenderLayer, Map<EnumFacing, List<BakedQuad>>> generateLayerFaceQuadMap(IExtendedBlockState state) {
        Map<BlockRenderLayer, Map<EnumFacing, TextureAtlasSprite>> layerFaceSpriteMap = state.getValue(BlockBakeryProperties.LAYER_FACE_SPRITE_MAP);
        Map<BlockRenderLayer, Map<EnumFacing, List<BakedQuad>>> layerFaceQuadMap = new HashMap<>();
        for (BlockRenderLayer layer : layerFaceSpriteMap.keySet()) {
            Map<EnumFacing, TextureAtlasSprite> faceSpriteMap = layerFaceSpriteMap.get(layer);
            Map<EnumFacing, List<BakedQuad>> faceQuadMap = new HashMap<>();
            for (EnumFacing face : faceSpriteMap.keySet()) {
                List<BakedQuad> quads = new LinkedList<>();
                quads.add(PlanarFaceBakery.bakeFace(face, faceSpriteMap.get(face)));
                faceQuadMap.put(face, quads);
            }
            layerFaceQuadMap.put(layer, faceQuadMap);
        }
        return layerFaceQuadMap;
    }

    public static void nukeModelCache() {
        keyModelCache.invalidateAll();
    }
}
