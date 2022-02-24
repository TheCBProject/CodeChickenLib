package codechicken.lib.internal.command.admin;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Enemy;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static codechicken.lib.internal.command.EntityTypeArgument.entityType;
import static codechicken.lib.internal.command.EntityTypeArgument.getEntityType;
import static codechicken.lib.math.MathHelper.floor;
import static net.minecraft.ChatFormatting.*;
import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

/**
 * Created by covers1624 on 17/9/20.
 */
public class KillAllCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literal("ccl")
                .then(literal("killall")
                        .requires(e -> e.hasPermission(2))
                        .then(argument("entity", entityType())
                                .executes(ctx -> {
                                    EntityType<?> entityType = getEntityType(ctx, "entity");
                                    return killallForce(ctx, entityType, e -> Objects.equals(e.getType(), entityType));
                                })
                        )
                        .executes(ctx -> killallForce(ctx, null, e -> e instanceof Enemy))
                        .then(literal("gracefully")
                                .then(argument("entity", entityType())
                                        .executes(ctx -> {
                                            EntityType<?> entityType = getEntityType(ctx, "entity");
                                            return killAllGracefully(ctx, entityType, e -> Objects.equals(e.getType(), entityType));
                                        })
                                )
                                .executes(ctx -> killAllGracefully(ctx, null, e -> e instanceof Enemy))
                        )
                )
        );
    }

    private static int killAllGracefully(CommandContext<CommandSourceStack> ctx, @Nullable EntityType<?> type, Predicate<Entity> predicate) {
        return killEntities(ctx, type, predicate, Entity::kill);
    }

    private static int killallForce(CommandContext<CommandSourceStack> ctx, @Nullable EntityType<?> type, Predicate<Entity> predicate) {
        CommandSourceStack source = ctx.getSource();
        ServerLevel world = source.getLevel();
        return killEntities(ctx, type, predicate, world::removeEntity);
    }

    private static int killEntities(CommandContext<CommandSourceStack> ctx, @Nullable EntityType<?> type, Predicate<Entity> predicate, Consumer<Entity> killFunc) {
        if (type == EntityType.PLAYER) {
            ctx.getSource().sendSuccess(new TranslatableComponent("ccl.commands.killall.fail.player").withStyle(RED), false);
            return 0;
        }
        CommandSourceStack source = ctx.getSource();
        ServerLevel world = source.getLevel();
        ServerChunkCache provider = world.getChunkSource();
        Object2IntMap<EntityType<?>> counts = new Object2IntOpenHashMap<>();
        counts.defaultReturnValue(0);
        List<Entity> entities = StreamSupport.stream(world.getEntities().getAll().spliterator(), false)
                .filter(Objects::nonNull)
                .filter(predicate)
                .filter(e -> provider.hasChunk(floor(e.getX()) >> 4, floor(e.getZ()) >> 4))
                .collect(Collectors.toList());

        entities.forEach(e -> {
            killFunc.accept(e);
            int count = counts.getInt(e.getType());
            counts.put(e.getType(), count + 1);
        });

        List<EntityType<?>> order = new ArrayList<>(counts.keySet());
        order.sort(Comparator.comparingInt(counts::getInt));

        int total = 0;
        for (EntityType<?> t : order) {
            int count = counts.getInt(t);
            String name = t.getRegistryName().toString();
            ctx.getSource().sendSuccess(new TranslatableComponent("ccl.commands.killall.success.line", RED + name + RESET + " x " + AQUA + count), false);
            total += count;
        }
        if (order.size() == 0) {
            ctx.getSource().sendSuccess(new TranslatableComponent("ccl.commands.killall.fail"), false);
        } else if (order.size() > 1) {
            ctx.getSource().sendSuccess(new TranslatableComponent("ccl.commands.killall.success", AQUA.toString() + total + RESET), false);
        }
        return total;
    }

}
