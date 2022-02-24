package codechicken.lib.internal.command;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.synchronization.ArgumentSerializer;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

import static codechicken.lib.CodeChickenLib.MOD_ID;

/**
 * Created by covers1624 on 17/9/20.
 */
public class EntityTypeArgument implements ArgumentType<EntityType<?>> {

    private static final Collection<String> EXAMPLES = ImmutableList.of("item", "minecraft:pig", "minecraft:wither");
    private static final DynamicCommandExceptionType MISSING = new DynamicCommandExceptionType(p ->//
            new TranslatableComponent(MOD_ID + ":argument.entity_type.invalid", p)//
    );

    @Override
    public EntityType<?> parse(StringReader reader) throws CommandSyntaxException {
        ResourceLocation loc = ResourceLocation.read(reader);
        return Registry.ENTITY_TYPE.getOptional(loc).orElseThrow(() -> MISSING.create(loc));
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return SharedSuggestionProvider.suggestResource(Registry.ENTITY_TYPE.keySet().stream(), builder);
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    public static EntityTypeArgument entityType() {
        return new EntityTypeArgument();
    }

    public static EntityType<?> getEntityType(CommandContext<CommandSourceStack> src, String name) {
        return src.getArgument(name, EntityType.class);
    }

    public static class Serializer implements ArgumentSerializer<EntityTypeArgument> {

        @Override
        public void serializeToNetwork(EntityTypeArgument argument, FriendlyByteBuf buffer) {
        }

        @Override
        public EntityTypeArgument deserializeFromNetwork(FriendlyByteBuf buffer) {
            return new EntityTypeArgument();
        }

        @Override
        public void serializeToJson(EntityTypeArgument p_212244_1_, JsonObject p_212244_2_) {
        }
    }
}
