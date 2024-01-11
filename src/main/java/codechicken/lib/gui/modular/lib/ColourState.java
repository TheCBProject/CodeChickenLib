package codechicken.lib.gui.modular.lib;

import codechicken.lib.colour.Colour;
import codechicken.lib.colour.ColourARGB;

import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Created by brandon3055 on 19/11/2023
 */
public interface ColourState {

    int get();

    default ColourARGB getColour() {
        return new ColourARGB(get());
    }

    void set(int colour);

    default void set(Colour colour) {
        set(colour.argb());
    }

    default String getHexColour() {
        return Integer.toHexString(get()).toUpperCase(Locale.ROOT);
    }

    default void setHexColour(String hexColour) {
        try {
            set(Integer.parseUnsignedInt(hexColour, 16));
        } catch (Throwable e) {
            set(0);
        }
    }

    static ColourState create() {
        return create(null);
    }

    static ColourState create(Consumer<Integer> listener) {
        return new ColourState() {
            int colour = 0;
            @Override
            public int get() {
                return colour;
            }

            @Override
            public void set(int colour) {
                this.colour = colour;
                if (listener != null) listener.accept(colour);
            }
        };
    }

    static ColourState create(Supplier<Integer> getter, Consumer<Integer> setter) {
        return new ColourState() {
            @Override
            public int get() {
                return getter.get();
            }

            @Override
            public void set(int colour) {
                setter.accept(colour);
            }
        };
    }
}
