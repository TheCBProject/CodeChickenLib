package codechicken.lib.gui.modular.lib;

import codechicken.lib.gui.modular.lib.geometry.Axis;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * The primary interface for managing getting and setting the position of slider elements.
 * <p>
 * Created by brandon3055 on 01/09/2023
 */
public interface SliderState {

    /**
     * @return the current position. (Between 0 and 1)
     */
    double getPos();

    /**
     * Set the current slide position,
     * When using this method, make sure the provided value can not go outside the valid range of 0 to 1.
     *
     * @param pos Set the current position (Between 0 and 1)
     */
    void setPos(double pos);

    /**
     * The slider ratio is computed by dividing the length of the slider element by the total length of the slider track.
     * This is primarily for things like setting the size of scroll bar sliders, and calibrating scrolling via middle-click + drag.
     *
     * @return scroll ratio, viewable content size / total content size (Range 0 to 1)
     */
    default double sliderRatio() {
        return 0.1;
    }

    /**
     * For controlling a slider via mouse scroll wheel.
     * You can return a negative value to invert the scroll direction.
     *
     * @return The amount added to the position per scroll increment.
     */
    default double scrollSpeed() {
        double ratio = sliderRatio();
        return ratio < 0.1 ? ratio * 0.1 : ratio * ratio;
    }

    /**
     * @param scrollAxis The moving axis of the slider element.
     * @return true if the current scroll wheel event should affect this slider.
     */
    default boolean canScroll(Axis scrollAxis) {
        return true;
    }

    /**
     * Creates a basic slide state which stores its position internally.
     * Useful for things like simple slide control elements.
     */
    static SliderState create(double speed) {
        return create(speed, null);
    }

    /**
     * Creates a basic slide state which stores its position internally.
     * And allows you to attach a change listener.
     * Useful for things like simple slide control elements.
     */
    static SliderState create(double speed, @Nullable Consumer<Double> changeListener) {
        return new SliderState() {
            double pos = 0;

            @Override
            public double getPos() {
                return pos;
            }

            @Override
            public void setPos(double pos) {
                this.pos = pos;
                if (changeListener != null) {
                    changeListener.accept(pos);
                }
            }

            @Override
            public double scrollSpeed() {
                return speed;
            }
        };
    }

    static SliderState forScrollBar(Supplier<Double> getPos, Consumer<Double> setPos, Supplier<Double> getRatio) {
        return new SliderState() {
            @Override
            public double getPos() {
                return getPos.get();
            }

            @Override
            public void setPos(double pos) {
                setPos.accept(pos);
            }

            @Override
            public double sliderRatio() {
                return getRatio.get();
            }

            @Override
            public boolean canScroll(Axis scrollAxis) {
                //Controls scrolling left and right when shift key is down.
                return (scrollAxis == Axis.Y) != Screen.hasShiftDown();
            }
        };
    }

    static SliderState forSlider(Supplier<Double> getPos, Consumer<Double> setPos, Supplier<Double> getSpeed) {
        return new SliderState() {
            @Override
            public double getPos() {
                return getPos.get();
            }

            @Override
            public void setPos(double pos) {
                setPos.accept(pos);
            }

            @Override
            public double scrollSpeed() {
                return getSpeed.get();
            }
        };
    }
}
