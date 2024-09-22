package codechicken.lib.datagen.recipe;

import net.minecraft.advancements.*;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.resources.ResourceLocation;
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
    protected final ResourceLocation id;
    protected final R result;
    private final Set<ItemLike> criteriaItems = new HashSet<>();
    private final Set<TagKey<Item>> criteriaTags = new HashSet<>();
    private int criteriaCounter = 0;
    protected boolean generateCriteria = false;
    protected boolean enableUnlocking = false;
    protected String group = "";

    protected AbstractRecipeBuilder(ResourceLocation id, R result) {
        this.id = id;
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
    public final ResourceLocation getId() {
        return id;
    }

    @Override
    public final BuiltRecipe build() {
        validate();
        if (enableUnlocking) {
            advancementBuilder.parent(new ResourceLocation("recipes/root"))
                    .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
                    .rewards(AdvancementRewards.Builder.recipe(id))
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
        return this.hasItem(ItemPredicate.Builder.item().of(itemIn).build());
    }

    protected Criterion<?> hasItem(TagKey<Item> tagIn) {
        return this.hasItem(ItemPredicate.Builder.item().of(tagIn).build());
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
