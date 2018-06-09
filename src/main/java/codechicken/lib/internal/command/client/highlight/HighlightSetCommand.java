package codechicken.lib.internal.command.client.highlight;

import codechicken.lib.command.ClientCommandBase;
import codechicken.lib.command.help.IBetterHelpCommand;
import codechicken.lib.internal.HighlightHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.command.CommandException;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

import java.util.Collections;
import java.util.List;

/**
 * Created by covers1624 on 9/06/18.
 */
public class HighlightSetCommand extends ClientCommandBase implements IBetterHelpCommand {

    @Override
    public void execute(Minecraft mc, EntityPlayerSP player, String[] args) throws CommandException {
        if (args.length < 3) {
            throw new CommandException("Please specify a position.");
        }
        BlockPos prev = HighlightHandler.highlight;
        BlockPos newPos = parseBlockPos(player, args, 0, false);
        HighlightHandler.highlight = newPos;
        if (prev != null) {
            player.sendMessage(new TextComponentString(String.format("Moved highlight from %s, %s, %s to %s, %s, %s", prev.getX(), prev.getY(), prev.getZ(), newPos.getX(), newPos.getY(), newPos.getZ())));
        } else {
            player.sendMessage(new TextComponentString(String.format("Set highlight at %s, %s, %s", newPos.getX(), newPos.getY(), newPos.getZ())));
        }
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public String getDesc() {
        return "Set the location for the highlight overlay.";
    }

    @Override
    public List<String> getHelp() {
        return Collections.singletonList(getDesc());
    }

    @Override
    public String getName() {
        return "set";
    }
}
