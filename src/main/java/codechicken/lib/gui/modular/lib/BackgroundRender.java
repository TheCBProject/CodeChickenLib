package codechicken.lib.gui.modular.lib;

import com.mojang.blaze3d.vertex.PoseStack;

/**
 * Allows a Gui Elements to render content behind child elements.
 * This is the default render mode for the majority of elements.
 * <p>
 * Created by brandon3055 on 07/08/2023
 */
public interface BackgroundRender {

    /**
     * Specifies the z depth of the background content.
     * After {@link #renderBackground(GuiRender, double, double, float)} is called, the PoseStack will be translated by this amount in the z direction
     * before any assigned child elements are rendered.
     * Recommended minimum depth is 0.01 or 0.035 if this element renders text. (text shadows are rendered with a 0.03 offset)
     *
     * @return the z height of the background content.
     */
    default double getBackgroundDepth() {
        return 0.01;
    }

    /**
     * Used to render content behind this elements child elements.
     * When rendering element content, always use the {@link PoseStack} available via the provided {@link GuiRender}
     * Where applicable, always use push/pop to ensure the stack is returned to its original state after your rendering is complete.
     *
     * @param render       Contains gui context information as well as essential render methods/utils including the PoseStack.
     */
    void renderBackground(GuiRender render, double mouseX, double mouseY, float partialTicks);

}
