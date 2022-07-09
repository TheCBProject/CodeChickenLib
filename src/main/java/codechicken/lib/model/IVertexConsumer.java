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

import codechicken.lib.util.VertexUtils;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;

public interface IVertexConsumer {

    VertexFormat getVertexFormat();

    void setQuadTint(int tint);

    void setQuadOrientation(Direction orientation);

    void setApplyDiffuseLighting(boolean diffuse);

    void setTexture(TextureAtlasSprite texture);

    void put(int element, float... data);

    /**
     * Assumes the data is already completely unpacked.
     * You must always copy the data from the quad provided to an internal cache.
     * basically:
     * this.quad.put(quad);
     *
     * @param quad The quad to copy data from.
     */
    void put(Quad quad);

    default void put(BakedQuad quad) {
        VertexUtils.putQuad(this, quad);
    }
}
