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

/**
 * Reinterpolates quad properties after vertices have been transformed.
 *
 * @author covers1624
 */
public class QuadReInterpolator extends QuadTransformer {

    private final InterpHelper interpHelper = new InterpHelper();

    @Override
    public void beforeTransform(Quad quad) {
        assert quad.direction != null;
        quad.resetInterp(interpHelper, quad.direction.ordinal() >> 1);
    }

    @Override
    public boolean transform(Quad quad) {
        quad.reinterpolate(interpHelper);
        return true;
    }
}
