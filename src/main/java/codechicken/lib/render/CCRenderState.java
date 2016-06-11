package codechicken.lib.render;

import codechicken.lib.colour.ColourRGBA;
import codechicken.lib.lighting.LC;
import codechicken.lib.lighting.LightMatrix;
import codechicken.lib.util.Copyable;
import codechicken.lib.vec.Rotation;
import codechicken.lib.vec.Transformation;
import codechicken.lib.vec.Vector3;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

/**
 * The core of the CodeChickenLib render system.
 * Rendering operations are written to avoid object allocations by reusing static variables.
 */
public class CCRenderState {
    private static int nextOperationIndex;

    public static int registerOperation() {
        return nextOperationIndex++;
    }

    public static int operationCount() {
        return nextOperationIndex;
    }

    /**
     * Represents an operation to be run for each vertex that operates on and modifies the current state
     */
    public interface IVertexOperation {
        /**
         * Load any required references and add dependencies to the pipeline based on the current model (may be null)
         * Return false if this operation is redundant in the pipeline with the given model
         */
        boolean load();

        /**
         * Perform the operation on the current render state
         */
        void operate();

        /**
         * Get the unique id representing this type of operation. Duplicate operation IDs within the pipeline may have unexpected results.
         * ID shoulld be obtained from CCRenderState.registerOperation() and stored in a static variable
         */
        int operationID();
    }

    private static ArrayList<VertexAttribute<?>> vertexAttributes = new ArrayList<VertexAttribute<?>>();

    private static int registerVertexAttribute(VertexAttribute<?> attr) {
        vertexAttributes.add(attr);
        return vertexAttributes.size() - 1;
    }

    public static VertexAttribute<?> getAttribute(int index) {
        return vertexAttributes.get(index);
    }

    /**
     * Gets all registered VertexAttributes.
     *
     * @return Returns an ImmutableList of registered VertexAttributes.
     */
    public static List<VertexAttribute<?>> getRegisteredVertexAttributes() {
        return ImmutableList.copyOf(vertexAttributes);
    }

    /**
     * Management class for a vertex attrute such as colour, normal etc
     * This class should handle the loading of the attrute from an array provided by IVertexSource.getAttributes or the computation of this attrute from others
     *
     * @param <T> The array type for this attrute eg. int[], Vector3[]
     */
    public static abstract class VertexAttribute<T> implements IVertexOperation {
        public final int attributeIndex = registerVertexAttribute(this);
        private final int operationIndex = registerOperation();
        /**
         * Set to true when the attrute is part of the pipeline. Should only be managed by CCRenderState when constructing the pipeline
         */
        public boolean active = false;

        /**
         * Construct a new array for storage of vertex attrutes in a model
         */
        public abstract T newArray(int length);

        @Override
        public int operationID() {
            return operationIndex;
        }
    }

    public static void arrayCopy(Object src, int srcPos, Object dst, int destPos, int length) {
        System.arraycopy(src, srcPos, dst, destPos, length);
        if (dst instanceof Copyable[]) {
            Object[] oa = (Object[]) dst;
            Copyable<Object>[] c = (Copyable[]) dst;
            for (int i = destPos; i < destPos + length; i++) {
                if (c[i] != null) {
                    oa[i] = c[i].copy();
                }
            }
        }
    }

    public static <T> T copyOf(VertexAttribute<T> attr, T src, int length) {
        T dst = attr.newArray(length);
        arrayCopy(src, 0, dst, 0, ((Object[]) src).length);
        return dst;
    }

    public interface IVertexSource {
        Vertex5[] getVertices();

        /**
         * Gets an array of vertex attrutes
         *
         * @param attr The vertex attrute to get
         * @param <T>  The attrute array type
         * @return An array, or null if not computed
         */
        <T> T getAttributes(VertexAttribute<T> attr);

        /**
         * @return True if the specified attrute is provided by this model, either by returning an array from getAttributes or by setting the state in prepareVertex
         */
        boolean hasAttribute(VertexAttribute<?> attr);

        /**
         * Callback to set CCRenderState for a vertex before the pipeline runs
         */
        void prepareVertex();
    }

    public static VertexAttribute<Vector3[]> normalAttrib = new VertexAttribute<Vector3[]>() {
        private Vector3[] normalRef;

        @Override
        public Vector3[] newArray(int length) {
            return new Vector3[length];
        }

        @Override
        public boolean load() {
            normalRef = model.getAttributes(this);
            if (model.hasAttribute(this)) {
                return normalRef != null;
            }

            if (model.hasAttribute(sideAttrib)) {
                pipeline.addDependency(sideAttrib);
                return true;
            }
            throw new IllegalStateException("Normals requested but neither normal or side attrutes are provided by the model");
        }

        @Override
        public void operate() {
            if (normalRef != null) {
                normal.set(normalRef[vertexIndex]);
            } else {
                normal.set(Rotation.axes[side]);
            }
        }
    };
    public static VertexAttribute<int[]> colourAttrib = new VertexAttribute<int[]>() {
        private int[] colourRef;

        @Override
        public int[] newArray(int length) {
            return new int[length];
        }

        @Override
        public boolean load() {
            colourRef = model.getAttributes(this);
            return colourRef != null || !model.hasAttribute(this);
        }

        @Override
        public void operate() {
            if (colourRef != null) {
                colour = ColourRGBA.multiply(baseColour, colourRef[vertexIndex]);
            } else {
                colour = baseColour;
            }
        }
    };
    public static VertexAttribute<int[]> lightingAttrib = new VertexAttribute<int[]>() {
        private int[] colourRef;

        @Override
        public int[] newArray(int length) {
            return new int[length];
        }

        @Override
        public boolean load() {
            if (!computeLighting || !fmt.hasColor() || !model.hasAttribute(this)) {
                return false;
            }

            colourRef = model.getAttributes(this);
            if (colourRef != null) {
                pipeline.addDependency(colourAttrib);
                return true;
            }
            return false;
        }

        @Override
        public void operate() {
            colour = ColourRGBA.multiply(colour, colourRef[vertexIndex]);
        }
    };
    public static VertexAttribute<int[]> sideAttrib = new VertexAttribute<int[]>() {
        private int[] sideRef;

        @Override
        public int[] newArray(int length) {
            return new int[length];
        }

        @Override
        public boolean load() {
            sideRef = model.getAttributes(this);
            if (model.hasAttribute(this)) {
                return sideRef != null;
            }

            pipeline.addDependency(normalAttrib);
            return true;
        }

        @Override
        public void operate() {
            if (sideRef != null) {
                side = sideRef[vertexIndex];
            } else {
                side = CCModel.findSide(normal);
            }
        }
    };
    /**
     * Uses the position of the lightmatrix to compute LC if not provided
     */
    public static VertexAttribute<LC[]> lightCoordAttrib = new VertexAttribute<LC[]>() {
        private LC[] lcRef;
        private Vector3 vec = new Vector3();//for computation
        private Vector3 pos = new Vector3();

        @Override
        public LC[] newArray(int length) {
            return new LC[length];
        }

        @Override
        public boolean load() {
            lcRef = model.getAttributes(this);
            if (model.hasAttribute(this)) {
                return lcRef != null;
            }

            pos.set(lightMatrix.pos.x, lightMatrix.pos.y, lightMatrix.pos.z);
            pipeline.addDependency(sideAttrib);
            pipeline.addRequirement(Transformation.operationIndex);
            return true;
        }

        @Override
        public void operate() {
            if (lcRef != null) {
                lc.set(lcRef[vertexIndex]);
            } else {
                lc.compute(vec.set(vert.vec).sub(pos), side);
            }
        }
    };

    //pipeline state
    public static IVertexSource model;
    public static int firstVertexIndex;
    public static int lastVertexIndex;
    public static int vertexIndex;
    public static CCRenderPipeline pipeline = new CCRenderPipeline();
    @SideOnly(Side.CLIENT)
    public static VertexBuffer r;
    @SideOnly(Side.CLIENT)
    public static VertexFormat fmt;

    //context
    public static int baseColour;
    public static int alphaOverride;
    public static boolean computeLighting;
    public static LightMatrix lightMatrix = new LightMatrix();

    //vertex outputs
    public static final Vertex5 vert = new Vertex5();
    public static final Vector3 normal = new Vector3();
    public static int colour;
    public static int brightness;

    //attrute storage
    public static int side;
    public static LC lc = new LC();

    //vertex formats
    //@SideOnly(Side.CLIENT)
    //public static VertexFormat POSITION_TEX_LMAP = new VertexFormat().addElement(POSITION_3F).addElement(TEX_2F).addElement(TEX_2S);
    //@SideOnly(Side.CLIENT)
    //public static VertexFormat POSITION_TEX_LMAP_NORMAL = new VertexFormat().addElement(POSITION_3F).addElement(TEX_2F).addElement(TEX_2S).addElement(NORMAL_3B).addElement(PADDING_1B);
    //@SideOnly(Side.CLIENT)
    //public static VertexFormat POSITION_TEX_LMAP_COLOR_NORMAL = new VertexFormat().addElement(POSITION_3F).addElement(TEX_2F).addElement(TEX_2S).addElement(COLOR_4UB).addElement(NORMAL_3B).addElement(PADDING_1B);

    public static void reset() {
        model = null;
        pipeline.reset();
        computeLighting = true;
        baseColour = alphaOverride = -1;
    }

    public static void setPipeline(IVertexOperation... ops) {
        pipeline.setPipeline(ops);
    }

    public static void setPipeline(IVertexSource model, int start, int end, IVertexOperation... ops) {
        pipeline.reset();
        setModel(model, start, end);
        pipeline.setPipeline(ops);
    }

    public static void bindModel(IVertexSource model) {
        if (CCRenderState.model != model) {
            CCRenderState.model = model;
            pipeline.rebuild();
        }
    }

    public static void setModel(IVertexSource source) {
        setModel(source, 0, source.getVertices().length);
    }

    public static void setModel(IVertexSource source, int start, int end) {
        bindModel(source);
        setVertexRange(start, end);
    }

    public static void setVertexRange(int start, int end) {
        firstVertexIndex = start;
        lastVertexIndex = end;
    }

    public static CCDynamicModel dynamicModel(VertexAttribute... attrs) {
        CCDynamicModel m = new CCDynamicModel(attrs);
        bindModel(m);
        return m;
    }

    public static void render(IVertexOperation... ops) {
        setPipeline(ops);
        render();
    }

    public static void render() {
        Vertex5[] verts = model.getVertices();
        for (vertexIndex = firstVertexIndex; vertexIndex < lastVertexIndex; vertexIndex++) {
            model.prepareVertex();
            vert.set(verts[vertexIndex]);
            runPipeline();
            writeVert();
        }
    }

    public static void runPipeline() {
        pipeline.operate();
    }

    public static void writeVert() {
        for (int e = 0; e < fmt.getElementCount(); e++) {
            VertexFormatElement fmte = fmt.getElement(e);
            switch (fmte.getUsage()) {
            case POSITION:
                r.pos(vert.vec.x, vert.vec.y, vert.vec.z);
                break;
            case UV:
                if (fmte.getIndex() == 0) {
                    r.tex(vert.uv.u, vert.uv.v);
                } else {
                    r.lightmap(brightness >> 16 & 65535, brightness & 65535);
                }
                break;
            case COLOR:
                r.color(colour >>> 24, colour >> 16 & 0xFF, colour >> 8 & 0xFF, alphaOverride >= 0 ? alphaOverride : colour & 0xFF);
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

    public static void pushColour() {
        GlStateManager.color((colour >>> 24) / 255F, (colour >> 16 & 0xFF) / 255F, (colour >> 8 & 0xFF) / 255F, (alphaOverride >= 0 ? alphaOverride : colour & 0xFF) / 255F);
    }

    public static void setBrightness(IBlockAccess world, BlockPos pos) {
        brightness = world.getBlockState(pos).getBlock().getPackedLightmapCoords(world.getBlockState(pos), world, pos);
    }

    public static void pullLightmap() {
        brightness = (int) OpenGlHelper.lastBrightnessY << 16 | (int) OpenGlHelper.lastBrightnessX;
    }

    public static void pushLightmap() {
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, brightness & 0xFFFF, brightness >>> 16);
    }

    @Deprecated//Use TextureUtils.changeTexture
    public static void changeTexture(String texture) {
        TextureUtils.changeTexture(texture);
    }

    @Deprecated//Use TextureUtils.changeTexture
    public static void changeTexture(ResourceLocation texture) {
        TextureUtils.changeTexture(texture);
    }

    @SideOnly(Side.CLIENT)
    public static VertexBuffer startDrawing(int mode, VertexFormat format) {
        VertexBuffer r = Tessellator.getInstance().getBuffer();
        r.begin(mode, format);
        bind(r);
        return r;
    }

    @SideOnly(Side.CLIENT)
    public static VertexBuffer startDrawing(int mode, VertexFormat format, VertexBuffer buffer) {
        buffer.begin(mode, format);
        bind(buffer);
        return buffer;
    }

    @SideOnly(Side.CLIENT)
    public static void bind(VertexBuffer r) {
        CCRenderState.r = r;
        fmt = r.getVertexFormat();
    }

    @SideOnly(Side.CLIENT)
    @Deprecated
    public static VertexBuffer pullBuffer() {
        bind(Tessellator.getInstance().getBuffer());
        return r;
    }

    @SideOnly(Side.CLIENT)
    public static VertexBuffer getBuffer() {
        return r;
    }

    public static void draw() {
        Tessellator.getInstance().draw();
    }
}
