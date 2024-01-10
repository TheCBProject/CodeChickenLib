package codechicken.lib.gui.modular.elements;

import codechicken.lib.gui.modular.sprite.CCGuiTextures;
import codechicken.lib.gui.modular.sprite.Material;
import codechicken.lib.gui.modular.lib.Assembly;
import codechicken.lib.gui.modular.lib.BackgroundRender;
import codechicken.lib.gui.modular.lib.Constraints;
import codechicken.lib.gui.modular.lib.GuiRender;
import codechicken.lib.gui.modular.lib.geometry.GuiParent;
import codechicken.lib.util.FormatUtil;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import static net.minecraft.ChatFormatting.*;

/**
 * Created by brandon3055 on 10/09/2023
 */
public class GuiEnergyBar extends GuiElement<GuiEnergyBar> implements BackgroundRender {
    public static final DecimalFormat COMMA_FORMAT = new DecimalFormat("###,###,###,###,###", DecimalFormatSymbols.getInstance(Locale.ROOT));
    public static final Material EMPTY = CCGuiTextures.getUncached("widgets/energy_empty");
    public static final Material FULL = CCGuiTextures.getUncached("widgets/energy_full");

    private Supplier<Long> energy = () -> 0L;
    private Supplier<Long> capacity = () -> 0L;
    private Material emptyTexture = EMPTY;
    private Material fullTexture = FULL;
    private BiFunction<Long, Long, List<Component>> toolTipFormatter;

    public GuiEnergyBar(@NotNull GuiParent<?> parent) {
        super(parent);
        setTooltipDelay(0);
        setToolTipFormatter(defaultFormatter());
    }

    public GuiEnergyBar setEmptyTexture(Material emptyTexture) {
        this.emptyTexture = emptyTexture;
        return this;
    }

    public GuiEnergyBar setFullTexture(Material fullTexture) {
        this.fullTexture = fullTexture;
        return this;
    }

    public GuiEnergyBar setCapacity(long capacity) {
        return setCapacity(() -> capacity);
    }

    public GuiEnergyBar setCapacity(Supplier<Long> capacity) {
        this.capacity = capacity;
        return this;
    }

    public GuiEnergyBar setEnergy(long energy) {
        return setEnergy(() -> energy);
    }

    public GuiEnergyBar setEnergy(Supplier<Long> energy) {
        this.energy = energy;
        return this;
    }

    public long getEnergy() {
        return energy.get();
    }

    public long getCapacity() {
        return capacity.get();
    }

    /**
     * Install a custom formatter to control how the energy tool tip renders.
     */
    public GuiEnergyBar setToolTipFormatter(BiFunction<Long, Long, List<Component>> toolTipFormatter) {
        this.toolTipFormatter = toolTipFormatter;
        setTooltip(() -> this.toolTipFormatter.apply(getEnergy(), getCapacity()));
        return this;
    }

    @Override
    public void renderBehind(GuiRender render, double mouseX, double mouseY, float partialTicks) {
        float p = 1/128F;
        float height = getCapacity() <= 0 ? 0 : (float) ySize() * (getEnergy() / (float) getCapacity());
        float texHeight = height * p;
        render.partialSprite(EMPTY.renderType(GuiRender::texColType), xMin(), yMin(), xMax(), yMax(), EMPTY.sprite(), 0F, 1F - (p * (float) ySize()), p * (float) xSize(), 1F, 0xFFFFFFFF);
        render.partialSprite(FULL.renderType(GuiRender::texColType), xMin(), yMin() + (ySize() - height), xMax(), yMax(), FULL.sprite(), 0F, 1F - texHeight, p * (float) xSize(), 1F, 0xFFFFFFFF);
    }

    /**
     * Creates a simple energy bar using a simple slot as a background to make it look nice.
     */
    public static Assembly<GuiRectangle, GuiEnergyBar> simpleBar(@NotNull GuiParent<?> parent) {
        GuiRectangle container = GuiRectangle.vanillaSlot(parent);
        GuiEnergyBar energyBar = new GuiEnergyBar(container);
        Constraints.bind(energyBar, container, 1);
        return new Assembly<>(container, energyBar);
    }


    public static BiFunction<Long, Long, List<Component>> defaultFormatter() {
        return (energy, capacity) -> {
            List<Component> tooltip = new ArrayList<>();
            tooltip.add(Component.translatable("energy_bar.polylib.energy_storage").withStyle(DARK_AQUA));
            boolean shift = Screen.hasShiftDown();
            tooltip.add(Component.translatable("energy_bar.polylib.capacity")
                    .withStyle(GOLD)
                    .append(" ")
                    .append(Component.literal(shift ? FormatUtil.addCommas(capacity) : FormatUtil.formatNumber(capacity))
                            .withStyle(GRAY)
                            .append(" ")
                            .append(Component.translatable("energy_bar.polylib.rf")
                                    .withStyle(GRAY)
                            )
                    )
            );
            tooltip.add(Component.translatable("energy_bar.polylib.stored")
                    .withStyle(GOLD)
                    .append(" ")
                    .append(Component.literal(shift ? FormatUtil.addCommas(energy) : FormatUtil.formatNumber(energy))
                            .withStyle(GRAY)
                    )
                    .append(" ")
                    .append(Component.translatable("energy_bar.polylib.rf")
                            .withStyle(GRAY)
                    )
                    .append(Component.literal(String.format(" (%.2f%%)", ((double) energy / (double) capacity) * 100D))
                            .withStyle(GRAY)
                    )
            );
            return tooltip;
        };
    }

}
