package codechicken.lib.internal.command.client;

import codechicken.lib.internal.ItemFileRenderer;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.CommandRuntimeException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

import java.nio.file.Path;
import java.nio.file.Paths;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

/**
 * Created by covers1624 on 27/2/23.
 */
public class RenderItemToFileCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literal("ccl")
                .then(literal("render_held_to_file")
                        .then(argument("resolution", IntegerArgumentType.integer(16))
                                .then(argument("name", StringArgumentType.greedyString())
                                        .executes(e -> renderToFile(e, getResolution(e)))
                                )
                        )
                        .then(argument("name", StringArgumentType.greedyString())
                                .executes(e -> renderToFile(e, ItemFileRenderer.DEFAULT_RES))
                        )
                )
                .then(literal("render_held_to_gif")
                        .then(argument("fps", IntegerArgumentType.integer(5, 75))
                                .then(argument("duration", IntegerArgumentType.integer())
                                        .then(argument("resolution", IntegerArgumentType.integer(16))
                                                .then(argument("name", StringArgumentType.greedyString())
                                                        .executes(e -> renderToGif(e, getResolution(e)))
                                                )
                                        )
                                        .then(argument("name", StringArgumentType.greedyString())
                                                .executes(e -> renderToGif(e, ItemFileRenderer.DEFAULT_RES))
                                        )
                                )
                        )
                )
        );
    }

    private static int renderToFile(CommandContext<CommandSourceStack> ctx, int resolution) {
        Path path = getPath(ctx, "png");
        ItemStack held = getHeldItem();

        ctx.getSource().sendSuccess(Component.literal("Queued item render to file: " + path), false);
        ItemFileRenderer.addRenderTask(held, path, resolution);
        return 0;
    }

    private static int renderToGif(CommandContext<CommandSourceStack> ctx, int resolution) {
        CommandSourceStack src = ctx.getSource();
        int fps = IntegerArgumentType.getInteger(ctx, "fps");
        int duration = IntegerArgumentType.getInteger(ctx, "duration");

        Path path = getPath(ctx, "gif");
        ItemStack held = getHeldItem();

        src.sendSuccess(Component.literal("Queued item render to gif: " + path), false);
        ItemFileRenderer.addGifRenderTask(held, path, resolution, fps, duration);
        return 0;
    }

    private static Path getPath(CommandContext<CommandSourceStack> ctx, String extension) {
        String str = StringArgumentType.getString(ctx, "name");
        if (str.contains("..")) {
            throw new CommandRuntimeException(Component.literal("'..' is not allowed in name."));
        }
        return Paths.get("exports", str + "." + extension);
    }

    public static int getResolution(CommandContext<CommandSourceStack> ctx) {
        int res = IntegerArgumentType.getInteger(ctx, "resolution");
        if ((res & res - 1) != 0) {
            throw new CommandRuntimeException(Component.literal("Resolution must be a power of 2. 16, 32, 64..."));
        }
        return res;
    }

    private static ItemStack getHeldItem() {
        LocalPlayer player = Minecraft.getInstance().player;
        ItemStack held = player.getItemInHand(InteractionHand.MAIN_HAND);
        if (held.isEmpty()) {
            held = player.getItemInHand(InteractionHand.OFF_HAND);
        }
        if (held.isEmpty()) {
            throw new CommandRuntimeException(Component.literal("You are not holding anything."));
        }
        return held;
    }
}
