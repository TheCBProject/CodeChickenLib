package codechicken.lib.render.shader;

import org.lwjgl.opengl.GL46;

/**
 * All currently supported shader binary types.
 * <p>
 * Created by KitsuneAlex on 19/11/21.
 */
public enum BinaryType {
    SPIR_V(GL46.GL_SHADER_BINARY_FORMAT_SPIR_V);

    private final int glCode;

    BinaryType(int glCode) {
        this.glCode = glCode;
    }

    public int getGLCode() {
        return glCode;
    }
}
