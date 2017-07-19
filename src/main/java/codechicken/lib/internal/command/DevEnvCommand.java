package codechicken.lib.internal.command;

import codechicken.lib.raytracer.RayTracer;
import codechicken.lib.thread.TaskProfiler;
import codechicken.lib.thread.TaskProfiler.ProfilerResult;
import codechicken.lib.util.BlockStateUtils;
import codechicken.lib.util.DirectoryWalker;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentString;
import org.apache.commons.io.FilenameUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
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

            doBlueprintStuffs();

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


    public static void doBlueprintStuffs() {
        File inputFolder = new File("bp_convert/");
        if (inputFolder.exists()) {
            try {
                DirectoryWalker walker = new DirectoryWalker(DirectoryWalker.TRUE, file -> file.getAbsolutePath().endsWith(".bpt"));
                for (File file : walker.walk(inputFolder)) {
                    NBTTagCompound compound = CompressedStreamTools.readCompressed(new FileInputStream(file));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
