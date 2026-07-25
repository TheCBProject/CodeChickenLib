package codechicken.lib.render;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

import static codechicken.lib.CodeChickenLib.MOD_ID;

/**
 * Created by covers1624 on 7/6/26.
 */
public class CCRenderPipelines {

    public static final RenderPipeline SOLID_TRIANGLES = RenderPipelines.SOLID_BLOCK.toBuilder()
            .withVertexFormat(DefaultVertexFormat.BLOCK, VertexFormat.Mode.TRIANGLES)
            .withLocation(Identifier.fromNamespaceAndPath(MOD_ID, "pipeline/solid_triangles"))
            .build();

    public static final RenderPipeline POSITION_COLOR = RenderPipeline.builder(RenderPipelines.DEBUG_FILLED_SNIPPET)
            .withVertexFormat(DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS)
            .withDepthWrite(true) // Overwritten from DEBUG_FILLED_SNIPPET as we very much want depth here.
            .withLocation(Identifier.fromNamespaceAndPath(MOD_ID, "pipeline/position_color"))
            .build();

    public static final RenderPipeline POSITION_COLOR_NO_DEPTH = RenderPipeline.builder(RenderPipelines.DEBUG_FILLED_SNIPPET)
            .withVertexFormat(DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS)
            .withDepthWrite(false)
            .withLocation(Identifier.fromNamespaceAndPath(MOD_ID, "pipeline/position_color_no_depth"))
            .build();
}
