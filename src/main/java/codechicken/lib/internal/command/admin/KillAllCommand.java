package codechicken.lib.internal.command.admin;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.monster.IMob;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerChunkProvider;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static codechicken.lib.internal.command.EntityTypeArgument.entityType;
import static codechicken.lib.internal.command.EntityTypeArgument.getEntityType;
import static codechicken.lib.math.MathHelper.floor;
import static net.minecraft.command.Commands.argument;
import static net.minecraft.command.Commands.literal;
import static net.minecraft.util.text.TextFormatting.*;

/**
 * Created by covers1624 on 17/9/20.
 */
public class KillAllCommand {

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(literal("ccl")
                .then(literal("killall")
                        .requires(e -> e.hasPermission(2))
                        .then(argument("entity", entityType())
                                .executes(ctx -> {
                                    EntityType<?> entityType = getEntityType(ctx, "entity");
                                    return killallForce(ctx, entityType, e -> Objects.equals(e.getType(), entityType));
                                })
                        )
                        .executes(ctx -> killallForce(ctx, null, e -> e instanceof IMob))
                        .then(literal("gracefully")
                                .then(argument("entity", entityType())
                                        .executes(ctx -> {
                                            EntityType<?> entityType = getEntityType(ctx, "entity");
                                            return killAllGracefully(ctx, entityType, e -> Objects.equals(e.getType(), entityType));
                                        })
                                )
                                .executes(ctx -> killAllGracefully(ctx, null, e -> e instanceof IMob))
                        )
                )
        );
    }

    private static int killAllGracefully(CommandContext<CommandSource> ctx, @Nullable EntityType<?> type, Predicate<Entity> predicate) {
        return killEntities(ctx, type, predicate, Entity::kill);
    }

    private static int killallForce(CommandContext<CommandSource> ctx, @Nullable EntityType<?> type, Predicate<Entity> predicate) {
        CommandSource source = ctx.getSource();
        ServerWorld world = source.getLevel();
        return killEntities(ctx, type, predicate, world::despawn);
    }

    private static int killEntities(CommandContext<CommandSource> ctx, @Nullable EntityType<?> type, Predicate<Entity> predicate, Consumer<Entity> killFunc) {
        if (type == EntityType.PLAYER) {
            ctx.getSource().sendSuccess(new TranslationTextComponent("ccl.commands.killall.fail.player").withStyle(TextFormatting.RED), false);
            return 0;
        }
        CommandSource source = ctx.getSource();
        ServerWorld world = source.getLevel();
        ServerChunkProvider provider = world.getChunkSource();
        Object2IntMap<EntityType<?>> counts = new Object2IntOpenHashMap<>();
        counts.defaultReturnValue(0);
        List<Entity> entities = world.getEntities()
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
            ctx.getSource().sendSuccess(new TranslationTextComponent("ccl.commands.killall.success.line", RED + name + RESET + " x " + AQUA + count), false);
            total += count;
        }
        if (order.size() == 0) {
            ctx.getSource().sendSuccess(new TranslationTextComponent("ccl.commands.killall.fail"), false);
        } else if (order.size() > 1) {
            ctx.getSource().sendSuccess(new TranslationTextComponent("ccl.commands.killall.success", AQUA.toString() + total + RESET), false);
        }
        return total;
    }

}
