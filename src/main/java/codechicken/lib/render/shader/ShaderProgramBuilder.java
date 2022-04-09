package codechicken.lib.render.shader;

import codechicken.lib.render.OpenGLUtils;
import codechicken.lib.render.shader.ShaderObject.ShaderType;
import net.covers1624.quack.util.SneakyUtils;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

import static net.covers1624.quack.util.SneakyUtils.nullCons;

/**
 * Created by covers1624 on 24/5/20.
 */
public class ShaderProgramBuilder {

    private final Map<String, ShaderObject> shaders = new HashMap<>();
    private final Map<String, UniformPair> allUniforms = new HashMap<>();
    @Nullable
    private Runnable applyCallback;

    private ShaderProgramBuilder() {
    }

    public static ShaderProgramBuilder builder() {
        return new ShaderProgramBuilder();
    }

    public ShaderProgramBuilder addBinaryShader(String name, Consumer<BinaryShaderObjectBuilder> func) {
        if (!OpenGLUtils.openGL46) {
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

    public ShaderProgramBuilder whenUsed(Runnable callback) {
        if (applyCallback == null) {
            applyCallback = callback;
        } else {
            applyCallback = SneakyUtils.concat(applyCallback, callback);
        }
        return this;
    }

    public ShaderProgram build() {
        return new ShaderProgram(shaders.values(), allUniforms.values(), applyCallback);
    }

    /**
     * Created by covers1624 on 24/5/20.
     * Edited by KitsuneAlex on 18/11/21.
     */
    public class ShaderObjectBuilder {

        protected final String name;
        protected final Map<String, UniformPair> uniforms = new HashMap<>();
        @Nullable
        protected ShaderType type;
        @Nullable
        protected String simpleSource;
        @Nullable
        protected ResourceLocation assetSource;

        private ShaderObjectBuilder(String name) {
            this.name = Objects.requireNonNull(name);
        }

        public ShaderObjectBuilder type(ShaderType type) {
            if (this.type != null) throw new IllegalArgumentException("Type already set.");

            this.type = Objects.requireNonNull(type);
            return this;
        }

        public ShaderObjectBuilder source(String source) {
            if (simpleSource != null || assetSource != null) throw new IllegalArgumentException("Source already set.");

            simpleSource = Objects.requireNonNull(source);
            return this;
        }

        public ShaderObjectBuilder source(ResourceLocation asset) {
            if (assetSource != null || simpleSource != null) throw new IllegalArgumentException("Source already set.");

            assetSource = Objects.requireNonNull(asset);
            return this;
        }

        public ShaderObjectBuilder uniform(String name, UniformType type) {
            UniformPair uniform = allUniforms.get(name);
            if (uniform != null && uniform.type() != type) throw new IllegalArgumentException("Uniform with name '" + name + "' already exists with a different type: " + uniform.type());
            if (!type.isSupported()) throw new UnsupportedOperationException("Uniform type '" + type + "' is not supported in this Environment.");

            if (uniform == null) {
                uniform = new UniformPair(name, type);
                allUniforms.put(name, uniform);
            }
            uniforms.put(name, uniform);
            return this;
        }

        protected ShaderObject build() {
            if (type == null) throw new IllegalStateException("Type not set.");
            if (simpleSource == null && assetSource == null) throw new IllegalStateException("SimpleSource or AssetSource not set.");
            if (simpleSource != null) return new SimpleShaderObject(name, type, uniforms.values(), simpleSource);

            return new AssetShaderObject(name, type, uniforms.values(), assetSource);
        }
    }

    /**
     * Created by KitsuneAlex on 18/11/21.
     */
    public class BinaryShaderObjectBuilder extends ShaderObjectBuilder {

        @Nullable
        private BinaryType binaryType;
        @Nullable
        private String entryPoint;
        private Consumer<ConstantCache> specializationCallback = nullCons();

        private BinaryShaderObjectBuilder(String name) {
            super(name);
        }

        @Override
        public BinaryShaderObjectBuilder source(String source) {
            throw new IllegalStateException("Binary shaders don't have string source.");
        }

        @Override
        public BinaryShaderObjectBuilder source(ResourceLocation asset) {
            return (BinaryShaderObjectBuilder) super.source(asset);
        }

        @Override
        public BinaryShaderObjectBuilder uniform(String name, UniformType type) {
            return (BinaryShaderObjectBuilder) super.uniform(name, type);
        }

        public BinaryShaderObjectBuilder binaryType(BinaryType binaryType) {
            if (this.binaryType != null) throw new IllegalStateException("Binary type already set.");

            this.binaryType = binaryType;
            return this;
        }

        public BinaryShaderObjectBuilder entryPoint(String entryPoint) {
            if (this.entryPoint != null) throw new IllegalStateException("Entry point already set.");

            this.entryPoint = entryPoint;
            return this;
        }

        public BinaryShaderObjectBuilder whenSpecialized(Consumer<ConstantCache> specializationCallback) {
            this.specializationCallback = specializationCallback;
            return this;
        }

        @Override
        protected ShaderObject build() {
            if (type == null) throw new IllegalStateException("Type not set.");
            if (assetSource == null) throw new IllegalStateException("AssetSource not set.");
            if (binaryType == null) throw new IllegalStateException("Binary type not set");
            if (entryPoint == null || entryPoint.isEmpty()) throw new IllegalStateException("Entry point not set.");

            return new BinaryShaderObject(name, assetSource, type, binaryType, entryPoint, allUniforms.values(), specializationCallback);
        }

    }
}
