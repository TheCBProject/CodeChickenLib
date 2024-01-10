package codechicken.lib.gui.modular.lib;

import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Created by brandon3055 on 19/11/2023
 */
public interface ColourState {

    int get();

    void set(int colour);

    default void set(int a, int r, int g, int b) {
        set(a << 24 | r << 16 | g << 8 | b);
    }

    default void set(int r, int g, int b) {
        set(r << 16 | g << 8 | b);
    }

    default void set(float a, float r, float g, float b) {
        set((int) (a * 255), (int) (r * 255), (int) (g * 255), (int) (b * 255));
    }

    default void set(float r, float g, float b) {
        set((int) (r * 255), (int) (g * 255), (int) (b * 255));
    }

    default void setAlpha(int a) {
        set(get() & 0x00FFFFFF | a << 24);
    }

    default void setRed(int r) {
        set(get() & 0xFF00FFFF | r << 16);
    }

    default void setGreen(int g) {
        set(get() & 0xFFFF00FF | g << 8);
    }

    default void setBlue(int b) {
        set(get() & 0xFFFFFF00 | b);
    }

    default void setAlpha(float a) {
        setAlpha((int) (a * 255));
    }

    default void setRed(float r) {
        setRed((int) (r * 255));
    }

    default void setGreen(float g) {
        setGreen((int) (g * 255));
    }

    default void setBlue(float b) {
        setBlue((int) (b * 255));
    }

    default int alphaI() {
        return get() >> 24 & 0xFF;
    }

    default int redI() {
        return get() >> 16 & 0xFF;
    }

    default int greenI() {
        return get() >> 8 & 0xFF;
    }

    default int blueI() {
        return get() & 0xFF;
    }

    default float alpha() {
        return alphaI() / 255F;
    }

    default float red() {
        return redI() / 255F;
    }

    default float green() {
        return greenI() / 255F;
    }

    default float blue() {
        return blueI() / 255F;
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
