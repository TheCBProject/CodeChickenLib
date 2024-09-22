package codechicken.lib.internal.compat;

import codechicken.lib.CodeChickenLib;
import codechicken.lib.gui.modular.ModularGui;
import codechicken.lib.gui.modular.ModularGuiContainer;
import codechicken.lib.gui.modular.elements.GuiElement;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.handlers.IGhostIngredientHandler;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import net.covers1624.quack.collection.FastStream;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static net.covers1624.quack.util.SneakyUtils.unsafeCast;

/**
 * Created by brandon3055 on 31/12/2023
 */
@JeiPlugin
public class JEIPlugin implements IModPlugin {

    private static final ResourceLocation ID = new ResourceLocation(CodeChickenLib.MOD_ID, "jei_plugin");

    public JEIPlugin() {
    }

    @Override
    public ResourceLocation getPluginUid() {
        return ID;
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addGuiContainerHandler(ModularGuiContainer.class, new IGuiContainerHandler<>() {
            @Override
            public List<Rect2i> getGuiExtraAreas(ModularGuiContainer screen) {
                return FastStream.of(screen.getModularGui().getJeiExclusions()).map(e -> e.getRectangle().toRect2i()).toList();
            }
        });
        Class<ModularGuiContainer<?>> clazz = unsafeCast(ModularGuiContainer.class);
        registration.addGhostIngredientHandler(clazz, new IngredientDropHandler());
    }

    private static class IngredientDropHandler implements IGhostIngredientHandler<ModularGuiContainer<?>> {

        private ModularGui gui;
        private boolean highlight = true;

        @Override
        public <I> List<Target<I>> getTargetsTyped(ModularGuiContainer<?> screen, ITypedIngredient<I> ingredient, boolean doStart) {
            gui = screen.getModularGui();
            gui.setJeiHighlightTime(doStart ? 60 * 20 : 3 * 20);
            highlight = !doStart;
            if (!doStart) return Collections.emptyList();
            List<Target<I>> targets = new ArrayList<>();
            ingredient.getIngredient(VanillaTypes.ITEM_STACK).ifPresent(stack -> gui.getJeiDropTargets().forEach(e -> targets.add(new DropTarget<>(e))));
            return targets;
        }

        @Override
        public void onComplete() {
            highlight = true;
            gui.setJeiHighlightTime(0);
        }

        @Override
        public boolean shouldHighlightTargets() {
            return highlight;
        }
    }

    private record DropTarget<I>(GuiElement<?> element) implements IGhostIngredientHandler.Target<I> {

        @Override
        public Rect2i getArea() {
            return element.getRectangle().toRect2i();
        }

        @Override
        public void accept(I ingredient) {
            element.getJeiDropConsumer().accept((ItemStack) ingredient);
        }
    }
}
