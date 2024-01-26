package codechicken.lib.gui.modular.lib;

import codechicken.lib.gui.modular.elements.GuiTextField;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * The primary interface for managing getting and setting the text in a {@link GuiTextField}
 * <p>
 * Created by brandon3055 on 03/09/2023
 */
public interface TextState {

    String getText();

    void setText(String text);

    static TextState simpleState(String defaultValue) {
        return simpleState(defaultValue, null);
    }

    static TextState simpleState(String defaultValue, @Nullable Consumer<String> changeListener) {
        return new TextState() {
            private String value = defaultValue;

            @Override
            public String getText() {
                return value;
            }

            @Override
            public void setText(String text) {
                this.value = text;
                if (changeListener != null) {
                    changeListener.accept(value);
                }
            }
        };
    }

    static TextState create(Supplier<String> getValue, Consumer<String> setValue) {
        return new TextState() {
            @Override
            public String getText() {
                return getValue.get();
            }

            @Override
            public void setText(String text) {
                setValue.accept(text);
            }
        };
    }
}
