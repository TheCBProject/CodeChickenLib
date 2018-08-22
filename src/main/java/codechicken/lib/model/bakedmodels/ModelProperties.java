package codechicken.lib.model.bakedmodels;

import codechicken.lib.model.bakedmodels.ModelProperties.PerspectiveProperties.PerspectiveBuilder;
import codechicken.lib.texture.TextureUtils;
import codechicken.lib.util.Copyable;
import codechicken.lib.util.TransformUtils;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraftforge.common.model.IModelState;

/**
 * Created by covers1624 on 19/11/2016.
 */
public class ModelProperties implements Copyable<ModelProperties> {

    public static final ModelProperties DEFAULT_ITEM = new ModelProperties(true, false);
    public static final ModelProperties DEFAULT_BLOCK = new ModelProperties(true, true);

    private final boolean isAO;
    private final boolean isGui3D;
    private final boolean isBuiltInRenderer;
    private final TextureAtlasSprite particle;

    public ModelProperties(boolean isAO, boolean isGui3D) {
        this(isAO, isGui3D, false, TextureUtils.getMissingSprite());
    }

    public ModelProperties(ModelProperties properties, TextureAtlasSprite sprite) {
        this(properties.isAmbientOcclusion(), properties.isGui3d(), properties.isBuiltInRenderer(), sprite);
    }

    public ModelProperties(boolean isAO, boolean isGui3D, TextureAtlasSprite sprite) {
        this(isAO, isGui3D, false, sprite);
    }

    public ModelProperties(ModelProperties properties) {
        this(properties.isAO, properties.isGui3D, properties.isBuiltInRenderer, properties.particle);
    }

    public ModelProperties(boolean isAO, boolean isGui3D, boolean isBuiltInRenderer, TextureAtlasSprite particle) {
        this.isAO = isAO;
        this.isGui3D = isGui3D;
        this.isBuiltInRenderer = isBuiltInRenderer;
        this.particle = particle;
    }

    public static ModelProperties createFromModel(IBakedModel model) {
        return new ModelProperties(model.isAmbientOcclusion(), model.isGui3d(), model.isBuiltInRenderer(), model.getParticleTexture());
    }

    public boolean isAmbientOcclusion() {
        return isAO;
    }

    public boolean isGui3d() {
        return isGui3D;
    }

    public boolean isBuiltInRenderer() {
        return isBuiltInRenderer;
    }

    public TextureAtlasSprite getParticleTexture() {
        return particle;
    }

    @Override
    public ModelProperties copy() {
        return new ModelProperties(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * Created by covers1624 on 21/02/2017.
     */
    public static class PerspectiveProperties extends ModelProperties {

        public static final PerspectiveProperties DEFAULT_ITEM = new PerspectiveProperties(TransformUtils.DEFAULT_ITEM, true, false);
        public static final PerspectiveProperties DEFAULT_BLOCK = new PerspectiveProperties(TransformUtils.DEFAULT_BLOCK, true, true);

        private final IModelState modelState;

        public PerspectiveProperties(IModelState state, boolean isAO, boolean isGui3D) {
            this(state, isAO, isGui3D, false, null);
        }

        public PerspectiveProperties(IModelState state, boolean isAO, boolean isGui3D, boolean isBuiltInRenderer, TextureAtlasSprite particle) {
            this(state, new ModelProperties(isAO, isGui3D, isBuiltInRenderer, particle));
        }

        public PerspectiveProperties(PerspectiveProperties properties) {
            this(properties.getModelState(), properties);
        }

        public PerspectiveProperties(IModelState state, ModelProperties properties) {
            super(properties);
            this.modelState = state;
        }

        public IModelState getModelState() {
            return modelState;
        }

        @Override
        public PerspectiveProperties copy() {
            return new PerspectiveProperties(this);
        }

        public static class PerspectiveBuilder extends Builder {

            private IModelState state;

            protected PerspectiveBuilder(Builder builder) {
                super(builder.isAO, builder.isAO, builder.isBuiltInRenderer, builder.particle);
            }

            @Override
            public PerspectiveBuilder withState(IModelState state) {
                this.state = state;
                return this;
            }

            @Override
            public PerspectiveProperties build() {
                return new PerspectiveProperties(state, super.build());
            }
        }
    }

    public static class Builder {

        private boolean isAO;
        private boolean isGui3D;
        private boolean isBuiltInRenderer;
        private TextureAtlasSprite particle;

        protected Builder() {
        }

        protected Builder(boolean ao, boolean gui3D, boolean builtInRenderer, TextureAtlasSprite sprite) {
            isAO = ao;
            isGui3D = gui3D;
            isBuiltInRenderer = builtInRenderer;
            particle = sprite;
        }

        public Builder copyFrom(IBakedModel model) {
            isAO = model.isAmbientOcclusion();
            isGui3D = model.isGui3d();
            isBuiltInRenderer = model.isBuiltInRenderer();
            particle = model.getParticleTexture();
            return this;
        }

        public Builder copyFrom(ModelProperties properties) {
            isAO = properties.isAO;
            isGui3D = properties.isGui3D;
            isBuiltInRenderer = properties.isBuiltInRenderer;
            particle = properties.particle;
            return this;
        }

        public Builder withAO(boolean ao) {
            isAO = ao;
            return this;
        }

        public Builder withGui3D(boolean gui3D) {
            isGui3D = gui3D;
            return this;
        }

        public Builder withBuiltInRenderer(boolean builtInRenderer) {
            isBuiltInRenderer = builtInRenderer;
            return this;
        }

        public Builder withParticle(TextureAtlasSprite sprite) {
            particle = sprite;
            return this;
        }

        public PerspectiveBuilder withState(IModelState state) {
            PerspectiveBuilder builder = new PerspectiveBuilder(this);
            builder.withState(state);
            return builder;
        }

        public ModelProperties build() {
            return new ModelProperties(isAO, isGui3D, isBuiltInRenderer, particle);
        }

    }
}
