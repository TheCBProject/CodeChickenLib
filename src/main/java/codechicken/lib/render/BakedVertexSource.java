package codechicken.lib.render;

import codechicken.lib.colour.Colour;
import codechicken.lib.model.CachedFormat;
import codechicken.lib.model.ISmartVertexConsumer;
import codechicken.lib.model.Quad;
import codechicken.lib.render.lighting.LC;
import codechicken.lib.render.pipeline.IVertexSource;
import codechicken.lib.render.pipeline.attribute.AttributeKey;
import codechicken.lib.render.pipeline.attribute.AttributeKey.AttributeKeyRegistry;
import codechicken.lib.render.pipeline.attribute.ColourAttribute;
import codechicken.lib.render.pipeline.attribute.LightCoordAttribute;
import codechicken.lib.render.pipeline.attribute.NormalAttribute;
import codechicken.lib.vec.Vector3;
import codechicken.lib.vec.Vertex5;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.Direction;

import java.util.Arrays;
import java.util.function.Supplier;

import static codechicken.lib.util.SneakyUtils.unsafeCast;

/**
 * Created by covers1624 on 12/5/20.
 */
public class BakedVertexSource implements IVertexSource, ISmartVertexConsumer {

    private static final ThreadLocal<BakedVertexSource> instances = ThreadLocal.withInitial(BakedVertexSource::new);

    private final Quad unpacker = new Quad(CachedFormat.lookup(DefaultVertexFormats.BLOCK));

    private int vertexIndex = -1;
    private Vertex5[] vertices = new Vertex5[0];
    private Object[] attributes = new Object[0];
    private TextureAtlasSprite[] sprites = new TextureAtlasSprite[0];

    private BakedVertexSource() {
        ensureAttr(AttributeKeyRegistry.numAttributes() - 1);
        ensureSpace(24); //Ensure enough space for a full standard block model.
    }

    public static BakedVertexSource instance() {
        return instances.get();
    }

    @Override
    public Vertex5[] getVertices() {
        return vertices;
    }

    @Override
    public <T> T getAttributes(AttributeKey<T> attr) {
        ensureAttr(attr.attributeKeyIndex);
        return unsafeCast(attributes[attr.attributeKeyIndex]);
    }

    @Override
    public boolean hasAttribute(AttributeKey<?> attr) {
        return attr == NormalAttribute.attributeKey || attr == ColourAttribute.attributeKey || attr == LightCoordAttribute.attributeKey;
    }

    @Override
    public void prepareVertex(CCRenderState ccrs) {
        ccrs.sprite = sprites[ccrs.vertexIndex];
    }

    public void reset() {
        reset(CachedFormat.lookup(DefaultVertexFormats.BLOCK));
    }

    public void reset(CachedFormat format) {
        vertexIndex = -1;
        unpacker.reset(format);
    }

    public int availableVertices() {
        return vertexIndex + 1;
    }

    private void onFull() {
        if (vertexIndex == -1) {
            vertexIndex = 0;
        }
        ensureSpace(availableVertices() + 4);
        for (int i = 0; i < 4; i++) {
            int v = vertexIndex++;
            Quad.Vertex vertex = unpacker.vertices[i];
            Vertex5 vertex5 = vertices[v];
            vertex5.vec.set(vertex.vec);
            vertex5.uv.set(vertex.uv);

            Vector3 normal = getAttr(NormalAttribute.attributeKey)[v];
            normal.set(vertex.normal);
            getAttr(LightCoordAttribute.attributeKey)[v].compute(vertex5.vec, normal);

            getAttr(ColourAttribute.attributeKey)[v] = Colour.packRGBA(vertex.color);

            sprites[v] = unpacker.sprite;
        }
    }

    private void ensureAttr(int aIdx) {
        if (attributes.length <= aIdx) {
            attributes = Arrays.copyOf(attributes, aIdx + 1);
        }
    }

    //Expands array storage.
    private void ensureSpace(int numVertices) {
        //If we don't have enough, or are full, expand.
        if (vertices.length <= numVertices) {
            int prevLen = vertices.length;
            int fillStart = vertexIndex == -1 ? 0 : vertexIndex;
            vertices = Arrays.copyOf(vertices, numVertices);
            fill(vertices, fillStart, numVertices, Vertex5::new);
            for (int aIdx = 0; aIdx < attributes.length; aIdx++) {
                Object attr = attributes[aIdx];
                AttributeKey<?> key = AttributeKeyRegistry.getAttributeKey(aIdx);
                if (attr == null) {
                    attr = key.newArray(numVertices);
                } else {
                    Object newAttr = key.newArray(numVertices);
                    //noinspection SuspiciousSystemArraycopy
                    System.arraycopy(attr, 0, newAttr, 0, prevLen);
                }
                attributes[aIdx] = attr;
                //Fill non primitive vertex attributes with new things.
                if (key == NormalAttribute.attributeKey) {
                    fill((Object[]) attr, fillStart, numVertices, Vector3::new);
                } else if (key == LightCoordAttribute.attributeKey) {
                    fill((Object[]) attr, fillStart, numVertices, LC::new);
                }
            }
            sprites = Arrays.copyOf(sprites, numVertices);
        }
    }

    private <T> T getAttr(AttributeKey<T> key) {
        return unsafeCast(attributes[key.attributeKeyIndex]);
    }

    @Override
    public void put(Quad quad) {
        unpacker.put(quad);
        onFull();
    }

    @Override
    public void put(int element, float... data) {
        unpacker.put(element, data);
        if (unpacker.full) {
            onFull();
        }
    }

    //@formatter:off
    @Override public VertexFormat getVertexFormat() { return DefaultVertexFormats.BLOCK; }
    @Override public void setQuadTint(int tint) { unpacker.setQuadTint(tint); }
    @Override public void setQuadOrientation(Direction orientation) { unpacker.setQuadOrientation(orientation); }
    @Override public void setApplyDiffuseLighting(boolean diffuse) { unpacker.setApplyDiffuseLighting(diffuse); }
    @Override public void setTexture(TextureAtlasSprite texture) { unpacker.setTexture(texture); }
    //@formatter:on

    private static void fill(Object[] arr, int start, int end, Supplier<Object> supplier) {
        for (int i = start; i < end; i++) {
            arr[i] = supplier.get();
        }
    }
}
