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
import net.minecraft.util.ARGB;

/**
 * This transformer tints quads..
 * Feed it the output of BlockColors.colorMultiplier.
 * Colors are in ARGB, Alpha is completely ignored.
 * Color format: 0000 RRRR GGGG BBBB
 *
 * @author covers1624
 */
public class QuadTinter extends QuadTransformer {

    private int tint;

    public QuadTinter setTint(int tint) {
        this.tint = tint;
        return this;
    }

    @Override
    public boolean transform(Quad quad) {
        if (tint == -1) return true;

        quad.tintIndex = -1;
        for (Vertex v : quad.vertices) {
            v.color = ARGB.multiply(v.color, tint | 0xFF000000);
        }
        return true;
    }
}
