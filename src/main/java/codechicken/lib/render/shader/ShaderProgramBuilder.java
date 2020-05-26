package codechicken.lib.render.shader;

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

    private final Map<String, ShaderObject> shaders = new HashMap<>();
    private Consumer<UniformCache> cacheCallback;

    private ShaderProgramBuilder() {
    }

    public static ShaderProgramBuilder builder() {
        return new ShaderProgramBuilder();
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
        return new ShaderProgram(shaders.values(), cacheCallback);
    }

    public static class ShaderObjectBuilder {

        private final String name;
        private final Map<String, Uniform> uniforms = new HashMap<>();
        private ShaderType type;
        private String simpleSource;
        private ResourceLocation assetSource;

        private ShaderObjectBuilder(String name) {
            this.name = Objects.requireNonNull(name);
        }

        public ShaderObjectBuilder type(ShaderType type) {
            if (this.type != null) {
                throw new IllegalArgumentException("Type already set.");
            }
            this.type = Objects.requireNonNull(type);
            return this;
        }

        public ShaderObjectBuilder source(String source) {
            if (this.simpleSource != null || assetSource != null) {
                throw new IllegalArgumentException("Source already set.");
            }
            this.simpleSource = Objects.requireNonNull(source);
            return this;
        }

        public ShaderObjectBuilder source(ResourceLocation asset) {
            if (assetSource != null || this.simpleSource != null) {
                throw new IllegalArgumentException("Source already set.");
            }
            this.assetSource = Objects.requireNonNull(asset);
            return this;
        }

        public ShaderObjectBuilder uniform(String name, UniformType type) {
            if (uniforms.containsKey(name)) {
                throw new IllegalArgumentException("Duplicate uniform with name: " + name);
            }
            uniforms.put(name, new Uniform(name, type));
            return this;
        }

        private ShaderObject build() {
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
