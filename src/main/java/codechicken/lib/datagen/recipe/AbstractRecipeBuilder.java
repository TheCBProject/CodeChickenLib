package codechicken.lib.datagen.recipe;

import com.google.gson.JsonObject;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.advancements.IRequirementsStrategy;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.advancements.criterion.RecipeUnlockedTrigger;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;

import javax.annotation.Nullable;

import static codechicken.lib.util.SneakyUtils.unsafeCast;

/**
 * Created by covers1624 on 28/12/20.
 */
public abstract class AbstractRecipeBuilder<R, T extends AbstractRecipeBuilder<R, T>> implements RecipeBuilder {

    protected final Advancement.Builder advancementBuilder = Advancement.Builder.builder();
    protected final IRecipeSerializer<?> serializer;
    protected final ResourceLocation id;
    protected final R result;
    private String group;

    protected AbstractRecipeBuilder(IRecipeSerializer<?> serializer, ResourceLocation id, R result) {
        this.serializer = serializer;
        this.id = id;
        this.result = result;
    }

    protected T getThis() {
        return unsafeCast(this);
    }

    protected abstract ResourceLocation getAdvancementId();

    public T setGroup(String group) {
        this.group = group;
        return getThis();
    }

    public T addCriterion(String name, ICriterionInstance criterion) {
        advancementBuilder.withCriterion(name, criterion);
        return getThis();
    }

    @Override
    public final ResourceLocation getId() {
        return id;
    }

    @Override
    public final IFinishedRecipe build() {
        validate();
        advancementBuilder.withParentId(new ResourceLocation("recipes/root"))
                .withCriterion("has_the_recipe", RecipeUnlockedTrigger.create(id))
                .withRewards(AdvancementRewards.Builder.recipe(id))
                .withRequirementsStrategy(IRequirementsStrategy.OR);
        return _build();
    }

    protected void validate() {
        if (advancementBuilder.getCriteria().isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe " + id);
        }

    }

    protected abstract AbstractFinishedRecipe _build();

    public abstract class AbstractFinishedRecipe implements IFinishedRecipe {

        @Override
        public void serialize(JsonObject json) {
            if (!StringUtils.isNullOrEmpty(group)) {
                json.addProperty("group", group);
            }
        }

        @Override
        public IRecipeSerializer<?> getSerializer() {
            return serializer;
        }

        @Override
        public ResourceLocation getID() {
            return id;
        }

        @Nullable
        @Override
        public JsonObject getAdvancementJson() {
            return advancementBuilder.serialize();
        }

        @Nullable
        @Override
        public ResourceLocation getAdvancementID() {
            return AbstractRecipeBuilder.this.getAdvancementId();
        }
    }
}
