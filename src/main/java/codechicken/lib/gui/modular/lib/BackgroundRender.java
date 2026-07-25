package codechicken.lib.gui.modular.lib;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;

/**
 * Allows a Gui Elements to render content behind child elements.
 * This is the default render mode for the majority of elements.
 * <p>
 * Created by brandon3055 on 07/08/2023
 */
public interface BackgroundRender {

    /**
     * Used to render content behind this elements child elements.
     * When rendering element content, always use the {@link PoseStack} available via the provided {@link GuiRender}
     * Where applicable, always use push/pop to ensure the stack is returned to its original state after your rendering is complete.
     *
     * @param render       Contains gui context information as well as essential render methods/utils including the PoseStack.
     */
    void renderBehind(GuiGraphics render, double mouseX, double mouseY, float partialTicks);

}
