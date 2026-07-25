package codechicken.lib.gui.modular.elements;

import codechicken.lib.gui.modular.lib.BackgroundRender;
import codechicken.lib.gui.modular.lib.geometry.Borders;
import codechicken.lib.gui.modular.lib.geometry.GuiParent;
import codechicken.lib.gui.modular.SpriteSupplier;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

/**
 * Created by brandon3055 on 28/08/2023
 */
public class GuiTexture extends GuiElement<GuiTexture> implements BackgroundRender {

    private SpriteSupplier spriteSupplier = SpriteSupplier.EMPTY;
    private Supplier<Integer> colour = () -> 0xFFFFFFFF;
    private @Nullable Borders dynamicBorders = null;
    private Supplier<Integer> rotation = () -> 0;

    public GuiTexture(GuiParent<?> parent) {
        this(parent, SpriteSupplier.EMPTY);
    }

    public GuiTexture(GuiParent<?> parent, SpriteSupplier spriteSupplier) {
        super(parent);
        setMaterial(spriteSupplier);
    }

    public GuiTexture setMaterial(SpriteSupplier spriteSupplier) {
        this.spriteSupplier = spriteSupplier;
        return this;
    }

    public SpriteSupplier getSprite() {
        return spriteSupplier;
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
    public GuiTexture dynamicTexture(@Nullable Borders textureBorders) {
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
    public void renderBehind(GuiGraphics graphics, double mouseX, double mouseY, float partialTicks) {
        getSprite().ifPresent(sprite -> {
            if (dynamicBorders != null) {
                graphics.cc$blitDynamicSprite(RenderPipelines.GUI_TEXTURED, sprite, getRectangle(), dynamicBorders, colour.get());
            } else {
                graphics.cc$blitSprite(RenderPipelines.GUI_TEXTURED, sprite, rotation.get(), getRectangle(), colour.get());
            }
        });
    }
}
