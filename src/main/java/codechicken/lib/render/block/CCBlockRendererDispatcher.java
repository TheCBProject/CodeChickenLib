package codechicken.lib.render.block;

import codechicken.lib.internal.CCLLog;
import codechicken.lib.internal.ExceptionMessageEventHandler;
import codechicken.lib.internal.proxy.ProxyClient;
import codechicken.lib.model.bakedmodels.ModelProperties;
import codechicken.lib.model.bakedmodels.PerspectiveAwareBakedModel;
import codechicken.lib.render.buffer.BakingVertexBuffer;
import codechicken.lib.util.TransformUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.IFluidState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.IEnviromentBlockReader;
import net.minecraftforge.client.model.data.IModelData;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.Level;

import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static codechicken.lib.util.LambdaUtils.tryOrNull;

/**
 * Created by covers1624 on 8/09/2016.
 */
public class CCBlockRendererDispatcher extends BlockRendererDispatcher {

    public final BlockRendererDispatcher parentDispatcher;
    private static long lastTime;

    public CCBlockRendererDispatcher(BlockRendererDispatcher parent, BlockColors blockColours) {
        super(parent.getBlockModelShapes(), blockColours);
        parentDispatcher = parent;
        this.blockModelRenderer = parent.blockModelRenderer;
        this.fluidRenderer = parent.fluidRenderer;
        this.blockModelShapes = parent.blockModelShapes;
    }

    @Override
    public void renderBlockDamage(BlockState state, BlockPos pos, TextureAtlasSprite sprite, IEnviromentBlockReader world) {
        Optional<ICCBlockRenderer> renderOpt = BlockRenderingRegistry.getBlockRenderers().stream().filter(e -> e.canHandle(world, pos, state)).findFirst();
        if (renderOpt.isPresent()) {
            ICCBlockRenderer renderer = renderOpt.get();
            //state = state.getActualState(world, pos);
            //TODO This needs to be optimized, probably not the most efficient thing in the world..
            BufferBuilder parent = Tessellator.getInstance().getBuffer();
            BakingVertexBuffer buffer = BakingVertexBuffer.create();
            buffer.setTranslation(-pos.getX(), -pos.getY(), -pos.getZ());
            buffer.begin(7, parent.getVertexFormat());
            renderer.handleRenderBlockDamage(world, pos, state, sprite, buffer);
            buffer.finishDrawing();
            buffer.setTranslation(0, 0, 0);
            IBakedModel model = new PerspectiveAwareBakedModel(buffer.bake(), TransformUtils.DEFAULT_BLOCK, new ModelProperties(true, true, null));
            blockModelRenderer.renderModel(world, model, state, pos, parent, true, new Random(), state.getPositionRandom(pos));

        } else {
            parentDispatcher.renderBlockDamage(state, pos, sprite, world);
        }
    }

    @Override
    public boolean renderBlock(BlockState state, BlockPos pos, IEnviromentBlockReader world, BufferBuilder buffer, Random random, IModelData modelData) {
        BlockState inState = state;
        try {
            Optional<ICCBlockRenderer> renderOpt = BlockRenderingRegistry.getBlockRenderers().stream().filter(e -> e.canHandle(world, pos, inState)).findFirst();
            if (renderOpt.isPresent()) {
                return renderOpt.get().renderBlock(world, pos, state, buffer, random, modelData);
            }
        } catch (Throwable t) {
            if (ProxyClient.catchBlockRenderExceptions) {
                handleCaughtException(t, inState, pos, world);
                return false;
            }
            CrashReport crashreport = CrashReport.makeCrashReport(t, "Tessellating CCL block in world");
            CrashReportCategory crashreportcategory = crashreport.makeCategory("Block being tessellated");
            CrashReportCategory.addBlockInfo(crashreportcategory, pos, state);
            throw new ReportedException(crashreport);
        }
        try {
            return parentDispatcher.renderBlock(state, pos, world, buffer, random, modelData);
        } catch (Throwable t) {
            if (ProxyClient.catchBlockRenderExceptions) {
                handleCaughtException(t, inState, pos, world);
                return false;
            }
            throw t;
        }
    }

    @Override
    @SuppressWarnings ("OptionalIsPresent")
    public boolean renderFluid(BlockPos pos, IEnviromentBlockReader world, BufferBuilder buffer, IFluidState state) {
        Optional<ICCBlockRenderer> renderOpt = BlockRenderingRegistry.getBlockRenderers().stream().filter(e -> e.canHandle(world, pos, state)).findFirst();
        if (renderOpt.isPresent()) {
            return renderOpt.get().renderFluid(world, pos, state, buffer);
        } else {
            return super.renderFluid(pos, world, buffer, state);
        }
    }

    @Override
    public void renderBlockBrightness(BlockState state, float brightness) {
        Optional<ICCBlockRenderer> renderOpt = BlockRenderingRegistry.getBlockRenderers().stream().filter(e -> e.canHandleBrightness(state)).findFirst();
        if (renderOpt.isPresent()) {
            renderOpt.get().renderBrightness(state, brightness);
        } else {
            parentDispatcher.renderBlockBrightness(state, brightness);
        }
    }

    @SuppressWarnings ("Convert2MethodRef")//Suppress these, the lambdas need to be synthetic functions instead of a method reference.
    private static void handleCaughtException(Throwable t, BlockState inState, BlockPos pos, IEnviromentBlockReader world) {
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
        if (ProxyClient.messagePlayerOnRenderExceptionCaught) {
            builder.append("You can turn off player messages in the CCL config file.\n");
        }
        String logMessage = builder.toString();
        String key = ExceptionUtils.getStackTrace(t) + logMessage;
        if (!ExceptionMessageEventHandler.exceptionMessageCache.contains(key)) {
            ExceptionMessageEventHandler.exceptionMessageCache.add(key);
            CCLLog.log(Level.ERROR, t, logMessage);
        }
        PlayerEntity player = Minecraft.getInstance().player;
        if (ProxyClient.messagePlayerOnRenderExceptionCaught && player != null) {
            long time = System.nanoTime();
            if (TimeUnit.NANOSECONDS.toSeconds(time - lastTime) > 5) {
                lastTime = time;
                player.sendMessage(new StringTextComponent("CCL Caught an exception rendering a block. See the log for info."));
            }
        }
    }

}
