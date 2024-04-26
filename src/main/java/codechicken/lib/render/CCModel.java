package codechicken.lib.render;

import codechicken.lib.render.lighting.LC;
import codechicken.lib.render.lighting.LightModel;
import codechicken.lib.render.model.ModelMaterial;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.render.pipeline.IVertexSource;
import codechicken.lib.render.pipeline.attribute.*;
import codechicken.lib.render.pipeline.attribute.AttributeKey.AttributeKeyRegistry;
import codechicken.lib.util.Copyable;
import codechicken.lib.util.VectorUtils;
import codechicken.lib.vec.*;
import codechicken.lib.vec.uv.UV;
import codechicken.lib.vec.uv.UVTransformation;
import codechicken.lib.vec.uv.UVTranslation;
import com.mojang.blaze3d.vertex.VertexFormat;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static codechicken.lib.vec.Rotation.sideRotations;
import static net.covers1624.quack.util.SneakyUtils.unsafeCast;

public class CCModel implements IVertexSource, Copyable<CCModel> {

    public final VertexFormat.Mode vertexMode;
    public final int vp;
    public Vertex5[] verts;
    public ArrayList<Object> attributes = new ArrayList<>();

    protected CCModel(VertexFormat.Mode vertexMode) {
        if (vertexMode != VertexFormat.Mode.QUADS && vertexMode != VertexFormat.Mode.TRIANGLES) {
            throw new IllegalArgumentException("Models must be QUADS or TRIANGLES");
        }

        this.vertexMode = vertexMode;
        vp = vertexMode == VertexFormat.Mode.QUADS ? 4 : 3;
    }

    public Vector3[] normals() {
        return getAttribute(NormalAttribute.attributeKey);
    }

    @Nullable
    public ModelMaterial material() {
        return getAttribute(ModelMaterial.MATERIAL_KEY);
    }

    @Override
    public Vertex5[] getVertices() {
        return verts;
    }

    @Override
    public <T> T getAttribute(AttributeKey<T> attr) {
        if (attr.attributeKeyIndex < attributes.size()) {
            return unsafeCast(attributes.get(attr.attributeKeyIndex));
        }
        return null;
    }

    @Override
    public boolean hasAttribute(AttributeKey<?> attr) {
        return attr.attributeKeyIndex < attributes.size() && attributes.get(attr.attributeKeyIndex) != null;
    }

    @Override
    public void prepareVertex(CCRenderState ccrs) {
    }

    /**
     * Gets an attribute.
     * <p>
     * If the model doesn't have this attribute, a new storage will be created
     * and set.
     *
     * @param attr The attribute to get.
     * @return The value.
     */
    public <T> T getOrAllocate(AttributeKey<T> attr) {
        T value = getAttribute(attr);
        if (value == null) {
            allocateAttr(attr);
            attributes.set(attr.attributeKeyIndex, value = attr.createDefault(verts.length));
        }
        return value;
    }

    /**
     * Set an attribute.
     *
     * @param attr  The attribute to set.
     * @param value The value to set.
     */
    public <T> void setAttribute(AttributeKey<T> attr, @Nullable T value) {
        allocateAttr(attr);
        attributes.set(attr.attributeKeyIndex, value);

    }

    /**
     * Ensires the specified {@link AttributeKey}'s index
     * is available in the {@link #attributes} list.
     *
     * @param attr The attribute key.
     */
    private void allocateAttr(AttributeKey<?> attr) {
        if (attr.attributeKeyIndex >= attributes.size()) {
            while (attributes.size() <= attr.attributeKeyIndex) {
                attributes.add(null);
            }
        }
    }

    /**
     * Each pixel corresponds to one unit of position when generating the model
     *
     * @param i  Vertex index to start generating at
     * @param x1 The minX bound of the box
     * @param y1 The minY bound of the box
     * @param z1 The minZ bound of the box
     * @param w  The width of the box
     * @param h  The height of the box
     * @param d  The depth of the box
     * @param tx The distance of the top left corner of the texture map from the left in pixels
     * @param ty The distance of the top left corner of the texture map from the top in pixels
     * @param tw The width of the texture in pixels
     * @param th The height of the texture in pixels
     * @param f  The scale of the model, pixels per block, normally 16
     * @return The generated model
     */
    public CCModel generateBox(int i, double x1, double y1, double z1, double w, double h, double d, double tx, double ty, double tw, double th, double f) {
        double u1, v1, u2, v2;
        double x2 = x1 + w;
        double y2 = y1 + h;
        double z2 = z1 + d;
        x1 /= f;
        x2 /= f;
        y1 /= f;
        y2 /= f;
        z1 /= f;
        z2 /= f;

        //bottom face
        u1 = (tx + d + w) / tw;
        v1 = (ty + d) / th;
        u2 = (tx + d * 2 + w) / tw;
        v2 = ty / th;
        verts[i++] = new Vertex5(x1, y1, z2, u1, v2);
        verts[i++] = new Vertex5(x1, y1, z1, u1, v1);
        verts[i++] = new Vertex5(x2, y1, z1, u2, v1);
        verts[i++] = new Vertex5(x2, y1, z2, u2, v2);

        //top face
        u1 = (tx + d) / tw;
        v1 = (ty + d) / th;
        u2 = (tx + d + w) / tw;
        v2 = ty / th;
        verts[i++] = new Vertex5(x2, y2, z2, u2, v2);
        verts[i++] = new Vertex5(x2, y2, z1, u2, v1);
        verts[i++] = new Vertex5(x1, y2, z1, u1, v1);
        verts[i++] = new Vertex5(x1, y2, z2, u1, v2);

        //front face
        u1 = (tx + d + w) / tw;
        v1 = (ty + d) / th;
        u2 = (tx + d) / tw;
        v2 = (ty + d + h) / th;
        verts[i++] = new Vertex5(x1, y2, z1, u2, v1);
        verts[i++] = new Vertex5(x2, y2, z1, u1, v1);
        verts[i++] = new Vertex5(x2, y1, z1, u1, v2);
        verts[i++] = new Vertex5(x1, y1, z1, u2, v2);

        //back face
        u1 = (tx + d * 2 + w * 2) / tw;
        v1 = (ty + d) / th;
        u2 = (tx + d * 2 + w) / tw;
        v2 = (ty + d + h) / th;
        verts[i++] = new Vertex5(x1, y2, z2, u1, v1);
        verts[i++] = new Vertex5(x1, y1, z2, u1, v2);
        verts[i++] = new Vertex5(x2, y1, z2, u2, v2);
        verts[i++] = new Vertex5(x2, y2, z2, u2, v1);

        //left face
        u1 = (tx + d) / tw;
        v1 = (ty + d) / th;
        u2 = (tx) / tw;
        v2 = (ty + d + h) / th;
        verts[i++] = new Vertex5(x1, y2, z2, u2, v1);
        verts[i++] = new Vertex5(x1, y2, z1, u1, v1);
        verts[i++] = new Vertex5(x1, y1, z1, u1, v2);
        verts[i++] = new Vertex5(x1, y1, z2, u2, v2);

        //right face
        u1 = (tx + d * 2 + w) / tw;
        v1 = (ty + d) / th;
        u2 = (tx + d + w) / tw;
        v2 = (ty + d + h) / th;
        verts[i++] = new Vertex5(x2, y1, z2, u1, v2);
        verts[i++] = new Vertex5(x2, y1, z1, u2, v2);
        verts[i++] = new Vertex5(x2, y2, z1, u2, v1);
        verts[i++] = new Vertex5(x2, y2, z2, u1, v1);

        return this;
    }

    /**
     * Generates a box, uv mapped to be the same as a minecraft block with the same bounds
     *
     * @param i      The vertex index to start generating at
     * @param bounds The bounds of the block, 0 to 1
     * @return The generated model. When rendering an icon will need to be supplied for the UV transformation.
     */
    public CCModel generateBlock(int i, Cuboid6 bounds) {
        return generateBlock(i, bounds, 0);
    }

    public CCModel generateBlock(int i, Cuboid6 bounds, int mask) {
        return generateBlock(i, bounds.min.x, bounds.min.y, bounds.min.z, bounds.max.x, bounds.max.y, bounds.max.z, mask);
    }

    public CCModel generateBlock(int i, double x1, double y1, double z1, double x2, double y2, double z2) {
        return generateBlock(i, x1, y1, z1, x2, y2, z2, 0);
    }

    /**
     * Generates a box, uv mapped to be the same as a minecraft block with the same bounds
     *
     * @param i    The vertex index to start generating at
     * @param x1   minX
     * @param y1   minY
     * @param z1   minZ
     * @param x2   maxX
     * @param y2   maxY
     * @param z2   maxZ
     * @param mask A bitmask of sides NOT to generate. I high bit at index s means side s will not be generated
     * @return The generated model. When rendering an icon will need to be supplied for the UV transformation.
     */
    public CCModel generateBlock(int i, double x1, double y1, double z1, double x2, double y2, double z2, int mask) {
        double u1, v1, u2, v2;

        if ((mask & 1) == 0) {//bottom face
            u1 = x1;
            v1 = z1;
            u2 = x2;
            v2 = z2;
            verts[i++] = new Vertex5(x1, y1, z2, u1, v2, 0);
            verts[i++] = new Vertex5(x1, y1, z1, u1, v1, 0);
            verts[i++] = new Vertex5(x2, y1, z1, u2, v1, 0);
            verts[i++] = new Vertex5(x2, y1, z2, u2, v2, 0);
        }

        if ((mask & 2) == 0) {//top face
            u1 = x1;
            v1 = z1;
            u2 = x2;
            v2 = z2;
            verts[i++] = new Vertex5(x2, y2, z2, u2, v2, 1);
            verts[i++] = new Vertex5(x2, y2, z1, u2, v1, 1);
            verts[i++] = new Vertex5(x1, y2, z1, u1, v1, 1);
            verts[i++] = new Vertex5(x1, y2, z2, u1, v2, 1);
        }

        if ((mask & 4) == 0) {//north face
            u1 = 1 - x1;
            v1 = 1 - y2;
            u2 = 1 - x2;
            v2 = 1 - y1;
            verts[i++] = new Vertex5(x1, y1, z1, u1, v2, 2);
            verts[i++] = new Vertex5(x1, y2, z1, u1, v1, 2);
            verts[i++] = new Vertex5(x2, y2, z1, u2, v1, 2);
            verts[i++] = new Vertex5(x2, y1, z1, u2, v2, 2);
        }

        if ((mask & 8) == 0) {//south face
            u1 = x1;
            v1 = 1 - y2;
            u2 = x2;
            v2 = 1 - y1;
            verts[i++] = new Vertex5(x2, y1, z2, u2, v2, 3);
            verts[i++] = new Vertex5(x2, y2, z2, u2, v1, 3);
            verts[i++] = new Vertex5(x1, y2, z2, u1, v1, 3);
            verts[i++] = new Vertex5(x1, y1, z2, u1, v2, 3);
        }

        if ((mask & 0x10) == 0) {//west face
            u1 = z1;
            v1 = 1 - y2;
            u2 = z2;
            v2 = 1 - y1;
            verts[i++] = new Vertex5(x1, y1, z2, u2, v2, 4);
            verts[i++] = new Vertex5(x1, y2, z2, u2, v1, 4);
            verts[i++] = new Vertex5(x1, y2, z1, u1, v1, 4);
            verts[i++] = new Vertex5(x1, y1, z1, u1, v2, 4);
        }

        if ((mask & 0x20) == 0) {//east face
            u1 = 1 - z1;
            v1 = 1 - y2;
            u2 = 1 - z2;
            v2 = 1 - y1;
            verts[i++] = new Vertex5(x2, y1, z1, u1, v2, 5);
            verts[i++] = new Vertex5(x2, y2, z1, u1, v1, 5);
            verts[i++] = new Vertex5(x2, y2, z2, u2, v1, 5);
            verts[i++] = new Vertex5(x2, y1, z2, u2, v2, 5);
        }

        return this;
    }

    public CCModel computeNormals() {
        return computeNormals(0, verts.length);
    }

    /**
     * Computes the normals of all faces in the model.
     * Uses the cross product of the vectors along 2 sides of the face
     *
     * @param start  The first vertex to generate normals for
     * @param length The number of vertices to generate normals for. Note this must be a multiple of 3 for triangles or 4 for quads
     * @return The model
     */
    public CCModel computeNormals(int start, int length) {
        if (length % vp != 0 || start % vp != 0) {
            throw new IllegalArgumentException("Cannot generate normals across polygons");
        }

        Vector3[] normals = getOrAllocate(NormalAttribute.attributeKey);
        for (int k = 0; k < length; k += vp) {
            int i = k + start;
            Vector3 diff1 = verts[i + 1].vec.copy().subtract(verts[i].vec);
            Vector3 diff2 = verts[i + vp - 1].vec.copy().subtract(verts[i].vec);
            normals[i] = diff1.crossProduct(diff2).normalize();
            for (int d = 1; d < vp; d++) {
                normals[i + d] = normals[i].copy();
            }
        }

        return this;
    }

    /**
     * Computes lighting using the normals add a light model
     * If the model is rotated, the lighting will no longer be valid
     *
     * @return The model
     */
    public CCModel computeLighting(LightModel light) {
        Vector3[] normals = normals();
        int[] colours = getAttribute(LightingAttribute.attributeKey);
        if (colours == null) {
            colours = getOrAllocate(LightingAttribute.attributeKey);
            Arrays.fill(colours, -1);
        }
        for (int k = 0; k < verts.length; k++) {
            colours[k] = light.apply(colours[k], normals[k]);
        }
        return this;
    }

    public CCModel setColour(int c) {
        int[] colours = getOrAllocate(ColourAttribute.attributeKey);
        Arrays.fill(colours, c);
        return this;
    }

    public CCModel setTex(int tex) {
        for (Vertex5 vert : verts) {
            vert.uv.tex = tex;
        }
        return this;
    }

    /**
     * Computes the minecraft lighting coordinates for use with a LightMatrix
     *
     * @return The model
     */
    public CCModel computeLightCoords() {
        LC[] lcs = getOrAllocate(LightCoordAttribute.attributeKey);
        Vector3[] normals = normals();
        for (int i = 0; i < verts.length; i++) {
            lcs[i] = new LC().compute(verts[i].vec, normals[i]);
        }
        return this;
    }

    /**
     * Averages all normals at the same position to produce a smooth lighting effect.
     *
     * @return The model
     */
    public CCModel smoothNormals() {
        ArrayList<PositionNormalEntry> map = new ArrayList<>();
        Vector3[] normals = normals();
        nextvert:
        for (int k = 0; k < verts.length; k++) {
            Vector3 vec = verts[k].vec;
            for (PositionNormalEntry e : map) {
                if (e.positionEqual(vec)) {
                    e.addNormal(normals[k]);
                    continue nextvert;
                }
            }

            map.add(new PositionNormalEntry(vec).addNormal(normals[k]));
        }

        for (PositionNormalEntry e : map) {
            if (e.normals.size() <= 1) {
                continue;
            }

            Vector3 new_n = new Vector3();
            for (Vector3 n : e.normals) {
                new_n.add(n);
            }

            new_n.normalize();
            for (Vector3 n : e.normals) {
                n.set(new_n);
            }
        }

        return this;
    }

    public CCModel apply(Transformation t) {
        for (Vertex5 vert : verts) {
            vert.apply(t);
        }

        Vector3[] normals = normals();
        if (normals != null) {
            for (Vector3 normal : normals) {
                t.applyN(normal);
            }
        }

        return this;
    }

    public CCModel apply(UVTransformation uvt) {
        for (Vertex5 vert : verts) {
            vert.apply(uvt);
        }

        return this;
    }

    public CCModel expand(int extraVerts) {
        int newLen = verts.length + extraVerts;
        verts = Arrays.copyOf(verts, newLen);
        for (int i = 0; i < attributes.size(); i++) {
            if (attributes.get(i) != null) {
                attributes.set(i, AttributeKeyRegistry.getAttributeKey(i).copy(attributes.get(i), newLen));
            }
        }

        return this;
    }

    public void render(CCRenderState state, double x, double y, double z, double u, double v) {
        render(state, new Vector3(x, y, z).translation(), new UVTranslation(u, v));
    }

    public void render(CCRenderState state, double x, double y, double z, UVTransformation u) {
        render(state, new Vector3(x, y, z).translation(), u);
    }

    public void render(CCRenderState state, Transformation t, double u, double v) {
        render(state, t, new UVTranslation(u, v));
    }

    public void render(CCRenderState state, IVertexOperation... ops) {
        render(state, 0, verts.length, ops);
    }

    /**
     * Renders vertices start through start+length-1 of the model
     *
     * @param start The first vertex index to render
     * @param end   The vertex index to render until
     * @param ops   Operations to apply
     */
    public void render(CCRenderState state, int start, int end, IVertexOperation... ops) {
        state.setPipeline(this, start, end, ops);
        state.render();
    }

    public static CCModel quadModel(int numVerts) {
        return newModel(VertexFormat.Mode.QUADS, numVerts);
    }

    public static CCModel triModel(int numVerts) {
        return newModel(VertexFormat.Mode.TRIANGLES, numVerts);
    }

    public static CCModel newModel(VertexFormat.Mode vertexMode, int numVerts) {
        CCModel model = newModel(vertexMode);
        model.verts = new Vertex5[numVerts];
        return model;
    }

    public static CCModel newModel(VertexFormat.Mode vertexMode) {
        return new CCModel(vertexMode);
    }

    public static CCModel createModel(List<Vector3> verts, List<Vector3> uvs, List<Vector3> normals, VertexFormat.Mode vertexMode, List<int[]> polys) {
        int vp = vertexMode == VertexFormat.Mode.QUADS ? 4 : 3;
        if (polys.size() < vp || polys.size() % vp != 0) {
            throw new IllegalArgumentException("Invalid number of vertices for model: " + polys.size());
        }

        boolean hasNormals = polys.get(0)[2] > 0;
        CCModel model = CCModel.newModel(vertexMode, polys.size());
        if (hasNormals) {
            model.getOrAllocate(NormalAttribute.attributeKey);
        }

        for (int i = 0; i < polys.size(); i++) {
            int[] ai = polys.get(i);
            Vector3 vert = verts.get(ai[0] - 1).copy();
            Vector3 uv = ai[1] <= 0 ? new Vector3() : uvs.get(ai[1] - 1).copy();
            if (ai[2] > 0 != hasNormals) {
                throw new IllegalArgumentException("Normals are an all or nothing deal here.");
            }

            model.verts[i] = new Vertex5(vert, uv.x, uv.y);
            if (hasNormals) {
                model.normals()[i] = normals.get(ai[2] - 1).copy();
            }
        }

        return model;
    }

    /**
     * Brings the UV coordinates of each face closer to the center UV by d.
     * Useful for fixing texture seams
     */
    public CCModel shrinkUVs(double d) {
        for (int k = 0; k < verts.length; k += vp) {
            UV uv = new UV();
            for (int i = 0; i < vp; i++) {
                uv.add(verts[k + i].uv);
            }
            uv.multiply(1D / vp);
            for (int i = 0; i < vp; i++) {
                Vertex5 vert = verts[k + i];
                vert.uv.u += vert.uv.u < uv.u ? d : -d;
                vert.uv.v += vert.uv.v < uv.v ? d : -d;
            }
        }
        return this;
    }

    /**
     * @param side1 The side of this model
     * @param side2 The side of the new model
     * @param point The point to rotate around
     * @return A copy of this model rotated to the appropriate side
     */
    public CCModel sidedCopy(int side1, int side2, Vector3 point) {
        return copy().apply(new TransformationList(sideRotations[side1].inverse(), sideRotations[side2]).at(point));
    }

    /**
     * Copies a range of vertices and attributes form one model to another.
     * <p>
     * This will deeply copy all vertex data and attributes.
     * {@link ModelMaterial} attributes will only be copied to the destination model
     * if the provided {@code srcpos} and {@code destpos} are {@code 0}.
     *
     * @param src     The source model.
     * @param srcpos  The index in the source model to copy from.
     * @param dst     The destination model.
     * @param destpos The index in the destination model to copy to.
     * @param length  The number of vertices to copy.
     */
    public static void copy(CCModel src, int srcpos, CCModel dst, int destpos, int length) {
        for (int k = 0; k < length; k++) {
            dst.verts[destpos + k] = src.verts[srcpos + k].copy();
        }

        for (int i = 0; i < src.attributes.size(); i++) {
            if (src.attributes.get(i) != null) {
                AttributeKey<?> key = AttributeKeyRegistry.getAttributeKey(i);
                dst.allocateAttr(key);
                dst.attributes.set(i, key.copyRange(
                        unsafeCast(src.attributes.get(i)),
                        srcpos,
                        unsafeCast(dst.getOrAllocate(key)),
                        destpos,
                        length
                ));
            }
        }
    }

    /**
     * Generate models rotated to the other 5 sides of the block
     *
     * @param models An array of 6 models
     * @param side   The side of this model
     * @param point  The rotation point
     */
    public static void generateSidedModels(CCModel[] models, int side, Vector3 point) {
        for (int s = 0; s < 6; s++) {
            if (s == side) {
                continue;
            }

            models[s] = models[side].sidedCopy(side, s, point);
        }
    }

    /**
     * Generate models rotated to the other 3 horizontal of the block
     *
     * @param models An array of 4 models
     * @param side   The side of this model
     * @param point  The rotation point
     */
    public static void generateSidedModelsH(CCModel[] models, int side, Vector3 point) {
        for (int s = 2; s < 6; s++) {
            if (s == side) {
                continue;
            }

            models[s] = models[side].sidedCopy(side, s, point);
        }
    }

    public CCModel backfacedCopy() {
        return generateBackface(this, 0, copy(), 0, verts.length);
    }

    /**
     * Generates copies of faces with clockwise vertices
     *
     * @return The model
     */
    public static CCModel generateBackface(CCModel src, int srcpos, CCModel dst, int destpos, int length) {
        int vp = src.vp;
        if (srcpos % vp != 0 || destpos % vp != 0 || length % vp != 0) {
            throw new IllegalArgumentException("Vertices do not align with polygons");
        }

        int[][] o = new int[][] { { 0, 0 }, { 1, vp - 1 }, { 2, vp - 2 }, { 3, vp - 3 } };
        for (int i = 0; i < length; i++) {
            int b = (i / vp) * vp;
            int d = i % vp;
            int di = destpos + b + o[d][1];
            int si = srcpos + b + o[d][0];
            dst.verts[di] = src.verts[si].copy();
            for (int a = 0; a < src.attributes.size(); a++) {
                if (src.attributes.get(a) != null) {
                    AttributeKey<?> key = AttributeKeyRegistry.getAttributeKey(a);
                    dst.attributes.set(a, key.copyRange(
                            unsafeCast(src.attributes.get(a)),
                            si,
                            unsafeCast(dst.getOrAllocate(key)),
                            di,
                            1
                    ));
                }
            }

            if (dst.normals() != null && dst.normals()[di] != null) {
                dst.normals()[di].negate();
            }
        }
        return dst;
    }

    /**
     * Generates sided copies of vertices into this model.
     * Assumes that your model has been generated at vertex side*(numVerts/6)
     */
    public CCModel generateSidedParts(int side, Vector3 point) {
        if (verts.length % (6 * vp) != 0) {
            throw new IllegalArgumentException("Invalid number of vertices for sided part generation");
        }
        int length = verts.length / 6;

        for (int s = 0; s < 6; s++) {
            if (s == side) {
                continue;
            }

            generateSidedPart(side, s, point, length * side, length * s, length);
        }

        return this;
    }

    /**
     * Generates sided copies of vertices into this model.
     * Assumes that your model has been generated at vertex side*(numVerts/4)
     */
    public CCModel generateSidedPartsH(int side, Vector3 point) {
        if (verts.length % (4 * vp) != 0) {
            throw new IllegalArgumentException("Invalid number of vertices for sided part generation");
        }
        int length = verts.length / 4;

        for (int s = 2; s < 6; s++) {
            if (s == side) {
                continue;
            }

            generateSidedPart(side, s, point, length * (side - 2), length * (s - 2), length);
        }

        return this;
    }

    /**
     * Generates a sided copy of verts into this model
     */
    public CCModel generateSidedPart(int side1, int side2, Vector3 point, int srcpos, int destpos, int length) {
        return apply(new TransformationList(sideRotations[side1].inverse(), sideRotations[side2]).at(point), srcpos, destpos, length);
    }

    /**
     * Generates a rotated copy of verts into this model
     */
    public CCModel apply(Transformation t, int srcpos, int destpos, int length) {
        for (int k = 0; k < length; k++) {
            verts[destpos + k] = verts[srcpos + k].copy();
            verts[destpos + k].vec.apply(t);
        }

        Vector3[] normals = normals();
        if (normals != null) {
            for (int k = 0; k < length; k++) {
                normals[destpos + k] = normals[srcpos + k].copy();
                t.applyN(normals[destpos + k]);
            }
        }

        return this;
    }

    /**
     * Combines the given models together.
     * <p>
     * This will deeply copy all vertices and attributes.
     * The returned model will have the {@link ModelMaterial}
     * of the first provided Model.
     *
     * @param models The Models.
     * @return The combined model.
     */
    public static CCModel combine(Collection<CCModel> models) {
        if (models.isEmpty()) {
            return null;
        }

        int numVerts = 0;
        VertexFormat.Mode vertexMode = null;
        for (CCModel model : models) {
            if (vertexMode == null) {
                vertexMode = model.vertexMode;
            }
            if (vertexMode != model.vertexMode) {
                throw new IllegalArgumentException("Cannot combine models with different vertex modes");
            }

            numVerts += model.verts.length;
        }

        CCModel c_model = newModel(vertexMode, numVerts);
        int i = 0;
        for (CCModel model : models) {
            copy(model, 0, c_model, i, model.verts.length);
            i += model.verts.length;
        }

        return c_model;
    }

    public CCModel twoFacedCopy() {
        CCModel model = newModel(vertexMode, verts.length * 2);
        copy(this, 0, model, 0, verts.length);
        return generateBackface(model, 0, model, verts.length, verts.length);
    }

    @Override
    public CCModel copy() {
        CCModel model = newModel(vertexMode, verts.length);
        copy(this, 0, model, 0, verts.length);
        return model;
    }

    /**
     * @return The average of all vertices, for bones.
     */
    public Vector3 collapse() {
        Vector3 v = new Vector3();
        for (Vertex5 vert : verts) {
            v.add(vert.vec);
        }
        v.multiply(1 / (double) verts.length);
        return v;
    }

    public CCModel zOffset(Cuboid6 offsets) {
        for (int k = 0; k < verts.length; k++) {
            Vertex5 vert = verts[k];
            Vector3 normal = normals()[k];
            switch (VectorUtils.findSide(normal)) {
                case 0 -> vert.vec.y += offsets.min.y;
                case 1 -> vert.vec.y += offsets.max.y;
                case 2 -> vert.vec.z += offsets.min.z;
                case 3 -> vert.vec.z += offsets.max.z;
                case 4 -> vert.vec.x += offsets.min.x;
                case 5 -> vert.vec.x += offsets.max.x;
            }
        }
        return this;
    }

    /**
     * @return A Cuboid6 containing all the verts in this model
     */
    public Cuboid6 bounds() {
        Vector3 vec1 = verts[0].vec;
        Cuboid6 c = new Cuboid6(vec1.copy(), vec1.copy());
        for (int i = 1; i < verts.length; i++) {
            c.enclose(verts[i].vec);
        }
        return c;
    }

    private static class PositionNormalEntry {

        public Vector3 pos;
        public LinkedList<Vector3> normals = new LinkedList<>();

        public PositionNormalEntry(Vector3 position) {
            pos = position;
        }

        public boolean positionEqual(Vector3 v) {
            return pos.x == v.x && pos.y == v.y && pos.z == v.z;
        }

        public PositionNormalEntry addNormal(Vector3 normal) {
            normals.add(normal);
            return this;
        }
    }
}
