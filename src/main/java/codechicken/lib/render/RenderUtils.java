package codechicken.lib.render;

import codechicken.lib.render.buffer.TransformingVertexBuilder;
import codechicken.lib.util.SneakyUtils;
import codechicken.lib.vec.*;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Material;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

//TODO Document and reorder things in here.
public class RenderUtils {

    private static final Vector3[] vectors = new Vector3[8];
    private static net.minecraft.client.renderer.entity.ItemRenderer uniformRenderItem;
    private static boolean hasInitRenderItem;

    @Deprecated
    private static ItemEntity entityItem;

    static {
        for (int i = 0; i < vectors.length; i++) {
            vectors[i] = new Vector3();
        }

        entityItem = new ItemEntity(EntityType.ITEM, null);
        entityItem.hoverStart = 0;
    }

    @Deprecated
    private static void loadItemRenderer() {
        if (!hasInitRenderItem) {
            Minecraft minecraft = Minecraft.getInstance();
            uniformRenderItem = new net.minecraft.client.renderer.entity.ItemRenderer(minecraft.getRenderManager(), minecraft.getItemRenderer());
            hasInitRenderItem = true;
        }
    }

    public static RenderType getFluidRenderType() {
        return RenderType.makeType("ccl:fluid_render", DefaultVertexFormats.POSITION_COLOR_TEX, GL11.GL_QUADS, 256, RenderType.State.getBuilder()//
                .texture(RenderType.BLOCK_SHEET)//
                .transparency(RenderType.TRANSLUCENT_TRANSPARENCY)//
                .texturing(new RenderState.TexturingState("disable_lighting", RenderSystem::disableLighting, SneakyUtils::none))//
                .build(false)//
        );
    }

    public static void renderFluidCuboid(CCRenderState ccrs, Matrix4 mat, RenderType renderType, IRenderTypeBuffer getter, FluidStack stack, Cuboid6 bound, double capacity, double res) {
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
        ccrs.bind(renderType, getter);
        ccrs.baseColour = attributes.getColor(stack) << 8 | alpha;
        makeFluidModel(bound, material.getSprite(), res).render(ccrs, mat);
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

        double u1 = icon.getMinU();
        double du = icon.getMaxU() - icon.getMinU();
        double v2 = icon.getMaxV();
        double dv = icon.getMaxV() - icon.getMinV();

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
     * @param builder The {@link IVertexBuilder}
     * @param c       The {@link Cuboid6}
     * @param r       Red color.
     * @param g       Green color.
     * @param b       Blue Color.
     * @param a       Alpha channel.
     */
    public static void bufferCuboidSolid(IVertexBuilder builder, Cuboid6 c, float r, float g, float b, float a) {
        builder.pos(c.min.x, c.max.y, c.min.z).color(r, g, b, a).endVertex();
        builder.pos(c.max.x, c.max.y, c.min.z).color(r, g, b, a).endVertex();
        builder.pos(c.max.x, c.min.y, c.min.z).color(r, g, b, a).endVertex();
        builder.pos(c.min.x, c.min.y, c.min.z).color(r, g, b, a).endVertex();

        builder.pos(c.min.x, c.min.y, c.max.z).color(r, g, b, a).endVertex();
        builder.pos(c.max.x, c.min.y, c.max.z).color(r, g, b, a).endVertex();
        builder.pos(c.max.x, c.max.y, c.max.z).color(r, g, b, a).endVertex();
        builder.pos(c.min.x, c.max.y, c.max.z).color(r, g, b, a).endVertex();

        builder.pos(c.min.x, c.min.y, c.min.z).color(r, g, b, a).endVertex();
        builder.pos(c.max.x, c.min.y, c.min.z).color(r, g, b, a).endVertex();
        builder.pos(c.max.x, c.min.y, c.max.z).color(r, g, b, a).endVertex();
        builder.pos(c.min.x, c.min.y, c.max.z).color(r, g, b, a).endVertex();

        builder.pos(c.min.x, c.max.y, c.max.z).color(r, g, b, a).endVertex();
        builder.pos(c.max.x, c.max.y, c.max.z).color(r, g, b, a).endVertex();
        builder.pos(c.max.x, c.max.y, c.min.z).color(r, g, b, a).endVertex();
        builder.pos(c.min.x, c.max.y, c.min.z).color(r, g, b, a).endVertex();

        builder.pos(c.min.x, c.min.y, c.max.z).color(r, g, b, a).endVertex();
        builder.pos(c.min.x, c.max.y, c.max.z).color(r, g, b, a).endVertex();
        builder.pos(c.min.x, c.max.y, c.min.z).color(r, g, b, a).endVertex();
        builder.pos(c.min.x, c.min.y, c.min.z).color(r, g, b, a).endVertex();

        builder.pos(c.max.x, c.min.y, c.min.z).color(r, g, b, a).endVertex();
        builder.pos(c.max.x, c.max.y, c.min.z).color(r, g, b, a).endVertex();
        builder.pos(c.max.x, c.max.y, c.max.z).color(r, g, b, a).endVertex();
        builder.pos(c.max.x, c.min.y, c.max.z).color(r, g, b, a).endVertex();
    }

    public static void bufferHitbox(Matrix4 mat, IRenderTypeBuffer getter, ActiveRenderInfo renderInfo, Cuboid6 cuboid) {
        Vec3d projectedView = renderInfo.getProjectedView();
        bufferHitBox(mat.copy().translate(-projectedView.x, -projectedView.y, -projectedView.z), getter, cuboid);
    }

    public static void bufferHitBox(Matrix4 mat, IRenderTypeBuffer getter, Cuboid6 cuboid) {
        IVertexBuilder builder = new TransformingVertexBuilder(getter.getBuffer(RenderType.getLines()), mat);
        bufferCuboidOutline(builder, cuboid.copy().expand(0.0020000000949949026D), 0.0F, 0.0F, 0.0F, 0.4F);
    }

    public static void bufferCuboidOutline(IVertexBuilder builder, Cuboid6 c, float r, float g, float b, float a) {
        builder.pos(c.min.x, c.min.y, c.min.z).color(r, g, b, a).endVertex();
        builder.pos(c.max.x, c.min.y, c.min.z).color(r, g, b, a).endVertex();
        builder.pos(c.max.x, c.min.y, c.min.z).color(r, g, b, a).endVertex();
        builder.pos(c.max.x, c.min.y, c.max.z).color(r, g, b, a).endVertex();
        builder.pos(c.max.x, c.min.y, c.max.z).color(r, g, b, a).endVertex();
        builder.pos(c.min.x, c.min.y, c.max.z).color(r, g, b, a).endVertex();
        builder.pos(c.min.x, c.min.y, c.max.z).color(r, g, b, a).endVertex();
        builder.pos(c.min.x, c.min.y, c.min.z).color(r, g, b, a).endVertex();
        builder.pos(c.min.x, c.max.y, c.min.z).color(r, g, b, a).endVertex();
        builder.pos(c.max.x, c.max.y, c.min.z).color(r, g, b, a).endVertex();
        builder.pos(c.max.x, c.max.y, c.min.z).color(r, g, b, a).endVertex();
        builder.pos(c.max.x, c.max.y, c.max.z).color(r, g, b, a).endVertex();
        builder.pos(c.max.x, c.max.y, c.max.z).color(r, g, b, a).endVertex();
        builder.pos(c.min.x, c.max.y, c.max.z).color(r, g, b, a).endVertex();
        builder.pos(c.min.x, c.max.y, c.max.z).color(r, g, b, a).endVertex();
        builder.pos(c.min.x, c.max.y, c.min.z).color(r, g, b, a).endVertex();
        builder.pos(c.min.x, c.min.y, c.min.z).color(r, g, b, a).endVertex();
        builder.pos(c.min.x, c.max.y, c.min.z).color(r, g, b, a).endVertex();
        builder.pos(c.max.x, c.min.y, c.min.z).color(r, g, b, a).endVertex();
        builder.pos(c.max.x, c.max.y, c.min.z).color(r, g, b, a).endVertex();
        builder.pos(c.max.x, c.min.y, c.max.z).color(r, g, b, a).endVertex();
        builder.pos(c.max.x, c.max.y, c.max.z).color(r, g, b, a).endVertex();
        builder.pos(c.min.x, c.min.y, c.max.z).color(r, g, b, a).endVertex();
        builder.pos(c.min.x, c.max.y, c.max.z).color(r, g, b, a).endVertex();
    }

    public static void bufferShapeHitBox(Matrix4 mat, IRenderTypeBuffer buffers, ActiveRenderInfo renderInfo, VoxelShape shape) {
        Vec3d projectedView = renderInfo.getProjectedView();
        bufferShapeHitBox(mat.copy().translate(-projectedView.x, -projectedView.y, -projectedView.z), buffers, shape);
    }

    public static void bufferShapeHitBox(Matrix4 mat, IRenderTypeBuffer buffers, VoxelShape shape) {
        IVertexBuilder builder = new TransformingVertexBuilder(buffers.getBuffer(RenderType.getLines()), mat);
        bufferShapeOutline(builder, shape, 0.0F, 0.0F, 0.0F, 0.4F);
    }

    public static void bufferShapeOutline(IVertexBuilder builder, VoxelShape shape, float r, float g, float b, float a) {
        shape.forEachEdge((x1, y1, z1, x2, y2, z2) -> {
            builder.pos(x1, y1, z1).color(r, g, b, a).endVertex();
            builder.pos(x2, y2, z2).color(r, g, b, a).endVertex();
        });
    }

    public static Matrix4 getMatrix(Matrix4 in, Vector3 translation, Rotation rotation, double scale) {
        return in.translate(translation).scale(scale).rotate(rotation);
    }

    @Deprecated
    public static Matrix4 getMatrix(Vector3 translation, Rotation rotation, double scale) {
        return getMatrix(new Matrix4(), translation, rotation, scale);
    }

    //    /**
    //     * Renders items and blocks in the world at 0,0,0 with transformations that size them appropriately
    //     */
    //    public static void renderItemUniform(ItemStack item) {
    //        renderItemUniform(item, 0);
    //    }

    //    /*
    //     * Renders items and blocks in the world at 0,0,0 with transformations that size them appropriately
    //     *
    //     * @param spin The spin angle of the item around the y axis in degrees
    //     */
    //    public static void renderItemUniform(ItemStack item, double spin) {
    //        loadItemRenderer();
    //
    //        GlStateManager.color4f(1, 1, 1, 1);
    //
    //        entityItem.setItem(item);
    //        uniformRenderItem.doRender(entityItem, 0, 0.06, 0, 0, (float) (spin * 9 / Math.PI));
    //    }

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
