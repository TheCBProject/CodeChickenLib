package codechicken.lib.internal.command.admin;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandSource;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import static net.minecraft.command.Commands.literal;

/**
 * Created by covers1624 on 27/11/20.
 */
public class MiscCommands {

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(literal("ccl")
                .then(literal("meminfo")
                        .requires(e -> e.hasPermission(4))
                        .executes(MiscCommands::printMemInfo)
                )
                .then(literal("gc")
                        .requires(e -> e.hasPermission(4))
                        .executes(ctx -> {
                            ctx.getSource().sendSuccess(new TranslationTextComponent("ccl.commands.gc.before"), true);
                            printMemInfo(ctx, true);
                            ctx.getSource().sendSuccess(new TranslationTextComponent("ccl.commands.gc.performing"), true);
                            System.gc();
                            ctx.getSource().sendSuccess(new TranslationTextComponent("ccl.commands.gc.after"), true);
                            printMemInfo(ctx, true);
                            return 0;
                        })
                )
        );
    }

    private static int printMemInfo(CommandContext<CommandSource> ctx) {
        return printMemInfo(ctx, false);
    }

    private static int printMemInfo(CommandContext<CommandSource> ctx, boolean indent) {
        long max = Runtime.getRuntime().maxMemory();
        long total = Runtime.getRuntime().totalMemory();
        long free = Runtime.getRuntime().freeMemory();
        long used = total - free;
        String mem = String.format("Mem: % 2d%% %03d/%03dMB", used * 100L / max, bytesToMb(used), bytesToMb(max));
        String allocated = String.format("Allocated: % 2d%% %03dMB", total * 100L / max, bytesToMb(total));
        if (indent) {
            mem = " " + mem;
            allocated = " " + allocated;
        }
        ctx.getSource().sendSuccess(new StringTextComponent(mem), true);
        ctx.getSource().sendSuccess(new StringTextComponent(allocated), true);
        return 0;
    }

    private static long bytesToMb(long bytes) {
        return bytes / 1024L / 1024L;
    }
}
