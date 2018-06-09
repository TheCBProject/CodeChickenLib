package codechicken.lib.command.help;

import codechicken.lib.internal.command.client.CCLClientCommand;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import static net.minecraft.util.text.TextFormatting.*;

/**
 * A base command for dealing with help.
 * Register this as a Sub command how ever you do commands.
 * See {@link CCLClientCommand} for an example usage.
 * Created by covers1624 on 9/06/18.
 */
public class HelpCommandBase extends CommandBase {

    private IHelpCommandHost host;
    private List<IHelpPage> helpPages;

    public HelpCommandBase(IHelpCommandHost host) {
        this.host = host;
        helpPages = new LinkedList<>();
    }

    /**
     * Adds a help page.
     * see {@link IHelpPage} for more info.
     *
     * @param page The page.
     * @return This instance for call chaining.
     */
    public HelpCommandBase addHelpPage(IHelpPage page) {
        helpPages.add(page);
        return this;
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "Displays help for /" + host.getParentName() + " commands.";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length < 1) {
            displayHelp(server, sender);
        } else if (args.length == 1) {
            String arg = args[0];
            if (host.getSubCommandMap().containsKey(arg)) {
                ICommand subCommand = host.getSubCommandMap().get(arg);
                sender.sendMessage(new TextComponentString(GREEN + "Help for: /" + host.getParentName() + " " + arg));
                if (subCommand instanceof IBetterHelpCommand) {
                    for (String line : ((IBetterHelpCommand) subCommand).getHelp()) {
                        sender.sendMessage(new TextComponentString(YELLOW + line));
                    }
                } else {
                    sender.sendMessage(new TextComponentString(YELLOW + subCommand.getUsage(sender)));
                }
            } else {
                for (IHelpPage page : helpPages) {
                    if (page.getName().equals(arg)) {
                        sender.sendMessage(new TextComponentString(GREEN + "Displaying Help Page: " + arg));
                        for (String line : page.getHelp()) {
                            sender.sendMessage(new TextComponentString(BLUE + line));
                        }
                        return;
                    }
                }
                sender.sendMessage(new TextComponentString(RED + "No Sub Command or Help Page exists for \"" + arg + "\"!"));
            }

        } else {
            sender.sendMessage(new TextComponentString(RED + "Too many arguments!"));
        }
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        if (args.length == 1) {
            List<String> keys = host.getSubCommandMap().values().stream()//
                    .filter(p -> p.checkPermission(server, sender))//
                    .map(ICommand::getName)//
                    .collect(Collectors.toList());
            keys.addAll(helpPages.stream().map(IHelpPage::getName).collect(Collectors.toList()));
            keys.sort(null);
            return getListOfStringsMatchingLastWord(args, keys);
        }
        return super.getTabCompletions(server, sender, args, targetPos);
    }

    public void displayHelp(MinecraftServer server, ICommandSender sender) {
        sender.sendMessage(new TextComponentString(TextFormatting.DARK_GREEN + "Available commands for /" + host.getParentName() + ":"));
        sender.sendMessage(new TextComponentString(TextFormatting.GOLD + "For more info use \"/" + host.getParentName() + " help [command]\""));
        for (Entry<String, ICommand> entry : host.getSubCommandMap().entrySet()) {
            String prefix = "";
            if (!entry.getValue().checkPermission(server, sender)) {
                prefix = RED.toString();
            }
            sender.sendMessage(new TextComponentString(prefix + "/" + host.getParentName() + " " + YELLOW + entry.getValue().getName() + BLUE + " " + getCommandBrief(entry.getValue(), sender)));
        }
        for (IHelpPage page : helpPages) {
            sender.sendMessage(new TextComponentString("/" + host.getParentName() + YELLOW + " help " + page.getName() + BLUE + " " + page.getDesc()));
        }
    }

    private static String getCommandBrief(ICommand command, ICommandSender sender) {
        if (command instanceof IBetterHelpCommand) {
            return ((IBetterHelpCommand) command).getDesc();
        } else {
            return command.getUsage(sender);
        }
    }
}
