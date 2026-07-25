package codechicken.lib.datagen.recipe;

import net.minecraft.advancements.*;
import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.advancements.criterion.RecipeUnlockedTrigger;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.conditions.ICondition;

import java.util.*;

import static net.covers1624.quack.util.SneakyUtils.unsafeCast;

/**
 * Created by covers1624 on 28/12/20.
 */
public abstract class AbstractRecipeBuilder<R, T extends AbstractRecipeBuilder<R, T>> implements RecipeBuilder {

    protected final Throwable created = new Throwable("Created at");
    protected final Advancement.Builder advancementBuilder = Advancement.Builder.advancement();
    protected final List<ICondition> conditions = new LinkedList<>();
    protected final Identifier id;
    protected final ResourceKey<Recipe<?>> key;
    protected final HolderGetter<Item> items;
    protected final R result;
    private final Set<ItemLike> criteriaItems = new HashSet<>();
    private final Set<TagKey<Item>> criteriaTags = new HashSet<>();
    private int criteriaCounter = 0;
    protected boolean generateCriteria = false;
    protected boolean enableUnlocking = false;
    protected String group = "";

    protected AbstractRecipeBuilder(Identifier id, HolderGetter<Item> items, R result) {
        this.id = id;
        key = ResourceKey.create(Registries.RECIPE, id);
        this.items = items;
        this.result = result;
    }

    protected T getThis() {
        return unsafeCast(this);
    }

    public T withCondition(ICondition cond) {
        conditions.add(cond);
        return getThis();
    }

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

    public T addCriterion(String name, Criterion<?> criterion) {
        if (!enableUnlocking) {
            throw new IllegalStateException("Recipe unlocking must be enabled with 'enableUnlocking'");
        }
        advancementBuilder.addCriterion(name, criterion);
        return getThis();
    }

    @Override
    public final Identifier getId() {
        return id;
    }

    @Override
    public final ResourceKey<Recipe<?>> getKey() {
        return key;
    }

    @Override
    public final BuiltRecipe build() {
        validate();
        if (enableUnlocking) {
            advancementBuilder.parent(Identifier.withDefaultNamespace("recipes/root"))
                    .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(getKey()))
                    .rewards(AdvancementRewards.Builder.recipe(getKey()))
                    .requirements(AdvancementRequirements.Strategy.OR);
        }
        AdvancementHolder advancement = advancementBuilder.build(id.withPrefix("recipes"));
        if (advancement.value().criteria().isEmpty()) {
            advancement = null;
        }
        return new BuiltRecipe(_build(), advancement, conditions);
    }

    protected void validate() {
    }

    protected abstract Recipe<?> _build();

    protected void addAutoCriteria(ItemLike item) {
        if (generateCriteria && criteriaItems.add(item)) {
            addCriterion("has_ingredient_" + criteriaCounter++, hasItem(item));
        }
    }

    protected void addAutoCriteria(TagKey<Item> tag) {
        if (generateCriteria && criteriaTags.add(tag)) {
            addCriterion("has_ingredient_" + criteriaCounter++, hasItem(tag));
        }
    }

    protected Criterion<?> hasItem(ItemLike itemIn) {
        return this.hasItem(ItemPredicate.Builder.item().of(items, itemIn).build());
    }

    protected Criterion<?> hasItem(TagKey<Item> tagIn) {
        return this.hasItem(ItemPredicate.Builder.item().of(items, tagIn).build());
    }

    protected Criterion<?> hasItem(ItemPredicate... predicates) {
        return CriteriaTriggers.INVENTORY_CHANGED.createCriterion(
                new InventoryChangeTrigger.TriggerInstance(
                        Optional.empty(),
                        InventoryChangeTrigger.TriggerInstance.Slots.ANY,
                        List.of(predicates)
                )
        );
    }
}
