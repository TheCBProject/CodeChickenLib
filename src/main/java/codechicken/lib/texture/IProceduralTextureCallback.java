package codechicken.lib.texture;

import codechicken.lib.colour.Colour;

import javax.annotation.Nonnull;
import java.util.Random;

/**
 * Provides a callback function for dynamically updating the raw data of a texture.
 *
 * @author KitsuneAlex
 * @since 12/04/2021
 */
@FunctionalInterface
public interface IProceduralTextureCallback {

    /**
     * Called each time the texture frames are cycled to update the texture data.
     *
     * @param accessor The texture accessor. See {@link ITextureAccessor}.
     * @param random An independent, shared random instance.
     */
    void updateTexture(@Nonnull ITextureAccessor accessor, @Nonnull Random random);

    /**
     * Determines if {@link #updateTexture(ITextureAccessor, Random)} should be called during the current tick.
     *
     * @param random An independent, shared random instance.
     * @return True if the texture should be ticked.
     */
    default boolean shouldUpdate(@Nonnull Random random) {
        return true;
    }

    /**
     * Provides read-write access to the raw data of a texture.
     *
     * @author KitsuneAlex
     * @since 12/04/2021
     */
    interface ITextureAccessor {

        void setPixel(int x, int y, @Nonnull Colour colour);

        @Nonnull Colour getPixel(int x, int y);

    }

}
