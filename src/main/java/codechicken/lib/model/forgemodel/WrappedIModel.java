//package codechicken.lib.model.forgemodel;
//
//import com.google.common.collect.ImmutableMap;
//import net.minecraft.client.renderer.block.model.IBakedModel;
//import net.minecraft.client.renderer.texture.TextureAtlasSprite;
//import net.minecraft.client.renderer.vertex.VertexFormat;
//import net.minecraft.util.ResourceLocation;
//import net.minecraftforge.client.model.IModel;
//import net.minecraftforge.common.model.IModelState;
//import net.minecraftforge.common.model.animation.IClip;
//
//import java.util.Collection;
//import java.util.Optional;
//import java.util.function.Function;
//
///**
// * Created by covers1624 on 12/07/2017.
// */
//public class WrappedIModel implements IModel {
//
//    private IModel wrapped;
//
//    public WrappedIModel(IModel wrapped) {
//        this.wrapped = wrapped;
//    }
//
//    @Override
//    public Collection<ResourceLocation> getDependencies() {
//        return wrapped.getDependencies();
//    }
//
//    @Override
//    public Collection<ResourceLocation> getTextures() {
//        return wrapped.getTextures();
//    }
//
//    @Override
//    public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
//        return wrapped.bake(state, format, bakedTextureGetter);
//    }
//
//    @Override
//    public IModelState getDefaultState() {
//        return wrapped.getDefaultState();
//    }
//
//    @Override
//    public Optional<? extends IClip> getClip(String name) {
//        return wrapped.getClip(name);
//    }
//
//    @Override
//    public IModel process(ImmutableMap<String, String> customData) {
//        wrapped = wrapped.process(customData);
//        return this;
//    }
//
//    @Override
//    public IModel smoothLighting(boolean value) {
//        wrapped = wrapped.smoothLighting(value);
//        return this;
//    }
//
//    @Override
//    public IModel gui3d(boolean value) {
//        wrapped = wrapped.gui3d(value);
//        return this;
//    }
//
//    @Override
//    public IModel uvlock(boolean value) {
//        wrapped = wrapped.uvlock(value);
//        return this;
//    }
//
//    @Override
//    public IModel retexture(ImmutableMap<String, String> textures) {
//        wrapped = wrapped.retexture(textures);
//        return this;
//    }
//}
