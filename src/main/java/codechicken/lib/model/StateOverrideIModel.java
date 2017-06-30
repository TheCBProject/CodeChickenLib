package codechicken.lib.model;

import com.google.common.collect.ImmutableMap;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.common.model.IModelState;

import java.util.Collection;
import java.util.function.Function;

/**
 * Created by covers1624 on 17/12/2016.
 * TODO Decide on a standard place for CCL's IModels.
 */
public class StateOverrideIModel implements IModel {

    private IModel wrapped;
    private final IModelState wrappedState;

    public StateOverrideIModel(IModel wrapped, IModelState wrappedState) {
        this.wrapped = wrapped;
        this.wrappedState = wrappedState;
    }

    @Override
    public Collection<ResourceLocation> getDependencies() {
        return wrapped.getDependencies();
    }

    @Override
    public Collection<ResourceLocation> getTextures() {
        return wrapped.getTextures();
    }

    @Override
    public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
        return wrapped.bake(state, format, bakedTextureGetter);
    }

    @Override
    public IModelState getDefaultState() {
        return wrappedState;
    }

    @Override
    public IModel retexture(ImmutableMap<String, String> textures) {
        wrapped = wrapped.retexture(textures);
        return this;
    }

    @Override
    public IModel uvlock(boolean value) {
        wrapped = wrapped.uvlock(value);
        return this;
    }

    @Override
    public IModel smoothLighting(boolean value) {
        wrapped = wrapped.smoothLighting(value);
        return this;
    }

    @Override
    public IModel gui3d(boolean value) {
        wrapped = wrapped.gui3d(value);
        return this;
    }

    @Override
    public IModel process(ImmutableMap<String, String> customData) {
        wrapped = wrapped.process(customData);
        return this;
    }
}
