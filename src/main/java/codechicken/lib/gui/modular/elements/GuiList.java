package codechicken.lib.gui.modular.elements;

import codechicken.lib.gui.modular.lib.GuiRender;
import codechicken.lib.gui.modular.lib.SliderState;
import codechicken.lib.gui.modular.lib.geometry.*;
import codechicken.lib.math.MathHelper;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Predicate;

import static codechicken.lib.gui.modular.lib.geometry.Constraint.*;
import static codechicken.lib.gui.modular.lib.geometry.GeoParam.*;

/**
 * GuiList, as the name suggests allows you to display a list of objects.
 * The list type can be whatever you want, and you can install a converter to
 * map list objects to elements for display.
 * <p>
 * The default converter simply displays the toString() value of the object.
 * <p>
 * Element width will be fixed to the width of the GuiList, element height can be whatever you want.
 * <p>
 * Note on adding child elements to this:
 * If any child elements extend beyond the bounds of this element, that part will be culled.
 * Also, child elements will always end up bellow the list items when the list is updated.
 * <p>
 * Created by brandon3055 on 21/09/2023
 */
public class GuiList<E> extends GuiElement<GuiList<E>> {

    /**
     * This is made available primarily for debugging purposes where it can be useful to see what's going on behind the scenes.
     */
    public boolean enableScissor = true;
    private double yScrollPos = 0;
    private double lastWidth = 0;
    private double contentHeight = 0;
    private double itemSpacing = 1;
    private boolean rebuild = true;
    private GuiSlider hiddenBar = null;

    private final List<E> listContent = new ArrayList<>();
    private final Map<E, GuiElement<?>> elementMap = new HashMap<>();
    private final LinkedList<GuiElement<?>> visible = new LinkedList<>();
    private Predicate<E> filter = e -> true;

    private BiFunction<GuiList<E>, E, ? extends GuiElement<?>> displayBuilder = (parent, e) -> {
        GuiText text = new GuiText(parent, () -> Component.literal(String.valueOf(e))).setWrap(true);
        text.constrain(GeoParam.HEIGHT, Constraint.dynamic(() -> (double) font().wordWrapHeight(text.getText(), (int) text.xSize())));
        return text;
    };

    public GuiList(@NotNull GuiParent<?> parent) {
        super(parent);
        this.setZStacking(false);
        this.setRenderCull(getRectangle());
    }

    public boolean add(E e) {
        rebuild = true;
        return listContent.add(e);
    }

    public boolean remove(E e) {
        rebuild = true;
        return listContent.remove(e);
    }

    /**
     * You are allowed to modify this list directly, but if you do you must call
     * {@link #markDirty()} otherwise the display elements will not get updated.
     */
    public List<E> getList() {
        return listContent;
    }

    public void markDirty() {
        this.rebuild = true;
    }

    public GuiList<E> setDisplayBuilder(BiFunction<GuiList<E>, E, ? extends GuiElement<?>> displayBuilder) {
        this.displayBuilder = displayBuilder;
        return this;
    }

    public GuiList<E> setFilter(Predicate<E> filter) {
        this.filter = filter;
        return this;
    }

    public GuiList<E> setItemSpacing(double itemSpacing) {
        this.itemSpacing = itemSpacing;
        return this;
    }

    public SliderState scrollState() {
        return SliderState.forScrollBar(() -> yScrollPos, e -> {
            yScrollPos = e;
            updateVisible();
        }, () -> MathHelper.clip(ySize() / contentHeight, 0, 1));
    }

    /**
     * You can choose to attach a scroll bar to this element the same way you would a {@link GuiScrolling}
     * But sometimes you just want to be able to mouse-wheel scroll without an actual scroll bar.
     * <p>
     * This method will add a hidden scroll bar to enable mouse wheel scrolling and middle-click dragging without the need for an actual scroll bar.
     * The scroll bar will be an invisible zero width element on the right side of this list.
     */
    public GuiList<E> addHiddenScrollBar() {
        if (hiddenBar != null) removeChild(hiddenBar);
        hiddenBar = new GuiSlider(this, Axis.Y)
                .setSliderState(scrollState())
                .setScrollableElement(this)
                .constrain(TOP, match(get(TOP)))
                .constrain(LEFT, relative(get(RIGHT), -5))
                .constrain(BOTTOM, match(get(BOTTOM)))
                .constrain(RIGHT, match(get(RIGHT)));
        return this;
    }

    public GuiList<E> removeHiddenScrollBar() {
        if (hiddenBar != null) removeChild(hiddenBar);
        return this;
    }

    //=== Internal Logic ===//

    public double hiddenSize() {
        return Math.max(contentHeight - ySize(), 0);
    }

    @Override
    public void tick(double mouseX, double mouseY) {
        if (lastWidth != xSize()) {
            lastWidth = xSize();
            markDirty();
        }
        if (rebuild) {
            rebuildElements();
        }
        super.tick(mouseX, mouseY);
    }

    public void rebuildElements() {
        elementMap.values().forEach(this::removeChild);
        elementMap.clear();

        for (E item : listContent) {
            if (!filter.test(item)) continue;
            GuiElement<?> next = displayBuilder.apply(this, item);
            next.constrain(LEFT, match(get(LEFT)));
            next.constrain(RIGHT, match(get(RIGHT)));
            removeChild(next);
            elementMap.put(item, next);
        }
        rebuild = false;
        updateVisible();
    }

    public Map<E, GuiElement<?>> getElementMap() {
        return elementMap;
    }

    public void scrollTo(E scrollTo) {
        if (rebuild) {
            rebuild = false;
            rebuildElements();
        }

        if (elementMap.containsKey(scrollTo)) {
            scrollState().setPos(0);
            double yMax = yMin();

            for (E item : getList()) {
                GuiElement<?> e = elementMap.get(item);
                if (e != null) {
                    yMax += e.ySize() + 1;
                    if (item.equals(scrollTo)) break;
                }
            }

            if (yMax > yMax()) {
                double move = yMax - yMax();
                scrollState().setPos(move / hiddenSize());
            }
        }
    }

    private void updateVisible() {
        visible.forEach(this::removeChild);
        visible.clear();
        contentHeight = 0;
        if (listContent.isEmpty()) return;

        for (GuiElement<?> item : elementMap.values()) {
            contentHeight += item.ySize() + itemSpacing;
        }
        contentHeight -= itemSpacing;

        double winTop = yMin();
        double winBottom = yMax();

        double yPos = winTop + (yScrollPos * -hiddenSize());
        for (E item : listContent) {
            GuiElement<?> element = elementMap.get(item);
            if (element == null) continue;
            double top = yPos;
            double bottom = yPos + element.ySize();

            if ((top >= winTop && top <= winBottom) || (bottom >= winTop && bottom <= winBottom)) {
                addChild(element);
                visible.add(element);
                element.constrain(TOP, literal(top));
            }
            yPos = bottom + itemSpacing;
        }
    }

    @Override
    public boolean blockMouseOver(GuiElement<?> element, double mouseX, double mouseY) {
        return super.blockMouseOver(element, mouseX, mouseY) || (element.isDescendantOf(this) && !isMouseOver());
    }

    @Override
    public void render(GuiRender render, double mouseX, double mouseY, float partialTicks) {
        if (enableScissor) render.pushScissorRect(getRectangle());
        super.render(render, mouseX, mouseY, partialTicks);
        if (enableScissor) render.popScissor();
    }
}
