package codechicken.lib.internal.command;

import codechicken.lib.internal.command.admin.CountCommand;
import codechicken.lib.internal.command.admin.KillAllCommand;
import codechicken.lib.internal.command.admin.MiscCommands;
import codechicken.lib.internal.command.client.HighlightCommand;
import codechicken.lib.internal.command.client.ItemInfoCommand;
import codechicken.lib.internal.command.client.RenderItemToFileCommand;
import codechicken.lib.internal.command.dev.DevCommands;
import com.mojang.brigadier.CommandDispatcher;
import net.covers1624.quack.util.CrashLock;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;

/**
 * Created by covers1624 on 17/9/20.
 */
public class CCLCommands {

    private static final CrashLock LOCK = new CrashLock("Already Initialized");

    public static void init() {
        LOCK.lock();

        MinecraftForge.EVENT_BUS.addListener(CCLCommands::registerServerCommands);
        MinecraftForge.EVENT_BUS.addListener(CCLCommands::registerClientCommands);
    }

    private static void registerServerCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        CommandBuildContext context = event.getBuildContext();
        CountCommand.register(dispatcher, context);
        KillAllCommand.register(dispatcher, context);
        MiscCommands.register(dispatcher, context);
        DevCommands.register(dispatcher, context);
    }

    private static void registerClientCommands(RegisterClientCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        HighlightCommand.register(dispatcher);
        ItemInfoCommand.register(dispatcher);
        RenderItemToFileCommand.register(dispatcher);
    }

}
