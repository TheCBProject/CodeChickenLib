package codechicken.lib.compat;

import codechicken.lib.CodeChickenLib;
import codechicken.lib.gui.modular.ModularGuiContainer;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

/**
 * Created by brandon3055 on 31/12/2023
 */
@JeiPlugin
public class JEIPlugin implements IModPlugin {
    private static final ResourceLocation ID = new ResourceLocation(CodeChickenLib.MOD_ID, "jei_plugin");

    public JEIPlugin() {}

    @Override
    public ResourceLocation getPluginUid() {
        return ID;
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addGuiContainerHandler(ModularGuiContainer.class, new IGuiContainerHandler<>() {
            @Override
            public List<Rect2i> getGuiExtraAreas(ModularGuiContainer screen) {
                return screen.getModularGui().getJeiExclusions().map(e -> e.getRectangle().toRect2i()).toList();
            }
        });
    }
}
