package codechicken.lib.render.shader;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import java.util.Collection;

/**
 * Created by covers1624 on 24/5/20.
 */
public abstract class AbstractShaderObject extends NamedShaderObject {

    protected int shaderId = -1;
    protected boolean dirty;

    protected AbstractShaderObject(String name, ShaderType type, Collection<UniformPair> uniforms) {
        super(name, type, uniforms);
    }

    @Override
    public boolean isDirty() {
        return dirty;
    }

    @Override
    public void alloc() {
        if (dirty || shaderId == -1) {
            if (shaderId == -1) {
                shaderId = GL20.glCreateShader(getShaderType().getGLCode());
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
