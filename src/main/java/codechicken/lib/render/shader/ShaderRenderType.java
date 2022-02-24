/*
package codechicken.lib.render.shader;

import net.minecraft.client.renderer.RenderType;

*/
/**
 * Created by covers1624 on 24/5/20.
 *//*

public class ShaderRenderType extends RenderType {

    private final RenderType parent;
    private final ShaderProgram program;
    private final UniformCache uniforms;

    public ShaderRenderType(RenderType parent, ShaderProgram program, UniformCache uniforms) {
        super(parent.name, parent.format(), parent.mode(), parent.bufferSize(), parent.affectsCrumbling(), parent.sortOnUpload, null, null);
        this.parent = parent;
        this.program = program;
        this.uniforms = uniforms;
    }

    @Override
    public void setupRenderState() {
        parent.setupRenderState();
        program.use();
        program.popCache(uniforms);
    }

    @Override
    public void clearRenderState() {
        program.release();
        parent.clearRenderState();
    }

    public ShaderProgram getProgram() {
        return program;
    }

    public UniformCache getUniforms() {
        return uniforms;
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
*/
