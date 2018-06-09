package codechicken.lib.command.help;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import java.util.List;

/**
 * Created by covers1624 on 9/06/18.
 */
public interface IBetterHelpCommand extends ICommand {

    /**
     * Minecraft calls this Usage, but description is a better fit.
     * Basically a brief overview of what the command is about.
     * Use the help lines for syntax, what parameters do and such.
     *
     * @return The description.
     */
    String getDesc();

    /**
     * Gets the help lines for this command.
     *
     * @return The help lines.
     */
    List<String> getHelp();

    /**
     * Helper method to display your commands help text.
     *
     * @param sender The recipient of the help text.
     */
    default void displayHelpText(ICommandSender sender) {
        for (String line : getHelp()) {
            sender.sendMessage(new TextComponentString(TextFormatting.BLUE + line));
        }
    }

    /**
     * A default override for getUsage, as its just a duplicate of getDesc()
     */
    @Override
    default String getUsage(ICommandSender sender) {
        return getDesc();
    }
}
