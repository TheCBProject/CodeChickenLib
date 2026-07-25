package codechicken.lib.internal.command.dev;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.gamerules.GameRules;

import static net.minecraft.commands.Commands.literal;

/**
 * Created by covers1624 on 11/12/21.
 */
public class DevCommands {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context) {
        dispatcher.register(literal("ccl")
                .then(literal("setup_dev_world")
                        .requires(Commands.hasPermission(Commands.LEVEL_ADMINS))
                        .executes(DevCommands::setupWorld)
                )
        );
    }

    private static int setupWorld(CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack source = ctx.getSource();
        MinecraftServer server = source.getServer();
        GameRules gameRules = server.getWorldData().getGameRules();

        for (ServerLevel level : server.getAllLevels()) {
            level.setWeatherParameters(6000, 0, false, false);
        }

        gameRules.set(GameRules.ADVANCE_TIME, false, server);
        gameRules.set(GameRules.ADVANCE_WEATHER, false, server);
        gameRules.set(GameRules.SPAWN_WANDERING_TRADERS, false, server);
        return 0;
    }
}
