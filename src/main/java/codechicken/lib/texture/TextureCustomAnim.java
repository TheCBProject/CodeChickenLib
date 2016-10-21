package codechicken.lib.texture;

import com.google.common.collect.Lists;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.data.AnimationFrame;
import net.minecraft.client.resources.data.AnimationMetadataSection;

import java.awt.image.BufferedImage;
import java.util.Iterator;
import java.util.List;

/**
 * Created by covers1624 on 21/10/2016.
 */
public class TextureCustomAnim extends TextureAtlasSprite {

    protected TextureCustomAnim(String spriteName) {
        super(spriteName);
    }

    public void loadSprite(BufferedImage[] images, AnimationMetadataSection animData) {
        resetSprite();
        this.width = images[0].getWidth();
        this.height = images[0].getHeight();

        int[][] aint = new int[images.length][];
        aint[0] = new int[images[0].getWidth() * images[0].getHeight()];
        images[0].getRGB(0, 0, images[0].getWidth(), images[0].getHeight(), aint[0], 0, images[0].getWidth());

        if (animData == null) {
            this.framesTextureData.add(aint);
        } else {
            int i = images[0].getHeight() / this.width;

            if (animData.getFrameCount() > 0) {
                Iterator iterator = animData.getFrameIndexSet().iterator();

                while (iterator.hasNext()) {
                    int j = ((Integer) iterator.next()).intValue();

                    if (j >= i) {
                        throw new RuntimeException("invalid frameindex " + j);
                    }

                    this.allocateFrameTextureData(j);
                    this.framesTextureData.set(j, getFrameTextureData(aint, this.width, this.width, j));
                }

                this.animationMetadata = animData;
            } else {
                List<AnimationFrame> list = Lists.<AnimationFrame>newArrayList();

                for (int k = 0; k < i; ++k) {
                    this.framesTextureData.add(getFrameTextureData(aint, this.width, this.width, k));
                    list.add(new AnimationFrame(k, -1));
                }

                this.animationMetadata = new AnimationMetadataSection(list, this.width, this.height, animData.getFrameTime(), animData.isInterpolate());
            }
        }
    }
}
