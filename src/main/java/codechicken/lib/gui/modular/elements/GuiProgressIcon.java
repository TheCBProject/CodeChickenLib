package codechicken.lib.gui.modular.elements;

import codechicken.lib.gui.modular.lib.BackgroundRender;
import codechicken.lib.gui.modular.lib.geometry.Axis;
import codechicken.lib.gui.modular.lib.geometry.Direction;
import codechicken.lib.gui.modular.lib.geometry.GuiParent;
import codechicken.lib.gui.modular.SpriteSupplier;
import codechicken.lib.math.MathHelper;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;

import java.util.function.Supplier;

/**
 * This can be used to create a simple progress indicator like those used in machines like furnaces.
 * <p>
 * The background texture (if one is used) and the animated texture must be the same shape and size,
 * They must be designed so that the animated texture can be rendered directly on top of the background texture with no offset.
 * The animated texture should not have any empty space on ether end as the entire width of the texture is used in the animation.
 * <p>
 * Texture must be designed for left to right animation,
 * <p>
 * Created by brandon3055 on 04/09/2023
 */
public class GuiProgressIcon extends GuiElement<GuiProgressIcon> implements BackgroundRender {

    private SpriteSupplier background = SpriteSupplier.EMPTY;
    private SpriteSupplier animated = SpriteSupplier.EMPTY;
    private Supplier<Double> progress = () -> 0D;
    private boolean rotateToDirection = true;
    private Direction direction = Direction.RIGHT;

    public GuiProgressIcon(GuiParent<?> parent) {
        this(parent, SpriteSupplier.EMPTY, SpriteSupplier.EMPTY);
    }

    public GuiProgressIcon(GuiParent<?> parent, SpriteSupplier animated) {
        this(parent, SpriteSupplier.EMPTY, animated);
    }

    public GuiProgressIcon(GuiParent<?> parent, SpriteSupplier background, SpriteSupplier animated) {
        super(parent);
        this.background = background;
        this.animated = animated;
    }

    /**
     * The expected default direction for a progress texture is left-to-right e.g. furnace arrow.
     * The texture will then be rotated to the direction specified via {@link #setDirection(Direction)}
     * If you do not want the texture to be rotated then set this to false.
     */
    public GuiProgressIcon setRotateToDirection(boolean rotateToDirection) {
        this.rotateToDirection = rotateToDirection;
        return this;
    }

    /**
     * Set the direction this progress icon is pointing, Default is RIGHT
     */
    public GuiProgressIcon setDirection(Direction direction) {
        this.direction = direction;
        return this;
    }

    /**
     * Sets the background texture, aka the "empty" texture.
     */
    public GuiProgressIcon setBackground(SpriteSupplier background) {
        this.background = background;
        return this;
    }

    /**
     * Sets the texture that will be animated.
     */
    public GuiProgressIcon setAnimated(SpriteSupplier animated) {
        this.animated = animated;
        return this;
    }

    /**
     * Set the current progress to a fixed value.
     *
     * @see #setProgress(Supplier)
     */
    public GuiProgressIcon setProgress(double progress) {
        return setProgress(() -> progress);
    }

    /**
     * Attach a supplier that returns the current progress value for this progress icon (0 to 1)
     */
    public GuiProgressIcon setProgress(Supplier<Double> progress) {
        this.progress = progress;
        return this;
    }

    public double getProgress() {
        return progress.get();
    }

    @Override
    public void renderBehind(GuiGraphics graphics, double mouseX, double mouseY, float partialTicks) {
        if (rotateToDirection) {
            graphics.pose().pushMatrix();

            double width = direction.getAxis() == Axis.X ? xSize() : ySize();
            double height = direction.getAxis() == Axis.X ? ySize() : xSize();

            graphics.pose().translate((float) (xMin() + (xSize() / 2)), (float) (yMin() + (ySize() / 2)));
            graphics.pose().rotate((float) (Direction.RIGHT.rotationTo(direction) * MathHelper.torad));

            double halfWidth = width / 2;
            double halfHeight = height / 2;
            background.ifPresent(sprite -> {
                graphics.cc$blitSprite(RenderPipelines.GUI_TEXTURED, sprite, -halfWidth, -halfHeight, halfWidth, halfHeight);
            });

            animated.ifPresent(sprite -> {
                float progress = (float) getProgress();
                graphics.cc$blitPartialSprite(RenderPipelines.GUI_TEXTURED, -halfWidth, -halfHeight, -halfWidth + (width * progress), -halfHeight + height, sprite, 0F, 0F, progress, 1F, 0xFFFFFFFF);
            });
            graphics.pose().popMatrix();
        } else {
            background.ifPresent(sprite -> {
                graphics.cc$blitSprite(RenderPipelines.GUI_TEXTURED, sprite, getRectangle());
            });
            animated.ifPresent(sprite -> {
                float progress = (float) getProgress();
                switch (direction) {
                    case UP -> graphics.cc$blitPartialSprite(RenderPipelines.GUI_TEXTURED, xMin(), yMax() - (ySize() * progress), xMax(), yMax(), sprite, 0F, 1F - progress, 1F, 1F);
                    case LEFT -> graphics.cc$blitPartialSprite(RenderPipelines.GUI_TEXTURED, xMax() - (xSize() * progress), yMin(), xMax(), yMax(), sprite, 1F - progress, 0F, 1F, 1F);
                    case DOWN -> graphics.cc$blitPartialSprite(RenderPipelines.GUI_TEXTURED, xMin(), yMin(), xMax(), yMin() + (ySize() * progress), sprite, 0F, 0F, 1F, progress);
                    case RIGHT -> graphics.cc$blitPartialSprite(RenderPipelines.GUI_TEXTURED, xMin(), yMin(), xMin() + (xSize() * progress), yMax(), sprite, 0F, 0F, progress, 1F);
                }
            });
        }
    }
}
