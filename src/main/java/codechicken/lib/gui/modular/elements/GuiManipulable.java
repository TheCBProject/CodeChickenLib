package codechicken.lib.gui.modular.elements;

import codechicken.lib.gui.modular.lib.ContentElement;
import codechicken.lib.gui.modular.lib.geometry.Constraint;
import codechicken.lib.gui.modular.lib.geometry.GeoParam;
import codechicken.lib.gui.modular.lib.geometry.GuiParent;
import codechicken.lib.gui.modular.lib.geometry.Rectangle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static codechicken.lib.gui.modular.lib.geometry.Constraint.literal;
import static codechicken.lib.gui.modular.lib.geometry.Constraint.match;
import static codechicken.lib.gui.modular.lib.geometry.GeoParam.*;

/**
 * This element can be used to create movable/resizable guis of gui elements.
 * This is achieved via a "contentElement" to which all child elements should eb attached.
 * Initially the bounds of the content element will match the parent {@link GuiManipulable} element.
 * However, depending on which features are enabled, it is possible for the user to resize the
 * content element by clicking and dragging the edges, or move the element by clicking and dragging
 * a specified "dragArea".
 * <p>
 * It should be noted that the constraints on the underlying {@link GuiManipulable} should be fairly rigid.
 * Things like dynamic constraint changes will not translate through to the contentElement,
 * <p>
 * If a UI resize occurs the content element's bounds will be reset to default.
 * You can also trigger a manual reset by calling {@link #resetBounds()}
 * <p>
 * Created by brandon3055 on 13/11/2023
 */
public class GuiManipulable extends GuiElement<GuiManipulable> implements ContentElement<GuiElement<?>> {
    private final GuiElement<?> contentElement;

    private int dragXOffset = 0;
    private int dragYOffset = 0;
    private boolean isDragging = false;
    private boolean dragPos = false;
    private boolean dragTop = false;
    private boolean dragLeft = false;
    private boolean dragBottom = false;
    private boolean dragRight = false;
    private ResourceLocation[] cursors = null;

    //Made available for external position restraints
    public int xMin = 0;
    public int xMax = 0;
    public int yMin = 0;
    public int yMax = 0;

    protected Rectangle minSize = Rectangle.create(0, 0, 50, 50);
    protected Rectangle maxSize = Rectangle.create(0, 0, 256, 256);
    protected Runnable onMovedCallback = null;
    protected Runnable onResizedCallback = null;
    protected PositionRestraint positionRestraint = draggable -> {
        if (xMin < 0) {
            int move = -xMin;
            xMin += move;
            xMax += move;
        } else if (xMax > scaledScreenWidth()) {
            int move = xMax - scaledScreenWidth();
            xMin -= move;
            xMax -= move;
        }
        if (yMin < 0) {
            int move = -yMin;
            yMin += move;
            yMax += move;
        } else if (yMax > scaledScreenHeight()) {
            int move = yMax - scaledScreenHeight();
            yMin -= move;
            yMax -= move;
        }
    };

    private GuiElement<?> moveHandle;
    private GuiElement<?> leftHandle;
    private GuiElement<?> rightHandle;
    private GuiElement<?> topHandle;
    private GuiElement<?> bottomHandle;

    public GuiManipulable(@NotNull GuiParent<?> parent) {
        super(parent);
        this.contentElement = new GuiElement<>(this)
                .constrain(LEFT, Constraint.dynamic(() -> (double) xMin))
                .constrain(RIGHT, Constraint.dynamic(() -> (double) xMax))
                .constrain(TOP, Constraint.dynamic(() -> (double) yMin))
                .constrain(BOTTOM, Constraint.dynamic(() -> (double) yMax));
        moveHandle = new GuiRectangle(contentElement);
        leftHandle = new GuiRectangle(contentElement);
        rightHandle = new GuiRectangle(contentElement);
        topHandle = new GuiRectangle(contentElement);
        bottomHandle = new GuiRectangle(contentElement);
    }

    public GuiManipulable resetBounds() {
        xMin = (int)xMin();
        xMax = (int)xMax();
        yMin = (int)yMin();
        yMax = (int)yMax();
        return this;
    }

    @Override
    public GuiManipulable constrain(GeoParam param, @Nullable Constraint constraint) {
        return super.constrain(param, constraint).resetBounds(); //TODO, This will break if strict constraints are enabled...
    }

    @Override
    public void onScreenInit(Minecraft mc, Font font, int screenWidth, int screenHeight) {
        super.onScreenInit(mc, font, screenWidth, screenHeight);
        resetBounds();
    }

    @Override
    public GuiElement<?> getContentElement() {
        return contentElement;
    }

    public GuiManipulable addResizeHandles(int handleSize, boolean includeTopHandle) {
        if (includeTopHandle) addTopHandle(handleSize);
        addLeftHandle(handleSize);
        addRightHandle(handleSize);
        addBottomHandle(handleSize);
        return this;
    }

    public GuiManipulable addTopHandle(int handleSize) {
//        if (topHandle != null) throw new IllegalStateException("Top handle already exists!");
        this.topHandle/* = new GuiRectangle(contentElement)*/
                .constrain(TOP, match(contentElement.get(TOP)))
                .constrain(LEFT, match(contentElement.get(LEFT)))
                .constrain(RIGHT, match(contentElement.get(RIGHT)))
                .constrain(HEIGHT, literal(handleSize));
        return this;
    }

    public GuiManipulable addBottomHandle(int handleSize) {
//        if (bottomHandle != null) throw new IllegalStateException("Bottom handle already exists!");
        this.bottomHandle/* = new GuiRectangle(contentElement)*/
                .constrain(BOTTOM, match(contentElement.get(BOTTOM)))
                .constrain(LEFT, match(contentElement.get(LEFT)))
                .constrain(RIGHT, match(contentElement.get(RIGHT)))
                .constrain(HEIGHT, literal(handleSize));
        return this;
    }

    public GuiManipulable addLeftHandle(int handleSize) {
//        if (leftHandle != null) throw new IllegalStateException("Left handle already exists!");
        this.leftHandle/* = new GuiRectangle(contentElement)*/
                .constrain(LEFT, match(contentElement.get(LEFT)))
                .constrain(TOP, match(contentElement.get(TOP)))
                .constrain(BOTTOM, match(contentElement.get(BOTTOM)))
                .constrain(WIDTH, literal(handleSize));
        return this;
    }

    public GuiManipulable addRightHandle(int handleSize) {
//        if (rightHandle != null) throw new IllegalStateException("Left handle already exists!");
        this.rightHandle /*= new GuiRectangle(contentElement)*/
                .constrain(RIGHT, match(contentElement.get(RIGHT)))
                .constrain(TOP, match(contentElement.get(TOP)))
                .constrain(BOTTOM, match(contentElement.get(BOTTOM)))
                .constrain(WIDTH, literal(handleSize));
        return this;
    }

    public GuiManipulable addMoveHandle(int handleSize) {
//        if (moveHandle != null) throw new IllegalStateException("Move handle already exists!");
        this.moveHandle/* = new GuiRectangle(contentElement)*/
                .constrain(TOP, match(contentElement.get(TOP)))
                .constrain(LEFT, match(contentElement.get(LEFT)))
                .constrain(RIGHT, match(contentElement.get(RIGHT)))
                .constrain(HEIGHT, literal(handleSize));
        return this;
    }

    /**
     * You can use this to retrieve the current move handle.
     * You are free to update the constraints on this handle, but it must be constrained relative to the content element.
     */
    public GuiElement<?> getMoveHandle() {
        return moveHandle;
    }

    /**
     * You can use this to retrieve the current left resize handle.
     * You are free to update the constraints on this handle, but it must be constrained relative to the content element.
     */
    public GuiElement<?> getLeftHandle() {
        return leftHandle;
    }

    /**
     * You can use this to retrieve the current right resize handle.
     * You are free to update the constraints on this handle, but it must be constrained relative to the content element.
     */
    public GuiElement<?> getRightHandle() {
        return rightHandle;
    }

    /**
     * You can use this to retrieve the current top resize handle.
     * You are free to update the constraints on this handle, but it must be constrained relative to the content element.
     */
    public GuiElement<?> getTopHandle() {
        return topHandle;
    }

    /**
     * You can use this to retrieve the current bottom resize handle.
     * You are free to update the constraints on this handle, but it must be constrained relative to the content element.
     */
    public GuiElement<?> getBottomHandle() {
        return bottomHandle;
    }

    /**
     * Set an array of 5 mouse cursor icons to be used when manipulating the gui element.
     * These cursors are (in order) Drag (Any direction), Drag-Horizontal, Drag-Vertical, Drag-Corner (Top Right/Bottom Left), Drag-Corner (Top Left/Bottom Right)
     */
    public GuiManipulable setCursors(ResourceLocation[] cursors) {
        if (cursors != null && cursors.length != 5) throw new IllegalStateException("Must provide 5 cursor icons, no more, no less. Found "+ cursors.length);
        this.cursors = cursors;
        return this;
    }

    public GuiManipulable setOnMovedCallback(Runnable onMovedCallback) {
        this.onMovedCallback = onMovedCallback;
        return this;
    }

    public GuiManipulable setOnResizedCallback(Runnable onResizedCallback) {
        this.onResizedCallback = onResizedCallback;
        return this;
    }

    public GuiManipulable setPositionRestraint(PositionRestraint positionRestraint) {
        this.positionRestraint = positionRestraint;
        return this;
    }

    public void setMinSize(Rectangle minSize) {
        this.minSize = minSize;
    }

    public void setMaxSize(Rectangle maxSize) {
        this.maxSize = maxSize;
    }

    public Rectangle getMinSize() {
        return minSize;
    }

    public Rectangle getMaxSize() {
        return maxSize;
    }


    @Override
    public void tick(double mouseX, double mouseY) {
        if (cursors != null) {
            boolean posFlag = moveHandle != null && moveHandle.isMouseOver();
            boolean topFlag = topHandle != null && topHandle.isMouseOver();
            boolean leftFlag = leftHandle != null && leftHandle.isMouseOver();
            boolean bottomFlag = bottomHandle != null && bottomHandle.isMouseOver();
            boolean rightFlag = rightHandle != null && rightHandle.isMouseOver();
            boolean any = posFlag || topFlag || leftFlag || bottomFlag || rightFlag;

            if (any) {
                if (posFlag) {
                    getModularGui().setCursor(cursors[0]);
                } else if ((topFlag && leftFlag) || (bottomFlag && rightFlag)) {
                    getModularGui().setCursor(cursors[4]);
                } else if ((topFlag && rightFlag) || (bottomFlag && leftFlag)) {
                    getModularGui().setCursor(cursors[3]);
                } else if (topFlag || bottomFlag) {
                    getModularGui().setCursor(cursors[2]);
                } else {
                    getModularGui().setCursor(cursors[1]);
                }
            }
        }

        super.tick(mouseX, mouseY);
    }

    public void startDragging() {
        double mouseX = getModularGui().computeMouseX();
        double mouseY = getModularGui().computeMouseY();
        dragXOffset = (int) (mouseX - xMin);
        dragYOffset = (int) (mouseY - yMin);
        isDragging = true;
        dragPos = true;
        onStartMove(mouseX, mouseY);
        onStartManipulation(mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (super.mouseClicked(mouseX, mouseY, button)) return true;

        boolean posFlag = moveHandle != null && moveHandle.isMouseOver();
        boolean topFlag = topHandle != null && topHandle.isMouseOver();
        boolean leftFlag = leftHandle != null && leftHandle.isMouseOver();
        boolean bottomFlag = bottomHandle != null && bottomHandle.isMouseOver();
        boolean rightFlag = rightHandle != null && rightHandle.isMouseOver();

        if (posFlag || topFlag || leftFlag || bottomFlag || rightFlag) {
            dragXOffset = (int) (mouseX - xMin);
            dragYOffset = (int) (mouseY - yMin);
            isDragging = true;
            if (posFlag) {
                dragPos = true;
                if (onStartMove(mouseX, mouseY)) {
                    isDragging = false;
                    return true;
                }
            } else {
                dragTop = topFlag;
                dragLeft = leftFlag;
                dragBottom = bottomFlag;
                dragRight = rightFlag;
                onStartResized(mouseX, mouseY);
            }
            if (onStartManipulation(mouseX, mouseY)) {
                dragPos = dragTop = dragLeft = dragBottom = dragRight = false;
            }
            return true;
        }

        return false;
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        if (isDragging) {
            int xMove = (int) (mouseX - dragXOffset) - xMin;
            int yMove = (int) (mouseY - dragYOffset) - yMin;
            if (dragPos) {
                Rectangle previous = Rectangle.create(xMin, yMin, xMax - xMin, yMax - yMin);
                xMin += xMove;
                xMax += xMove;
                yMin += yMove;
                yMax += yMove;
                validatePosition();
                validateMove(previous, (int) mouseX, (int) mouseY);
                onMoved();
            } else {
                Rectangle min = getMinSize();
                Rectangle max = getMaxSize();
                if (dragTop) {
                    yMin += yMove;
                    if (yMax - yMin < min.height()) yMin = yMax - (int) min.height();
                    if (yMax - yMin > max.height()) yMin = yMax - (int) max.height();
                    if (yMin < 0) yMin = 0;
                }
                if (dragLeft) {
                    xMin += xMove;
                    if (xMax - xMin < min.width()) xMin = xMax - (int) min.width();
                    if (xMax - xMin > max.width()) xMin = xMax - (int) max.width();
                    if (xMin < 0) xMin = 0;
                }
                if (dragBottom) {
                    yMax = yMin + (dragYOffset + yMove);
                    if (yMax - yMin < min.height()) yMax = yMin + (int) min.height();
                    if (yMax - yMin > max.height()) yMax = yMin + (int) max.height();
                    if (yMax > scaledScreenHeight()) yMax = scaledScreenHeight();
                }
                if (dragRight) {
                    xMax = xMin + (dragXOffset + xMove);
                    if (xMax - xMin < min.width()) xMax = xMin + (int) min.width();
                    if (xMax - xMin > max.width()) xMax = xMin + (int) max.width();
                    if (xMax > scaledScreenWidth()) xMax = scaledScreenWidth();
                }
                validatePosition();
                onResized();
            }
            onManipulated(mouseX, mouseY);
        }
        super.mouseMoved(mouseX, mouseY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button, boolean consumed) {
        if (isDragging) {
            validatePosition();
            if (dragPos) {
                onFinishMove(mouseX, mouseY);
            } else {
                onFinishResized(mouseX, mouseY);
            }
            onFinishManipulation(mouseX, mouseY);
        }
        isDragging = dragPos = dragTop = dragLeft = dragBottom = dragRight = false;
        return super.mouseReleased(mouseX, mouseY, button, consumed);
    }

    protected void validatePosition() {
        double x = xMin;
        double y = yMin;
        positionRestraint.restrainPosition(this);
        if ((x != xMin || y != yMin) && onMovedCallback != null) {
            onMovedCallback.run();
        }
    }

    protected void onMoved() {
        if (onMovedCallback != null) {
            onMovedCallback.run();
        }
    }

    protected void onResized() {
        if (onResizedCallback != null) {
            onResizedCallback.run();
        }
    }

    //TODO, In v2 these were made available for elements that extend GuiManipulable, In v3 i probably just want to add listener hooks.
    protected void onManipulated(double mouseX, double mouseY) {}

    protected boolean onStartManipulation(double mouseX, double mouseY) {
        return false;
    }

    protected void onFinishManipulation(double mouseX, double mouseY) {}

    protected boolean onStartMove(double mouseX, double mouseY) {
        return false;
    }

    protected void onStartResized(double mouseX, double mouseY) {}

    protected void onFinishMove(double mouseX, double mouseY) {}

    protected void onFinishResized(double mouseX, double mouseY) {}

    protected void validateMove(Rectangle previous, double mouseX, double mouseY) {
    }

    public interface PositionRestraint {
        void restrainPosition(GuiManipulable draggable);
    }
}
