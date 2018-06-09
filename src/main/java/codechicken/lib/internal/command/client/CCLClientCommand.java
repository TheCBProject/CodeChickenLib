package codechicken.lib.internal.command.client;

import codechicken.lib.command.help.HelpCommandBase;
import codechicken.lib.command.help.IHelpCommandHost;
import codechicken.lib.internal.command.client.highlight.HighlightCommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.client.IClientCommand;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.server.command.CommandTreeBase;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Sort of an example implementation for HelpCommandBase.
 * Otherwise these are just simple dev toolish commands.
 * Created by covers1624 on 9/06/18.
 */
@SideOnly (Side.CLIENT)
public class CCLClientCommand extends CommandTreeBase implements IClientCommand, IHelpCommandHost {

    private HelpCommandBase helpCommand;

    public CCLClientCommand() {
        helpCommand = new HelpCommandBase(this);
        addSubcommand(helpCommand);
        addSubcommand(new ItemInfoCommand());
        addSubcommand(new ModelLocationInfoCommand());
        addSubcommand(new NukeDynamicBakeryCacheCommand());
        addSubcommand(new HighlightCommandBase());
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length < 1) {
            helpCommand.displayHelp(server, sender);
        } else {
            super.execute(server, sender, args);
        }
    }

    @Override
    public boolean allowUsageWithoutPrefix(ICommandSender sender, String message) {
        return false;
    }

    @Override
    public String getName() {
        return "ccl";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/" + getName() + " help";
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("ccl", "codechickenlib");
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public Map<String, ICommand> getSubCommandMap() {
        return getCommandMap();
    }

    @Override
    public String getParentName() {
        return getName();
    }
}
