package codechicken.lib.render;

import codechicken.lib.render.buffer.TransformingVertexConsumer;
import codechicken.lib.vec.*;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

//TODO Document and reorder things in here.
public class RenderUtils {

    private static final Vector3[] vectors = new Vector3[8];

    static {
        for (int i = 0; i < vectors.length; i++) {
            vectors[i] = new Vector3();
        }
    }

    public static RenderType getFluidRenderType() {
        return RenderType.create("ccl:fluid_render", DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP, VertexFormat.Mode.QUADS, 256, RenderType.CompositeState.builder()
                .setShaderState(RenderType.POSITION_COLOR_TEX_LIGHTMAP_SHADER)
                .setTextureState(RenderType.BLOCK_SHEET)
                .setTransparencyState(RenderType.TRANSLUCENT_TRANSPARENCY)
                .setLightmapState(RenderType.LIGHTMAP)
                .createCompositeState(false)
        );
    }

    public static void renderFluidCuboid(CCRenderState ccrs, Matrix4 mat, RenderType renderType, MultiBufferSource source, FluidStack stack, Cuboid6 bound, double capacity, double res) {
        if (stack.isEmpty()) {
            return;
        }
        int alpha = 255;
        FluidAttributes attributes = stack.getFluid().getAttributes();
        if (attributes.isGaseous(stack)) {
            alpha = (int) (Math.pow(capacity, 0.4) * 255);
        } else {
            bound.max.y = bound.min.y + (bound.max.y - bound.min.y) * capacity;
        }
        Material material = ForgeHooksClient.getBlockMaterial(attributes.getStillTexture(stack));
        ccrs.bind(renderType, source);
        ccrs.baseColour = attributes.getColor(stack) << 8 | alpha;
        makeFluidModel(bound, material.sprite(), res).render(ccrs, mat);
    }

    public static CCModel makeFluidModel(Cuboid6 bound, TextureAtlasSprite tex, double res) {
        CCModel model = CCModel.newModel(GL11.GL_QUADS);
        List<Vertex5> verts = new ArrayList<>();
        makeFluidCuboid(verts, bound, tex, res);
        model.verts = verts.toArray(new Vertex5[0]);
        return model;
    }

    public static void makeFluidCuboid(List<Vertex5> vertices, Cuboid6 bound, TextureAtlasSprite tex, double res) {
        makeFluidQuadVertices(vertices, new Vector3(bound.min.x, bound.min.y, bound.min.z), new Vector3(bound.max.x, bound.min.y, bound.min.z), new Vector3(bound.max.x, bound.min.y, bound.max.z), new Vector3(bound.min.x, bound.min.y, bound.max.z), tex, res);
        makeFluidQuadVertices(vertices, new Vector3(bound.min.x, bound.max.y, bound.min.z), new Vector3(bound.min.x, bound.max.y, bound.max.z), new Vector3(bound.max.x, bound.max.y, bound.max.z), new Vector3(bound.max.x, bound.max.y, bound.min.z), tex, res);
        makeFluidQuadVertices(vertices, new Vector3(bound.min.x, bound.max.y, bound.min.z), new Vector3(bound.min.x, bound.min.y, bound.min.z), new Vector3(bound.min.x, bound.min.y, bound.max.z), new Vector3(bound.min.x, bound.max.y, bound.max.z), tex, res);
        makeFluidQuadVertices(vertices, new Vector3(bound.max.x, bound.max.y, bound.max.z), new Vector3(bound.max.x, bound.min.y, bound.max.z), new Vector3(bound.max.x, bound.min.y, bound.min.z), new Vector3(bound.max.x, bound.max.y, bound.min.z), tex, res);
        makeFluidQuadVertices(vertices, new Vector3(bound.max.x, bound.max.y, bound.min.z), new Vector3(bound.max.x, bound.min.y, bound.min.z), new Vector3(bound.min.x, bound.min.y, bound.min.z), new Vector3(bound.min.x, bound.max.y, bound.min.z), tex, res);
        makeFluidQuadVertices(vertices, new Vector3(bound.min.x, bound.max.y, bound.max.z), new Vector3(bound.min.x, bound.min.y, bound.max.z), new Vector3(bound.max.x, bound.min.y, bound.max.z), new Vector3(bound.max.x, bound.max.y, bound.max.z), tex, res);
    }

    public static void makeFluidQuadVertices(List<Vertex5> vertices, Vector3 point1, Vector3 point2, Vector3 point3, Vector3 point4, TextureAtlasSprite icon, double res) {
        makeFluidQuadVertices(vertices, point2, vectors[0].set(point4).subtract(point1), vectors[1].set(point1).subtract(point2), icon, res);
    }

    public static void makeFluidQuadVertices(List<Vertex5> vertices, Vector3 base, Vector3 wide, Vector3 high, TextureAtlasSprite icon, double res) {
        Vector3 a = new Vector3();
        Vector3 b = new Vector3();
        Vector3 c = new Vector3();
        Vector3 d = new Vector3();

        double u1 = icon.getU0();
        double du = icon.getU1() - icon.getU0();
        double v2 = icon.getV1();
        double dv = icon.getV1() - icon.getV0();

        double wlen = wide.mag();
        double hlen = high.mag();

        double x = 0;
        while (x < wlen) {
            double rx = wlen - x;
            if (rx > res) {
                rx = res;
            }

            double y = 0;
            while (y < hlen) {
                double ry = hlen - y;
                if (ry > res) {
                    ry = res;
                }

                Vector3 dx1 = a.set(wide).multiply(x / wlen);
                Vector3 dx2 = b.set(wide).multiply((x + rx) / wlen);
                Vector3 dy1 = c.set(high).multiply(y / hlen);
                Vector3 dy2 = d.set(high).multiply((y + ry) / hlen);

                vertices.add(new Vertex5(base.x + dx1.x + dy2.x, base.y + dx1.y + dy2.y, base.z + dx1.z + dy2.z, u1, v2 - ry / res * dv));
                vertices.add(new Vertex5(base.x + dx1.x + dy1.x, base.y + dx1.y + dy1.y, base.z + dx1.z + dy1.z, u1, v2));
                vertices.add(new Vertex5(base.x + dx2.x + dy1.x, base.y + dx2.y + dy1.y, base.z + dx2.z + dy1.z, (u1 + rx / res * du), v2));
                vertices.add(new Vertex5(base.x + dx2.x + dy2.x, base.y + dx2.y + dy2.y, base.z + dx2.z + dy2.z, (u1 + rx / res * du), v2 - ry / res * dv));

                y += ry;
            }

            x += rx;
        }
    }

    /**
     * Builds a solid cuboid.
     * Expects VertexFormat of POSITION_COLOR.
     * If you need anything more specialized, Use a {@link CCModel}.
     *
     * @param builder The {@link VertexConsumer}
     * @param c       The {@link Cuboid6}
     * @param r       Red color.
     * @param g       Green color.
     * @param b       Blue Color.
     * @param a       Alpha channel.
     */
    public static void bufferCuboidSolid(VertexConsumer builder, Cuboid6 c, float r, float g, float b, float a) {
        builder.vertex(c.min.x, c.max.y, c.min.z).color(r, g, b, a).endVertex();
        builder.vertex(c.max.x, c.max.y, c.min.z).color(r, g, b, a).endVertex();
        builder.vertex(c.max.x, c.min.y, c.min.z).color(r, g, b, a).endVertex();
        builder.vertex(c.min.x, c.min.y, c.min.z).color(r, g, b, a).endVertex();

        builder.vertex(c.min.x, c.min.y, c.max.z).color(r, g, b, a).endVertex();
        builder.vertex(c.max.x, c.min.y, c.max.z).color(r, g, b, a).endVertex();
        builder.vertex(c.max.x, c.max.y, c.max.z).color(r, g, b, a).endVertex();
        builder.vertex(c.min.x, c.max.y, c.max.z).color(r, g, b, a).endVertex();

        builder.vertex(c.min.x, c.min.y, c.min.z).color(r, g, b, a).endVertex();
        builder.vertex(c.max.x, c.min.y, c.min.z).color(r, g, b, a).endVertex();
        builder.vertex(c.max.x, c.min.y, c.max.z).color(r, g, b, a).endVertex();
        builder.vertex(c.min.x, c.min.y, c.max.z).color(r, g, b, a).endVertex();

        builder.vertex(c.min.x, c.max.y, c.max.z).color(r, g, b, a).endVertex();
        builder.vertex(c.max.x, c.max.y, c.max.z).color(r, g, b, a).endVertex();
        builder.vertex(c.max.x, c.max.y, c.min.z).color(r, g, b, a).endVertex();
        builder.vertex(c.min.x, c.max.y, c.min.z).color(r, g, b, a).endVertex();

        builder.vertex(c.min.x, c.min.y, c.max.z).color(r, g, b, a).endVertex();
        builder.vertex(c.min.x, c.max.y, c.max.z).color(r, g, b, a).endVertex();
        builder.vertex(c.min.x, c.max.y, c.min.z).color(r, g, b, a).endVertex();
        builder.vertex(c.min.x, c.min.y, c.min.z).color(r, g, b, a).endVertex();

        builder.vertex(c.max.x, c.min.y, c.min.z).color(r, g, b, a).endVertex();
        builder.vertex(c.max.x, c.max.y, c.min.z).color(r, g, b, a).endVertex();
        builder.vertex(c.max.x, c.max.y, c.max.z).color(r, g, b, a).endVertex();
        builder.vertex(c.max.x, c.min.y, c.max.z).color(r, g, b, a).endVertex();
    }

    public static void bufferHitbox(Matrix4 mat, MultiBufferSource getter, Camera renderInfo, Cuboid6 cuboid) {
        Vec3 projectedView = renderInfo.getPosition();
        bufferHitBox(mat.copy().translate(-projectedView.x, -projectedView.y, -projectedView.z), getter, cuboid);
    }

    public static void bufferHitBox(Matrix4 mat, MultiBufferSource getter, Cuboid6 cuboid) {
        VertexConsumer builder = new TransformingVertexConsumer(getter.getBuffer(RenderType.lines()), mat);
        bufferCuboidOutline(builder, cuboid.copy().expand(0.0020000000949949026D), 0.0F, 0.0F, 0.0F, 0.4F);
    }

    public static void bufferCuboidOutline(VertexConsumer builder, Cuboid6 c, float r, float g, float b, float a) {
        bufferLinePair(builder, c.min.x, c.min.y, c.min.z, c.max.x, c.min.y, c.min.z, r, g, b, a);
        bufferLinePair(builder, c.max.x, c.min.y, c.min.z, c.max.x, c.min.y, c.max.z, r, g, b, a);
        bufferLinePair(builder, c.max.x, c.min.y, c.max.z, c.min.x, c.min.y, c.max.z, r, g, b, a);
        bufferLinePair(builder, c.min.x, c.min.y, c.max.z, c.min.x, c.min.y, c.min.z, r, g, b, a);
        bufferLinePair(builder, c.min.x, c.max.y, c.min.z, c.max.x, c.max.y, c.min.z, r, g, b, a);
        bufferLinePair(builder, c.max.x, c.max.y, c.min.z, c.max.x, c.max.y, c.max.z, r, g, b, a);
        bufferLinePair(builder, c.max.x, c.max.y, c.max.z, c.min.x, c.max.y, c.max.z, r, g, b, a);
        bufferLinePair(builder, c.min.x, c.max.y, c.max.z, c.min.x, c.max.y, c.min.z, r, g, b, a);
        bufferLinePair(builder, c.min.x, c.min.y, c.min.z, c.min.x, c.max.y, c.min.z, r, g, b, a);
        bufferLinePair(builder, c.max.x, c.min.y, c.min.z, c.max.x, c.max.y, c.min.z, r, g, b, a);
        bufferLinePair(builder, c.max.x, c.min.y, c.max.z, c.max.x, c.max.y, c.max.z, r, g, b, a);
        bufferLinePair(builder, c.min.x, c.min.y, c.max.z, c.min.x, c.max.y, c.max.z, r, g, b, a);
    }

    public static void bufferShapeHitBox(Matrix4 mat, MultiBufferSource buffers, Camera renderInfo, VoxelShape shape) {
        Vec3 projectedView = renderInfo.getPosition();
        bufferShapeHitBox(mat.copy().translate(-projectedView.x, -projectedView.y, -projectedView.z), buffers, shape);
    }

    public static void bufferShapeHitBox(Matrix4 mat, MultiBufferSource buffers, VoxelShape shape) {
        VertexConsumer builder = new TransformingVertexConsumer(buffers.getBuffer(RenderType.lines()), mat);
        bufferShapeOutline(builder, shape, 0.0F, 0.0F, 0.0F, 0.4F);
    }

    public static void bufferShapeOutline(VertexConsumer builder, VoxelShape shape, float r, float g, float b, float a) {
        shape.forAllEdges((x1, y1, z1, x2, y2, z2) -> {
            bufferLinePair(builder, x1, y1, z1, x2, y2, z2, r, g, b, a);
        });
    }

    private static void bufferLinePair(VertexConsumer builder, double x1, double y1, double z1, double x2, double y2, double z2, float r, float g, float b, float a) {
        Vector3 v1 = vectors[0].set(x1, y1, z1).subtract(x2, y2, z2);
        double d = v1.mag();
        v1.divide(d);
        builder.vertex(x1, y1, z1).color(r, g, b, a).normal((float) v1.x, (float) v1.y, (float) v1.z).endVertex();
        builder.vertex(x2, y2, z2).color(r, g, b, a).normal((float) v1.x, (float) v1.y, (float) v1.z).endVertex();
    }

    public static Matrix4 getMatrix(Matrix4 in, Vector3 translation, Rotation rotation, double scale) {
        return in.translate(translation).scale(scale).rotate(rotation);
    }

    @Deprecated
    public static Matrix4 getMatrix(Vector3 translation, Rotation rotation, double scale) {
        return getMatrix(new Matrix4(), translation, rotation, scale);
    }

    public static float getPearlBob(double time) {
        return (float) Math.sin(time / 25 * 3.141593) * 0.1F;
    }

    public static int getTimeOffset(BlockPos pos) {
        return getTimeOffset(pos.getX(), pos.getY(), pos.getZ());
    }

    public static int getTimeOffset(int x, int y, int z) {
        return x * 3 + y * 5 + z * 9;
    }
}
