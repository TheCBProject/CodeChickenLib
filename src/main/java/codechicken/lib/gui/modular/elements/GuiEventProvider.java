package codechicken.lib.gui.modular.elements;

import codechicken.lib.gui.modular.lib.geometry.GuiParent;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Created by brandon3055 on 15/11/2023
 */
public class GuiEventProvider extends GuiElement<GuiEventProvider> {

    private boolean ignoreConsumed = false;
    private final List<Consumer<MouseButtonEvent>> clickListeners = new ArrayList<>();
    private final List<Consumer<MouseButtonEvent>> releaseListeners = new ArrayList<>();
    private final List<MouseMoveListener> movedListeners = new ArrayList<>();
    private final List<ScrollListener> scrollListeners = new ArrayList<>();
    private final List<Consumer<KeyEvent>> keyPressListeners = new ArrayList<>();
    private final List<Consumer<KeyEvent>> keyReleaseListeners = new ArrayList<>();
    private final List<Consumer<CharacterEvent>> charTypedListeners = new ArrayList<>();

    public GuiEventProvider(GuiParent<?> parent) {
        super(parent);
    }

    public GuiEventProvider setIgnoreConsumed(boolean ignoreConsumed) {
        this.ignoreConsumed = ignoreConsumed;
        return this;
    }

    public GuiEventProvider onMouseClick(Consumer<MouseButtonEvent> listener) {
        clickListeners.add(listener);
        return this;
    }

    public GuiEventProvider onMouseRelease(Consumer<MouseButtonEvent> listener) {
        releaseListeners.add(listener);
        return this;
    }

    public GuiEventProvider onMouseMove(MouseMoveListener listener) {
        movedListeners.add(listener);
        return this;
    }

    public GuiEventProvider onScroll(ScrollListener listener) {
        scrollListeners.add(listener);
        return this;
    }

    public GuiEventProvider onKeyPress(Consumer<KeyEvent> listener) {
        keyPressListeners.add(listener);
        return this;
    }

    public GuiEventProvider onKeyRelease(Consumer<KeyEvent> listener) {
        keyReleaseListeners.add(listener);
        return this;
    }

    public GuiEventProvider onCharTyped(Consumer<CharacterEvent> listener) {
        charTypedListeners.add(listener);
        return this;
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean consumed) {
        if (ignoreConsumed || !consumed) {
            clickListeners.forEach(e -> e.accept(event));
        }
        return super.mouseClicked(event, consumed);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event, boolean consumed) {
        if (ignoreConsumed || !consumed) {
            releaseListeners.forEach(e -> e.accept(event));
        }
        return super.mouseReleased(event, consumed);
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        movedListeners.forEach(e -> e.onMoved(mouseX, mouseY));
        super.mouseMoved(mouseX, mouseY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY, boolean consumed) {
        if (ignoreConsumed || !consumed) {
            scrollListeners.forEach(e -> e.onScrolled(mouseX, mouseY, scrollX, scrollY));
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY, consumed);
    }

    @Override
    public boolean keyPressed(KeyEvent event, boolean consumed) {
        if (ignoreConsumed || !consumed) {
            keyPressListeners.forEach(e -> e.accept(event));
        }
        return super.keyPressed(event, consumed);
    }

    @Override
    public boolean keyReleased(KeyEvent event, boolean consumed) {
        if (ignoreConsumed || !consumed) {
            keyReleaseListeners.forEach(e -> e.accept(event));
        }
        return super.keyReleased(event, consumed);
    }

    @Override
    public boolean charTyped(CharacterEvent event, boolean consumed) {
        if (ignoreConsumed || !consumed) {
            charTypedListeners.forEach(e -> e.accept(event));
        }
        return super.charTyped(event, consumed);
    }

    public interface MouseMoveListener {

        void onMoved(double mouseX, double mouseY);
    }

    public interface ScrollListener {

        void onScrolled(double mouseX, double mouseY, double scrollX, double scrollY);
    }
}
