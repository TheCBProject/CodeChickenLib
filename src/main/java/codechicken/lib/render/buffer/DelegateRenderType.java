package codechicken.lib.render.buffer;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderType;

/**
 * Created by covers1624 on 25/5/20.
 */
public class DelegateRenderType extends RenderType {

    protected RenderType parent;

    public DelegateRenderType(RenderType parent) {
        this(parent, parent.format());
    }

    public DelegateRenderType(RenderType parent, VertexFormat format) {
        super(parent.name, format, parent.mode(), parent.bufferSize(), parent.affectsCrumbling(), parent.sortOnUpload, null, null);
        this.parent = parent;
    }

    @Override
    public void end(BufferBuilder p_228631_1_, int p_228631_2_, int p_228631_3_, int p_228631_4_) {
        parent.end(p_228631_1_, p_228631_2_, p_228631_3_, p_228631_4_);
    }

    @Override
    public void setupRenderState() {
        parent.setupRenderState();
    }

    @Override
    public void clearRenderState() {
        parent.clearRenderState();
    }
}
