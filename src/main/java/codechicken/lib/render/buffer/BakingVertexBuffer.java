package codechicken.lib.render.buffer;

import codechicken.lib.render.TextureUtils;
import codechicken.lib.render.uv.UV;
import codechicken.lib.util.VectorUtils;
import codechicken.lib.util.VertexDataUtils;
import codechicken.lib.vec.Vector3;
import com.google.common.collect.ImmutableList;
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
 * //TODO Allow baking on a thread for more efficiency.
 */
public class BakingVertexBuffer extends VertexBuffer {

    private HashMap<Integer, TextureAtlasSprite> spriteMap;
    private boolean useSprites = true;

    public static BakingVertexBuffer create() {
        return new BakingVertexBuffer(0x200000);
    }

    private BakingVertexBuffer(int bufferSizeIn) {
        super(bufferSizeIn);
    }

    @Override
    public void begin(int glMode, VertexFormat format) {
        if (glMode != 7) {
            throw new IllegalArgumentException("Unable to bake GL Mode, only Quads supported! To bake triangles pipe through CCQuad then quadulate.");
        }
        spriteMap = new HashMap<Integer, TextureAtlasSprite>();
        super.begin(glMode, format);
    }

    /**
     * Sets the sprite for a specific vertex.
     * The baker WILL use the same sprite for that vertex and onwards if there is not another one set.
     * So, it caches the sprite until another one is provided.
     *
     * @param sprite The sprite to set.
     */
    public BakingVertexBuffer setSprite(TextureAtlasSprite sprite) {
        spriteMap.put(getVertexCount(), sprite);
        return this;
    }

    /**
     * Sets the baker to not automatically calculate sprites.
     */
    public BakingVertexBuffer ignoreSprites() {
        useSprites = false;
        return this;
    }

    /**
     * Tries to automatically calculate sprites for given UV mappings, Or uses the provided one for a given vertex range.
     */
    public BakingVertexBuffer useSprites() {
        useSprites = true;
        return this;
    }

    /**
     * Bakes the data inside the VertexBuffer to a baked quad.
     *
     * @return The list of quads baked.
     */
    public List<BakedQuad> bake() {
        State state = getVertexState();
        VertexFormat format = state.getVertexFormat();
        if (!format.hasUvOffset(0)) {
            throw new IllegalStateException("Unable to bake format that does not have UV mappings..");
        }
        int[] rawBuffer = Arrays.copyOf(state.getRawBuffer(), state.getRawBuffer().length);

        List<BakedQuad> quads = new LinkedList<BakedQuad>();
        TextureAtlasSprite sprite = TextureUtils.getMissingSprite();

        int next = 0;
        int i = 0;
        while (rawBuffer.length > next) {
            next = format.getNextOffset() * i;
            int[] quadData = Arrays.copyOfRange(rawBuffer, next, next + format.getNextOffset());
            Vector3 normal = new Vector3();
            if (format.hasNormal()) {
                //Grab first normal.
                float[] normalData = new float[4];
                LightUtil.unpack(quadData, normalData, format, 0, VertexDataUtils.getNormalElement(format));
                normal = Vector3.fromAxes(normalData);
            } else {
                float[][] posData = new float[4][4];
                for (int v = 0; v < 4; v++) {
                    LightUtil.unpack(quadData, posData[v], format, v, VertexDataUtils.getPositionElement(format));
                }
                normal.set(VectorUtils.calculateNormal(Vector3.fromAxes(posData[0]), Vector3.fromAxes(posData[1]), Vector3.fromAxes(posData[3])));
            }
            if (useSprites) {
                if (spriteMap.containsKey(i)) {
                    sprite = spriteMap.get(i);
                } else {
                    float[] uvData = new float[4];
                    LightUtil.unpack(quadData, uvData, format, 0, VertexDataUtils.getUVElement(format));
                    UV uv = new UV(uvData[0], uvData[1]);
                    sprite = VertexDataUtils.getSpriteForUV(TextureUtils.getTextureMap(), uv);
                }
            }
            EnumFacing facing = VectorUtils.calcNormalSide(normal);
            BakedQuad quad = new BakedQuad(quadData, -1, facing != null ? facing : EnumFacing.UP, sprite, true, format);
            quads.add(quad);
            i++;
        }
        return ImmutableList.copyOf(quads);
    }
}
