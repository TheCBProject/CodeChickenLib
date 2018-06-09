package codechicken.lib.internal.command.client.highlight;

import codechicken.lib.command.ClientCommandBase;
import codechicken.lib.command.help.IBetterHelpCommand;
import codechicken.lib.internal.HighlightHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.command.CommandException;
import net.minecraft.util.text.TextComponentString;

import java.util.Collections;
import java.util.List;

/**
 * Created by covers1624 on 9/06/18.
 */
public class HighlightClearCommand extends ClientCommandBase implements IBetterHelpCommand {

    @Override
    public void execute(Minecraft mc, EntityPlayerSP player, String[] args) throws CommandException {
        if (HighlightHandler.highlight == null) {
            throw new CommandException("Not set.");
        }
        HighlightHandler.highlight = null;
        HighlightHandler.useDepth = true;
        player.sendMessage(new TextComponentString("Highlight position cleared."));
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public String getDesc() {
        return "Clears the current highlight.";
    }

    @Override
    public List<String> getHelp() {
        return Collections.singletonList(getDesc());
    }

    @Override
    public String getName() {
        return "clear";
    }
}
