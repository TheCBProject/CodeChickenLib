package codechicken.lib.gui.modular.elements;

import codechicken.lib.gui.modular.lib.BackgroundRender;
import codechicken.lib.gui.modular.lib.Constraints;
import codechicken.lib.gui.modular.lib.GuiRender;
import codechicken.lib.gui.modular.lib.geometry.GuiParent;
import codechicken.lib.gui.modular.sprite.CCGuiTextures;
import codechicken.lib.gui.modular.sprite.Material;
import codechicken.lib.util.FormatUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import static net.minecraft.ChatFormatting.*;

/**
 * When implementing this tank, you must specify the tank capacity in mb,
 * And then you have two options for specifying the tank contents.
 * You can set the fluid and the amount stored,
 * Or you can provide a {@link FluidStack}
 * <p>
 * Created by brandon3055 on 11/09/2023
 */
public class GuiFluidTank extends GuiElement<GuiFluidTank> implements BackgroundRender {
    //TODO make a better texture, This feels a little too.. cluttered.
    public static final Material DEFAULT_WINDOW = CCGuiTextures.getUncached("widgets/tank_window");

    private int gaugeColour = 0xFF909090;
    private boolean drawGauge = true;
    private Material window = null;
    private Supplier<Long> capacity = () -> 10000L;
    private Supplier<FluidStack> fluidStack = () -> FluidStack.EMPTY;

    private BiFunction<FluidStack, Long, List<Component>> toolTipFormatter;

    public GuiFluidTank(@NotNull GuiParent<?> parent) {
        super(parent);
        setTooltipDelay(0);
        setToolTipFormatter(defaultFormatter());
    }

    /**
     * Creates a simple tank using a simple slot as a background to make it look nice.
     */
    public static FluidTank simpleTank(@NotNull GuiParent<?> parent) {
        GuiRectangle container = GuiRectangle.vanillaSlot(parent);
        GuiFluidTank energyBar = new GuiFluidTank(container);
        Constraints.bind(energyBar, container, 1);
        return new FluidTank(container, energyBar);
    }

    /**
     * Sets the capacity of this tank in milli-buckets.
     */
    public GuiFluidTank setCapacity(long capacity) {
        return setCapacity(() -> capacity);
    }

    /**
     * Supply the capacity of this tank in milli-buckets.
     */
    public GuiFluidTank setCapacity(Supplier<Long> capacity) {
        this.capacity = capacity;
        return this;
    }

    /**
     * Allows you to set the current stored fluid stack.
     */
    public GuiFluidTank setFluidStack(FluidStack fluidStack) {
        return setFluidStack(() -> fluidStack);
    }

    /**
     * Allows you to supply the current stored fluid stack.
     */
    public GuiFluidTank setFluidStack(Supplier<FluidStack> fluidStack) {
        this.fluidStack = fluidStack;
        return this;
    }

    /**
     * Install a custom formatter to control how the fluid tool tip renders.
     */
    public GuiFluidTank setToolTipFormatter(BiFunction<FluidStack, Long, List<Component>> toolTipFormatter) {
        this.toolTipFormatter = toolTipFormatter;
        setTooltip(() -> this.toolTipFormatter.apply(getFluidStack(), getCapacity()));
        return this;
    }

    /**
     * Sets the tank window texture, Will be tiled to fit the tank size.
     *
     * @param window New window texture or null for no window texture.
     */
    public GuiFluidTank setWindow(@Nullable Material window) {
        this.window = window;
        return this;
    }

    /**
     * Enable the built-in fluid gauge lines.
     */
    public GuiFluidTank setDrawGauge(boolean drawGauge) {
        this.drawGauge = drawGauge;
        return this;
    }

    /**
     * Sets the colour of the built-in fluid gauge lines
     */
    public GuiFluidTank setGaugeColour(int gaugeColour) {
        this.gaugeColour = gaugeColour;
        return this;
    }

    public Long getCapacity() {
        return capacity.get();
    }

    public FluidStack getFluidStack() {
        return fluidStack.get();
    }

    @Override
    public void renderBackground(GuiRender render, double mouseX, double mouseY, float partialTicks) {
        FluidStack stack = getFluidStack();
        Material fluidMat = Material.fromSprite(getStillTexture(stack));

        if (!stack.isEmpty() && fluidMat != null) {
            int fluidColor = getColour(stack);
            float height = getCapacity() <= 0 ? 0 : (float) ySize() * (stack.getAmount() / (float) getCapacity());
            render.tileSprite(fluidMat.renderType(GuiRender::texColType), xMin(), yMax() - height, xMax(), yMax(), fluidMat.sprite(), fluidColor);
        }

        if (window != null) {
            render.tileSprite(window.renderType(GuiRender::texColType), xMin(), yMin(), xMax(), yMax(), window.sprite(), 0xFFFFFFFF);
        }

        gaugeColour = 0xFF000000;
        if (drawGauge) {
            double spacing = computeGaugeSpacing();
            if (spacing == 0) return;

            double pos = spacing;
            while (pos + 1 < ySize()) {
                double width = xSize() / 4;
                double yPos = yMax() - 1 - pos;
                render.fill(xMax() - width, yPos, xMax(), yPos + 1, gaugeColour);
                pos += spacing;
            }
        }
    }

    private double computeGaugeSpacing() {
        double ySize = ySize();
        double capacity = getCapacity();
        if (ySize / (capacity / 100D) > 3) return ySize / (capacity / 100D);
        else if (ySize / (capacity / 500D) > 3) return ySize / (capacity / 500D);
        else if (ySize / (capacity / 1000D) > 3) return ySize / (capacity / 1000D);
        else if (ySize / (capacity / 5000D) > 3) return ySize / (capacity / 5000D);
        else if (ySize / (capacity / 10000D) > 3) return ySize / (capacity / 10000D);
        else if (ySize / (capacity / 50000D) > 3) return ySize / (capacity / 50000D);
        else if (ySize / (capacity / 100000D) > 3) return ySize / (capacity / 100000D);
        return 0;
    }

    public static BiFunction<FluidStack, Long, List<Component>> defaultFormatter() {
        return (fluidStack, capacity) -> {
            List<Component> tooltip = new ArrayList<>();
            tooltip.add(Component.translatable("fluid_tank.polylib.fluid_storage").withStyle(DARK_AQUA));
            if (!fluidStack.isEmpty()) {
                tooltip.add(Component.translatable("fluid_tank.polylib.contains")
                        .withStyle(GOLD)
                        .append(" ")
                        .append(fluidStack.getDisplayName().copy()
                                .setStyle(Style.EMPTY
                                        .withColor(getColour(fluidStack))
                                )
                        )
                );
            }

            tooltip.add(Component.translatable("fluid_tank.polylib.capacity")
                    .withStyle(GOLD)
                    .append(" ")
                    .append(Component.literal(FormatUtil.addCommas(capacity))
                            .withStyle(GRAY)
                            .append(" ")
                            .append(Component.translatable("fluid_tank.polylib.mb")
                                    .withStyle(GRAY)
                            )
                    )
            );
            tooltip.add(Component.translatable("fluid_tank.polylib.stored")
                    .withStyle(GOLD)
                    .append(" ")
                    .append(Component.literal(FormatUtil.addCommas(fluidStack.getAmount()))
                            .withStyle(GRAY)
                    )
                    .append(" ")
                    .append(Component.translatable("fluid_tank.polylib.mb")
                            .withStyle(GRAY)
                    )
                    .append(Component.literal(String.format(" (%.2f%%)", ((double) fluidStack.getAmount() / (double) capacity) * 100D))
                            .withStyle(GRAY)
                    )
            );
            return tooltip;
        };
    }

    //TODO These could maybe go in FluidUtils? but they are client side only so...

    public static int getColour(FluidStack fluidStack) {
        return fluidStack.getFluid() == Fluids.EMPTY ? -1 : IClientFluidTypeExtensions.of(fluidStack.getFluid()).getTintColor(fluidStack);
    }

    public static int getColour(Fluid fluid) {
        return fluid == Fluids.EMPTY ? -1 : IClientFluidTypeExtensions.of(fluid).getTintColor();
    }

    @Nullable
    public static TextureAtlasSprite getStillTexture(FluidStack stack) {
        if (stack.getFluid() == Fluids.EMPTY) return null;
        ResourceLocation texture = IClientFluidTypeExtensions.of(stack.getFluid()).getStillTexture(stack);
        return Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(texture);
    }

    @Nullable
    public static TextureAtlasSprite getStillTexture(Fluid fluid) {
        if (fluid == Fluids.EMPTY) return null;
        ResourceLocation texture = IClientFluidTypeExtensions.of(fluid).getStillTexture();
        return Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(texture);
    }

    public record FluidTank(GuiRectangle container, GuiFluidTank tank) {}
}
