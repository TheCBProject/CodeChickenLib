package codechicken.lib.render.shader;

import codechicken.lib.render.buffer.DelegateRenderType;
import net.minecraft.client.renderer.RenderType;

/**
 * Created by covers1624 on 24/5/20.
 */
public class ShaderRenderType extends DelegateRenderType {

    private final ShaderProgram program;
    private final UniformCache uniforms;

    public ShaderRenderType(RenderType parent, ShaderProgram program, UniformCache uniforms) {
        super(parent);
        this.parent = parent;
        this.program = program;
        this.uniforms = uniforms;
    }

    @Override
    public void setupRenderState() {
        super.setupRenderState();
        program.use();
        program.popCache(uniforms);
    }

    @Override
    public void clearRenderState() {
        program.release();
        parent.clearRenderState();
        super.clearRenderState();
    }

    @Override
    public boolean equals(Object other) {
        return other == this;
    }

    @Override
    public int hashCode() {
        return System.identityHashCode(this);
    }
}
