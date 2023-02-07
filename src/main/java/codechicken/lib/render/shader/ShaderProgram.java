package codechicken.lib.render.shader;

import com.google.common.collect.ImmutableList;
import net.covers1624.quack.collection.StreamableIterable;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * A ShaderProgram.
 * <p>
 * As of 1.18.2, you will probably want to use {@link CCShaderInstance} instead as an extension
 * to Vanilla's {@link ShaderInstance}.
 * <p>
 * You probably want {@link ShaderProgramBuilder} to construct a ShaderProgram.
 * it should be noted, that a ShaderProgram is a {@link ResourceManagerReloadListener},
 * its recommended that you ensure this is registered to {@link ReloadableResourceManager}
 * to ensure {@link ShaderObject}s are re loaded properly when Resources are reloaded.
 * <p>
 * Created by covers1624 on 24/5/20.
 */
public class ShaderProgram implements ResourceManagerReloadListener {

    private final List<ShaderObject> shaders;
    private final Map<String, CCUniform> uniforms;
    @Nullable
    private final Runnable applyCallback;
    private int programId = -1;
    private boolean bound;

    ShaderProgram(Collection<ShaderObject> shaders, Collection<UniformPair> uniforms, @Nullable Runnable applyCallback) {
        this.shaders = ImmutableList.copyOf(shaders);
        this.uniforms = StreamableIterable.of(uniforms)
                .toImmutableMap(UniformPair::name, e -> CCUniform.makeUniform(e.name(), e.type(), 1, null)); // TODO dont specify count of 1 here.
        this.applyCallback = applyCallback;
    }

    /**
     * Gets all {@link ShaderObject}s that make up this {@link ShaderProgram}.
     *
     * @return The {@link ShaderObject}s.
     */
    public List<ShaderObject> getShaders() {
        return shaders;
    }

    /**
     * Get all {@link UniformPair}s exposed by this shader.
     *
     * @return The uniforms.
     */
    public Map<String, CCUniform> getUniforms() {
        return uniforms;
    }

    /**
     * Get a {@link CCUniform} from this {@link ShaderProgram}.
     *
     * @param name The name of the Uniform.
     * @return the {@link CCUniform}.
     */
    @Nullable
    public CCUniform getUniform(String name) {
        return uniforms.get(name);
    }

    /**
     * Gets the GL {@link ShaderProgram} id for this shader.
     * Might not be initialized until {@link #use()} is called once.
     *
     * @return The id, -1 if not initialized.
     */
    public int getProgramId() {
        return programId;
    }

    /**
     * Binds this shader for use, Lazily allocates, links
     * and compiles all {@link ShaderObject}s.
     */
    public void use() {
        if (bound) {
            throw new IllegalStateException("Already bound.");
        }
        compile();
        GL20.glUseProgram(programId);
        if (applyCallback != null) {
            applyCallback.run();
        }
        bound = true;
    }

    /**
     * Forces the ShaderProgram to compile and link.
     * <p>
     * This will happen automatically when calling {@link #use()}, however,
     * it may be required to call this ahead of time in some cases.
     * <p>
     * Be sure to only call this when you have GL context.
     */
    public void compile() {
        if (programId != -1 && shaders.stream().noneMatch(ShaderObject::isDirty)) return;

        for (ShaderObject shaderObject : shaders) {
            shaderObject.alloc();
        }

        if (programId == -1) {
            programId = GL20.glCreateProgram();
            if (programId == 0) {
                throw new IllegalStateException("Allocation of ShaderProgram has failed.");
            }
            shaders.forEach(shader -> GL20.glAttachShader(programId, shader.getShaderID()));
        }
        GL20.glLinkProgram(programId);
        if (GL20.glGetProgrami(programId, GL20.GL_LINK_STATUS) == GL11.GL_FALSE) {
            throw new RuntimeException("ShaderProgram linkage failure. \n" + GL20.glGetProgramInfoLog(programId));
        }
        for (ShaderObject shader : shaders) {
            shader.onLink(programId);
        }
        for (CCUniform value : uniforms.values()) {
            value.setLocation(GL20.glGetUniformLocation(programId, value.getName()));
        }
    }

    /**
     * Releases this shader.
     */
    public void release() {
        if (!bound) {
            throw new IllegalStateException("Not bound");
        }
        bound = false;
        GL20.glUseProgram(0);
    }

    @Override
    public void onResourceManagerReload(ResourceManager resourceManager) {
        for (ShaderObject shader : shaders) {
            if (shader instanceof ResourceManagerReloadListener) {
                ((ResourceManagerReloadListener) shader).onResourceManagerReload(resourceManager);
            }
        }
        compile();
    }
}
