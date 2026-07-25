//package codechicken.lib.render;
//
//import com.mojang.blaze3d.buffers.GpuBuffer;
//import com.mojang.blaze3d.systems.RenderPass;
//import com.mojang.blaze3d.systems.RenderSystem;
//import com.mojang.blaze3d.vertex.*;
//import net.minecraft.client.renderer.rendertype.RenderType;
//import net.minecraft.util.Mth;
//import org.jetbrains.annotations.Nullable;
//import org.joml.Vector3f;
//import org.joml.Vector4f;
//import org.lwjgl.system.MemoryUtil;
//
//import java.util.OptionalDouble;
//import java.util.OptionalInt;
//import java.util.function.BiConsumer;
//import java.util.function.Consumer;
//import java.util.function.IntConsumer;
//
///**
// * Created by covers1624 on 11/3/25.
// */
//public class CachedModel {
//
//    private final String label;
//    private final RenderType.CompositeRenderType renderType;
//    private final BiConsumer<VertexFormat, VertexConsumer> factory;
//
//    private final ByteBufferBuilder builder;
//
//    private boolean dirty = true;
//    private @Nullable GpuBuffer vertexBuffer;
//
//    private @Nullable IndexBuffer indexBuffer;
//
//    public CachedModel(String label, RenderType renderType, BiConsumer<VertexFormat, VertexConsumer> factory) {
//        this.label = label;
//        this.renderType = (RenderType.CompositeRenderType) renderType;
//        this.factory = factory;
//
//        builder = new ByteBufferBuilder(renderType.bufferSize());
//    }
//
//    public void setDirty() {
//        dirty = true;
//    }
//
//    public void render(Consumer<RenderPass> cons) {
//        renderType.setupRenderState();
//        if (dirty) {
//            // Re-build the mesh data if we are marked dirty, we cache the underlying native byte buffer used,
//            // we may wish to consider a cleanup heuristic, after 5 mins or smth.
//            builder.discard();
//            BufferBuilder buffer = new BufferBuilder(builder, renderType.mode(), renderType.format());
//            factory.accept(renderType.format(), buffer);
//            uploadMesh(buffer.buildOrThrow());
//            builder.discard();
//            dirty = false;
//        }
//        // These should be impossible, uploadMesh always sets both.
//        assert vertexBuffer != null;
//        assert indexBuffer != null;
//
//        // Mirrors all the logic in RenderType.CompositeRenderType#render
//        // However, uses our cached gpu buffers, and fires a callback to modify the RenderPass before draw.
//        var target = renderType.state.outputState.getRenderTarget();
//        var colourTexture = RenderSystem.outputColorTextureOverride != null ? RenderSystem.outputColorTextureOverride : target.getColorTextureView();
//        var depthTexture = target.useDepth ? (RenderSystem.outputDepthTextureOverride != null ? RenderSystem.outputDepthTextureOverride : target.getDepthTextureView()) : null;
//
//        var dynamicTransforms = RenderSystem.getDynamicUniforms()
//                .writeTransform(
//                        RenderSystem.getModelViewMatrix(),
//                        new Vector4f(1.0F, 1.0F, 1.0F, 1.0F),
//                        new Vector3f(),
//                        RenderSystem.getTextureMatrix(),
//                        RenderSystem.getShaderLineWidth()
//                );
//
//        try (var pass = RenderSystem.getDevice()
//                .createCommandEncoder()
//                .createRenderPass(() -> "CachedModel draw: " + label, colourTexture, OptionalInt.empty(), depthTexture, OptionalDouble.empty())) {
//            pass.setPipeline(renderType.pipeline());
//            var scissorState = RenderSystem.getScissorStateForRenderTypeDraws();
//            if (scissorState.enabled()) {
//                pass.enableScissor(scissorState.x(), scissorState.y(), scissorState.width(), scissorState.height());
//            }
//
//            RenderSystem.bindDefaultUniforms(pass);
//            pass.setUniform("DynamicTransforms", dynamicTransforms);
//            cons.accept(pass);
//
//            pass.setVertexBuffer(0, vertexBuffer);
//
//            for (int i = 0; i < 12; i++) {
//                var texture = RenderSystem.getShaderTexture(i);
//                if (texture != null) {
//                    pass.bindSampler("Sampler" + i, texture);
//                }
//            }
//
//            pass.setIndexBuffer(indexBuffer.buffer, indexBuffer.type);
//            pass.drawIndexed(0, 0, indexBuffer.indexCount(), 1);
//        }
//        renderType.clearRenderState();
//    }
//
//    private void uploadMesh(MeshData mesh) {
//        try (mesh) {
//            vertexBuffer = VertexFormat.uploadToBuffer(
//                    vertexBuffer,
//                    mesh.vertexBuffer(),
//                    GpuBuffer.USAGE_VERTEX | GpuBuffer.USAGE_COPY_DST,
//                    () -> "Vertex Buffer for CachedModel: " + label
//            );
//
//            if (mesh.indexBuffer() == null) {
//                indexBuffer = buildIndexBuffer(
//                        indexBuffer != null ? indexBuffer.buffer : null,
//                        mesh.drawState().mode(),
//                        mesh.drawState().indexCount()
//                );
//            } else {
//                indexBuffer = new IndexBuffer(
//                        VertexFormat.uploadToBuffer(
//                                indexBuffer != null ? indexBuffer.buffer : null,
//                                mesh.vertexBuffer(),
//                                GpuBuffer.USAGE_INDEX | GpuBuffer.USAGE_COPY_DST,
//                                () -> "Index Buffer for CachedModel: " + label
//                        ),
//                        mesh.drawState().indexType(),
//                        mesh.drawState().indexCount()
//                );
//            }
//        }
//    }
//
//    // Mostly mirrors RenderSystem.AutoStorageIndexBuffer, however, does not over-provision the buffer.
//    private IndexBuffer buildIndexBuffer(@Nullable GpuBuffer indexBuffer, VertexFormat.Mode mode, int indexCount) {
//        var gen = getGenForMode(mode);
//
//        var type = VertexFormat.IndexType.least(indexCount);
//        var buffer = MemoryUtil.memAlloc(Mth.roundToward(indexCount * type.bytes, 4));
//        IntConsumer inserter = switch (type) {
//            case SHORT -> e -> buffer.putShort((short) e);
//            case INT -> buffer::putInt;
//        };
//
//        try {
//            for (int i = 0; i < indexCount; i += gen.indexStride) {
//                gen.func.buildIndex(inserter, i * gen.vertexStride / gen.indexStride);
//            }
//
//            buffer.flip();
//
//            return new IndexBuffer(
//                    VertexFormat.uploadToBuffer(
//                            indexBuffer,
//                            buffer,
//                            GpuBuffer.USAGE_INDEX | GpuBuffer.USAGE_COPY_DST,
//                            () -> "Index Buffer for CachedModel: " + label
//                    ),
//                    type,
//                    indexCount
//            );
//        } finally {
//            MemoryUtil.memFree(buffer);
//        }
//    }
//
//    private static IndexGen getGenForMode(VertexFormat.Mode mode) {
//        return switch (mode) {
//            case QUADS -> new IndexGen(4, 6, (cons, i) -> {
//                cons.accept(i);
//                cons.accept(i + 1);
//                cons.accept(i + 2);
//                cons.accept(i + 2);
//                cons.accept(i + 3);
//                cons.accept(i);
//            });
//            case LINES -> new IndexGen(4, 6, (cons, i) -> {
//                cons.accept(i);
//                cons.accept(i + 1);
//                cons.accept(i + 2);
//                cons.accept(i + 3);
//                cons.accept(i + 2);
//                cons.accept(i + 1);
//            });
//            default -> new IndexGen(1, 1, IntConsumer::accept);
//        };
//    }
//
//    private record IndexGen(int vertexStride, int indexStride, GenFunc func) {
//    }
//
//    private interface GenFunc {
//        void buildIndex(IntConsumer cons, int i);
//    }
//
//    private record IndexBuffer(GpuBuffer buffer, VertexFormat.IndexType type, int indexCount) { }
//}
