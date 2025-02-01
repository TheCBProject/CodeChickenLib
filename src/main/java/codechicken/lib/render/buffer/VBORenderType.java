package codechicken.lib.render.buffer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;

import java.util.LinkedList;
import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Objects.requireNonNull;
import static net.covers1624.quack.util.SneakyUtils.none;

/**
 * A RenderType that is backed by a VertexBufferObject used for Cached rendering.
 * <p>
 * Created by covers1624 on 25/5/20.
 */
public class VBORenderType extends DelegateRenderType implements AutoCloseable {

    private final BiConsumer<VertexFormat, BufferBuilder> factory;
    private final VertexBuffer vertexBuffer = new VertexBuffer(VertexBuffer.Usage.DYNAMIC);
    private final ByteBufferBuilder builder;
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
        builder = new ByteBufferBuilder(bufferSize());
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

    /**
     * Called to draw a VBORenderType.
     *
     * @param buffers The buffers instance.
     */
    public void draw(MultiBufferSource buffers) {
        draw(buffers, this);
    }

    private static void draw(MultiBufferSource buffers, RenderType type) {
        var cons = buffers.getBuffer(type);
        // Add some garbage so a mesh gets generated.
        for (int i = 0; i < type.mode().primitiveLength; i++) {
            cons.addVertex(0F, 0F, 0F, 0, 0F, 0F, 0, 0, 0F, 0F, 0F);
        }
    }

    private void render() {
        rebuild();
        vertexBuffer.bind();
        vertexBuffer.drawWithShader(RenderSystem.getModelViewMatrix(), RenderSystem.getProjectionMatrix(), requireNonNull(RenderSystem.getShader()));
    }

    private void rebuild() {
        if (!dirty) return;
        builder.discard();
        BufferBuilder buffer = new BufferBuilder(builder, mode(), format());
        factory.accept(format(), buffer);
        vertexBuffer.bind();
        vertexBuffer.upload(buffer.build());
        builder.clear();
        dirty = false;
    }

    @Override
    public void draw(MeshData meshData) {
        try (meshData) {
            setupRenderState();
            render();
            clearRenderState();
        }
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
        public void draw(MeshData meshData) {
            try (meshData) {
                setupRenderState();
                render();
                clearRenderState();
            }
        }

        @Override
        public void clearRenderState() {
            for (RenderStateShard state : shards) {
                state.clearRenderState();
            }
            super.clearRenderState();
        }

        public void draw(MultiBufferSource buffers) {
            VBORenderType.draw(buffers, this);
        }
    }
}
