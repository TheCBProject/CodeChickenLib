package codechicken.lib.gui.modular;

import codechicken.lib.gui.modular.lib.GuiProvider;
import codechicken.lib.gui.modular.lib.GuiRender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

/**
 * A simple ModularGui screen implementation.
 * This is simply a wrapper for a {@link ModularGui} that takes a {@link GuiProvider}
 * This should be suitable for most basic gui screens.
 * <p>
 * Created by brandon3055 on 19/08/2023
 */
public class ModularGuiScreen extends Screen {

    protected final ModularGui modularGui;

    public ModularGuiScreen(GuiProvider provider) {
        super(Component.empty());
        this.modularGui = new ModularGui(provider);
        this.modularGui.setScreen(this);
    }

    public ModularGuiScreen(GuiProvider builder, Screen parentScreen) {
        super(Component.empty());
        this.modularGui = new ModularGui(builder, parentScreen);
        this.modularGui.setScreen(this);
    }

    public ModularGui getModularGui() {
        return modularGui;
    }

    @NotNull
    @Override
    public Component getTitle() {
        return modularGui.getGuiTitle();
    }

    @Override
    public boolean isPauseScreen() {
        return modularGui.isPauseScreen();
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return modularGui.closeOnEscape();
    }

    @Override
    protected void init() {
        modularGui.onScreenInit(minecraft, font, width, height);
    }

    @Override
    public void resize(@NotNull Minecraft minecraft, int width, int height) {
        super.resize(minecraft, width, height);
        modularGui.onScreenInit(minecraft, font, width, height);
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        if (modularGui.renderBackground()) {
            renderBackground(graphics);
        }
        GuiRender render = GuiRender.convert(graphics);
        modularGui.render(render, partialTicks);
        modularGui.renderOverlay(render, partialTicks);
    }

    @Override
    public void tick() {
        modularGui.tick();
    }

    @Override
    public void removed() {
        modularGui.onGuiClose();
    }

    //=== Input Pass-though ===//

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        modularGui.mouseMoved(mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return modularGui.mouseClicked(mouseX, mouseY, button) || super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return modularGui.mouseReleased(mouseX, mouseY, button) || super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scroll) {
        return modularGui.mouseScrolled(mouseX, mouseY, scroll) || super.mouseScrolled(mouseX, mouseY, scroll);
    }

    @Override
    public boolean keyPressed(int key, int scancode, int modifiers) {
        return modularGui.keyPressed(key, scancode, modifiers) || super.keyPressed(key, scancode, modifiers);
    }

    @Override
    public boolean keyReleased(int key, int scancode, int modifiers) {
        return modularGui.keyReleased(key, scancode, modifiers) || super.keyReleased(key, scancode, modifiers);
    }

    @Override
    public boolean charTyped(char character, int modifiers) {
        return modularGui.charTyped(character, modifiers) || super.charTyped(character, modifiers);
    }
}
