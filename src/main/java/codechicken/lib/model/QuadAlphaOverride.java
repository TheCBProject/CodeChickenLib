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

import codechicken.lib.colour.Colour;
import codechicken.lib.model.Quad.Vertex;

/**
 * This transformer simply overrides the alpha of the quad.
 * Only operates if the format has color.
 *
 * @author covers1624
 */
public class QuadAlphaOverride extends QuadTransformer {

    public float alphaOverride;

    @Override
    public boolean transform(Quad quad) {
        for (Vertex v : quad.vertices) {
            v.color = Colour.replace(v.color, 3, alphaOverride);
        }
        return true;
    }
}
