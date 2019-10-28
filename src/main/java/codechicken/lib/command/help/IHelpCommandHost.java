//package codechicken.lib.command.help;
//
//import codechicken.lib.internal.command.client.highlight.HighlightCommandBase;
//import net.minecraft.command.ICommand;
//import net.minecraftforge.server.command.CommandTreeBase;
//
//import java.util.Map;
//
///**
// * Implement on your base command and passed to a HelpCommandBase to retrieve data.
// * Created by covers1624 on 9/06/18.
// */
//public interface IHelpCommandHost {
//
//    /**
//     * Gets the sub command map backing your command.
//     * In the case of {@link CommandTreeBase}, {@link CommandTreeBase#getCommandMap()}
//     *
//     * @return The sub command map.
//     */
//    Map<String, ICommand> getSubCommandMap();
//
//    /**
//     * The name of your command, basically {@link ICommand#getName()}
//     * When you have a help command inside a sub command, make sure this returns
//     * something like this 'ccl highlight' not 'highlight' See {@link HighlightCommandBase}.
//     * This is used purely for chat messages.
//     *
//     * @return The name for your command.
//     */
//    String getParentName();
//
//}
