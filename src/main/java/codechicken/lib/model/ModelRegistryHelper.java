package codechicken.lib.model;

import codechicken.lib.render.item.IItemRenderer;
import codechicken.lib.texture.TextureUtils;
import codechicken.lib.util.TransformUtils;
import com.google.common.collect.Maps;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.BuiltInModel;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.IRegistry;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.LinkedList;
import java.util.List;

public class ModelRegistryHelper {

    private static List<Pair<ModelResourceLocation, IBakedModel>> registerModels = new LinkedList<>();
    private static List<IModelBakeCallbackPre> modelBakePreCallbacks = new LinkedList<>();
    private static List<IModelBakeCallback> modelBakeCallbacks = new LinkedList<>();

    public static void registerPreBakeCallback(IModelBakeCallbackPre callback) {
        modelBakePreCallbacks.add(callback);
    }

    public static void registerCallback(IModelBakeCallback callback) {
        modelBakeCallbacks.add(callback);
    }

    public static void register(ModelResourceLocation location, IBakedModel model) {
        registerModels.add(new ImmutablePair<>(location, model));
    }

    /**
     * Inserts the item renderer at itemRegistry.getNameForObject(block)#inventory and binds it to the item with a custom mesh definition
     */
    public static void registerItemRenderer(Item item, IItemRenderer renderer) {
        final ModelResourceLocation modelLoc = new ModelResourceLocation(item.getRegistryName(), "inventory");
        register(modelLoc, renderer);
        ModelLoader.setCustomMeshDefinition(item, stack -> modelLoc);
    }

    @SubscribeEvent
    public void onModelBake(ModelBakeEvent event) {
        for (IModelBakeCallbackPre callback : modelBakePreCallbacks) {
            callback.onModelBakePre(event.getModelRegistry());
        }

        for (Pair<ModelResourceLocation, IBakedModel> pair : registerModels) {
            event.getModelRegistry().putObject(pair.getKey(), pair.getValue());
        }

        for (IModelBakeCallback callback : modelBakeCallbacks) {
            callback.onModelBake(event.getModelRegistry());
        }
    }

//    /**
//     * Creates a dummy model at blockRegistry.getNameForObject(block)#particle for all states of the block overriding getParticleTexture
//     */TODO, Pretty sure this doesn't work this way anymore..
//    public static void setParticleTexture(Block block, final ResourceLocation tex) {
//        final ModelResourceLocation modelLoc = new ModelResourceLocation(block.getRegistryName(), "particle");
//        register(modelLoc, new BuiltInModel(TransformUtils.DEFAULT_BLOCK.toVanillaTransform(), ItemOverrideList.NONE) {
//            @Override
//            public TextureAtlasSprite getParticleTexture() {
//                return TextureUtils.getTexture(tex);
//            }
//        });
//        ModelLoader.setCustomStateMapper(block, blockIn -> Maps.toMap(blockIn.getBlockState().getValidStates(), input -> modelLoc));
//    }

    public interface IModelBakeCallbackPre {

        /**
         * Called before CCL does anything to the ModelRegistry.
         * Useful for wrapped models, Use this in the constructor of the wrapped model.
         *
         * @param modelRegistry The Model registry.
         */
        void onModelBakePre(IRegistry<ModelResourceLocation, IBakedModel> modelRegistry);
    }

    public interface IModelBakeCallback {

        /**
         * A Simple callback for model baking.
         *
         * @param modelRegistry The Model registry.
         */
        void onModelBake(IRegistry<ModelResourceLocation, IBakedModel> modelRegistry);
    }
}
