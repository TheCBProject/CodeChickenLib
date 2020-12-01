package codechicken.lib.internal.command;

import codechicken.lib.CodeChickenLib;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.ArgumentTypes;
import net.minecraft.command.arguments.IArgumentSerializer;
import net.minecraft.entity.EntityType;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

import static codechicken.lib.CodeChickenLib.MOD_ID;

/**
 * Created by covers1624 on 17/9/20.
 */
public class EntityTypeArgument implements ArgumentType<EntityType<?>> {

    private static final Collection<String> EXAMPLES = ImmutableList.of("item", "minecraft:pig", "minecraft:wither");
    private static final DynamicCommandExceptionType MISSING = new DynamicCommandExceptionType(p ->//
            new TranslationTextComponent(MOD_ID + ":argument.entity_type.invalid", p)//
    );

    @Override
    public EntityType<?> parse(StringReader reader) throws CommandSyntaxException {
        ResourceLocation loc = ResourceLocation.read(reader);
        return Registry.ENTITY_TYPE.getValue(loc).orElseThrow(() -> MISSING.create(loc));
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return ISuggestionProvider.func_212476_a(Registry.ENTITY_TYPE.keySet().stream(), builder);
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    public static EntityTypeArgument entityType() {
        return new EntityTypeArgument();
    }

    public static EntityType<?> getEntityType(CommandContext<CommandSource> src, String name) {
        return src.getArgument(name, EntityType.class);
    }

    public static class Serializer implements IArgumentSerializer<EntityTypeArgument> {

        @Override
        public void write(EntityTypeArgument argument, PacketBuffer buffer) {
        }

        @Override
        public EntityTypeArgument read(PacketBuffer buffer) {
            return new EntityTypeArgument();
        }

        @Override
        public void write(EntityTypeArgument p_212244_1_, JsonObject p_212244_2_) {
        }
    }
}
