package codechicken.lib.datagen.recipe;

import com.google.gson.JsonObject;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.advancements.IRequirementsStrategy;
import net.minecraft.advancements.criterion.*;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.tags.ITag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

import static codechicken.lib.util.SneakyUtils.unsafeCast;

/**
 * Created by covers1624 on 28/12/20.
 */
public abstract class AbstractRecipeBuilder<R, T extends AbstractRecipeBuilder<R, T>> implements RecipeBuilder {

    protected final Throwable created = new Throwable("Created at");
    protected final Advancement.Builder advancementBuilder = Advancement.Builder.advancement();
    protected final IRecipeSerializer<?> serializer;
    protected final ResourceLocation id;
    protected final R result;
    private final Set<IItemProvider> criteriaItems = new HashSet<>();
    private final Set<ITag<Item>> criteriaTags = new HashSet<>();
    private int criteriaCounter = 0;
    protected boolean generateCriteria = false;
    protected boolean enableUnlocking = false;
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

    public T addCriterion(String name, ICriterionInstance criterion) {
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
    public final IFinishedRecipe build() {
        validate();
        if (enableUnlocking) {
            advancementBuilder.parent(new ResourceLocation("recipes/root"))
                    .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
                    .rewards(AdvancementRewards.Builder.recipe(id))
                    .requirements(IRequirementsStrategy.OR);
        }
        return _build();
    }

    protected void validate() {
        if (enableUnlocking && advancementBuilder.getCriteria().isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe " + id, created);
        }

    }

    protected abstract AbstractFinishedRecipe _build();

    protected void addAutoCriteria(IItemProvider item) {
        if (generateCriteria && criteriaItems.add(item)) {
            addCriterion("has_ingredient_" + criteriaCounter++, hasItem(item));
        }
    }

    protected void addAutoCriteria(ITag<Item> tag) {
        if (generateCriteria && criteriaTags.add(tag)) {
            addCriterion("has_ingredient_" + criteriaCounter++, hasItem(tag));
        }
    }

    protected InventoryChangeTrigger.Instance hasItem(IItemProvider itemIn) {
        return this.hasItem(ItemPredicate.Builder.item().of(itemIn).build());
    }

    protected InventoryChangeTrigger.Instance hasItem(ITag<Item> tagIn) {
        return this.hasItem(ItemPredicate.Builder.item().of(tagIn).build());
    }

    protected InventoryChangeTrigger.Instance hasItem(ItemPredicate... predicates) {
        return new InventoryChangeTrigger.Instance(EntityPredicate.AndPredicate.ANY, MinMaxBounds.IntBound.ANY, MinMaxBounds.IntBound.ANY, MinMaxBounds.IntBound.ANY, predicates);
    }

    public abstract class AbstractFinishedRecipe implements IFinishedRecipe {

        @Override
        public void serializeRecipeData(JsonObject json) {
            if (!StringUtils.isNullOrEmpty(group)) {
                json.addProperty("group", group);
            }
        }

        @Override
        public IRecipeSerializer<?> getType() {
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
