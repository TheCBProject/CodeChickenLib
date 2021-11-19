package codechicken.lib.render.shader;

import codechicken.lib.render.OpenGLUtils;
import codechicken.lib.render.shader.ShaderObject.ShaderType;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Created by covers1624 on 24/5/20.
 */
public class ShaderProgramBuilder {

    private static final Consumer<UniformCache> NULL_CALLBACK = e -> {};

    private final Map<String, ShaderObject> shaders = new HashMap<>();
    private Consumer<UniformCache> cacheCallback;

    private ShaderProgramBuilder() {
    }

    public static ShaderProgramBuilder builder() {
        return new ShaderProgramBuilder();
    }

    public ShaderProgramBuilder addBinaryShader(String name, Consumer<BinaryShaderObjectBuilder> func) {
        if(!OpenGLUtils.openGL46) {
            throw new IllegalStateException("OpenGL 4.6 is not available, someone forgot to check this!");
        }
        BinaryShaderObjectBuilder builder = new BinaryShaderObjectBuilder(name);
        func.accept(builder);
        return addShader(builder.build());
    }

    public ShaderProgramBuilder addShader(String name, Consumer<ShaderObjectBuilder> func) {
        ShaderObjectBuilder builder = new ShaderObjectBuilder(name);
        func.accept(builder);
        return addShader(builder.build());
    }

    public ShaderProgramBuilder addShader(ShaderObject shader) {
        if (shaders.containsKey(shader.getName())) {
            throw new IllegalArgumentException("Duplicate shader with name: " + shader.getName());
        }
        shaders.put(shader.getName(), shader);
        return this;
    }

    public ShaderProgramBuilder whenUsed(Consumer<UniformCache> callback) {
        if (cacheCallback == null) {
            cacheCallback = callback;
        } else {
            cacheCallback = cacheCallback.andThen(callback);
        }
        return this;
    }

    public ShaderProgram build() {
        return new ShaderProgram(shaders.values(), cacheCallback == null ? NULL_CALLBACK : cacheCallback);
    }

    /**
     * Created by KitsuneAlex on 18/11/21.
     */
    @SuppressWarnings("unchecked")
    private static abstract class AbstractShaderObjectBuilder<B extends AbstractShaderObjectBuilder<B>> {

        protected final String name;
        protected final Map<String, Uniform> uniforms = new HashMap<>();
        protected ShaderType type;

        protected AbstractShaderObjectBuilder(String name) {
            this.name = Objects.requireNonNull(name);
        }

        public B type(ShaderType type) {
            if (this.type != null) {
                throw new IllegalArgumentException("Type already set.");
            }

            this.type = Objects.requireNonNull(type);
            return (B)this;
        }

        public B uniform(String name, UniformType type) {
            if (uniforms.containsKey(name)) {
                throw new IllegalArgumentException("Duplicate uniform with name: " + name);
            }

            uniforms.put(name, new Uniform(name, type));
            return (B)this;
        }

        public abstract ShaderObject build();

    }

    /**
     * Created by KitsuneAlex on 18/11/21.
     */
    public static class BinaryShaderObjectBuilder extends AbstractShaderObjectBuilder<BinaryShaderObjectBuilder> {

        private static final Consumer<ConstantCache> NULL_CALLBACK = c -> {};
        private BinaryType binaryType;
        private ResourceLocation assetSource;
        private String entryPoint;
        private Consumer<ConstantCache> specializationCallback = NULL_CALLBACK;

        private BinaryShaderObjectBuilder(String name) {
            super(name);
        }

        public BinaryShaderObjectBuilder binaryType(BinaryType binaryType) {
            if(this.binaryType != null) {
                throw new IllegalStateException("Binary type already set.");
            }
            this.binaryType = binaryType;
            return this;
        }

        public BinaryShaderObjectBuilder source(ResourceLocation assetSource) {
            if(this.assetSource != null) {
                throw new IllegalStateException("Source already set.");
            }
            this.assetSource = assetSource;
            return this;
        }

        public BinaryShaderObjectBuilder entryPoint(String entryPoint) {
            if(this.entryPoint != null) {
                throw new IllegalStateException("Entry point already set.");
            }
            this.entryPoint = entryPoint;
            return this;
        }

        public BinaryShaderObjectBuilder whenSpecialized(Consumer<ConstantCache> specializationCallback) {
            this.specializationCallback = specializationCallback;
            return this;
        }

        @Override
        public ShaderObject build() {
            if (type == null) {
                throw new IllegalStateException("Type not set.");
            }
            if(entryPoint == null || entryPoint.isEmpty()) {
                throw new IllegalStateException("Entry point not set.");
            }
            return new BinaryShaderObject(name, assetSource, type, binaryType, entryPoint, uniforms.values(), specializationCallback);
        }

    }

    /**
     * Created by covers1624 on 24/5/20.
     * Edited by KitsuneAlex on 18/11/21.
     */
    public static class ShaderObjectBuilder extends AbstractShaderObjectBuilder<ShaderObjectBuilder> {

        private String simpleSource;
        private ResourceLocation assetSource;

        private ShaderObjectBuilder(String name) {
            super(name);
        }

        public ShaderObjectBuilder source(String source) {
            if (simpleSource != null || assetSource != null) {
                throw new IllegalArgumentException("Source already set.");
            }
            simpleSource = Objects.requireNonNull(source);
            return this;
        }

        public ShaderObjectBuilder source(ResourceLocation asset) {
            if (assetSource != null || simpleSource != null) {
                throw new IllegalArgumentException("Source already set.");
            }
            assetSource = Objects.requireNonNull(asset);
            return this;
        }

        @Override
        public ShaderObject build() {
            if (type == null) {
                throw new IllegalStateException("Type not set.");
            }
            if (simpleSource == null && assetSource == null) {
                throw new IllegalStateException("SimpleSource or AssetSource not set.");
            }
            if (simpleSource != null) {
                return new SimpleShaderObject(name, type, uniforms.values(), simpleSource);
            }
            return new AssetShaderObject(name, type, uniforms.values(), assetSource);
        }

    }
}
