package codechicken.lib.command;

import codechicken.lib.util.ServerUtils;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.FMLCommonHandler;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public abstract class CoreCommand implements ICommand {

    public static void chatT(ICommandSender sender, String s, Object... params) {
        sender.sendMessage(new TextComponentTranslation(s, params));
    }

    public static void chatOpsT(String s, Object... params) {
        for (EntityPlayerMP player : ServerUtils.getPlayers()) {
            if (FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().canSendCommands(player.getGameProfile())) {
                player.sendMessage(new TextComponentTranslation(s, params));
            }
        }
    }

    public abstract boolean isOpOnly();

    @Override
    @Nonnull
    public String getUsage(@Nonnull ICommandSender commandSender) {
        return "/" + getName() + " help";
    }

    @Override
    public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args) {
        if (args.length < minimumParameters() || args.length == 1 && args[0].equals("help")) {
            printHelp(sender);
            return;
        }

        String command = getName();
        for (String arg : args) {
            command += " " + arg;
        }

        handleCommand(command, sender.getName(), args, sender);
    }

    public abstract void handleCommand(String command, String playername, String[] args, ICommandSender listener);

    public abstract void printHelp(ICommandSender sender);

    public final EntityPlayerMP getPlayer(String name) {
        return ServerUtils.getPlayer(name);
    }

    public WorldServer getWorld(int dimension) {
        return DimensionManager.getWorld(dimension);
    }

    public WorldServer getWorld(EntityPlayer player) {
        return (WorldServer) player.world;
    }

    @Override
    public int compareTo(ICommand o) {
        return getName().compareTo(o.getName());
    }

    @Override
    @Nonnull
    public List<String> getAliases() {
        return new ArrayList<>();
    }

    @Override
    @Nonnull
    public List<String> getTabCompletions(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args, BlockPos pos) {
        return null;
    }

    @Override
    public boolean isUsernameIndex(@Nonnull String[] args, int index) {
        return false;
    }

    @Override
    public boolean checkPermission(@Nonnull MinecraftServer server, @Nonnull ICommandSender commandSender) {
        if (isOpOnly()) {
            if (commandSender instanceof EntityPlayer) {
                return FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().canSendCommands(((EntityPlayer) commandSender).getGameProfile());
            }

            return commandSender instanceof MinecraftServer;
        }
        return true;
    }

    public abstract int minimumParameters();
}
