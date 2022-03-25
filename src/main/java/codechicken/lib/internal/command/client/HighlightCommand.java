package codechicken.lib.internal.command.client;

import codechicken.lib.internal.HighlightHandler;
import codechicken.lib.raytracer.RayTracer;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import static codechicken.lib.util.LambdaUtils.tryOrNull;
import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

/**
 * Created by covers1624 on 25/3/22.
 */
public class HighlightCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literal("ccl")
                .then(literal("highlight")
                        .then(literal("set")
                                .executes(HighlightCommand::setHighlightRayTrace)
                                .then(argument("pos", BlockPosArgument.blockPos())
                                        .executes(HighlightCommand::setHighlightArg)
                                )
                        )
                        .then(literal("clear")
                                .executes(HighlightCommand::clearHighlight)
                        )
                        .then(literal("toggle_depth")
                                .executes(HighlightCommand::toggleDepth)
                        )
                        .then(literal("info")
                                .executes(HighlightCommand::dumpInfo)
                        )
                )
        );
    }

    private static int setHighlightRayTrace(CommandContext<CommandSourceStack> ctx) {
        BlockHitResult hitResult = RayTracer.retrace(Minecraft.getInstance().player, 3000, ClipContext.Block.OUTLINE, ClipContext.Fluid.ANY);
        if (hitResult.getType() == HitResult.Type.MISS) {
            ctx.getSource().sendFailure(new TextComponent("Not looking at a block."));
            return 0;
        }
        return setHighlight(ctx, hitResult.getBlockPos());
    }

    private static int setHighlightArg(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        return setHighlight(ctx, BlockPosArgument.getSpawnablePos(ctx, "pos"));
    }

    private static int setHighlight(CommandContext<CommandSourceStack> ctx, BlockPos pos) {
        CommandSourceStack source = ctx.getSource();
        if (HighlightHandler.highlight == null) {
            source.sendSuccess(new TextComponent("Set highlight at " + pos.getX() + ", " + pos.getY() + ", " + pos.getZ()), false);
        } else {
            BlockPos prev = HighlightHandler.highlight;
            source.sendSuccess(new TextComponent("Moved highlight from " + prev.getX() + ", " + prev.getY() + ", " + prev.getZ() + " to " + pos.getX() + ", " + pos.getY() + ", " + pos.getZ()), false);
        }
        HighlightHandler.highlight = pos;
        return 0;
    }

    private static int clearHighlight(CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack source = ctx.getSource();
        if (HighlightHandler.highlight == null) {
            source.sendFailure(new TextComponent("Highlight not set."));
            return 0;
        }
        HighlightHandler.highlight = null;
        HighlightHandler.useDepth = true;
        source.sendSuccess(new TextComponent("Highlight position cleared."), false);
        return 0;
    }

    private static int toggleDepth(CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack source = ctx.getSource();
        if (HighlightHandler.highlight == null) {
            source.sendFailure(new TextComponent("Highlight not set."));
            return 0;
        }

        HighlightHandler.useDepth = !HighlightHandler.useDepth;

        if (HighlightHandler.useDepth) {
            source.sendSuccess(new TextComponent("Enabled highlight depth."), false);
        } else {
            source.sendSuccess(new TextComponent("Disabled highlight depth."), false);
        }
        return 0;
    }

    private static int dumpInfo(CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack source = ctx.getSource();
        BlockPos pos = HighlightHandler.highlight;
        if (HighlightHandler.highlight == null) {
            source.sendFailure(new TextComponent("Highlight not set."));
            return 0;
        }

        ClientLevel level = Minecraft.getInstance().level;
        assert level != null;

        BlockState state = level.getBlockState(pos);
        Block block = state.getBlock();
        BlockEntity tile = level.getBlockEntity(pos);
        StringBuilder builder = new StringBuilder("\nBlock info:\n");
        builder.append("  BlockPos:      ").append(String.format("x:%s, y:%s, z:%s", pos.getX(), pos.getY(), pos.getZ())).append("\n");
        builder.append("  Block Class:   ").append(tryOrNull(() -> block.getClass())).append("\n");
        builder.append("  Registry Name: ").append(tryOrNull(() -> block.getRegistryName())).append("\n");
        builder.append("  State:         ").append(state).append("\n");
        builder.append("Tile at position\n");
        builder.append("  Tile Class:    ").append(tryOrNull(() -> tile.getClass())).append("\n");
        builder.append("  Tile Id:       ").append(tryOrNull(() -> tile.getType().getRegistryName())).append("\n");
        builder.append("  Tile NBT:      ").append(tryOrNull(() -> tile.saveWithoutMetadata())).append("\n");
        source.sendSuccess(new TextComponent(builder.toString()), false);

        return 0;
    }
}
