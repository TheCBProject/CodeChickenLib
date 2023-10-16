package codechicken.lib.model;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.LinkedList;
import java.util.List;

public class ModelRegistryHelper {

    private final List<Pair<ModelResourceLocation, BakedModel>> registerModels = new LinkedList<>();

    public ModelRegistryHelper() {
        this(FMLJavaModLoadingContext.get().getModEventBus());
    }

    public ModelRegistryHelper(IEventBus eventBus) {
        eventBus.register(this);
    }

    public void register(ModelResourceLocation location, BakedModel model) {
        registerModels.add(new ImmutablePair<>(location, model));
    }

    @SubscribeEvent
    public void onModelBake(ModelEvent.ModifyBakingResult event) {
        for (Pair<ModelResourceLocation, BakedModel> pair : registerModels) {
            event.getModels().put(pair.getKey(), pair.getValue());
        }
    }
}
