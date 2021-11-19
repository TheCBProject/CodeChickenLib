package codechicken.lib.render.shader;

import com.google.common.collect.ImmutableList;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import java.util.Collection;
import java.util.Objects;

/**
 * Created by covers1624 on 24/5/20.
 */
public abstract class AbstractShaderObject implements ShaderObject {

    private final String name;
    private final ShaderType type;
    private final ImmutableList<Uniform> uniforms;
    protected int shaderId = -1;
    protected boolean dirty;

    protected AbstractShaderObject(String name, ShaderType type, Collection<Uniform> uniforms) {
        this.name = Objects.requireNonNull(name);
        this.type = Objects.requireNonNull(type);
        this.uniforms = ImmutableList.copyOf(uniforms);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ShaderType getShaderType() {
        return type;
    }

    @Override
    public ImmutableList<Uniform> getUniforms() {
        return uniforms;
    }

    @Override
    public boolean isDirty() {
        return dirty;
    }

    @Override
    public void alloc() {
        final boolean hasInvalidId = shaderId == -1;

        if (dirty || hasInvalidId) {
            if (hasInvalidId) {
                shaderId = GL20.glCreateShader(type.getGLCode());
                if (shaderId == 0) {
                    throw new RuntimeException("Allocation of ShaderObject failed.");
                }
            }
            GL20.glShaderSource(shaderId, getSource());
            GL20.glCompileShader(shaderId);
            if (GL20.glGetShaderi(shaderId, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
                throw new RuntimeException("ShaderProgram linkage failure. \n" + GL20.glGetShaderInfoLog(shaderId));
            }
            dirty = false;
        }
    }

    @Override
    public int getShaderID() {
        return shaderId;
    }

    @Override
    public void onLink(int programId) {

    }

    protected abstract String getSource();
}
