package codechicken.lib.command.help;

import java.util.List;

/**
 * Registered to a HelpCommandBase to have a help page the same as a command, without being a command.
 * Created by covers1624 on 9/06/18.
 */
public interface IHelpPage {

    /**
     * The name of the help page.
     *
     * @return The name.
     */
    String getName();

    /**
     * Minecraft calls this Usage, but description is a better fit.
     * Basically a brief overview of what the help page is about.
     *
     * @return The description.
     */
    String getDesc();

    /**
     * Gets the help lines for the page.
     *
     * @return The help lines.
     */
    List<String> getHelp();
}
