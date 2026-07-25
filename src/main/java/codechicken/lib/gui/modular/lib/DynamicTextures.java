package codechicken.lib.gui.modular.lib;

import net.minecraft.resources.Identifier;

import java.util.function.Function;

/**
 * Designed for use with DynamicTextureProvider
 * <p>
 * Created by brandon3055 on 07/09/2023
 */
public interface DynamicTextures {

    void makeTextures(Function<DynamicTexture, String> textures);

    default String dynamicTexture(Function<DynamicTexture, String> textures, Identifier dynamicInput, Identifier outputLocation, int width, int height, int border) {
        return textures.apply(new DynamicTexture(dynamicInput, outputLocation, width, height, border, border, border, border));
    }

    default String dynamicTexture(Function<DynamicTexture, String> textures, Identifier dynamicInput, Identifier outputLocation, int width, int height, int topBorder, int leftBorder, int bottomBorder, int rightBorder) {
        return textures.apply(new DynamicTexture(dynamicInput, outputLocation, width, height, topBorder, leftBorder, bottomBorder, rightBorder));
    }

    record DynamicTexture(Identifier dynamicInput, Identifier outputLocation, int width, int height, int topBorder, int leftBorder, int bottomBorder, int rightBorder) {
        public String guiTexturePath() {
            return outputLocation.getPath().replace("textures/gui/", "").replace(".gui", "");
        }
    }

}
