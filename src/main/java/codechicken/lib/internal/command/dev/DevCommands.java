package codechicken.lib.internal.command.dev;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandSource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.GameRules;
import net.minecraft.world.server.ServerWorld;

import static net.minecraft.command.Commands.literal;

/**
 * Created by covers1624 on 11/12/21.
 */
public class DevCommands {

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(literal("ccl")
                .then(literal("setup_dev_world")
                        .requires(e -> e.hasPermission(4))
                        .executes(DevCommands::setupWorld)
                )
        );
    }

    private static int setupWorld(CommandContext<CommandSource> ctx) {
        CommandSource source = ctx.getSource();
        MinecraftServer server = source.getServer();
        GameRules gameRules = server.getGameRules();

        for (ServerWorld level : server.getAllLevels()) {
            level.setDayTime(6000);
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
