package codechicken.lib.internal.command;

import codechicken.lib.raytracer.RayTracer;
import codechicken.lib.thread.TaskProfiler;
import codechicken.lib.thread.TaskProfiler.ProfilerResult;
import codechicken.lib.util.BlockStateUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentString;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by covers1624 on 9/01/2017.
 */
public class DevEnvCommand implements ICommand {

    @Nonnull
    @Override
    public String getName() {
        return "devStuff";
    }

    @Nonnull
    @Override
    public String getUsage(@Nonnull ICommandSender sender) {
        return "Does dev stuff.";
    }

    @Nonnull
    @Override
    public List<String> getAliases() {
        return new ArrayList<>();
    }

    @Override
    public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args) throws CommandException {
        if (sender instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) sender;
            RayTraceResult trace = RayTracer.retrace(player);
            if (trace == null) {
                addMessage(sender, "Null trace.");
                return;
            }
            IBlockState state = player.world.getBlockState(trace.getBlockPos());
            addMessage(sender, state);
            TaskProfiler timer = new TaskProfiler();
            timer.startOnce("task");
            int hash = BlockStateUtils.hashBlockState(state.getBlock().getExtendedState(state, player.world, trace.getBlockPos()));
            ProfilerResult result = timer.endOnce();

            addMessage(sender, "Hash: " + hash);

            addMessage(sender, "Time: " + result.time / 1000);

        } else {
            addMessage(sender, "You are not an EntityPlayer..");
        }

    }

    private static void addMessage(ICommandSender sender, Object object, Object... format) {
        sender.sendMessage(new TextComponentString(String.format(String.valueOf(object), format)));
    }

    @Override
    public boolean checkPermission(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender) {
        return true;
    }

    @Nonnull
    @Override
    public List<String> getTabCompletions(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args, @Nullable BlockPos pos) {
        return new ArrayList<>();
    }

    @Override
    public boolean isUsernameIndex(@Nonnull String[] args, int index) {
        return false;
    }

    @Override
    public int compareTo(@Nonnull ICommand o) {
        return 0;
    }
}
