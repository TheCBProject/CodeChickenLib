package codechicken.lib.gui.modular.elements;

import codechicken.lib.gui.modular.lib.BackgroundRender;
import codechicken.lib.gui.modular.lib.GuiRender;
import codechicken.lib.gui.modular.lib.geometry.Borders;
import codechicken.lib.gui.modular.lib.geometry.GuiParent;
import codechicken.lib.gui.modular.sprite.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

/**
 * Created by brandon3055 on 28/08/2023
 */
public class GuiTexture extends GuiElement<GuiTexture> implements BackgroundRender {
    private Supplier<Material> getMaterial;
    private Supplier<Integer> colour = () -> 0xFFFFFFFF;
    private Borders dynamicBorders = null;
    private Supplier<Integer> rotation = () -> 0;

    /**
     * @param parent parent {@link GuiParent}.
     */
    public GuiTexture(@NotNull GuiParent<?> parent) {
        super(parent);
    }

    public GuiTexture(@NotNull GuiParent<?> parent, Supplier<Material> supplier) {
        super(parent);
        setMaterial(supplier);
    }

    public GuiTexture(@NotNull GuiParent<?> parent, Material material) {
        super(parent);
        setMaterial(material);
    }

    public GuiTexture setMaterial(Supplier<Material> supplier) {
        this.getMaterial = supplier;
        return this;
    }

    public GuiTexture setMaterial(Material material) {
        this.getMaterial = () -> material;
        return this;
    }

    @Nullable
    public Material getMaterial() {
        return getMaterial == null ? null : getMaterial.get();
    }

    /**
     * Enables dynamic texture resizing though the use of cutting and tiling.
     * Only works with textures that can be cut up and tiled without issues, e.g. background textures or button textures.
     * This method uses the standard border with of 5 pixels on all sides.
     */
    public GuiTexture dynamicTexture() {
        return dynamicTexture(5);
    }

    /**
     * Enables dynamic texture resizing though the use of cutting and tiling.
     * Only works with textures that can be cut up and tiled without issues, e.g. background textures or button textures.
     * The border parameters indicate the width of border around the texture that must be maintained during the cutting and tiling process.
     * For standardisation purposes the border width should be >= 5
     */
    public GuiTexture dynamicTexture(int textureBorders) {
        return dynamicTexture(Borders.create(textureBorders));
    }

    /**
     * Enables dynamic texture resizing though the use of cutting and tiling.
     * Only works with textures that can be cut up and tiled without issues, e.g. background textures or button textures.
     * The border parameters indicate the width of border around the texture that must be maintained during the cutting and tiling process.
     * For standardisation purposes the border width should be >= 5
     */
    public GuiTexture dynamicTexture(Borders textureBorders) {
        dynamicBorders = textureBorders;
        return this;
    }

    /**
     * Allows you to set an argb colour.
     * This colour will be applied when rendering the texture.
     */
    public GuiTexture setColour(int colourARGB) {
        return setColour(() -> colourARGB);
    }

    /**
     * Allows you to set an argb colour provider.
     * This colour will be applied when rendering the texture.
     */
    public GuiTexture setColour(Supplier<Integer> colour) {
        this.colour = colour;
        return this;
    }

    /**
     * Sets the texture rotation, each integer increment will rotate the texture by 90 degrees.
     * (Not compatible with dynamic textures)
     */
    public GuiTexture setRotation(Supplier<Integer> rotation) {
        this.rotation = rotation;
        return this;
    }

    /**
     * Sets the texture rotation, each integer increment will rotate the texture by 90 degrees.
     * (Not compatible with dynamic textures)
     */
    public GuiTexture setRotation(int rotation) {
        this.rotation = () -> rotation;
        return this;
    }

    @Override
    public void renderBackground(GuiRender render, double mouseX, double mouseY, float partialTicks) {
        Material material = getMaterial();
        if (material == null) return;
        if (dynamicBorders != null) {
            render.dynamicTex(material, getRectangle(), dynamicBorders, colour.get());
        } else {
            render.texRect(material, rotation.get(), getRectangle(), colour.get());
        }
    }
}
