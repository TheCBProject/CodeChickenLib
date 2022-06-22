package codechicken.lib.internal.command.admin;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.StreamSupport;

import static codechicken.lib.internal.command.EntityTypeArgument.entityType;
import static codechicken.lib.internal.command.EntityTypeArgument.getEntityType;
import static codechicken.lib.math.MathHelper.floor;
import static net.minecraft.ChatFormatting.*;
import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

/**
 * Created by covers1624 on 27/11/20.
 */
public class CountCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literal("ccl")
                .then(literal("count")
                        .requires(e -> e.hasPermission(2))
                        .then(argument("entity", entityType())
                                .executes(ctx -> {
                                    EntityType<?> entityType = getEntityType(ctx, "entity").value();
                                    return countAllEntities(ctx, e -> e.equals(entityType));
                                })
                        )
                        .executes(ctx -> countAllEntities(ctx, e -> true))
                )
        );
    }

    private static int countAllEntities(CommandContext<CommandSourceStack> ctx, Predicate<EntityType<?>> predicate) {
        CommandSourceStack source = ctx.getSource();
        ServerLevel level = source.getLevel();
        ServerChunkCache chunkCache = level.getChunkSource();
        Object2IntMap<EntityType<?>> counts = new Object2IntOpenHashMap<>();
        counts.defaultReturnValue(0);
        StreamSupport.stream(level.getEntities().getAll().spliterator(), false)
                .filter(e -> predicate.test(e.getType()))
                .filter(e -> chunkCache.hasChunk(floor(e.getX()) >> 4, floor(e.getZ()) >> 4))
                .forEach(e -> {
                    int count = counts.getInt(e.getType());
                    counts.put(e.getType(), count + 1);
                });

        List<EntityType<?>> order = new ArrayList<>(counts.keySet());
        order.sort(Comparator.comparingInt(counts::getInt));

        int total = 0;
        for (EntityType<?> type : order) {
            int count = counts.getInt(type);
            String name = ForgeRegistries.ENTITIES.getKey(type).toString();
            ctx.getSource().sendSuccess(Component.literal(GREEN + name + RESET + " x " + AQUA + count), false);
            total += count;
        }
        if (order.size() == 0) {
            ctx.getSource().sendSuccess(Component.translatable("ccl.commands.count.fail"), false);
        } else if (order.size() > 1) {
            ctx.getSource().sendSuccess(Component.translatable("ccl.commands.count.total", AQUA.toString() + total + RESET), false);
        }

        return total;
    }

}
