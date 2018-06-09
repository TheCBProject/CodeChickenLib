package codechicken.lib.internal.command.client.highlight;

import codechicken.lib.command.help.HelpCommandBase;
import codechicken.lib.command.help.IBetterHelpCommand;
import codechicken.lib.command.help.IHelpCommandHost;
import codechicken.lib.internal.command.client.CCLClientCommand;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.server.command.CommandTreeBase;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Notice this command is registered as a sub command to {@link CCLClientCommand}
 * Separate Help command, this command is an IHelpCommandHost
 * Created by covers1624 on 9/06/18.
 */
public class HighlightCommandBase extends CommandTreeBase implements IBetterHelpCommand, IHelpCommandHost {

    private HelpCommandBase helpCommand;

    public HighlightCommandBase() {
        helpCommand = new HelpCommandBase(this);
        addSubcommand(helpCommand);
        addSubcommand(new HighlightSetCommand());
        addSubcommand(new HighlightClearCommand());
        addSubcommand(new HighlightToggleDepthCommand());
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
    public String getName() {
        return "highlight";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return getDesc();
    }

    @Override
    public String getDesc() {
        return "Allows highlighting of blocks for debug purposes.";
    }

    @Override
    public List<String> getHelp() {
        return Collections.singletonList("/ccl highlight help.");
    }

    @Override
    public Map<String, ICommand> getSubCommandMap() {
        return getCommandMap();
    }

    /**
     * Note this returns 'ccl highlight' as its a sub command of ccl.
     */
    @Override
    public String getParentName() {
        return "ccl highlight";
    }
}
