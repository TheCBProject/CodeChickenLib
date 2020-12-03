package codechicken.lib.render.block;

import codechicken.lib.internal.ExceptionMessageEventHandler;
import codechicken.lib.internal.proxy.ProxyClient;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.IBlockDisplayReader;
import net.minecraftforge.client.model.data.IModelData;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static codechicken.lib.util.LambdaUtils.tryOrNull;

/**
 * Created by covers1624 on 8/09/2016.
 */
public class CCBlockRendererDispatcher extends BlockRendererDispatcher {

    private static final Logger logger = LogManager.getLogger();

    public final BlockRendererDispatcher parentDispatcher;
    private static long lastTime;

    public CCBlockRendererDispatcher(BlockRendererDispatcher parent, BlockColors blockColours) {
        super(parent.getBlockModelShapes(), blockColours);
        parentDispatcher = parent;
        this.blockModelRenderer = parent.blockModelRenderer;
        this.fluidRenderer = parent.fluidRenderer;
        this.blockModelShapes = parent.blockModelShapes;
    }

    //In world.
    @Override
    public boolean renderModel(BlockState state, BlockPos pos, IBlockDisplayReader world, MatrixStack stack, IVertexBuilder builder, boolean checkSides, Random rand, IModelData modelData) {
        try {
            Optional<ICCBlockRenderer> renderOpt = BlockRenderingRegistry.getBlockRenderers().stream().filter(e -> e.canHandleBlock(world, pos, state)).findFirst();
            if (renderOpt.isPresent()) {
                return renderOpt.get().renderBlock(state, pos, world, stack, builder, rand, modelData);
            }
        } catch (Throwable t) {
            if (ProxyClient.catchBlockRenderExceptions) {
                handleCaughtException(t, state, pos, world);
                return false;
            }
            CrashReport crashreport = CrashReport.makeCrashReport(t, "Tessellating CCL block in world");
            CrashReportCategory crashreportcategory = crashreport.makeCategory("Block being tessellated");
            CrashReportCategory.addBlockInfo(crashreportcategory, pos, state);
            throw new ReportedException(crashreport);
        }
        try {
            return parentDispatcher.renderModel(state, pos, world, stack, builder, checkSides, rand, modelData);
        } catch (Throwable t) {
            if (ProxyClient.catchBlockRenderExceptions) {
                handleCaughtException(t, state, pos, world);
                return false;
            }
            throw t;
        }
    }

    //Block Damage
    @Override
    public void renderBlockDamage(BlockState state, BlockPos pos, IBlockDisplayReader world, MatrixStack matrixStackIn, IVertexBuilder vertexBuilderIn, IModelData data) {
        Optional<ICCBlockRenderer> renderOpt = BlockRenderingRegistry.getBlockRenderers().stream().filter(e -> e.canHandleBlock(world, pos, state)).findFirst();
        if (renderOpt.isPresent()) {
            renderOpt.get().renderBreaking(state, pos, world, matrixStackIn, vertexBuilderIn, data);
        } else {
            parentDispatcher.renderBlockDamage(state, pos, world, matrixStackIn, vertexBuilderIn, data);
        }
    }

    //Fluids
    @Override
    public boolean renderFluid(BlockPos pos, IBlockDisplayReader world, IVertexBuilder builder, FluidState state) {
        Optional<ICCBlockRenderer> renderOpt = BlockRenderingRegistry.getBlockRenderers().stream().filter(e -> e.canHandleFluid(world, pos, state)).findFirst();
        //noinspection OptionalIsPresent
        if (renderOpt.isPresent()) {
            return renderOpt.get().renderFluid(pos, world, builder, state);
        } else {
            return parentDispatcher.renderFluid(pos, world, builder, state);
        }
    }

    //From an entity
    @Override
    public void renderBlock(BlockState blockStateIn, MatrixStack matrixStackIn, IRenderTypeBuffer bufferTypeIn, int combinedLightIn, int combinedOverlayIn, IModelData modelData) {
        Optional<ICCBlockRenderer> renderOpt = BlockRenderingRegistry.getBlockRenderers().stream().filter(e -> e.canHandleEntity(blockStateIn)).findFirst();
        if (renderOpt.isPresent()) {
            renderOpt.get().renderEntity(blockStateIn, matrixStackIn, bufferTypeIn, combinedLightIn, combinedOverlayIn, modelData);
        } else {
            parentDispatcher.renderBlock(blockStateIn, matrixStackIn, bufferTypeIn, combinedLightIn, combinedOverlayIn, modelData);
        }
    }

    @SuppressWarnings ("Convert2MethodRef")//Suppress these, the lambdas need to be synthetic functions instead of a method reference.
    private static void handleCaughtException(Throwable t, BlockState inState, BlockPos pos, IBlockDisplayReader world) {
        Block inBlock = inState.getBlock();
        TileEntity tile = world.getTileEntity(pos);

        StringBuilder builder = new StringBuilder("\n CCL has caught an exception whilst rendering a block\n");
        builder.append("  BlockPos:      ").append(String.format("x:%s, y:%s, z:%s", pos.getX(), pos.getY(), pos.getZ())).append("\n");
        builder.append("  Block Class:   ").append(tryOrNull(() -> inBlock.getClass())).append("\n");
        builder.append("  Registry Name: ").append(tryOrNull(() -> inBlock.getRegistryName())).append("\n");
        builder.append("  State:         ").append(inState).append("\n");
        builder.append(" Tile at position\n");
        builder.append("  Tile Class:    ").append(tryOrNull(() -> tile.getClass())).append("\n");
        builder.append("  Tile Id:       ").append(tryOrNull(() -> TileEntityType.getId(tile.getType()))).append("\n");
        builder.append("  Tile NBT:      ").append(tryOrNull(() -> tile.write(new CompoundNBT()))).append("\n");
        builder.append("This functionality can be disabled in the CCL config file.\n");
        if (ProxyClient.messagePlayerOnRenderExceptionCaught) {
            builder.append("You can also turn off player messages in the CCL config file.\n");
        }
        String logMessage = builder.toString();
        String key = ExceptionUtils.getStackTrace(t) + logMessage;
        if (ExceptionMessageEventHandler.exceptionMessageCache.add(key)) {
            logger.error(logMessage, t);
        }
        PlayerEntity player = Minecraft.getInstance().player;
        if (ProxyClient.messagePlayerOnRenderExceptionCaught && player != null) {
            long time = System.nanoTime();
            if (TimeUnit.NANOSECONDS.toSeconds(time - lastTime) > 5) {
                lastTime = time;
                player.sendMessage(new StringTextComponent("CCL Caught an exception rendering a block. See the log for info."), Util.DUMMY_UUID);
            }
        }
    }

}
