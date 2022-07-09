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

package codechicken.lib.model.pipeline.transformers;

import codechicken.lib.model.IVertexConsumer;
import codechicken.lib.model.Quad.Vertex;
import codechicken.lib.model.pipeline.IPipelineElementFactory;
import codechicken.lib.model.pipeline.QuadTransformer;

/**
 * This transformer tints quads..
 * Feed it the output of BlockColors.colorMultiplier.
 * Color format: 0000 RRRR GGGG BBBB
 *
 * @author covers1624
 */
public class QuadTinter extends QuadTransformer {

    public static final IPipelineElementFactory<QuadTinter> FACTORY = QuadTinter::new;

    private int tint;

    QuadTinter() {
        super();
    }

    public QuadTinter(IVertexConsumer consumer, int tint) {
        super(consumer);
        this.tint = tint;
    }

    public QuadTinter setTint(int tint) {
        this.tint = tint;
        return this;
    }

    @Override
    public boolean transform() {
        // Nuke tintIndex.
        quad.tintIndex = -1;
        if (format.hasColor) {
            float r = (tint >> 0x10 & 0xFF) / 255F;
            float g = (tint >> 0x08 & 0xFF) / 255F;
            float b = (tint & 0xFF) / 255F;
            for (Vertex v : quad.vertices) {
                v.color[0] *= r;
                v.color[1] *= g;
                v.color[2] *= b;
            }
        }
        return true;
    }
}
