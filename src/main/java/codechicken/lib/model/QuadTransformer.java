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

import java.util.Arrays;

/**
 * A {@link Quad} transformer.
 *
 * @author covers1624
 */
public abstract class QuadTransformer {

    /**
     * Transform the given quad.
     *
     * @param quad         The quad to transform.
     * @param transformers The transformers to apply.
     * @return If the transforms completed.
     * {@code false} if the quad should be discarded.
     */
    public static boolean transform(Quad quad, QuadTransformer... transformers) {
        return transform(quad, Arrays.asList(transformers));
    }

    /**
     * Transform the given quad.
     *
     * @param quad         The quad to transform.
     * @param transformers The transformers to apply.
     * @return If the transforms completed.
     * {@code false} if the quad should be discarded.
     */
    public static boolean transform(Quad quad, Iterable<QuadTransformer> transformers) {
        transformers.forEach(e -> e.beforeTransform(quad));

        for (QuadTransformer transformer : transformers) {
            if (!transformer.transform(quad)) {
                return false; // Discarded
            }
        }
        return true;
    }

    /**
     * Called before any transformation is performed on the quad.
     *
     * @param quad The quad.
     */
    public void beforeTransform(Quad quad) { }

    /**
     * Transform the quad.
     *
     * @return If transformation should continue.
     * If {@code false}, the quad will be discarded.
     */
    public abstract boolean transform(Quad quad);

    // Should be small enough.
    private final static double EPSILON = 0.00001;

    public static boolean epsComp(double a, double b) {
        return a == b || Math.abs(a - b) < EPSILON;
    }
}
