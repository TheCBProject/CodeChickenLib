package codechicken.lib.internal.command.client;

import codechicken.lib.internal.ItemFileRenderer;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

import static java.util.Objects.requireNonNull;
import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

/**
 * Created by covers1624 on 27/2/23.
 */
public class RenderItemToFileCommand {

    private static final DynamicCommandExceptionType ERROR_INVALID = new DynamicCommandExceptionType(p_311534_ -> (Component) p_311534_);

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literal("ccl")
                .then(literal("render_held")
                        .then(argument("resolution", IntegerArgumentType.integer(16))
                                .then(argument("name", StringArgumentType.greedyString())
                                        .executes(e -> renderToFile(e, getResolution(e)))
                                )
                        )
                        .then(argument("name", StringArgumentType.greedyString())
                                .executes(e -> renderToFile(e, ItemFileRenderer.DEFAULT_RES))
                        )
                )
                .then(literal("render_held_anim")
                        .then(argument("fps", IntegerArgumentType.integer(5, 75))
                                .then(argument("duration", IntegerArgumentType.integer())
                                        .then(argument("resolution", IntegerArgumentType.integer(16))
                                                .then(argument("name", StringArgumentType.greedyString())
                                                        .executes(e -> renderAnim(e, getResolution(e)))
                                                )
                                        )
                                        .then(argument("name", StringArgumentType.greedyString())
                                                .executes(e -> renderAnim(e, ItemFileRenderer.DEFAULT_RES))
                                        )
                                )
                        )
                )
        );
    }

    private static int renderToFile(CommandContext<CommandSourceStack> ctx, int resolution) throws CommandSyntaxException {
        String path = getPath(ctx);
        ItemStack held = getHeldItem();

        ctx.getSource().sendSuccess(() -> Component.literal("Queued item render to file: " + path), false);
        ItemFileRenderer.renderStatic(held, path, resolution);
        return 0;
    }

    private static int renderAnim(CommandContext<CommandSourceStack> ctx, int resolution) throws CommandSyntaxException {
        CommandSourceStack src = ctx.getSource();
        int fps = IntegerArgumentType.getInteger(ctx, "fps");
        int duration = IntegerArgumentType.getInteger(ctx, "duration");

        String path = getPath(ctx);
        ItemStack held = getHeldItem();

        int lastSlash = path.lastIndexOf('/');
        int lastDot = path.lastIndexOf('.', lastSlash != -1 ? lastSlash : path.length() - 1);
        String ext = lastDot != -1 ? path.substring(lastDot + 1) : "";

        String finalPath;
        if (ext.isEmpty()) {
            ext = "gif";
            finalPath = path + ".gif";
        } else {
            finalPath = path;
        }

        switch (ext) {
            case "gif" -> {
                ItemFileRenderer.addGifRenderTask(held, finalPath, resolution, fps, duration);
                src.sendSuccess(() -> Component.literal("Queued item render to gif: " + finalPath), false);
            }
            case "webp" -> {
                if (!ItemFileRenderer.addWebpRenderTask(held, path, resolution, fps, duration)) {
                    src.sendFailure(Component.literal("Failed to queue render for webp, ffmpeg is not accessible. Either make it accessible on PATH, or set the `ccl.ffmpeg` system property."));
                } else {
                    src.sendSuccess(() -> Component.literal("Queued item render to webp: " + path), false);
                }
            }
        }

        return 0;
    }

    private static String getPath(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        String str = StringArgumentType.getString(ctx, "name");
        if (str.contains("..")) {
            throw ERROR_INVALID.create(Component.literal("'..' is not allowed in name."));
        }
        return str;
    }

    public static int getResolution(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        int res = IntegerArgumentType.getInteger(ctx, "resolution");
        if ((res & res - 1) != 0) {
            throw ERROR_INVALID.create(Component.literal("Resolution must be a power of 2. 16, 32, 64..."));
        }
        return res;
    }

    private static ItemStack getHeldItem() throws CommandSyntaxException {
        LocalPlayer player = requireNonNull(Minecraft.getInstance().player);
        ItemStack held = player.getItemInHand(InteractionHand.MAIN_HAND);
        if (held.isEmpty()) {
            held = player.getItemInHand(InteractionHand.OFF_HAND);
        }
        if (held.isEmpty()) {
            throw ERROR_INVALID.create(Component.literal("You are not holding anything."));
        }
        return held;
    }
}
