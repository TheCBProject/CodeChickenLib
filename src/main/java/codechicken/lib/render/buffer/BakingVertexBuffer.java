package codechicken.lib.render.buffer;

import codechicken.lib.render.uv.UV;
import codechicken.lib.util.VectorUtils;
import codechicken.lib.util.VertexDataUtils;
import codechicken.lib.vec.Vector3;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.pipeline.LightUtil;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by covers1624 on 4/10/2016.
 * Creates a List of BakedQuads from a VertexBuffer. That's it really..
 */
public class BakingVertexBuffer extends VertexBuffer {

    private HashMap<Integer, TextureAtlasSprite> spriteMap;

    public static BakingVertexBuffer create() {
        return new BakingVertexBuffer(0x200000);
    }

    private BakingVertexBuffer(int bufferSizeIn) {
        super(bufferSizeIn);
    }

    @Override
    public void begin(int glMode, VertexFormat format) {
        if (glMode != 4) {
            throw new IllegalArgumentException("Unable to bake GL Mode, only Quads supported! To bake triangles pipe through CCQuad then quadulate.");
        }
        spriteMap = new HashMap<Integer, TextureAtlasSprite>();
        super.begin(glMode, format);
    }

    /**
     * Sets the sprite
     *
     * @param sprite
     */
    public BakingVertexBuffer setSprite(TextureAtlasSprite sprite) {
        spriteMap.put(getVertexCount() + 1, sprite);
        return this;
    }

    /**
     * Bakes the data inside the VertexBuffer to a baked quad.
     *
     * @return The list of quads baked.
     */
    public List<BakedQuad> bake() {
        if (isDrawing) {
            throw new IllegalStateException("Still drawing!");
        }
        State state = getVertexState();
        VertexFormat format = state.getVertexFormat();
        if (!format.hasUvOffset(0)) {
            throw new IllegalStateException("Unable to bake format that does not have UV mappings..");
        }
        int[] rawBuffer = Arrays.copyOf(state.getRawBuffer(), state.getRawBuffer().length);

        List<BakedQuad> quads = new LinkedList<BakedQuad>();
        TextureAtlasSprite sprite;
        for (int i = 1; i < state.getVertexCount(); i++) {
            int next = format.getIntegerSize() * i;
            int[] quadData = Arrays.copyOfRange(rawBuffer, next, next + format.getIntegerSize());
            Vector3 normal = new Vector3();
            if (format.hasNormal()) {
                //Grab first normal.
                float[] normalData = new float[4];
                LightUtil.unpack(quadData, normalData, format, 0, format.getNormalOffset());
                normal = Vector3.fromAxes(normalData);
            } else {
                float[][] posData = new float[4][4];
                for (int v = 0; v < 4; v++) {
                    LightUtil.unpack(quadData, posData[v], format, v, VertexDataUtils.getPositionOffset(format));
                }
                normal.set(VectorUtils.calculateNormal(Vector3.fromAxes(posData[0]), Vector3.fromAxes(posData[1]), Vector3.fromAxes(posData[3])));
            }
            if (spriteMap.containsKey(i)) {
                sprite = spriteMap.get(i);
            } else {
                float[] uvData = new float[4];
                LightUtil.unpack(quadData, uvData, format, 0, format.getUvOffsetById(0));
                UV uv = new UV(uvData[0], uvData[1]);
                sprite = VertexDataUtils.getSpriteForUV(Minecraft.getMinecraft().getTextureMapBlocks(), uv);
            }
            EnumFacing facing = VectorUtils.calcNormalSide(normal);
            BakedQuad quad = new BakedQuad(quadData, -1, facing != null ? facing : EnumFacing.UP, sprite, true, format);
            quads.add(quad);
        }
        return ImmutableList.copyOf(quads);
    }
}
