package codechicken.lib.model.bakedmodels;

import codechicken.lib.model.bakedmodels.ModelProperties.PerspectiveProperties.PerspectiveBuilder;
import codechicken.lib.texture.TextureUtils;
import codechicken.lib.util.Copyable;
import codechicken.lib.util.TransformUtils;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IModelTransform;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

import javax.annotation.Nullable;

/**
 * Created by covers1624 on 19/11/2016.
 */
// TODO 1.18, Make all constructors private and force the use of the Builder.
public class ModelProperties implements Copyable<ModelProperties> {

    public static final ModelProperties DEFAULT_ITEM = ModelProperties.builder()
            .withAO(true)
            .build();
    public static final ModelProperties DEFAULT_BLOCK = ModelProperties.builder()
            .withAO(true)
            .withGui3D(true)
            .withUsesBlockLight(true)
            .build();

    private final boolean isAO;
    private final boolean isGui3D;
    private final boolean isBuiltInRenderer;
    private final boolean usesBlockLight;

    @Nullable
    private TextureAtlasSprite particle;

    public ModelProperties(boolean isAO, boolean isGui3D) {
        this(isAO, isGui3D, false, null);
    }

    public ModelProperties(boolean isAO, boolean isGui3D, TextureAtlasSprite sprite) {
        this(isAO, isGui3D, false, false, sprite);
    }

    @Deprecated // Use function with usesBlockLight parameter.
    public ModelProperties(boolean isAO, boolean isGui3D, boolean isBuiltInRenderer, TextureAtlasSprite particle) {
        this(isAO, isGui3D, isBuiltInRenderer, false, particle);
    }

    public ModelProperties(ModelProperties properties) {
        this(properties, properties.particle);
    }

    public ModelProperties(ModelProperties properties, TextureAtlasSprite sprite) {
        this(properties.isAmbientOcclusion(), properties.isGui3d(), properties.usesBlockLight(), properties.isBuiltInRenderer(), sprite);
    }

    public ModelProperties(boolean isAO, boolean isGui3D, boolean usesBlockLight, boolean isBuiltInRenderer, TextureAtlasSprite particle) {
        this.isAO = isAO;
        this.isGui3D = isGui3D;
        this.isBuiltInRenderer = isBuiltInRenderer;
        this.usesBlockLight = usesBlockLight;
        this.particle = particle;
    }

    public static ModelProperties createFromModel(IBakedModel model) {
        return new ModelProperties(model.useAmbientOcclusion(), model.isGui3d(), model.isCustomRenderer(), model.getParticleIcon());
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

    public boolean usesBlockLight() {
        return usesBlockLight;
    }

    public TextureAtlasSprite getParticleTexture() {
        //TODO, Move this to use a ResourceLocation.
        if (particle == null) {
            particle = TextureUtils.getMissingSprite();
        }
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

        public static final PerspectiveProperties DEFAULT_ITEM = ModelProperties.builder()
                .copyFrom(ModelProperties.DEFAULT_ITEM)
                .withTransforms(TransformUtils.DEFAULT_ITEM)
                .build();
        public static final PerspectiveProperties DEFAULT_BLOCK = ModelProperties.builder()
                .copyFrom(ModelProperties.DEFAULT_BLOCK)
                .withTransforms(TransformUtils.DEFAULT_BLOCK)
                .build();

        private final IModelTransform transforms;

        public PerspectiveProperties(IModelTransform transforms, boolean isAO, boolean isGui3D) {
            this(transforms, isAO, isGui3D, false, null);
        }

        @Deprecated // Use constructor with usesBlockLight parameter.`
        public PerspectiveProperties(IModelTransform transforms, boolean isAO, boolean isGui3D, boolean isBuiltInRenderer, TextureAtlasSprite particle) {
            this(transforms, isAO, isGui3D, false, isBuiltInRenderer, particle);
        }

        public PerspectiveProperties(IModelTransform transforms, boolean isAO, boolean isGui3D, boolean usesBlockLight, boolean isBuiltInRenderer, TextureAtlasSprite particle) {
            this(transforms, new ModelProperties(isAO, isGui3D, usesBlockLight, isBuiltInRenderer, particle));
        }

        public PerspectiveProperties(PerspectiveProperties properties) {
            this(properties.getTransforms(), properties);
        }

        public PerspectiveProperties(IModelTransform transforms, ModelProperties properties) {
            super(properties);
            this.transforms = transforms;
        }

        public IModelTransform getTransforms() {
            return transforms;
        }

        @Override
        public PerspectiveProperties copy() {
            return new PerspectiveProperties(this);
        }

        public static class PerspectiveBuilder extends Builder {

            private IModelTransform transforms;

            protected PerspectiveBuilder(Builder builder) {
                super(builder.isAO, builder.isAO, builder.usesBlockLight, builder.isBuiltInRenderer, builder.particle);
            }

            @Override
            public PerspectiveBuilder withTransforms(IModelTransform transforms) {
                this.transforms = transforms;
                return this;
            }

            @Override
            public PerspectiveProperties build() {
                return new PerspectiveProperties(transforms, super.build());
            }
        }
    }

    public static class Builder {

        private boolean isAO;
        private boolean isGui3D;
        private boolean isBuiltInRenderer;
        private boolean usesBlockLight;
        private TextureAtlasSprite particle;

        protected Builder() {
        }

        @Deprecated // Use constructor with usesBlockLight parameter.
        protected Builder(boolean isAO, boolean isGui3D, boolean isBuiltInRenderer, TextureAtlasSprite particle) {
            this(isAO, isGui3D, false, isBuiltInRenderer, particle);
        }

        protected Builder(boolean isAO, boolean isGui3D, boolean usesBlockLight, boolean isBuiltInRenderer, TextureAtlasSprite particle) {
            this.isAO = isAO;
            this.isGui3D = isGui3D;
            this.usesBlockLight = usesBlockLight;
            this.isBuiltInRenderer = isBuiltInRenderer;
            this.particle = particle;
        }

        public Builder copyFrom(IBakedModel model) {
            isAO = model.useAmbientOcclusion();
            isGui3D = model.isGui3d();
            usesBlockLight = model.usesBlockLight();
            isBuiltInRenderer = model.isCustomRenderer();
            particle = model.getParticleIcon();
            return this;
        }

        public Builder copyFrom(ModelProperties properties) {
            isAO = properties.isAO;
            isGui3D = properties.isGui3D;
            isBuiltInRenderer = properties.isBuiltInRenderer;
            particle = properties.particle;
            return this;
        }

        public Builder withAO(boolean isAO) {
            this.isAO = isAO;
            return this;
        }

        public Builder withGui3D(boolean isGui3D) {
            this.isGui3D = isGui3D;
            return this;
        }

        public Builder withUsesBlockLight(boolean usesBlockLight) {
            this.usesBlockLight = usesBlockLight;
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

        public PerspectiveBuilder withTransforms(IModelTransform transforms) {
            PerspectiveBuilder builder = new PerspectiveBuilder(this);
            builder.withTransforms(transforms);
            return builder;
        }

        public ModelProperties build() {
            return new ModelProperties(isAO, isGui3D, isBuiltInRenderer, particle);
        }

    }
}
