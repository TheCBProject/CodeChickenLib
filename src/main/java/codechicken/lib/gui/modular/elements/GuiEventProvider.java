package codechicken.lib.gui.modular.elements;

import codechicken.lib.gui.modular.lib.geometry.GuiParent;
import org.apache.logging.log4j.util.TriConsumer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * Created by brandon3055 on 15/11/2023
 */
public class GuiEventProvider extends GuiElement<GuiEventProvider> {

    private boolean ignoreConsumed = false;
    private final List<TriConsumer<Double, Double, Integer>> clickListeners = new ArrayList<>();
    private final List<TriConsumer<Double, Double, Integer>> releaseListeners = new ArrayList<>();
    private final List<BiConsumer<Double, Double>> movedListeners = new ArrayList<>();
    private final List<TriConsumer<Double, Double, Double>> scrollListeners = new ArrayList<>();
    private final List<TriConsumer<Integer, Integer, Integer>> keyPressListeners = new ArrayList<>();
    private final List<TriConsumer<Integer, Integer, Integer>> keyReleaseListeners = new ArrayList<>();
    private final List<BiConsumer<Character, Integer>> charTypedListeners = new ArrayList<>();

    public GuiEventProvider(@NotNull GuiParent<?> parent) {
        super(parent);
    }

    public GuiEventProvider setIgnoreConsumed(boolean ignoreConsumed) {
        this.ignoreConsumed = ignoreConsumed;
        return this;
    }

    public GuiEventProvider onMouseClick(TriConsumer<Double, Double, Integer> listener) {
        clickListeners.add(listener);
        return this;
    }

    public GuiEventProvider onMouseRelease(TriConsumer<Double, Double, Integer> listener) {
        releaseListeners.add(listener);
        return this;
    }

    public GuiEventProvider onMouseMove(BiConsumer<Double, Double> listener) {
        movedListeners.add(listener);
        return this;
    }

    public GuiEventProvider onScroll(TriConsumer<Double, Double, Double> listener) {
        scrollListeners.add(listener);
        return this;
    }

    public GuiEventProvider onKeyPress(TriConsumer<Integer, Integer, Integer> listener) {
        keyPressListeners.add(listener);
        return this;
    }

    public GuiEventProvider onKeyRelease(TriConsumer<Integer, Integer, Integer> listener) {
        keyReleaseListeners.add(listener);
        return this;
    }

    public GuiEventProvider onCharTyped(BiConsumer<Character, Integer> listener) {
        charTypedListeners.add(listener);
        return this;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button, boolean consumed) {
        if (ignoreConsumed || !consumed) {
            clickListeners.forEach(e -> e.accept(mouseX, mouseY, button));
        }
        return super.mouseClicked(mouseX, mouseY, button, consumed);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button, boolean consumed) {
        if (ignoreConsumed || !consumed) {
            releaseListeners.forEach(e -> e.accept(mouseX, mouseY, button));
        }
        return super.mouseReleased(mouseX, mouseY, button, consumed);
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        movedListeners.forEach(e -> e.accept(mouseX, mouseY));
        super.mouseMoved(mouseX, mouseY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scroll, boolean consumed) {
        if (ignoreConsumed || !consumed) {
            scrollListeners.forEach(e -> e.accept(mouseX, mouseY, scroll));
        }
        return super.mouseScrolled(mouseX, mouseY, scroll, consumed);
    }

    @Override
    public boolean keyPressed(int key, int scancode, int modifiers, boolean consumed) {
        if (ignoreConsumed || !consumed) {
            keyPressListeners.forEach(e -> e.accept(key, scancode, modifiers));
        }
        return super.keyPressed(key, scancode, modifiers, consumed);
    }

    @Override
    public boolean keyReleased(int key, int scancode, int modifiers, boolean consumed) {
        if (ignoreConsumed || !consumed) {
            keyReleaseListeners.forEach(e -> e.accept(key, scancode, modifiers));
        }
        return super.keyReleased(key, scancode, modifiers, consumed);
    }

    @Override
    public boolean charTyped(char character, int modifiers, boolean consumed) {
        if (ignoreConsumed || !consumed) {
            charTypedListeners.forEach(e -> e.accept(character, modifiers));
        }
        return super.charTyped(character, modifiers, consumed);
    }
}
