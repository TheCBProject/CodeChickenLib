package codechicken.lib.datagen.recipe;

import com.google.gson.JsonObject;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.*;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

import static net.covers1624.quack.util.SneakyUtils.unsafeCast;

/**
 * Created by covers1624 on 28/12/20.
 */
public abstract class AbstractRecipeBuilder<R, T extends AbstractRecipeBuilder<R, T>> implements RecipeBuilder {

    protected final Throwable created = new Throwable("Created at");
    protected final Advancement.Builder advancementBuilder = Advancement.Builder.advancement();
    protected final RecipeSerializer<?> serializer;
    protected final ResourceLocation id;
    protected final R result;
    private final Set<ItemLike> criteriaItems = new HashSet<>();
    private final Set<Tag<Item>> criteriaTags = new HashSet<>();
    private int criteriaCounter = 0;
    protected boolean generateCriteria = false;
    protected boolean enableUnlocking = false;
    private String group;

    protected AbstractRecipeBuilder(RecipeSerializer<?> serializer, ResourceLocation id, R result) {
        this.serializer = serializer;
        this.id = id;
        this.result = result;
    }

    protected T getThis() {
        return unsafeCast(this);
    }

    protected abstract ResourceLocation getAdvancementId();

    public T enableUnlocking() {
        enableUnlocking = true;
        return getThis();
    }

    public T autoCriteria() {
        enableUnlocking();
        generateCriteria = true;
        return getThis();
    }

    public T setGroup(String group) {
        this.group = group;
        return getThis();
    }

    public T addCriterion(String name, CriterionTriggerInstance criterion) {
        if (!enableUnlocking) {
            throw new IllegalStateException("Recipe unlocking must be enabled with 'enableUnlocking'");
        }
        advancementBuilder.addCriterion(name, criterion);
        return getThis();
    }

    @Override
    public final ResourceLocation getId() {
        return id;
    }

    @Override
    public final FinishedRecipe build() {
        validate();
        if (enableUnlocking) {
            advancementBuilder.parent(new ResourceLocation("recipes/root"))
                    .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
                    .rewards(AdvancementRewards.Builder.recipe(id))
                    .requirements(RequirementsStrategy.OR);
        }
        return _build();
    }

    protected void validate() {
        if (enableUnlocking && advancementBuilder.getCriteria().isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe " + id, created);
        }

    }

    protected abstract AbstractFinishedRecipe _build();

    protected void addAutoCriteria(ItemLike item) {
        if (generateCriteria && criteriaItems.add(item)) {
            addCriterion("has_ingredient_" + criteriaCounter++, hasItem(item));
        }
    }

    protected void addAutoCriteria(Tag<Item> tag) {
        if (generateCriteria && criteriaTags.add(tag)) {
            addCriterion("has_ingredient_" + criteriaCounter++, hasItem(tag));
        }
    }

    protected InventoryChangeTrigger.TriggerInstance hasItem(ItemLike itemIn) {
        return this.hasItem(ItemPredicate.Builder.item().of(itemIn).build());
    }

    protected InventoryChangeTrigger.TriggerInstance hasItem(Tag<Item> tagIn) {
        return this.hasItem(ItemPredicate.Builder.item().of(tagIn).build());
    }

    protected InventoryChangeTrigger.TriggerInstance hasItem(ItemPredicate... predicates) {
        return new InventoryChangeTrigger.TriggerInstance(EntityPredicate.Composite.ANY, MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.ANY, predicates);
    }

    public abstract class AbstractFinishedRecipe implements FinishedRecipe {

        @Override
        public void serializeRecipeData(JsonObject json) {
            if (StringUtils.isNotEmpty(group)) {
                json.addProperty("group", group);
            }
        }

        @Override
        public RecipeSerializer<?> getType() {
            return serializer;
        }

        @Override
        public ResourceLocation getId() {
            return id;
        }

        @Nullable
        @Override
        public JsonObject serializeAdvancement() {
            return enableUnlocking ? advancementBuilder.serializeToJson() : null;
        }

        @Nullable
        @Override
        public ResourceLocation getAdvancementId() {
            return AbstractRecipeBuilder.this.getAdvancementId();
        }
    }
}
