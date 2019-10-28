//package codechicken.lib.internal.command.client.highlight;
//
//import codechicken.lib.command.ClientCommandBase;
//import codechicken.lib.command.help.IBetterHelpCommand;
//import codechicken.lib.internal.HighlightHandler;
//import net.minecraft.client.Minecraft;
//import net.minecraft.client.entity.EntityPlayerSP;
//import net.minecraft.command.CommandException;
//import net.minecraft.util.text.TextComponentString;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * Created by covers1624 on 9/06/18.
// */
//public class `HighlightToggleDepthCommand extends ClientCommandBase implements IBetterHelpCommand {
//
//    @Override
//    public void execute(Minecraft mc, EntityPlayerSP player, String[] args) throws CommandException {
//        if (HighlightHandler.highlight == null) {
//            throw new CommandException("Highlight not enabled");
//        }
//        boolean curr = HighlightHandler.useDepth;
//        HighlightHandler.useDepth = !curr;
//
//        //If enabled say disabled.
//        if (curr) {
//            player.sendMessage(new TextComponentString("Disabled Highlight depth."));
//        } else {
//            player.sendMessage(new TextComponentString("Enabled Highlight depth."));
//        }
//
//    }
//
//    @Override
//    public int getRequiredPermissionLevel() {
//        return 0;
//    }
//
//    @Override
//    public String getDesc() {
//        return "Toggles Highlight overlay depth on / off.";
//    }
//
//    @Override
//    public List<String> getHelp() {
//        List<String> lines = new ArrayList<>();
//        lines.add("This toggles whether the Highlight overlay disables depth when rendering.");
//        lines.add("That means you can see the box through walls and such.");
//        lines.add("Useful to locate the block through a wall then turn depth off again.");
//        return lines;
//    }
//
//    @Override
//    public String getName() {
//        return "toggle_depth";
//    }
//}
