package codechicken.lib.internal.command.admin;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.EntityType;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerChunkProvider;
import net.minecraft.world.server.ServerWorld;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

import static codechicken.lib.internal.command.EntityTypeArgument.entityType;
import static codechicken.lib.internal.command.EntityTypeArgument.getEntityType;
import static codechicken.lib.math.MathHelper.floor;
import static net.minecraft.command.Commands.argument;
import static net.minecraft.command.Commands.literal;
import static net.minecraft.util.text.TextFormatting.*;

/**
 * Created by covers1624 on 27/11/20.
 */
public class CountCommand {

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(literal("ccl")
                .then(literal("count")
                        .requires(e -> e.hasPermissionLevel(2))
                        .then(argument("entity", entityType())
                                .executes(ctx -> {
                                    EntityType<?> entityType = getEntityType(ctx, "entity");
                                    return countAllEntities(ctx, e -> e.equals(entityType));
                                })
                        )
                        .executes(ctx -> countAllEntities(ctx, e -> true))
                )
        );
    }

    private static int countAllEntities(CommandContext<CommandSource> ctx, Predicate<EntityType<?>> predicate) {
        CommandSource source = ctx.getSource();
        ServerWorld world = source.getWorld();
        ServerChunkProvider provider = world.getChunkProvider();
        Object2IntMap<EntityType<?>> counts = new Object2IntOpenHashMap<>();
        counts.defaultReturnValue(0);
        world.getEntities()
                .filter(e -> predicate.test(e.getType()))
                .filter(e -> provider.chunkExists(floor(e.getPosX()) >> 4, floor(e.getPosZ()) >> 4))
                .forEach(e -> {
                    int count = counts.getInt(e.getType());
                    counts.put(e.getType(), count + 1);
                });

        List<EntityType<?>> order = new ArrayList<>(counts.keySet());
        order.sort(Comparator.comparingInt(counts::getInt));

        int total = 0;
        for (EntityType<?> type : order) {
            int count = counts.getInt(type);
            String name = type.getRegistryName().toString();
            ctx.getSource().sendFeedback(new StringTextComponent(GREEN + name + RESET + " x " + AQUA + count), false);
            total += count;
        }
        if (order.size() == 0) {
            ctx.getSource().sendFeedback(new TranslationTextComponent("ccl.commands.count.fail"), false);
        } else if (order.size() > 1) {
            ctx.getSource().sendFeedback(new TranslationTextComponent("ccl.commands.count.total", AQUA.toString() + total + RESET), false);
        }

        return total;
    }

}
