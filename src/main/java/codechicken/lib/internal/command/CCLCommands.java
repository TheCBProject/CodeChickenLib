package codechicken.lib.internal.command;

import codechicken.lib.internal.command.admin.CountCommand;
import codechicken.lib.internal.command.admin.KillAllCommand;
import codechicken.lib.internal.command.admin.MiscCommands;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandSource;

/**
 * Created by covers1624 on 17/9/20.
 */
public class CCLCommands {

    public static void registerCommands(CommandDispatcher<CommandSource> dispatcher) {
        CountCommand.register(dispatcher);
        KillAllCommand.register(dispatcher);
        MiscCommands.register(dispatcher);
    }

}
