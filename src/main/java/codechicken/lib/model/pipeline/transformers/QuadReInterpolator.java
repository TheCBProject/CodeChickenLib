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

import codechicken.lib.math.InterpHelper;
import codechicken.lib.model.CachedFormat;
import codechicken.lib.model.Quad;
import codechicken.lib.model.Quad.Vertex;
import codechicken.lib.model.pipeline.IPipelineElementFactory;
import codechicken.lib.model.pipeline.QuadTransformer;

/**
 * This transformer Re-Interpolates the Color, UV's and LightMaps.
 * Use this after all transformations that translate vertices in the pipeline.
 * <p>
 * This Transformation can only be used in the BakedPipeline.
 *
 * @author covers1624
 */
public class QuadReInterpolator extends QuadTransformer {

    public static final IPipelineElementFactory<QuadReInterpolator> FACTORY = QuadReInterpolator::new;

    private final Quad interpCache = new Quad();
    private final InterpHelper interpHelper = new InterpHelper();

    QuadReInterpolator() {
        super();
    }

    @Override
    public void reset(CachedFormat format) {
        super.reset(format);
        interpCache.reset(format);
    }

    @Override
    public void setInputQuad(Quad quad) {
        super.setInputQuad(quad);
        quad.resetInterp(interpHelper, quad.orientation.ordinal() >> 1);
    }

    @Override
    public boolean transform() {
        int s = quad.orientation.ordinal() >> 1;
        if (format.hasColor || format.hasUV || format.hasLightMap) {
            interpCache.copyFrom(quad);
            interpHelper.setup();
            for (Vertex v : quad.vertices) {
                interpHelper.locate(v.dx(s), v.dy(s));
                if (format.hasColor) {
                    v.interpColorFrom(interpHelper, interpCache.vertices);
                }
                if (format.hasUV) {
                    v.interpUVFrom(interpHelper, interpCache.vertices);
                }
                if (format.hasLightMap) {
                    v.interpLightMapFrom(interpHelper, interpCache.vertices);
                }
            }
        }
        return true;
    }
}
