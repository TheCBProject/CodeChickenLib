package codechicken.lib.model.loader.blockstate;

import com.google.common.collect.ImmutableMap;
import net.minecraft.client.renderer.block.model.ModelRotation;
import net.minecraft.client.renderer.block.model.Variant;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

/**
 * Created by covers1624 on 19/11/2016.
 * Here because ForgeVariant is yet again private..
 * Credits to fry.
 */
public class CCFinalVariant extends Variant {

    private final IModelState state;
    private final boolean smooth;
    private final boolean gui3d;
    private final ImmutableMap<String, String> customData;
    private final ImmutableMap<String, String> textures;

    public CCFinalVariant(ResourceLocation model, Optional<IModelState> state, boolean uvLock, boolean smooth, boolean gui3d, int weight, Map<String, String> textures, String textureDomain, Map<String, String> customData) {
        super(model == null ? new ResourceLocation("builtin/missing") : model, (state.isPresent() && state.get() instanceof ModelRotation) ? (ModelRotation) state.get() : ModelRotation.X0_Y0, uvLock, weight);
        this.state = state.orElse(TRSRTransformation.identity());
        this.smooth = smooth;
        this.gui3d = gui3d;

        Map<String, String> newTextures = new HashMap<>();
        for (Entry<String, String> entry : textures.entrySet()) {
            String prefixedTexture = entry.getValue();
            if (!entry.getValue().contains(":")) {
                prefixedTexture = textureDomain + ":" + prefixedTexture;
            }
            newTextures.put(entry.getKey(), prefixedTexture);
        }
        this.textures = ImmutableMap.copyOf(newTextures);
        this.customData = ImmutableMap.copyOf(customData);
    }

    @Override
    public IModel process(IModel base) {

        boolean hasBase = base != ModelLoaderRegistry.getMissingModel();

        if (hasBase) {
            base = base.retexture(textures).smoothLighting(smooth).gui3d(gui3d).uvlock(isUvLock()).process(customData);
        }

        return base;
    }

    @Override
    public IModelState getState() {
        return state;
    }
}
