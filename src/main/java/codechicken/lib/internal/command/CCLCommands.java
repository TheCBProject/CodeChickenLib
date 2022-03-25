package codechicken.lib.internal.command;

import codechicken.lib.internal.command.admin.CountCommand;
import codechicken.lib.internal.command.admin.KillAllCommand;
import codechicken.lib.internal.command.admin.MiscCommands;
import codechicken.lib.internal.command.client.HighlightCommand;
import codechicken.lib.internal.command.dev.DevCommands;
import com.mojang.brigadier.CommandDispatcher;
import net.covers1624.quack.util.CrashLock;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.synchronization.ArgumentTypes;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;

import static codechicken.lib.CodeChickenLib.MOD_ID;

/**
 * Created by covers1624 on 17/9/20.
 */
public class CCLCommands {

    private static final CrashLock LOCK = new CrashLock("Already Initialized");

    public static void init() {
        LOCK.lock();

        registerArguments();
        MinecraftForge.EVENT_BUS.addListener(CCLCommands::registerServerCommands);
        MinecraftForge.EVENT_BUS.addListener(CCLCommands::registerClientCommands);
    }

    private static void registerArguments() {
        ArgumentTypes.register(MOD_ID + ":entity_type", EntityTypeArgument.class, new EntityTypeArgument.Serializer());
    }

    private static void registerServerCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        CountCommand.register(dispatcher);
        KillAllCommand.register(dispatcher);
        MiscCommands.register(dispatcher);
        DevCommands.register(dispatcher);
    }

    private static void registerClientCommands(RegisterClientCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        HighlightCommand.register(dispatcher);
    }

}
