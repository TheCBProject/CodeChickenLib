package codechicken.lib.render.consumer;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.Direction;
import net.minecraftforge.client.model.pipeline.IVertexConsumer;

/**
 * Created by covers1624 on 5/11/2016.
 * TODO Document.
 */
public class UnpackingVertexConsumer implements IVertexConsumer {

    public int tintIndex;
    public Direction face;
    public VertexFormat format;
    public boolean applyDiffuesLight;
    public TextureAtlasSprite sprite;

    public float[][][] unpackedData;

    private int vertices = 0;

    public UnpackingVertexConsumer(VertexFormat format) {
        this.format = format;
        unpackedData = new float[4][format.getElements().size()][4];
    }

    @Override
    public VertexFormat getVertexFormat() {
        return format;
    }

    @Override
    public void setQuadTint(int tint) {
        tintIndex = tint;
    }

    @Override
    public void setQuadOrientation(Direction orientation) {
        face = orientation;
    }

    @Override
    public void setApplyDiffuseLighting(boolean diffuse) {
        applyDiffuesLight = diffuse;
    }

    @Override
    public void setTexture(TextureAtlasSprite texture) {
        sprite = texture;
    }

    @Override
    public void put(int element, float... data) {
        System.arraycopy(data, 0, unpackedData[vertices][element], 0, data.length);
        if (element == getVertexFormat().getElements().size() - 1) {
            vertices++;
        }
    }

    public float[][][] getUnpackedData() {
        return unpackedData;
    }

}
