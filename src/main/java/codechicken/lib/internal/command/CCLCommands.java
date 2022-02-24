package codechicken.lib.internal.command;

import codechicken.lib.internal.command.admin.CountCommand;
import codechicken.lib.internal.command.admin.KillAllCommand;
import codechicken.lib.internal.command.admin.MiscCommands;
import codechicken.lib.internal.command.dev.DevCommands;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.synchronization.ArgumentTypes;
import net.minecraftforge.event.RegisterCommandsEvent;

import static codechicken.lib.CodeChickenLib.MOD_ID;

/**
 * Created by covers1624 on 17/9/20.
 */
public class CCLCommands {

    public static void registerArguments() {
        ArgumentTypes.register(MOD_ID + ":entity_type", EntityTypeArgument.class, new EntityTypeArgument.Serializer());
    }

    public static void registerCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        CountCommand.register(dispatcher);
        KillAllCommand.register(dispatcher);
        MiscCommands.register(dispatcher);
        DevCommands.register(dispatcher);
    }

}
