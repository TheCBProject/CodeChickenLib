package codechicken.lib.model;

import codechicken.lib.render.item.IItemRenderer;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
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
