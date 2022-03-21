package codechicken.lib.render.shader;

import com.google.common.collect.ImmutableList;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

/**
 * A ShaderProgram.
 * You probably want {@link ShaderProgramBuilder} to construct a ShaderProgram.
 * it should be noted, that a ShaderProgram is a {@link ResourceManagerReloadListener},
 * its recommended that you ensure this is registered to {@link ReloadableResourceManager}
 * to ensure {@link ShaderObject}s are re loaded properly when Resources are reloaded.
 * <p>
 * Created by covers1624 on 24/5/20.
 */
public class ShaderProgram implements ResourceManagerReloadListener {

    private final List<ShaderObject> shaders;
    private final List<Uniform> uniforms;
    private final Consumer<UniformCache> cacheCallback;
    private final ShaderUniformCache uniformCache;
    private int programId = -1;
    private boolean bound;

    ShaderProgram(Collection<ShaderObject> shaders, Collection<Uniform> uniforms, Consumer<UniformCache> cacheCallback) {
        this.shaders = ImmutableList.copyOf(shaders);
        this.uniforms = ImmutableList.copyOf(uniforms);
        this.cacheCallback = cacheCallback;
        this.uniformCache = new ShaderUniformCache(this);
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
     * Get all {@link Uniform}s exposed by this shader.
     *
     * @return The uniforms.
     */
    public List<Uniform> getUniforms() {
        return uniforms;
    }

    /**
     * Get the {@link UniformCache} for updating/setting
     * uniforms for the current {@link ShaderProgram}.
     *
     * @return The {@link UniformCache}.
     */
    public UniformCache getUniformCache() {
        return uniformCache;
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
        cacheCallback.accept(uniformCache);
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
        uniformCache.onLink();
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
