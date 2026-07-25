/*
 * This file is part of CodeChickenLib.
 * Copyright (c) 2018, covers1624, All rights reserved.
 *
 * CodeChickenLib is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * CodeChickenLib is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with CodeChickenLib. If not, see <http://www.gnu.org/licenses/lgpl>.
 */

package codechicken.lib.model;

import codechicken.lib.math.InterpHelper;
import codechicken.lib.math.MathHelper;
import codechicken.lib.vec.Cuboid6;
import codechicken.lib.vec.Vector3;
import codechicken.lib.vec.uv.UV;
import net.minecraft.client.model.geom.builders.UVPair;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.util.ARGB;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.client.model.quad.BakedColors;
import net.neoforged.neoforge.client.model.quad.BakedNormals;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3fc;

import static java.util.Objects.requireNonNull;

/**
 * A mutable {@link BakedQuad}.
 *
 * @author covers1624
 */
public class Quad {

    public Vertex[] vertices = {
            new Vertex(),
            new Vertex(),
            new Vertex(),
            new Vertex()
    };
    public int tintIndex = -1;
    public @Nullable Direction direction;
    public @Nullable TextureAtlasSprite sprite;
    public boolean shade = false;
    public int lightEmission = 0;
    public boolean ambientOcclusion = true;

    // Cache for normal computation.
    private final Vector3 v1 = new Vector3();
    private final Vector3 v2 = new Vector3();
    private final Vector3 t = new Vector3();
    private final Cuboid6 c = new Cuboid6();

    public Quad() {
    }

    public Quad(Quad quad) {
        copyFrom(quad);
    }

    public Quad(BakedQuad quad) {
        copyFrom(quad);
    }

    /**
     * Used to reset the interpolation values inside the provided helper.
     *
     * @param helper The helper.
     * @param s      The axis. side >> 1;
     * @return The same helper.
     */
    public InterpHelper resetInterp(InterpHelper helper, int s) {
        helper.reset(
                vertices[0].dx(s), vertices[0].dy(s),
                vertices[1].dx(s), vertices[1].dy(s),
                vertices[2].dx(s), vertices[2].dy(s),
                vertices[3].dx(s), vertices[3].dy(s)
        );
        return helper;
    }

    /**
     * Clamps the Quad inside the box.
     *
     * @param bb The box.
     */
    public void clamp(AABB bb) {
        clamp(c.set(bb));
    }

    /**
     * Clamps the Quad inside the box.
     *
     * @param cuboid The box.
     */
    public void clamp(Cuboid6 cuboid) {
        for (Vertex vertex : vertices) {
            vertex.vec.x = MathHelper.clip(vertex.vec.x, cuboid.min.x, cuboid.max.x);
            vertex.vec.y = MathHelper.clip(vertex.vec.y, cuboid.min.y, cuboid.max.y);
            vertex.vec.z = MathHelper.clip(vertex.vec.z, cuboid.min.z, cuboid.max.z);
        }
        calculateOrientation(true);
    }

    /**
     * Reinterpolate this quad's UV's and Colors after some transformation.
     * <p>
     * You will have needed to use {@link #resetInterp(InterpHelper, int)} prior
     * to your transformations in order for reinterpolation to not output garbage.
     *
     * @param interp Your interpolation helper.
     */
    public void reinterpolate(InterpHelper interp) {
        requireNonNull(direction, "Quad must have a direction, side needed to compute dx/dy");
        reinterpolate(interp, direction.ordinal() >> 1);
    }

    private void reinterpolate(InterpHelper interp, int side) {
        reinterpolateUV(interp, side);
        reinterpolateColor(interp, side);
    }

    private void reinterpolateUV(InterpHelper interp, int side) {
        double u0 = vertices[0].uv.u;
        double v0 = vertices[0].uv.v;

        double u1 = vertices[1].uv.u;
        double v1 = vertices[1].uv.v;

        double u2 = vertices[2].uv.u;
        double v2 = vertices[2].uv.v;

        double u3 = vertices[3].uv.u;
        double v3 = vertices[3].uv.v;

        for (int v = 0; v < 4; v++) {
            interp.locate(vertices[v].dx(side), vertices[v].dy(side));
            var uv = vertices[v].uv;
            uv.set(
                    interpUV(interp, uv.u, u0, u1, u2, u3),
                    interpUV(interp, uv.v, v0, v1, v2, v3)
            );
        }
    }

    private void reinterpolateColor(InterpHelper interp, int side) {
        int c0 = vertices[0].color;
        int c1 = vertices[1].color;
        int c2 = vertices[2].color;
        int c3 = vertices[3].color;

        for (int v = 0; v < 4; v++) {
            interp.locate(vertices[v].dx(side), vertices[v].dy(side));
            var c = vertices[v].color;
            vertices[v].color = ARGB.colorFromFloat(
                    interpColor(interp, ARGB.alphaFloat(c),
                            ARGB.alphaFloat(c0),
                            ARGB.alphaFloat(c1),
                            ARGB.alphaFloat(c2),
                            ARGB.alphaFloat(c3)
                    ),
                    interpColor(interp, ARGB.redFloat(c),
                            ARGB.redFloat(c0),
                            ARGB.redFloat(c1),
                            ARGB.redFloat(c2),
                            ARGB.redFloat(c3)
                    ),
                    interpColor(interp, ARGB.greenFloat(c),
                            ARGB.greenFloat(c0),
                            ARGB.greenFloat(c1),
                            ARGB.greenFloat(c2),
                            ARGB.greenFloat(c3)
                    ),
                    interpColor(interp, ARGB.blueFloat(c),
                            ARGB.blueFloat(c0),
                            ARGB.blueFloat(c1),
                            ARGB.blueFloat(c2),
                            ARGB.blueFloat(c3)
                    )
            );
        }
    }

    /**
     * Re-calculates the Orientation of this quad,
     * optionally the normal vector.
     *
     * @param setNormal If the normal vector should be updated.
     */
    public void calculateOrientation(boolean setNormal) {
        v1.set(vertices[3].vec).subtract(t.set(vertices[1].vec));
        v2.set(vertices[2].vec).subtract(t.set(vertices[0].vec));

        Vector3 normal = v2.crossProduct(v1).normalize();

        if (setNormal) {
            for (Vertex vertex : vertices) {
                vertex.normal.set(normal);
            }
        }
        direction = Direction.getApproximateNearest(normal.x, normal.y, normal.z);
    }

    /**
     * @return A complete copy of this quad.
     */
    public Quad copy() {
        return new Quad(this);
    }

    /**
     * Copy the data from the given {@link BakedQuad} into this quad.
     *
     * @param quad The {@link BakedQuad} to copy from.
     * @return This quad.
     */
    public Quad copyFrom(BakedQuad quad) {
        var normals = quad.bakedNormals();
        var colors = quad.bakedColors();
        for (int i = 0; i < 4; i++) {
            vertices[i].set(
                    quad.position(i),
                    quad.packedUV(i),
                    normals.normal(i),
                    colors.color(i)
            );
        }
        tintIndex = quad.tintIndex();
        direction = quad.direction();
        sprite = quad.sprite();
        shade = quad.shade();
        lightEmission = quad.lightEmission();
        ambientOcclusion = quad.hasAmbientOcclusion();
        return this;
    }

    /**
     * Copies the data from the given {@link Quad} into this quad.
     *
     * @param quad The {@link Quad} to copy from.
     * @return This quad.
     */
    public Quad copyFrom(Quad quad) {
        for (int i = 0; i < vertices.length; i++) {
            vertices[i].copyFrom(quad.vertices[i]);
        }
        tintIndex = quad.tintIndex;
        direction = quad.direction;
        sprite = quad.sprite;
        shade = quad.shade;
        lightEmission = quad.lightEmission;
        ambientOcclusion = quad.ambientOcclusion;
        return this;
    }

    /**
     * Bakes this {@link Quad} to a {@link BakedQuad}.
     *
     * @return The {@link BakedQuad}.
     */
    public BakedQuad bake() {
        return new BakedQuad(
                vertices[0].vec.vector3f(),
                vertices[1].vec.vector3f(),
                vertices[2].vec.vector3f(),
                vertices[3].vec.vector3f(),
                vertices[0].packUV(),
                vertices[1].packUV(),
                vertices[2].packUV(),
                vertices[3].packUV(),
                tintIndex,
                requireNonNull(direction, "Direction not computed."),
                requireNonNull(sprite, "Quad requires a sprite."),
                shade,
                lightEmission,
                BakedNormals.of(
                        vertices[0].packNormal(),
                        vertices[1].packNormal(),
                        vertices[2].packNormal(),
                        vertices[3].packNormal()
                ),
                BakedColors.of(
                        vertices[0].color,
                        vertices[1].color,
                        vertices[2].color,
                        vertices[3].color
                ),
                ambientOcclusion
        );
    }

    private static float interpColor(InterpHelper interp, float orig, float a, float b, float c, float d) {
        if (a == b && b == c && c == d) return orig;

        return interp.interpolate(a, b, c, d);
    }

    private static double interpUV(InterpHelper interp, double orig, double a, double b, double c, double d) {
        if (a == b && b == c && c == d) return orig;

        return interp.interpolate(a, b, c, d);
    }

    public static class Vertex {

        public Vector3 vec = new Vector3();
        public UV uv = new UV();
        public Vector3 normal = new Vector3();
        public int color = 0xFFFFFFFF;

        public Vertex() { }

        /**
         * Creates a new Vertex using the data inside the other. A copy!
         *
         * @param other The other.
         */
        public Vertex(Vertex other) {
            copyFrom(other);
        }

        /**
         * Gets the 2d X coord for the given axis.
         *
         * @param s The axis. side >> 1
         * @return The x coord.
         */
        public double dx(int s) {
            if (s <= 1) {
                return vec.x;
            } else {
                return vec.z;
            }
        }

        /**
         * Gets the 2d Y coord for the given axis.
         *
         * @param s The axis. side >> 1
         * @return The y coord.
         */
        public double dy(int s) {
            if (s > 0) {
                return vec.y;
            } else {
                return vec.z;
            }
        }

        /**
         * Copies this Vertex to a new one.
         *
         * @return The new Vertex.
         */
        public Vertex copy() {
            return new Vertex(this);
        }

        /**
         * Copy the data in the given Vertex into this vertex.
         *
         * @param other The other vertex.
         */
        public void copyFrom(Vertex other) {
            vec.set(other.vec);
            uv.set(other.uv);
            normal.set(other.normal);
            color = other.color;
        }

        /**
         * Set the data in this Vertex from the provided packed data.
         *
         * @param vec          The vector.
         * @param packedUV     The packed UV.
         * @param packedNormal The packed normals.
         * @param packedColor  The packed color.
         */
        public void set(Vector3fc vec, long packedUV, int packedNormal, int packedColor) {
            this.vec.set(vec);
            uv.set(
                    UVPair.unpackU(packedUV),
                    UVPair.unpackV(packedUV)
            );
            normal.set(
                    BakedNormals.unpackX(packedNormal),
                    BakedNormals.unpackY(packedNormal),
                    BakedNormals.unpackZ(packedNormal)
            );
            color = packedColor;
        }

        /**
         * @return The UV repacked into a long.
         */
        public long packUV() {
            return UVPair.pack((float) uv.u, (float) uv.v);
        }

        /**
         * @return The Normals repacked into an int.
         */
        public int packNormal() {
            return BakedNormals.pack((float) normal.x, (float) normal.y, (float) normal.z);
        }
    }
}
