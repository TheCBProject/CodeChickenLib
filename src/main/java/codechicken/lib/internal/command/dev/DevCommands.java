package codechicken.lib.internal.command.dev;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.GameRules;

import static net.minecraft.commands.Commands.literal;

/**
 * Created by covers1624 on 11/12/21.
 */
public class DevCommands {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literal("ccl")
                .then(literal("setup_dev_world")
                        .requires(e -> e.hasPermission(4))
                        .executes(DevCommands::setupWorld)
                )
        );
    }

    private static int setupWorld(CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack source = ctx.getSource();
        MinecraftServer server = source.getServer();
        GameRules gameRules = server.getGameRules();

        for (ServerLevel level : server.getAllLevels()) {
            level.setWeatherParameters(6000, 0, false, false);
        }

        gameRules.getRule(GameRules.RULE_DAYLIGHT)
                .set(false, server);
        gameRules.getRule(GameRules.RULE_WEATHER_CYCLE)
                .set(false, server);
        gameRules.getRule(GameRules.RULE_DO_TRADER_SPAWNING)
                .set(false, server);
        return 0;
    }
}
