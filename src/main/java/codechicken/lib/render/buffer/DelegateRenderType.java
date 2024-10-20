package codechicken.lib.render.buffer;

import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.covers1624.quack.util.SneakyUtils;
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
        super(parent.name, format, parent.mode(), parent.bufferSize(), parent.affectsCrumbling(), parent.sortOnUpload, SneakyUtils.none(), SneakyUtils.none());
        this.parent = parent;
    }

    @Override
    public void draw(MeshData meshData) {
        parent.draw(meshData);
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
