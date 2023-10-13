package codechicken.lib.internal.command.admin;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

import static net.minecraft.commands.Commands.literal;

/**
 * Created by covers1624 on 27/11/20.
 */
public class MiscCommands {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literal("ccl")
                .then(literal("meminfo")
                        .requires(e -> e.hasPermission(4))
                        .executes(MiscCommands::printMemInfo)
                )
                .then(literal("gc")
                        .requires(e -> e.hasPermission(4))
                        .executes(ctx -> {
                            ctx.getSource().sendSuccess(() -> Component.translatable("ccl.commands.gc.before"), true);
                            printMemInfo(ctx, true);
                            ctx.getSource().sendSuccess(() -> Component.translatable("ccl.commands.gc.performing"), true);
                            System.gc();
                            ctx.getSource().sendSuccess(() -> Component.translatable("ccl.commands.gc.after"), true);
                            printMemInfo(ctx, true);
                            return 0;
                        })
                )
        );
    }

    private static int printMemInfo(CommandContext<CommandSourceStack> ctx) {
        return printMemInfo(ctx, false);
    }

    private static int printMemInfo(CommandContext<CommandSourceStack> ctx, boolean indent) {
        long max = Runtime.getRuntime().maxMemory();
        long total = Runtime.getRuntime().totalMemory();
        long free = Runtime.getRuntime().freeMemory();
        long used = total - free;
        ctx.getSource().sendSuccess(() -> Component.literal((indent ? " " : "") + String.format("Mem: % 2d%% %03d/%03dMB", used * 100L / max, bytesToMb(used), bytesToMb(max))), true);
        ctx.getSource().sendSuccess(() -> Component.literal((indent ? " " : "") + String.format("Allocated: % 2d%% %03dMB", total * 100L / max, bytesToMb(total))), true);
        return 0;
    }

    private static long bytesToMb(long bytes) {
        return bytes / 1024L / 1024L;
    }
}
