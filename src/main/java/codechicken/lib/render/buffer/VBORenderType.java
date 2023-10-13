package codechicken.lib.render.buffer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexSorting;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;
import java.util.function.BiConsumer;

import static net.covers1624.quack.util.SneakyUtils.none;

/**
 * A RenderType that is backed by a VertexBufferObject used for Instanced rendering.
 * <p>
 * Created by covers1624 on 25/5/20.
 */
public class VBORenderType extends DelegateRenderType implements AutoCloseable {

    private final BiConsumer<VertexFormat, BufferBuilder> factory;
    private final VertexBuffer vertexBuffer = new VertexBuffer(VertexBuffer.Usage.DYNAMIC);
    private final BufferBuilder builder;
    private boolean dirty = true;

    /**
     * Create a new VBORenderType, delegates render state setup
     * to the provided parent, also uses the parents VertexFormat.
     *
     * @param parent  The parent, for state setup and buffer VertexFormat.
     * @param factory The Factory used to fill the BufferBuilder with data.
     */
    public VBORenderType(RenderType parent, BiConsumer<VertexFormat, BufferBuilder> factory) {
        super(parent, parent.format());
        this.factory = factory;
        builder = new BufferBuilder(bufferSize());
    }

    /**
     * Marks this {@link VBORenderType} as needing to be re-built.
     */
    public void setDirty() {
        dirty = true;
    }

    /**
     * An extra {@link Runnable} to be applied before draw calls.
     *
     * @param action The action.
     * @return The same {@link WithCallbacks}.
     */
    public WithCallbacks withCallback(Runnable action) {
        return new WithCallbacks().withAction(action);
    }

    /**
     * An extra {@link RenderStateShard} to be applied.
     *
     * @param shard The {@link RenderStateShard}.
     * @return The same {@link WithCallbacks}.
     */
    public WithCallbacks withState(RenderStateShard shard) {
        return new WithCallbacks().withState(shard);
    }

    private void render() {
        rebuild();
        this.vertexBuffer.drawWithShader(RenderSystem.getModelViewMatrix(), RenderSystem.getProjectionMatrix(), RenderSystem.getShader());
    }

    private void rebuild() {
        if (!dirty) return;
        builder.begin(mode(), format());
        factory.accept(format(), builder);
        vertexBuffer.upload(builder.end());
        builder.clear();
        dirty = false;
    }

    @Override
    public void end(@NotNull BufferBuilder bufferBuilder, @NotNull VertexSorting vertexSorting) {
        // End buffer and discard state, we don't operate like this.
        bufferBuilder.endOrDiscardIfEmpty();
        setupRenderState();
        render();
        clearRenderState();
    }

    @Override
    public void close() throws Exception {
        vertexBuffer.close();
    }

    public class WithCallbacks extends DelegateRenderType {

        private final List<RenderStateShard> shards = new LinkedList<>();

        public WithCallbacks() {
            super(VBORenderType.this);
        }

        /**
         * An extra {@link Runnable} to be applied before draw calls.
         *
         * @param action The action.
         * @return The same {@link WithCallbacks}.
         */
        public WithCallbacks withAction(Runnable action) {
            return withState(new RenderStateShard("none", none(), none()) {
                @Override
                public void setupRenderState() {
                    action.run();
                }
            });
        }

        /**
         * An extra {@link RenderStateShard} to be applied.
         *
         * @param shard The {@link RenderStateShard}.
         * @return The same {@link WithCallbacks}.
         */
        public WithCallbacks withState(RenderStateShard shard) {
            shards.add(shard);
            return this;
        }

        @Override
        public void setupRenderState() {
            super.setupRenderState();
            for (RenderStateShard state : shards) {
                state.setupRenderState();
            }
        }

        @Override
        public void end(@NotNull BufferBuilder bufferBuilder, @NotNull VertexSorting vertexSorting) {
            // End buffer and discard state, we don't operate like this.
            bufferBuilder.endOrDiscardIfEmpty();
            setupRenderState();
            render();
            clearRenderState();
        }

        @Override
        public void clearRenderState() {
            for (RenderStateShard state : shards) {
                state.clearRenderState();
            }
            super.clearRenderState();
        }
    }
}
