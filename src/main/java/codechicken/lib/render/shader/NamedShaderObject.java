package codechicken.lib.render.shader;

import com.google.common.collect.ImmutableList;

import java.util.Collection;
import java.util.Objects;

/**
 * Created by covers1624 on 9/3/22.
 */
public abstract class NamedShaderObject implements ShaderObject {

    private final String name;
    private final ShaderType type;
    private final ImmutableList<UniformPair> uniforms;

    protected NamedShaderObject(String name, ShaderType type, Collection<UniformPair> uniforms) {
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
    public ImmutableList<UniformPair> getUniforms() {
        return uniforms;
    }
}
