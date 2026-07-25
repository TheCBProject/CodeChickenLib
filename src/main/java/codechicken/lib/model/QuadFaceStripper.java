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

import codechicken.lib.model.Quad.Vertex;
import codechicken.lib.vec.Cuboid6;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;

import static net.minecraft.core.Direction.AxisDirection.POSITIVE;

/**
 * This transformer strips quads that lie exactly on the faces of the provided bounds.
 * Simply set the bounds for the faces, and the strip mask.
 *
 * @author covers1624
 */
public class QuadFaceStripper extends QuadTransformer {

    private final Cuboid6 bounds = new Cuboid6();
    private int mask;

    /**
     * The bounds of the faces,
     * used as the .. bounds, if all vertices of a quad
     * lay on the bounds, it is up for stripping.
     *
     * @param bounds The bounds.
     */
    public void setBounds(Cuboid6 bounds) {
        this.bounds.set(bounds);
    }

    /**
     * Overload of {@link #setBounds(Cuboid6)} for {@link AABB}.
     */
    public void setBounds(AABB bounds) {
        this.bounds.set(bounds);
    }

    /**
     * The mask to strip edges.
     * This is an opt-in system,
     * the mask is simple {@code mask = (1 << side)}
     *
     * @param mask The mask.
     */
    public void setMask(int mask) {
        this.mask = mask;
    }

    @Override
    public boolean transform(Quad quad) {
        assert quad.direction != null;
        // No mask, nothing changes.
        if (mask == 0) return true;

        // If the bit for this quad is set, then check if we should strip.
        if ((mask & (1 << quad.direction.ordinal())) != 0) {
            Direction.AxisDirection dir = quad.direction.getAxisDirection();
            Vertex[] vertices = quad.vertices;
            switch (quad.direction.getAxis()) {
                case X -> {
                    float bound = (float) (dir == POSITIVE ? bounds.max.x : bounds.min.x);
                    double x1 = vertices[0].vec.x;
                    double x2 = vertices[1].vec.x;
                    double x3 = vertices[2].vec.x;
                    double x4 = vertices[3].vec.x;
                    return x1 != x2 || x2 != x3 || x3 != x4 || x4 != bound;
                }
                case Y -> {
                    float bound = (float) (dir == POSITIVE ? bounds.max.y : bounds.min.y);
                    double y1 = vertices[0].vec.y;
                    double y2 = vertices[1].vec.y;
                    double y3 = vertices[2].vec.y;
                    double y4 = vertices[3].vec.y;
                    return y1 != y2 || y2 != y3 || y3 != y4 || y4 != bound;
                }
                case Z -> {
                    float bound = (float) (dir == POSITIVE ? bounds.max.z : bounds.min.z);
                    double z1 = vertices[0].vec.z;
                    double z2 = vertices[1].vec.z;
                    double z3 = vertices[2].vec.z;
                    double z4 = vertices[3].vec.z;
                    return z1 != z2 || z2 != z3 || z3 != z4 || z4 != bound;
                }
            }
        }
        return true;
    }
}
