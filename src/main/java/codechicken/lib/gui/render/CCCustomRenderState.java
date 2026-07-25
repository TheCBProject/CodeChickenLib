package codechicken.lib.gui.render;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.gui.render.state.GuiElementRenderState;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;

/**
 * Created by covers1624 on 4/11/26.
 */
public record CCCustomRenderState(
        RenderPipeline pipeline,
        TextureSetup textureSetup,
        Matrix3x2f pose,
        @Nullable ScreenRectangle scissorArea,
        @Nullable ScreenRectangle bounds,
        VertBuilder builder
) implements GuiElementRenderState {

    @Override
    public void buildVertices(VertexConsumer consumer) {
        builder.buildVertices(consumer, pose);
    }

    public interface VertBuilder {

        void buildVertices(VertexConsumer consumer, Matrix3x2f pose);
    }
}
