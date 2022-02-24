/*
package codechicken.lib.render.buffer;

import codechicken.lib.vec.Matrix4;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.vertex.VertexFormat;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.function.BiConsumer;

import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;

*/
/**
 * A RenderType that is backed by a VertexBufferObject.
 * This has a few unique limited applications, mainly related to Item rendering,
 * although it _can_ be used for blocks.
 * Drawbacks:
 * - Doesn't support Overlay rendering. (block breaking)
 * - If LightMap support is required, it must be removed from the VertexFormat.
 * <p>
 * Created by covers1624 on 25/5/20.
 *//*

public class VBORenderType extends DelegateRenderType {

    private final BiConsumer<VertexFormat, BufferBuilder> factory;
    private int bufferId = -1;
    private int count;

    */
/**
     * Create a new VBORenderType, delegates render state setup
     * to the provided parent, also uses the parents VertexFormat.
     *
     * @param parent  The parent, for state setup and buffer VertexFormat.
     * @param factory The Factory used to fill the BufferBuilder with data.
     *//*

    public VBORenderType(RenderType parent, BiConsumer<VertexFormat, BufferBuilder> factory) {
        this(parent, parent.format(), factory);
    }

    */
/**
     * Create a new VBORenderType, delegates render state setup
     * to the provided parent, Uses the specified VertexFormat.
     *
     * @param parent       The parent, for state setup.
     * @param bufferFormat The VertexFormat to use.
     * @param factory      The Factory used to fill the BufferBuilder with data.
     *//*

    public VBORenderType(RenderType parent, VertexFormat bufferFormat, BiConsumer<VertexFormat, BufferBuilder> factory) {
        super(parent, bufferFormat);
        this.factory = factory;
    }

    */
/**
     * Can be called runtime to have the Buffer rebuilt,
     * doing so has very limited applications and is not recommended.
     *//*

    public void rebuild() {
        if (bufferId == -1) {
            bufferId = GL15.glGenBuffers();
        }

        BufferBuilder builder = new BufferBuilder(bufferSize());
        builder.begin(mode(), format());
        factory.accept(format(), builder);
        builder.end();
        Pair<BufferBuilder.DrawState, ByteBuffer> pair = builder.popNextBuffer();
        ByteBuffer buffer = pair.getSecond();
        count = buffer.remaining() / format().getVertexSize();

        GL15.glBindBuffer(GL_ARRAY_BUFFER, bufferId);
        GL15.glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
        GL15.glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    */
/**
 * A soft clone of this VBORenderType, using the provided Matrix4.
 *
 * @param matrix The matrix.
 * @return The soft clone.
 *//*

    public MatrixVBORenderType withMatrix(Matrix4 matrix) {
        return new MatrixVBORenderType(this, matrix);
    }

    private void render() {
        if (bufferId == -1) {
            rebuild();
        }
        GL15.glBindBuffer(GL_ARRAY_BUFFER, bufferId);
        format().setupBufferState(0);
        GL15.glDrawArrays(mode(), 0, count);
        format().clearBufferState();
        GL15.glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    @Override
    public void end(BufferBuilder buffer, int cameraX, int cameraY, int cameraZ) {
        buffer.end();//We dont care about this, but we need to tell it to finish.
        buffer.popNextBuffer();

        setupRenderState();
        render();
        clearRenderState();
    }

    public static class MatrixVBORenderType extends DelegateRenderType {

        private final LinkedList<RenderState> states = new LinkedList<>();
        private final VBORenderType parent;
        private final Matrix4 matrix;
        private boolean hasLightMap = false;

        private int packedLight;

        public MatrixVBORenderType(VBORenderType parent, Matrix4 matrix) {
            super(parent);
            this.parent = parent;
            this.matrix = matrix.copy();
        }

        */
/**
 * Enables LightMap support.
 *
 * @param packedLight The PackedLightMap value.
 * @return The same RenderType.
 *//*

        public MatrixVBORenderType withLightMap(int packedLight) {
            hasLightMap = true;
            this.packedLight = packedLight;
            return this;
        }

        */
/**
 * An extra RenderState to be applied, may be a RenderType.
 *
 * @param state The state.
 * @return The same RenderType.
 *//*

        public MatrixVBORenderType withState(RenderState state) {
            states.add(state);
            return this;
        }

        @Override
        public void setupRenderState() {
            super.setupRenderState();
            states.forEach(RenderState::setupRenderState);
            RenderSystem.pushMatrix();
            matrix.glApply();
            if (hasLightMap) {
                RenderSystem.glMultiTexCoord2f(GL13.GL_TEXTURE2, packedLight & 0xFFFF, packedLight >>> 16);
            }
        }

        @Override
        public void end(BufferBuilder buffer, int cameraX, int cameraY, int cameraZ) {
            buffer.end();//We dont care about this, but we need to tell it to finish.
            buffer.popNextBuffer();

            setupRenderState();
            parent.render();
            clearRenderState();
        }

        @Override
        public void clearRenderState() {
            RenderSystem.popMatrix();
            states.forEach(RenderState::clearRenderState);
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
}
*/
