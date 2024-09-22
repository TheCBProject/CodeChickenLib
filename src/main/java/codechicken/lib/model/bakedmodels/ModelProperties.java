package codechicken.lib.model.bakedmodels;

import codechicken.lib.model.PerspectiveModelState;
import codechicken.lib.model.bakedmodels.ModelProperties.PerspectiveProperties.PerspectiveBuilder;
import codechicken.lib.texture.TextureUtils;
import codechicken.lib.util.Copyable;
import codechicken.lib.util.TransformUtils;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import org.jetbrains.annotations.Nullable;

import static java.util.Objects.requireNonNull;

/**
 * Created by covers1624 on 19/11/2016.
 */
@Deprecated // These may go away unless there is sufficient need for generic implementations like these.
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

    private ModelProperties(boolean isAO, boolean isGui3D, boolean usesBlockLight, boolean isBuiltInRenderer, TextureAtlasSprite particle) {
        this.isAO = isAO;
        this.isGui3D = isGui3D;
        this.isBuiltInRenderer = isBuiltInRenderer;
        this.usesBlockLight = usesBlockLight;
        this.particle = particle;
    }

    private ModelProperties(ModelProperties other) {
        this.isAO = other.isAO;
        this.isGui3D = other.isGui3D;
        this.isBuiltInRenderer = other.isBuiltInRenderer;
        this.usesBlockLight = other.usesBlockLight;
        this.particle = other.particle;
    }

    public static ModelProperties fromModel(BakedModel model) {
        return new ModelProperties(model.useAmbientOcclusion(), model.isGui3d(), model.usesBlockLight(), model.isCustomRenderer(), model.getParticleIcon());
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
        return maybeMissingTexture(particle);
    }

    @Override
    public ModelProperties copy() {
        return new ModelProperties(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        return new Builder(this);
    }

    private static TextureAtlasSprite maybeMissingTexture(@Nullable TextureAtlasSprite sprite) {
        if (sprite == null) {
            sprite = TextureUtils.getMissingSprite();
        }
        return sprite;
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

        private final PerspectiveModelState transforms;

        private PerspectiveProperties(PerspectiveProperties properties) {
            this(properties.getTransforms(), properties);
        }

        private PerspectiveProperties(PerspectiveModelState transforms, ModelProperties properties) {
            super(properties);
            this.transforms = transforms;
        }

        public PerspectiveModelState getTransforms() {
            return transforms;
        }

        @Override
        public PerspectiveProperties copy() {
            return new PerspectiveProperties(this);
        }

        @Override
        public PerspectiveBuilder toBuilder() {
            return new PerspectiveBuilder(this);
        }

        public static class PerspectiveBuilder extends Builder {

            private @Nullable PerspectiveModelState transforms;

            protected PerspectiveBuilder(PerspectiveProperties properties) {
                super(properties);
                transforms = properties.transforms;
            }

            protected PerspectiveBuilder(Builder builder) {
                super(builder.isAO, builder.isAO, builder.usesBlockLight, builder.isBuiltInRenderer, builder.particle);
            }

            @Override
            public PerspectiveBuilder withTransforms(PerspectiveModelState transforms) {
                this.transforms = transforms;
                return this;
            }

            @Override
            public PerspectiveProperties build() {
                return new PerspectiveProperties(requireNonNull(transforms), super.build());
            }
        }
    }

    public static class Builder {

        private boolean isAO;
        private boolean isGui3D;
        private boolean isBuiltInRenderer;
        private boolean usesBlockLight;
        private @Nullable TextureAtlasSprite particle;

        protected Builder() {
        }

        protected Builder(ModelProperties properties) {
            isAO = properties.isAO;
            isGui3D = properties.isGui3D;
            usesBlockLight = properties.usesBlockLight;
            isBuiltInRenderer = properties.isBuiltInRenderer;
            particle = properties.particle;
        }

        protected Builder(boolean isAO, boolean isGui3D, boolean usesBlockLight, boolean isBuiltInRenderer, @Nullable TextureAtlasSprite particle) {
            this.isAO = isAO;
            this.isGui3D = isGui3D;
            this.usesBlockLight = usesBlockLight;
            this.isBuiltInRenderer = isBuiltInRenderer;
            this.particle = particle;
        }

        public Builder copyFrom(BakedModel model) {
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

        public PerspectiveBuilder withTransforms(PerspectiveModelState transforms) {
            PerspectiveBuilder builder = new PerspectiveBuilder(this);
            builder.withTransforms(transforms);
            return builder;
        }

        public ModelProperties build() {
            return new ModelProperties(isAO, isGui3D, usesBlockLight, isBuiltInRenderer, maybeMissingTexture(particle));
        }

    }
}
