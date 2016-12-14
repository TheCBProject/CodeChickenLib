package codechicken.lib.command;

import codechicken.lib.CodeChickenLib;
import com.google.common.base.Throwables;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by covers1624 on 14/12/2016.
 */
public class DumpClassCommand extends CommandBase {
    protected static final File DUMP_FOLDER = new File(CodeChickenLib.minecraftDir, "asm/ccl_class_dumps");

    @Override
    public String getCommandName() {
        return "dumpLoadedClass";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "Dumps a loaded class from ram to disk.";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        try {
            if (args.length == 0) {
                sender.addChatMessage(new TextComponentString("Class name must be provided."));
                return;
            } else if (args.length > 1) {
                sender.addChatMessage(new TextComponentString("Too many arguments."));
                return;
            }

            String className = args[0];

            byte[] bytes = Launch.classLoader.getClassBytes(className);

            if (bytes == null) {
                sender.addChatMessage(new TextComponentString(String.format("Class %s does not exist.", className)));
                return;
            }
            if (!DUMP_FOLDER.exists()) {
                DUMP_FOLDER.mkdirs();
            }
            File outFile = new File(DUMP_FOLDER, className + ".class");
            if (!outFile.exists()) {
                outFile.createNewFile();
            }

            FileOutputStream fos = new FileOutputStream(outFile);
            fos.write(bytes);
            fos.flush();
            fos.close();
            sender.addChatMessage(new TextComponentString("Class successfully dumped to: " + outFile.getAbsolutePath()));

        } catch (Exception e) {
            Throwables.propagate(e);
        }
    }
}
