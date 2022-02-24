package codechicken.lib.render.block;

import codechicken.lib.internal.ExceptionMessageEventHandler;
import codechicken.lib.internal.proxy.ProxyClient;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.client.model.data.IModelData;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import static codechicken.lib.util.LambdaUtils.tryOrNull;

/**
 * Created by covers1624 on 8/09/2016.
 */
public class CCBlockRendererDispatcher extends BlockRenderDispatcher {

    private static final Logger logger = LogManager.getLogger();

    public final BlockRenderDispatcher parentDispatcher;
    private static long lastTime;

    public CCBlockRendererDispatcher(BlockRenderDispatcher parent, BlockColors blockColours) {
        super(parent.getBlockModelShaper(), parent.blockEntityRenderer, blockColours);
        parentDispatcher = parent;
        this.modelRenderer = parent.modelRenderer;
        this.liquidBlockRenderer = parent.liquidBlockRenderer;
        this.blockModelShaper = parent.blockModelShaper;
    }

    //In world.
    @Override
    public boolean renderBatched(BlockState state, BlockPos pos, BlockAndTintGetter level, PoseStack stack, VertexConsumer builder, boolean checkSides, Random rand, IModelData modelData) {
        try {
            ICCBlockRenderer renderer = BlockRenderingRegistry.findFor(state.getBlock(), e -> e.canHandleBlock(level, pos, state));
            if (renderer != null) {
                return renderer.renderBlock(state, pos, level, stack, builder, rand, modelData);
            }
        } catch (Throwable t) {
            if (ProxyClient.catchBlockRenderExceptions) {
                handleCaughtException(t, state, pos, level);
                return false;
            }
            CrashReport crashreport = CrashReport.forThrowable(t, "Tessellating CCL block in world");
            CrashReportCategory crashreportcategory = crashreport.addCategory("Block being tessellated");
            CrashReportCategory.populateBlockDetails(crashreportcategory, level, pos, state);
            throw new ReportedException(crashreport);
        }
        try {
            return parentDispatcher.renderBatched(state, pos, level, stack, builder, checkSides, rand, modelData);
        } catch (Throwable t) {
            if (ProxyClient.catchBlockRenderExceptions) {
                handleCaughtException(t, state, pos, level);
                return false;
            }
            throw t;
        }
    }

    //Block Damage
    @Override
    public void renderBreakingTexture(BlockState state, BlockPos pos, BlockAndTintGetter world, PoseStack matrixStackIn, VertexConsumer vertexBuilderIn, IModelData data) {
        ICCBlockRenderer renderer = BlockRenderingRegistry.findFor(state.getBlock(), e -> e.canHandleBlock(world, pos, state));
        if (renderer != null) {
            renderer.renderBreaking(state, pos, world, matrixStackIn, vertexBuilderIn, data);
        } else {
            parentDispatcher.renderBreakingTexture(state, pos, world, matrixStackIn, vertexBuilderIn, data);
        }
    }

    //Fluids
    @Override
    public boolean renderLiquid(BlockPos pos, BlockAndTintGetter world, VertexConsumer builder, FluidState state) {
        ICCBlockRenderer renderer = BlockRenderingRegistry.findFor(state.getType(), e -> e.canHandleFluid(world, pos, state));
        if (renderer != null) {
            return renderer.renderFluid(pos, world, builder, state);
        }
        return parentDispatcher.renderLiquid(pos, world, builder, state);
    }

    //From an entity
    @Override
    public void renderSingleBlock(BlockState blockStateIn, PoseStack matrixStackIn, MultiBufferSource bufferTypeIn, int combinedLightIn, int combinedOverlayIn, IModelData modelData) {
        ICCBlockRenderer renderer = BlockRenderingRegistry.findFor(blockStateIn.getBlock(), e -> e.canHandleEntity(blockStateIn));
        if (renderer != null) {
            renderer.renderEntity(blockStateIn, matrixStackIn, bufferTypeIn, combinedLightIn, combinedOverlayIn, modelData);
        } else {
            parentDispatcher.renderSingleBlock(blockStateIn, matrixStackIn, bufferTypeIn, combinedLightIn, combinedOverlayIn, modelData);
        }
    }

    @SuppressWarnings ("Convert2MethodRef")//Suppress these, the lambdas need to be synthetic functions instead of a method reference.
    private static void handleCaughtException(Throwable t, BlockState inState, BlockPos pos, BlockAndTintGetter world) {
        Block inBlock = inState.getBlock();
        BlockEntity tile = world.getBlockEntity(pos);

        StringBuilder builder = new StringBuilder("\n CCL has caught an exception whilst rendering a block\n");
        builder.append("  BlockPos:      ").append(String.format("x:%s, y:%s, z:%s", pos.getX(), pos.getY(), pos.getZ())).append("\n");
        builder.append("  Block Class:   ").append(tryOrNull(() -> inBlock.getClass())).append("\n");
        builder.append("  Registry Name: ").append(tryOrNull(() -> inBlock.getRegistryName())).append("\n");
        builder.append("  State:         ").append(inState).append("\n");
        builder.append(" Tile at position\n");
        builder.append("  Tile Class:    ").append(tryOrNull(() -> tile.getClass())).append("\n");
        builder.append("  Tile Id:       ").append(tryOrNull(() -> BlockEntityType.getKey(tile.getType()))).append("\n");
        builder.append("  Tile NBT:      ").append(tryOrNull(() -> tile.saveWithFullMetadata())).append("\n");
        builder.append("This functionality can be disabled in the CCL config file.\n");
        if (ProxyClient.messagePlayerOnRenderExceptionCaught) {
            builder.append("You can also turn off player messages in the CCL config file.\n");
        }
        String logMessage = builder.toString();
        String key = ExceptionUtils.getStackTrace(t) + logMessage;
        if (ExceptionMessageEventHandler.exceptionMessageCache.add(key)) {
            logger.error(logMessage, t);
        }
        Player player = Minecraft.getInstance().player;
        if (ProxyClient.messagePlayerOnRenderExceptionCaught && player != null) {
            long time = System.nanoTime();
            if (TimeUnit.NANOSECONDS.toSeconds(time - lastTime) > 5) {
                lastTime = time;
                player.sendMessage(new TextComponent("CCL Caught an exception rendering a block. See the log for info."), Util.NIL_UUID);
            }
        }
    }

}
