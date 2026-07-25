//package codechicken.lib.gui.modular.elements;
//
//import codechicken.lib.gui.modular.lib.BackgroundRender;
//import codechicken.lib.gui.modular.lib.Constraints;
//import codechicken.lib.gui.modular.lib.geometry.GuiParent;
//import codechicken.lib.gui.modular.sprite.GuiTextures;
//import codechicken.lib.gui.modular.sprite.SpriteSupplier;
//import codechicken.lib.util.FormatUtil;
//import net.minecraft.client.Minecraft;
//import net.minecraft.client.gui.GuiGraphics;
//import net.minecraft.network.chat.Component;
//
//import java.text.DecimalFormat;
//import java.text.DecimalFormatSymbols;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Locale;
//import java.util.function.BiFunction;
//import java.util.function.Supplier;
//
//import static net.minecraft.ChatFormatting.*;
//
///**
// * Created by brandon3055 on 10/09/2023
// */
//public class GuiEnergyBar extends GuiElement<GuiEnergyBar> implements BackgroundRender {
//
//    public static final DecimalFormat COMMA_FORMAT = new DecimalFormat("###,###,###,###,###", DecimalFormatSymbols.getInstance(Locale.ROOT));
//    public static final SpriteSupplier EMPTY = GuiTextures.CCL.get("widgets/energy_empty");
//    public static final SpriteSupplier FULL = GuiTextures.CCL.get("widgets/energy_full");
//
//    private Supplier<Long> energy = () -> 0L;
//    private Supplier<Long> capacity = () -> 0L;
//    private SpriteSupplier emptyTexture = EMPTY;
//    private SpriteSupplier fullTexture = FULL;
//    private BiFunction<Long, Long, List<Component>> toolTipFormatter;
//
//    public GuiEnergyBar(GuiParent<?> parent) {
//        super(parent);
//        setTooltipDelay(0);
//        setToolTipFormatter(defaultFormatter());
//    }
//
//    /**
//     * Creates a simple energy bar using a simple slot as a background to make it look nice.
//     */
//    public static EnergyBar simpleBar(GuiParent<?> parent) {
//        GuiRectangle container = GuiRectangle.vanillaSlot(parent);
//        GuiEnergyBar energyBar = new GuiEnergyBar(container);
//        Constraints.bind(energyBar, container, 1);
//        return new EnergyBar(container, energyBar);
//    }
//
//    public static BiFunction<Long, Long, List<Component>> defaultFormatter() {
//        return (energy, capacity) -> {
//            List<Component> tooltip = new ArrayList<>();
//            tooltip.add(Component.translatable("ccl.energy_bar.energy_storage").withStyle(DARK_AQUA));
//            boolean shift = Minecraft.getInstance().hasShiftDown();
//            tooltip.add(Component.translatable("ccl.energy_bar.capacity")
//                    .withStyle(GOLD)
//                    .append(" ")
//                    .append(Component.literal(shift ? FormatUtil.addCommas(capacity) : FormatUtil.formatNumber(capacity))
//                            .withStyle(GRAY)
//                            .append(" ")
//                            .append(Component.translatable("ccl.energy_bar.rf")
//                                    .withStyle(GRAY)
//                            )
//                    )
//            );
//            tooltip.add(Component.translatable("ccl.energy_bar.stored")
//                    .withStyle(GOLD)
//                    .append(" ")
//                    .append(Component.literal(shift ? FormatUtil.addCommas(energy) : FormatUtil.formatNumber(energy))
//                            .withStyle(GRAY)
//                    )
//                    .append(" ")
//                    .append(Component.translatable("ccl.energy_bar.rf")
//                            .withStyle(GRAY)
//                    )
//                    .append(Component.literal(String.format(" (%.2f%%)", ((double) energy / (double) capacity) * 100D))
//                            .withStyle(GRAY)
//                    )
//            );
//            return tooltip;
//        };
//    }
//
//    public GuiEnergyBar setEmptyTexture(SpriteSupplier emptyTexture) {
//        this.emptyTexture = emptyTexture;
//        return this;
//    }
//
//    public GuiEnergyBar setFullTexture(SpriteSupplier fullTexture) {
//        this.fullTexture = fullTexture;
//        return this;
//    }
//
//    public GuiEnergyBar setCapacity(long capacity) {
//        return setCapacity(() -> capacity);
//    }
//
//    public GuiEnergyBar setCapacity(Supplier<Long> capacity) {
//        this.capacity = capacity;
//        return this;
//    }
//
//    public GuiEnergyBar setEnergy(long energy) {
//        return setEnergy(() -> energy);
//    }
//
//    public GuiEnergyBar setEnergy(Supplier<Long> energy) {
//        this.energy = energy;
//        return this;
//    }
//
//    public long getEnergy() {
//        return energy.get();
//    }
//
//    public long getCapacity() {
//        return capacity.get();
//    }
//
//    /**
//     * Install a custom formatter to control how the energy tool tip renders.
//     */
//    public GuiEnergyBar setToolTipFormatter(BiFunction<Long, Long, List<Component>> toolTipFormatter) {
//        this.toolTipFormatter = toolTipFormatter;
//        setTooltip(() -> this.toolTipFormatter.apply(getEnergy(), getCapacity()));
//        return this;
//    }
//
//    @Override
//    public void renderBehind(GuiGraphics graphics, double mouseX, double mouseY, float partialTicks) {
//        float p = 1 / 128F;
//        float height = getCapacity() <= 0 ? 0 : (float) ySize() * (getEnergy() / (float) getCapacity());
//        float texHeight = height * p;
//        graphics.partialSprite(EMPTY.renderType(GuiRender::texColType), xMin(), yMin(), xMax(), yMax(), EMPTY.sprite(), 0F, 1F - (p * (float) ySize()), p * (float) xSize(), 1F, 0xFFFFFFFF);
//        graphics.partialSprite(FULL.renderType(GuiRender::texColType), xMin(), yMin() + (ySize() - height), xMax(), yMax(), FULL.sprite(), 0F, 1F - texHeight, p * (float) xSize(), 1F, 0xFFFFFFFF);
//    }
//
//    public record EnergyBar(GuiRectangle container, GuiEnergyBar bar) { }
//}
