package codechicken.lib.render;

import codechicken.lib.colour.Colour;
import codechicken.lib.colour.ColourRGBA;
import codechicken.lib.model.CachedFormat;
import codechicken.lib.render.buffer.ISpriteAwareVertexConsumer;
import codechicken.lib.render.buffer.TransformingVertexConsumer;
import codechicken.lib.render.lighting.LC;
import codechicken.lib.render.lighting.LightMatrix;
import codechicken.lib.render.lighting.PlanarLightModel;
import codechicken.lib.render.pipeline.CCRenderPipeline;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.render.pipeline.IVertexSource;
import codechicken.lib.render.pipeline.VertexAttribute;
import codechicken.lib.render.pipeline.attribute.*;
import codechicken.lib.vec.Matrix4;
import codechicken.lib.vec.Vector3;
import codechicken.lib.vec.Vertex5;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;

/**
 * The core of the CodeChickenLib render system.
 * Where possible assign a local var of CCRenderState to avoid millions of calls to instance();
 * Uses a ThreadLocal system to assign each thread their own CCRenderState so we can use it in Multithreaded chunk batching.
 * TODO, proper piping of BakedQuads and CCBakedQuads.
 */
public class CCRenderState {

    private static final ThreadLocal<CCRenderState> instances = ThreadLocal.withInitial(CCRenderState::new);

    //Each attrib needs to be assigned in this order to have a valid operation index.
    public final VertexAttribute<Vector3[]> normalAttrib = new NormalAttribute();
    public final VertexAttribute<int[]> colourAttrib = new ColourAttribute();
    public final VertexAttribute<int[]> lightingAttrib = new LightingAttribute();
    public final VertexAttribute<int[]> sideAttrib = new SideAttribute();
    public final VertexAttribute<LC[]> lightCoordAttrib = new LightCoordAttribute();

    //pipeline state
    public IVertexSource model;
    public int firstVertexIndex;
    public int lastVertexIndex;
    public int vertexIndex;
    public CCRenderPipeline pipeline;
    public VertexConsumer r;
    public VertexFormat fmt;
    public CachedFormat cFmt;

    //context
    /**
     * The base color, multiplied by the {@link ColourAttribute} from the bound model if present otherwise used as-is.
     */
    public int baseColour;
    /**
     * An override for the alpha colour component.
     */
    public int alphaOverride;
    /**
     * Lets the {@link LightMatrix} or {@link PlanarLightModel} know if this {@link CCRenderState} should compute lighting.
     */
    public boolean computeLighting;
    /**
     * A standard {@link LightMatrix} instance to be shared on this pipeline.
     */
    public LightMatrix lightMatrix = new LightMatrix();

    //vertex outputs
    public final Vertex5 vert = new Vertex5();
    public final Vector3 normal = new Vector3();
    public int colour;
    public int brightness;
    public int overlay;

    //attribute storage
    public int side;
    public LC lc = new LC();
    public TextureAtlasSprite sprite;

    private CCRenderState() {
        pipeline = new CCRenderPipeline(this);
    }

    public static CCRenderState instance() {
        return instances.get();
    }

    /**
     * Bind this {@link CCRenderState} instance to the {@link Tesselator} buffer
     * and prepare to start drawing vertices for the given <code>mode</code> and {@link VertexFormat}.
     *
     * @param mode   The Draw Mode.
     * @param format The {@link VertexFormat}.
     * @return The {@link BufferBuilder} instance from {@link Tesselator}.
     */
    public BufferBuilder startDrawing(VertexFormat.Mode mode, VertexFormat format) {
        BufferBuilder r = Tesselator.getInstance().getBuilder();
        r.begin(mode, format);
        bind(r);
        return r;
    }

    /**
     * Bind this {@link CCRenderState} instance to the given {@link BufferBuilder}
     * and prepare to start drawing vertices for the given <code>mode</code> and {@link VertexFormat}.
     *
     * @param mode   The Draw Mode.
     * @param format The {@link VertexFormat}.
     * @param buffer The {@link BufferBuilder} to bind to.
     * @return The same {@link BufferBuilder} that was passed in.
     */
    public BufferBuilder startDrawing(VertexFormat.Mode mode, VertexFormat format, BufferBuilder buffer) {
        buffer.begin(mode, format);
        bind(buffer);
        return buffer;
    }

    /**
     * Bind this {@link CCRenderState} instance to the given {@link BufferBuilder}.
     *
     * @param r The {@link BufferBuilder}.
     */
    public void bind(BufferBuilder r) {
        bind(r, r.format);
    }

    /**
     * Bind this {@link CCRenderState} to the given {@link VertexConsumer} and {@link VertexFormat}.
     *
     * @param consumer The {@link VertexConsumer} to bind to.
     * @param format   The {@link VertexFormat} of the {@link VertexConsumer}.
     */
    public void bind(VertexConsumer consumer, VertexFormat format) {
        r = consumer;
        fmt = format;
        cFmt = CachedFormat.lookup(format);
    }

    /**
     * Bind this {@link CCRenderState} to the given {@link RenderType}.
     *
     * @param renderType The {@link RenderType} to bind to.
     * @param source     The {@link MultiBufferSource} instance.
     */
    public void bind(RenderType renderType, MultiBufferSource source) {
        bind(source.getBuffer(renderType), renderType.format());
    }

    /**
     * Bind this {@link CCRenderState} to the given {@link RenderType}, applying
     * the given MatrixStack.
     *
     * @param renderType The {@link RenderType} to bind to.
     * @param source     The {@link MultiBufferSource} instance.
     * @param mStack     The {@link PoseStack} to apply.
     */
    public void bind(RenderType renderType, MultiBufferSource source, PoseStack mStack) {
        bind(new TransformingVertexConsumer(source.getBuffer(renderType), mStack), renderType.format());
    }

    /**
     * Bind this {@link CCRenderState} to the given {@link RenderType}, applying
     * the given MatrixStack.
     *
     * @param renderType The {@link RenderType} to bind to.
     * @param getter     The {@link MultiBufferSource} instance.
     * @param mat        The {@link Matrix4} to apply.
     */
    public void bind(RenderType renderType, MultiBufferSource getter, Matrix4 mat) {
        bind(new TransformingVertexConsumer(getter.getBuffer(renderType), mat), renderType.format());
    }

    /**
     * Resets this {@link CCRenderState} instance's pipeline and internal state.
     */
    public void reset() {
        model = null;
        pipeline.reset();
        computeLighting = true;
        colour = baseColour = alphaOverride = -1;
    }

    public void preRenderWorld(BlockAndTintGetter world, BlockPos pos) {
        this.reset();
        this.colour = 0xFFFFFFFF;
        this.setBrightness(world, pos);
    }

    public void setPipeline(IVertexOperation... ops) {
        pipeline.setPipeline(ops);
    }

    public void setPipeline(IVertexSource model, int start, int end, IVertexOperation... ops) {
        pipeline.reset();
        setModel(model, start, end);
        pipeline.setPipeline(ops);
    }

    public void bindModel(IVertexSource model) {
        if (this.model != model) {
            this.model = model;
            pipeline.rebuild();
        }
    }

    public void setModel(IVertexSource source) {
        setModel(source, 0, source.getVertexCount());
    }

    public void setModel(IVertexSource source, int start, int end) {
        bindModel(source);
        setVertexRange(start, end);
    }

    public void setVertexRange(int start, int end) {
        firstVertexIndex = start;
        lastVertexIndex = end;
    }

    public void render(IVertexOperation... ops) {
        setPipeline(ops);
        render();
    }

    public void render() {
        Vertex5[] verts = model.getVertices();
        for (vertexIndex = firstVertexIndex; vertexIndex < lastVertexIndex; vertexIndex++) {
            model.prepareVertex(this);
            vert.set(verts[vertexIndex]);
            runPipeline();
            writeVert();
        }
    }

    public void runPipeline() {
        pipeline.operate();
    }

    public void writeVert() {
        if (r instanceof ISpriteAwareVertexConsumer) {
            ((ISpriteAwareVertexConsumer) r).sprite(sprite);
        }
        ImmutableList<VertexFormatElement> elements = fmt.getElements();
        for (int e = 0; e < elements.size(); e++) {
            VertexFormatElement fmte = elements.get(e);
            switch (fmte.getUsage()) {
                case POSITION:
                    r.vertex(vert.vec.x, vert.vec.y, vert.vec.z);
                    break;
                case UV:
                    int idx = fmte.getIndex();
                    switch (idx) {
                        case 0 -> r.uv((float) vert.uv.u, (float) vert.uv.v);
                        case 1 -> r.overlayCoords(overlay);
                        case 2 -> r.uv2(brightness);
                    }
                    break;
                case COLOR:
                    if (r instanceof BufferBuilder && ((BufferBuilder) r).defaultColorSet) {
                        //-_- Fucking mojang..
                        ((BufferBuilder) r).nextElement();
                    } else {
                        r.color(colour >>> 24, colour >> 16 & 0xFF, colour >> 8 & 0xFF, alphaOverride >= 0 ? alphaOverride : colour & 0xFF);
                    }
                    break;
                case NORMAL:
                    r.normal((float) normal.x, (float) normal.y, (float) normal.z);
                    break;
                case PADDING:
                    break;
                default:
                    throw new UnsupportedOperationException("Generic vertex format element");
            }
        }
        r.endVertex();
    }

    public void setBrightness(BlockAndTintGetter world, BlockPos pos) {
        brightness = LevelRenderer.getLightColor(world, world.getBlockState(pos), pos);
    }

    public void setBrightness(Entity entity, float frameDelta) {
        brightness = Minecraft.getInstance().getEntityRenderDispatcher().getPackedLightCoords(entity, frameDelta);
    }

    public void setFluidColour(FluidStack fluidStack) {
        setFluidColour(fluidStack, 0xFF);
    }

    public void setFluidColour(FluidStack fluidStack, int alpha) {
        this.baseColour = IClientFluidTypeExtensions.of(fluidStack.getFluid()).getTintColor(fluidStack) << 8 | alpha;
    }

    public void setColour(Colour colour) {
        this.colour = colour.rgba();
    }

    public ColourRGBA getColour() {
        return new ColourRGBA(colour);
    }

    public VertexConsumer getConsumer() {
        return r;
    }

    public VertexFormat getVertexFormat() {
        return fmt;
    }

    public void draw() {
        Tesselator.getInstance().end();
    }
}
