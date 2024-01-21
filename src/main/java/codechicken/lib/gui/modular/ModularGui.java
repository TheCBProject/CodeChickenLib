package codechicken.lib.gui.modular;

import codechicken.lib.gui.modular.elements.GuiElement;
import codechicken.lib.gui.modular.lib.*;
import codechicken.lib.gui.modular.lib.container.ContainerGuiProvider;
import codechicken.lib.gui.modular.lib.geometry.Constraint;
import codechicken.lib.gui.modular.lib.geometry.GeoParam;
import codechicken.lib.gui.modular.lib.geometry.GuiParent;
import net.covers1624.quack.collection.FastStream;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.Slot;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.TriConsumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * The modular gui system is built around "Gui Elements" but those elements need to be rendered by a base parent element. That's what this class is.
 * This class is essentially just a container for the root gui element.
 * <p>
 * Created by brandon3055 on 18/08/2023
 *
 * @see GuiProvider
 * @see ContainerGuiProvider
 */
public class ModularGui implements GuiParent<ModularGui> {
    private static final Logger LOGGER = LogManager.getLogger();

    private final GuiProvider provider;
    private final GuiElement<?> root;

    private boolean guiBuilt = false;
    private boolean pauseScreen = false;
    private boolean closeOnEscape = true;
    private boolean renderBackground = true;
    private boolean vanillaSlotRendering = false;

    private Font font;
    private Minecraft mc;
    private int screenWidth;
    private int screenHeight;
    private Screen screen;
    private Screen parentScreen;

    private Component guiTitle = Component.empty();
    private ResourceLocation newCursor = null;

    private final Map<Slot, GuiElement<?>> slotHandlers = new HashMap<>();
    private final List<Runnable> tickListeners = new ArrayList<>();
    private final List<Runnable> resizeListeners = new ArrayList<>();
    private final List<Runnable> closeListeners = new ArrayList<>();
    private final List<TriConsumer<Double, Double, Integer>> preClickListeners = new ArrayList<>();
    private final List<TriConsumer<Double, Double, Integer>> postClickListeners = new ArrayList<>();
    private final List<TriConsumer<Integer, Integer, Integer>> preKeyPressListeners = new ArrayList<>();
    private final List<TriConsumer<Integer, Integer, Integer>> postKeyPressListeners = new ArrayList<>();

    private final List<GuiElement<?>> jeiExclusions = new ArrayList<>();

    /**
     * @param provider The gui builder that will be used to construct this modular gui when the screen is initialized.
     */
    public ModularGui(GuiProvider provider) {
        this.provider = provider;
        if (provider instanceof DynamicTextures textures) textures.makeTextures(DynamicTextures.DynamicTexture::guiTexturePath);
        Minecraft mc = Minecraft.getInstance();
        updateScreenData(mc, mc.font, mc.getWindow().getGuiScaledWidth(), mc.getWindow().getGuiScaledHeight());
        try {
            this.root = provider.createRootElement(this);
        } catch (Throwable ex) {
            LOGGER.error("An error occurred while constructing a modular gui", ex);
            throw ex;
        }
    }

    public ModularGui(GuiProvider provider, Screen parentScreen) {
        this(provider);
        this.parentScreen = parentScreen;
    }

    public void setScreen(Screen screen) {
        this.screen = screen;
    }

    public GuiProvider getProvider() {
        return provider;
    }

    //=== Modular Gui Setup ===//

    public void setGuiTitle(@NotNull Component guiTitle) {
        this.guiTitle = guiTitle;
    }

    @NotNull
    public Component getGuiTitle() {
        return guiTitle;
    }

    /**
     * @param pauseScreen Should a single-player game pause while this screen is open?
     */
    public void setPauseScreen(boolean pauseScreen) {
        this.pauseScreen = pauseScreen;
    }

    public boolean isPauseScreen() {
        return pauseScreen;
    }

    public void setCloseOnEscape(boolean closeOnEscape) {
        this.closeOnEscape = closeOnEscape;
    }

    public boolean closeOnEscape() {
        return closeOnEscape;
    }

    /**
     * Enable / disable the default screen background. (Default Enabled)
     * This will be the usual darkened background when in-game, or the dirt background when not in game.
     */
    public void renderScreenBackground(boolean renderBackground) {
        this.renderBackground = renderBackground;
    }

    public boolean renderBackground() {
        return renderBackground;
    }

    /**
     * @return the root element to which content elements should be added.
     */
    public GuiElement<?> getRoot() {
        return root instanceof ContentElement<?> ? ((ContentElement<?>) root).getContentElement() : root;
    }

    public GuiElement<?> getDirectRoot() {
        return root;
    }

    /**
     * Sets up this gui to render like any other standards gui with the specified width and height.
     * Meaning, the root element (usually the gui background image) will be centered on the screen, and will have the specified width and height.
     * <p>
     *
     * @param guiWidth  Gui Width
     * @param guiHeight Gui Height
     * @see #initFullscreenGui()
     */
    public ModularGui initStandardGui(int guiWidth, int guiHeight) {
        root.constrain(GeoParam.WIDTH, Constraint.literal(guiWidth));
        root.constrain(GeoParam.HEIGHT, Constraint.literal(guiHeight));
        root.constrain(GeoParam.LEFT, Constraint.midPoint(get(GeoParam.LEFT), get(GeoParam.RIGHT), guiWidth / -2D));
        root.constrain(GeoParam.TOP, Constraint.midPoint(get(GeoParam.TOP), get(GeoParam.BOTTOM), guiHeight / -2D));
        return this;
    }

    /**
     * Sets up this gui to render as a full screen gui.
     * Meaning the root element's geometry will match that of the underlying minecraft screen.
     * <p>
     *
     * @see #initStandardGui(int, int)
     */
    public ModularGui initFullscreenGui() {
        root.constrain(GeoParam.WIDTH, Constraint.match(get(GeoParam.WIDTH)));
        root.constrain(GeoParam.HEIGHT, Constraint.match(get(GeoParam.HEIGHT)));
        root.constrain(GeoParam.TOP, Constraint.match(get(GeoParam.TOP)));
        root.constrain(GeoParam.LEFT, Constraint.match(get(GeoParam.LEFT)));
        return this;
    }

    /**
     * By default, modular gui completely overrides vanillas default slot rendering.
     * This ensures slots render within the depth constraint of the slot element and avoids situations where
     * stacks in slots render on top of other parts of the gui.
     * Meaning you can do things like hide slots by disabling the slot element, Or render elements on top of the slots.
     * <p>
     * This method allow you to return full rendering control to vanilla if you need to for whatever reason.
     */
    public void setVanillaSlotRendering(boolean vanillaSlotRendering) {
        this.vanillaSlotRendering = vanillaSlotRendering;
    }

    public boolean vanillaSlotRendering() {
        return vanillaSlotRendering;
    }

    //=== Modular Gui Passthrough Methods ===//

    /**
     * Create a new {@link GuiRender} for the current render call.
     *
     * @param buffers BufferSource can be retried from {@link GuiGraphics}
     * @return A new {@link GuiRender} for the current render call.
     */
    public GuiRender createRender(MultiBufferSource.BufferSource buffers) {
        return new GuiRender(mc, buffers);
    }

    /**
     * Primary render method for ModularGui. The screen implementing ModularGui must call this in its render method.
     * Followed by the {@link #renderOverlay(GuiRender, float)} method to handle overlay rendering.
     *
     * @param render A new gui render call should be constructed for each frame via {@link #createRender(MultiBufferSource.BufferSource)}
     */
    public void render(GuiRender render, float partialTicks) {
        root.clearGeometryCache();
        double mouseX = computeMouseX();
        double mouseY = computeMouseY();
        root.render(render, mouseX, mouseY, partialTicks);

        //Ensure overlay is rendered at a depth of ether 400 or total element depth + 100 (whichever is greater)
        double depth = root.getCombinedElementDepth();
        if (depth <= 300) {
            render.pose().translate(0, 0, 400 - depth);
        } else {
            render.pose().translate(0, 0, 100);
        }
    }

    /**
     * Handles gui overlay rendering. This is where things like tool tips are rendered.
     * This should be called immediately after {@link #render(GuiRender, float)}
     * <p>
     * The reason this is split out from {@link #render(GuiRender, float)} is to allow
     * stack tool tips to override gui overlay rendering in {@link ModularGuiContainer}
     *
     * @param render This should be the same render instance that was passed to the previous {@link #render(GuiRender, float)} call.
     * @return true if an overlay such as a tooltip is currently being drawn.
     */
    public boolean renderOverlay(GuiRender render, float partialTicks) {
        double mouseX = computeMouseX();
        double mouseY = computeMouseY();
        return root.renderOverlay(render, mouseX, mouseY, partialTicks, false);
    }

    /**
     * Primary update / tick method. Must be called from the tick method of the implementing screen.
     */
    public void tick() {
        newCursor = null;
        double mouseX = computeMouseX();
        double mouseY = computeMouseY();
        root.updateMouseOver(mouseX, mouseY, false);
        tickListeners.forEach(Runnable::run);
        root.tick(mouseX, mouseY);
        CursorHelper.setCursor(newCursor);
    }

    /**
     * Pass through for the mouseMoved event. Any screen implementing {@link ModularGui} must pass through this event.
     *
     * @param mouseX new mouse X position
     * @param mouseY new mouse Y position
     */
    public void mouseMoved(double mouseX, double mouseY) {
        root.mouseMoved(mouseX, mouseY);
    }

    /**
     * Pass through for the mouseClicked event. Any screen implementing {@link ModularGui} must pass through this event.
     *
     * @param mouseX Mouse X position
     * @param mouseY Mouse Y position
     * @param button Mouse Button
     * @return true if this event has been consumed.
     */
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        preClickListeners.forEach(e -> e.accept(mouseX, mouseY, button));
        boolean consumed = root.mouseClicked(mouseX, mouseY, button, false);
        if (!consumed) {
            postClickListeners.forEach(e -> e.accept(mouseX, mouseY, button));
        }
        return consumed;
    }

    /**
     * Pass through for the mouseReleased event. Any screen implementing {@link ModularGui} must pass through this event.
     *
     * @param mouseX Mouse X position
     * @param mouseY Mouse Y position
     * @param button Mouse Button
     * @return true if this event has been consumed.
     */
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return root.mouseReleased(mouseX, mouseY, button, false);
    }

    /**
     * Pass through for the keyPressed event. Any screen implementing {@link ModularGui} must pass through this event.
     *
     * @param key       the keyboard key that was pressed.
     * @param scancode  the system-specific scancode of the key
     * @param modifiers bitfield describing which modifier keys were held down.
     * @return true if this event has been consumed.
     */
    public boolean keyPressed(int key, int scancode, int modifiers) {
        preKeyPressListeners.forEach(e -> e.accept(key, scancode, modifiers));
        boolean consumed = root.keyPressed(key, scancode, modifiers, false);
        if (!consumed) {
            postKeyPressListeners.forEach(e -> e.accept(key, scancode, modifiers));
        }
        return consumed;
    }

    /**
     * Pass through for the keyReleased event. Any screen implementing {@link ModularGui} must pass through this event.
     *
     * @param key       the keyboard key that was released.
     * @param scancode  the system-specific scancode of the key
     * @param modifiers bitfield describing which modifier keys were held down.
     * @return true if this event has been consumed.
     */
    public boolean keyReleased(int key, int scancode, int modifiers) {
        return root.keyReleased(key, scancode, modifiers, false);
    }

    /**
     * Pass through for the charTyped event. Any screen implementing {@link ModularGui} must pass through this event.
     *
     * @param character The character typed.
     * @param modifiers bitfield describing which modifier keys were held down.
     * @return true if this event has been consumed.
     */
    public boolean charTyped(char character, int modifiers) {
        return root.charTyped(character, modifiers, false);
    }

    /**
     * Pass through for the mouseScrolled event. Any screen implementing {@link ModularGui} must pass through this event.
     *
     * @param mouseX Mouse X position
     * @param mouseY Mouse Y position
     * @param scroll Scroll direction and amount
     * @return true if this event has been consumed.
     */
    public boolean mouseScrolled(double mouseX, double mouseY, double scroll) {
        return root.mouseScrolled(mouseX, mouseY, scroll, false);
    }

    /**
     * Must be called by the screen when this gui is closed.
     */
    public void onGuiClose() {
        CursorHelper.resetCursor();
        closeListeners.forEach(Runnable::run);
    }

    //=== Basic Minecraft Stuff ===//

    protected void updateScreenData(Minecraft mc, Font font, int screenWidth, int screenHeight) {
        this.mc = mc;
        this.font = font;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }

    @Override
    public void onScreenInit(Minecraft mc, Font font, int screenWidth, int screenHeight) {
        updateScreenData(mc, font, screenWidth, screenHeight);
        root.clearGeometryCache();
        try {
            root.onScreenInit(mc, font, screenWidth, screenHeight);
            if (!guiBuilt) {
                guiBuilt = true;
                provider.buildGui(this);
            } else {
                resizeListeners.forEach(Runnable::run);
            }
        } catch (Throwable ex) {
            //Because it seems the default behavior is to just silently consume init errors... Not helpful!
            LOGGER.error("An error occurred while building a modular gui", ex);
            throw ex;
        }
    }

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
        return this;
    }

    /**
     * Returns the Screen housing this {@link ModularGui}
     * With custom ModularGui implementations this may be null.
     */
    public Screen getScreen() {
        return screen;
    }

    @Nullable
    public Screen getParentScreen() {
        return parentScreen;
    }

    //=== Child Elements ===//

    @Override
    public List<GuiElement<?>> getChildren() {
        throw new UnsupportedOperationException("Child elements must be managed via the root gui element not the modular gui itself.");
    }

    @Override
    public void addChild(GuiElement<?> child) {
        if (root == null) { //If child is null, we are adding the root element.
            child.initElement(this);
            return;
        }
        throw new UnsupportedOperationException("Child elements must be managed via the root gui element not the modular gui itself.");
    }

    @Override
    public ModularGui addChild(Consumer<ModularGui> createChild) {
        throw new UnsupportedOperationException("Child elements must be managed via the root gui element not the modular gui itself.");
    }

    @Override
    public void adoptChild(GuiElement<?> child) {
        throw new UnsupportedOperationException("Child elements must be managed via the root gui element not the modular gui itself.");
    }

    @Override
    public void removeChild(GuiElement<?> child) {
        throw new UnsupportedOperationException("Child elements must be managed via the root gui element not the modular gui itself.");
    }

    //=== Geometry ===//
    //The geometry of the base ModularGui class should always match the underlying minecraft screen.

    @Override
    public double xMin() {
        return 0;
    }

    @Override
    public double xMax() {
        return screenWidth;
    }

    @Override
    public double xSize() {
        return screenWidth;
    }

    @Override
    public double yMin() {
        return 0;
    }

    @Override
    public double yMax() {
        return screenHeight;
    }

    @Override
    public double ySize() {
        return screenHeight;
    }

    //=== Other ===//

    public double computeMouseX() {
        return mc.mouseHandler.xpos() * (double) mc.getWindow().getGuiScaledWidth() / (double) mc.getWindow().getScreenWidth();
    }

    public double computeMouseY() {
        return mc.mouseHandler.ypos() * (double) mc.getWindow().getGuiScaledHeight() / (double) mc.getWindow().getScreenHeight();
    }

    /**
     * Provides a way to later retrieve the gui element responsible for positioning and rendering a slot.
     */
    public void setSlotHandler(Slot slot, GuiElement<?> handler) {
        if (slotHandlers.containsKey(slot)) throw new IllegalStateException("A gui slot can only have a single handler!");
        slotHandlers.put(slot, handler);
    }

    /**
     * Returns the gui element responsible for managing a gui slot.
     */
    public GuiElement<?> getSlotHandler(Slot slot) {
        return slotHandlers.get(slot);
    }

    /**
     * Sets the current mouse cursor.
     * The cursor is reset at the end of each UI tick so this must be set every tick for as long as you want your custom cursor to be active.
     * */
    public void setCursor(ResourceLocation cursor) {
        this.newCursor = cursor;
    }

    /**
     * Add an element to the list of jei exclusions.
     * Use this for any elements that render outside the normal gui bounds.
     * This will ensure JEI does not try to render on top of these elements.
     */
    public void jeiExclude(GuiElement<?> element) {
        if (!jeiExclusions.contains(element)) {
            jeiExclusions.add(element);
        }
    }

    /**
     * Remove an element from the list of jei exclusions.
     */
    public void removeJEIExclude(GuiElement<?> element) {
        jeiExclusions.remove(element);
    }

    public FastStream<GuiElement<?>> getJeiExclusions() {
        return FastStream.of(jeiExclusions).filter(GuiElement::isEnabled);
    }

    /**
     * Allows you to attach a callback that will be fired at the start of each gui tick.
     */
    public void onTick(Runnable onTick) {
        tickListeners.add(onTick);
    }

    /**
     * Allows you to attach a callback that will be fired when the parent screen is resized.
     */
    public void onResize(Runnable onResize) {
        resizeListeners.add(onResize);
    }

    public void onClose(Runnable onClose) {
        closeListeners.add(onClose);
    }

    /**
     * Allows you to attach a callback that will be fired on mouse click, before the click is handled by the rest of the gui.
     */
    public void onMouseClickPre(TriConsumer<Double, Double, Integer> onClick) {
        preClickListeners.add(onClick);
    }

    /**
     * Allows you to attach a callback that will be fired on mouse click, after the click has been handled by the rest of the gui.
     * Will only be fired if the event was not consumed by an element.
     */
    public void onMouseClickPost(TriConsumer<Double, Double, Integer> onClick) {
        postClickListeners.add(onClick);
    }


    /**
     * Allows you to attach a callback that will be fired on key press, before the is handled by the rest of the gui.
     */
    public void onKeyPressPre(TriConsumer<Integer, Integer, Integer> preKeyPress) {
        preKeyPressListeners.add(preKeyPress);
    }

    /**
     * Allows you to attach a callback that will be fired on key press, after it has been handled by the rest of the gui.
     * Will only be fired if the event was not consumed by an element.
     */
    public void onKeyPressPost(TriConsumer<Integer, Integer, Integer> postKeyPress) {
        postKeyPressListeners.add(postKeyPress);
    }
}
