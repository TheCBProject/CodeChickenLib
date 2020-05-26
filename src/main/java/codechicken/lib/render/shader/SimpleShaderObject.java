package codechicken.lib.render.shader;

import java.util.Collection;

/**
 * Created by covers1624 on 24/5/20.
 */
public class SimpleShaderObject extends AbstractShaderObject {

    private final String source;

    protected SimpleShaderObject(String name, ShaderType type, Collection<Uniform> uniforms, String source) {
        super(name, type, uniforms);
        this.source = source;
    }

    @Override
    protected String getSource() {
        return source;
    }
}
