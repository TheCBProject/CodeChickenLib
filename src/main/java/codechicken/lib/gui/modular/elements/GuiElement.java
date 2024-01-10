package codechicken.lib.gui.modular.elements;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import codechicken.lib.gui.modular.ModularGui;
import codechicken.lib.gui.modular.lib.*;
import codechicken.lib.gui.modular.lib.geometry.ConstrainedGeometry;
import codechicken.lib.gui.modular.lib.geometry.GuiParent;
import codechicken.lib.gui.modular.lib.geometry.Position;
import codechicken.lib.gui.modular.lib.geometry.Rectangle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

/**
 * This is the Base class for all gui elements in Modular Gui Version 3.
 * <p>
 * In v2 this vas a massive monolithic class that had way too much crammed into it.
 * The primary goals of v3 are the following:
 * <tr>- Build a new, Extremely flexible system for handling element geometry, including relative positions, anchoring, etc.
 * This was archived using the new Geometry system. For details see {@link GuiParent} and {@link ConstrainedGeometry}
 * <tr>- Implement a system to properly handle element z offsets.
 * This was archived by giving all elements a 'depth' property which defines an elements size on the z axis.
 * This is then used to properly layer elements and child elements when they are rendered.
 * <tr>- Switch everything over to the new RenderType system. (This is mostly handled behind the scenes. You don't need to mess with it when creating a GUI)
 * <tr>- Consolidate all the various rendering helper methods into one convenient utility class.
 * The new {@link net.minecraft.client.gui.GuiGraphics} system showed me a good way to implement this.
 * <tr>- Reduce the amount of ambiguity when building GUIs. (Whether I succeeded here is up for debate xD)
 * <tr>- Cut out a lot of random bloat that was never used in v2.
 * <p>
 * <p>
 * Created by brandon3055 on 04/07/2023
 */
@SuppressWarnings ("unchecked")
public class GuiElement<T extends GuiElement<T>> extends ConstrainedGeometry<T> implements ElementEvents, ToolTipHandler<T> {

    @NotNull
    private GuiParent<?> parent;

    private final List<GuiElement<?>> addedQueue = new ArrayList<>();
    private final List<GuiElement<?>> addedFirstQueue = new ArrayList<>();
    private final List<GuiElement<?>> removeQueue = new ArrayList<>();
    private final List<GuiElement<?>> childElements = new ArrayList<>();
    public boolean initialized = false;

    private Font font;
    private Minecraft mc;
    private int screenWidth;
    private int screenHeight;

    protected int hoverTime = 0;
    private int hoverTextDelay = 10;
    private boolean isMouseOver = false;
    private boolean opaque = false;
    private boolean removed = true;
    private boolean zStacking = true;
    private Supplier<Boolean> enabled = () -> true;
    private Supplier<Boolean> enableToolTip = () -> true;
    private Supplier<List<Component>> toolTip = null;
    private Rectangle renderCull = Rectangle.create(Position.create(0, 0), () -> (double) screenWidth, () -> (double) screenHeight);

    /**
     * @param parent parent {@link GuiParent}.
     */
    public GuiElement(@NotNull GuiParent<?> parent) {
        this.parent = parent;
        this.parent.addChild(this);
    }

    @NotNull
    @Override
    public GuiParent<?> getParent() {
        return parent;
    }

    //=== Child Element Handling ===//

    /**
     * When creating custom gui elements, use this method to add any required child elements.
     * With ModularGui v3 it is technically possible to add children in the constructor,
     * But that may not always be supported. This is the preferred method.
     */
    @Deprecated//I'm starting to just do everything in the element constructors.
    protected void addChildElements() {
    }

    @Override
    public List<GuiElement<?>> getChildren() {
        return Collections.unmodifiableList(childElements);
    }

    /**
     * In Modular GUI v3, The add child method is primarily for internal use,
     * Child elements are automatically added to their parent on construction.
     *
     * @param child The child element to be added.
     */
    @Override
    public void addChild(GuiElement<?> child) {
        if (!initialized) throw new IllegalStateException("Attempted to add a child to an element before that element has been initialised!");
        if (child == this) throw new InvalidParameterException("Attempted to add element to itself as a child element.");
        if (child.getParent() != this) throw new UnsupportedOperationException("Attempted to add an already initialized element to a different parent element.");
        if (removeQueue.contains(child)) {
            removeQueue.remove(child);
            if (!childElements.contains(child)) {
                addedQueue.add(child);
            }
            child.initElement(this);
        } else if (!childElements.contains(child)) {
            addedQueue.add(child);
            child.initElement(this);
        }
    }

    protected void applyQueuedChildUpdates() {
        if (!removeQueue.isEmpty()) {
            childElements.removeAll(removeQueue);
            removeQueue.clear();
        }

        if (!addedQueue.isEmpty()) {
            childElements.addAll(addedQueue);
            addedQueue.clear();
        }
    }

    /**
     * Called immediately after an element is added to its parent, use to initialize the child element.
     */
    public void initElement(GuiParent<?> parent) {
        removed = false;
        updateScreenData(parent.mc(), parent.font(), parent.scaledScreenWidth(), parent.scaledScreenHeight());
        if (!initialized) {
            initialized = true;
            addChildElements();
        }
    }

    @Override
    public void adoptChild(GuiElement<?> child) {
        child.getParent().removeChild(child);
        child.parent = this;
        addChild(child);
    }

    @Override
    public void removeChild(GuiElement<?> child) {
        if (childElements.contains(child)) {
            child.removed = true;
            removeQueue.add(child);
        }
        addedQueue.remove(child);
    }

    @Override
    public boolean isDescendantOf(GuiElement<?> ancestor) {
        return ancestor == parent || parent.isDescendantOf(ancestor);
    }

    //=== Minecraft Properties / Initialisation ===//
    //TODO I can probably just pass these calls all the way up to the root parent...

    @Override
    public Minecraft mc() {
        return mc;
    }

    @Override
    public Font font() {
        return font;
    }

    @Override
    public int scaledScreenWidth() {
        return screenWidth;
    }

    @Override
    public int scaledScreenHeight() {
        return screenHeight;
    }

    @Override
    public ModularGui getModularGui() {
        return getParent().getModularGui();
    }

    @Override
    public void onScreenInit(Minecraft mc, Font font, int screenWidth, int screenHeight) {
        updateScreenData(mc, font, screenWidth, screenHeight);
        super.onScreenInit(mc, font, screenWidth, screenHeight);
    }

    protected void updateScreenData(Minecraft mc, Font font, int screenWidth, int screenHeight) {
        this.mc = mc;
        this.font = font;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }

    //=== Element Status ===//

    public T setEnabled(boolean enabled) {
        this.enabled = () -> enabled;
        return (T) this;
    }

    public T setEnabled(@Nullable Supplier<Boolean> enabled) {
        this.enabled = enabled;
        return (T) this;
    }

    public boolean isEnabled() {
        return !removed && enabled.get();
    }

    public boolean isRemoved() {
        return removed;
    }

    public T setEnableToolTip(Supplier<Boolean> enableToolTip) {
        this.enableToolTip = enableToolTip;
        return (T) this;
    }

    @Override
    public boolean blockMouseOver(GuiElement<?> element, double mouseX, double mouseY) {
        return getParent().blockMouseOver(element, mouseX, mouseY);
    }

    @Override
    public boolean blockMouseEvents() {
        return isMouseOver() && isOpaque();
    }

    /**
     * @return True if the cursor is within the bounds of this element.
     */
    @Deprecated(forRemoval = true) //use #isMouseOver()
    public boolean isMouseOver(double mouseX, double mouseY) {
        return isMouseOver;//GuiRender.isInRect(xMin(), yMin(), xSize(), ySize(), mouseX, mouseY) && !blockMouseOver(this, mouseX, mouseY);
    }

    /**
     * @return True if the cursor is within the bounds of this element, and there is no opaque element above this one obstructing the cursor.
     */
    public boolean isMouseOver() {
        return isMouseOver;
    }

    public boolean isOpaque() {
        return opaque;
    }

    /**
     * If an element is marked as opaque it will consume mouseOver updates, thereby preventing elements bellow from accepting mouseOver input.
     * Also prevents mouse events within this element from being passed to elements bellow.
     */
    public T setOpaque(boolean opaque) {
        this.opaque = opaque;
        return (T) this;
    }

    /**
     * @return the amount of time the cursor has spent inside this element's bounds,
     * resets to zero when the cursor leaves this element's bounds.
     */
    public int hoverTime() {
        return hoverTime;
    }

    /**
     * Note, Due to this using hoverTime, there may be a 1 tick delay in the updating of this value.
     */
    @Deprecated(forRemoval = true) //use #isMouseOver()
    public boolean hovered() {
        return hoverTime > 0;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "geometry=" + getRectangle() +
                '}';
    }

    /**
     * Add this element to the list of jei exclusions.
     * Use this for any elements that render outside the normal gui bounds.
     * This will ensure JEI does not try to render on top of these elements.
     */
    public T jeiExclude() {
        getModularGui().jeiExclude(this);
        return (T) this;
    }

    /**
     * Remove this element from the list of jei exclusions.
     */
    public T removeJEIExclude() {
        getModularGui().removeJEIExclude(this);
        return (T) this;
    }

    //=== Render / Update ===//

    /**
     * Any child elements completely outside this rectangle will not be rendered at all.
     * By default, this is set to the screen bounds (meaning the minecraft window)
     * Setting this to null will disable culling.
     */
    public T setRenderCull(@Nullable Rectangle renderCull) {
        this.renderCull = renderCull;
        return (T) this;
    }

    /**
     * Allows you to disable child z-stacking, Meaning all child elements will be rendered at the same z-level
     * rather than being stacked. (Not Recursive, children their sub elements with stacking)
     * <p>
     * This can be useful when rendering a lot of high z depth elements such as ItemStacks.
     * As long as you know for sure none of the elements intersect, it should be safe to disable stacking.
     *
     * @param zStacking Enable z stacking (default true)
     */
    public T setZStacking(boolean zStacking) {
        this.zStacking = zStacking;
        return (T) this;
    }

    public boolean zStacking() {
        return zStacking;
    }

    /**
     * Returns the depth of this element plus all of its children (recursively)
     * Note: You should almost never need to override this! Depth of background and / or foreground content
     * should be specified via {@link BackgroundRender#getBackgroundDepth()} and {@link ForegroundRender#getForegroundDepth()}
     *
     * @return The depth (z height) of this element plus all of its children.
     */
    public double getCombinedElementDepth() {
        double depth = 0;
        if (this instanceof BackgroundRender bgr) depth += bgr.getBackgroundDepth();
        if (this instanceof ForegroundRender fgr) depth += fgr.getForegroundDepth();

        double childDepth = 0;
        for (GuiElement<?> child : childElements) {
            if (!child.isEnabled()) continue;
            if (zStacking) {
                childDepth += child.getCombinedElementDepth();
            } else {
                childDepth = Math.max(childDepth, child.getCombinedElementDepth());
            }
        }

        return depth + childDepth;
    }

    /**
     * This is the main render method that handles rendering this element and any child elements it may have.
     * <b>This method almost never needs to be overridden</b>, instead when creating custom elements with custom rendering,
     * your element should implement {@link BackgroundRender} and / or {@link ForegroundRender} in or order to implement
     * its rendering.
     * <p>
     * Note: After the render is complete, the poseStack's z pos will be offset by the total depth of this element and its children.
     * This is intended behavior,
     *
     * @param render       Contains gui context information as well as essential render methods/utils including the PoseStack.
     * @param mouseX       Current mouse X position
     * @param mouseY       Current mouse Y position
     * @param partialTicks Partial render ticks
     */
    public void render(GuiRender render, double mouseX, double mouseY, float partialTicks) {
        applyQueuedChildUpdates();
        if (this instanceof BackgroundRender bgr) {
            double depth = bgr.getBackgroundDepth();
            bgr.renderBehind(render, mouseX, mouseY, partialTicks);
            if (depth > 0) {
                render.pose().translate(0, 0, depth);
            }
        }

        double maxDepth = 0;
        for (GuiElement<?> child : childElements) {
            if (child.isEnabled()) {
                boolean rendered = renderChild(child, render, mouseX, mouseY, partialTicks);
                //If z-stacking is disabled, we need to undo the z offset that was applied by the child element.
                if (!zStacking && rendered) {
                    double depth = child.getCombinedElementDepth();
                    maxDepth = Math.max(maxDepth, depth);
                    render.pose().translate(0, 0, -depth);
                }
            }
        }

        if (!zStacking) {
            //Now we need to apply the z offset of the tallest child.
            render.pose().translate(0, 0, maxDepth);
        }

        if (this instanceof ForegroundRender fgr) {
            double depth = fgr.getForegroundDepth();
            fgr.renderInFront(render, mouseX, mouseY, partialTicks);
            if (depth > 0) {
                render.pose().translate(0, 0, depth);
            }
        }
    }

    protected boolean renderChild(GuiElement<?> child, GuiRender render, double mouseX, double mouseY, float partialTicks) {
        if (renderCull != null && !renderCull.intersects(child.getRectangle())) return false;
        child.render(render, mouseX, mouseY, partialTicks);
        return true;
    }

    /**
     * Used to render overlay's such as hover text. Anything rendered in this method will be rendered on top of everything else on the screen.
     * Only one overlay should be rendered at a time, When an element renders content via the overlay method it must return true to indicate the render call has been 'consumed'
     * If the render call has already been consumed (Check via the consumed boolean) then this element should avoid rendering its overlay.
     * <p>
     * When rendering overlay content, always use the {@link PoseStack} available via the provided {@link GuiRender}
     * This stack will already have the correct Z translation to ensure the overlay renders above everything else on the screen.
     * <p>
     * To check if the cursor is over this element, use 'render.hoveredElement() == this'
     * {@link #isMouseOver()} Will also work, but may be problematic when multiple, stacked elements have overlay content.
     *
     * @param render       Contains gui context information as well as essential render methods/utils including the PoseStack.
     * @param mouseX       Current mouse X position
     * @param mouseY       Current mouse Y position
     * @param partialTicks Partial render ticks
     * @param consumed     Will be true if the overlay render call has already been consumed by another element.
     * @return true if the render call has been consumed.
     */
    public boolean renderOverlay(GuiRender render, double mouseX, double mouseY, float partialTicks, boolean consumed) {
        for (GuiElement<?> child : Lists.reverse(getChildren())) {
            if (child.isEnabled()) {
                consumed |= child.renderOverlay(render, mouseX, mouseY, partialTicks, consumed);
            }
        }
        return consumed || (showToolTip() && renderTooltip(render, mouseX, mouseY));
    }

    private boolean showToolTip() {
        return isMouseOver() && enableToolTip.get() && hoverTime() >= getTooltipDelay();
    }

    /**
     * Called every tick to update the element. Note this is called regardless of weather or not the element is actually enabled.
     *
     * @param mouseX Current mouse X position
     * @param mouseY Current mouse Y position
     */
    public void tick(double mouseX, double mouseY) {
        if (isMouseOver()) {
            hoverTime++;
        } else {
            hoverTime = 0;
        }

        for (GuiElement<?> childElement : childElements) {
            childElement.tick(mouseX, mouseY);
        }
    }

    /**
     * Called at the start of each tick to update the 'mouseOver' state of each element.
     * If the cursor is over an element that is marked as opaque, the update will be consumed.
     * This ensures no elements below the opaque element will have their mouseOver flag set to true.
     *
     * @param mouseX   Mouse X position
     * @param mouseY   Mouse Y position
     * @param consumed True if mouseover event has been consumed.
     * @return true if this event has been consumed.
     */
    public boolean updateMouseOver(double mouseX, double mouseY, boolean consumed) {
        for (GuiElement<?> child : Lists.reverse(getChildren())) {
            if (child.isEnabled()) {
                consumed |= child.updateMouseOver(mouseX, mouseY, consumed);
            }
        }

        isMouseOver = !consumed && GuiRender.isInRect(xMin(), yMin(), xSize(), ySize(), mouseX, mouseY) && !blockMouseOver(this, mouseX, mouseY);
        return consumed || (isMouseOver && isOpaque());
    }

    //=== Hover Text ===//

    @Override
    public Supplier<List<Component>> getTooltip() {
        return toolTip;
    }

    @Override
    public T setTooltipDelay(int tooltipDelay) {
        this.hoverTextDelay = tooltipDelay;
        return (T) this;
    }

    @Override
    public int getTooltipDelay() {
        return hoverTextDelay;
    }

    @Override
    public T setTooltip(@Nullable Supplier<List<Component>> tooltip) {
        this.toolTip = tooltip;
        return (T) this;
    }
}
