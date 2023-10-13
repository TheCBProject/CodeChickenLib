package codechicken.lib.render.buffer;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexSorting;
import net.minecraft.client.renderer.RenderType;
import org.jetbrains.annotations.NotNull;

/**
 * Created by covers1624 on 25/5/20.
 */
public class DelegateRenderType extends RenderType {
    // @formatter:off
    private static final Runnable EMPTY_RUNNABLE = () -> {};
    // @formatter:on

    protected RenderType parent;

    public DelegateRenderType(RenderType parent) {
        this(parent, parent.format());
    }

    public DelegateRenderType(RenderType parent, VertexFormat format) {
        super(parent.name, format, parent.mode(), parent.bufferSize(), parent.affectsCrumbling(), parent.sortOnUpload, EMPTY_RUNNABLE, EMPTY_RUNNABLE);
        this.parent = parent;
    }

    @Override
    public void end(@NotNull BufferBuilder bufferBuilder, @NotNull VertexSorting vertexSorting) {
        parent.end(bufferBuilder, vertexSorting);
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
