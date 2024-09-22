//package codechicken.lib.datagen;
//
//import codechicken.lib.datagen.recipe.ConditionBuilder;
//import com.google.gson.JsonArray;
//import com.google.gson.JsonElement;
//import com.google.gson.JsonObject;
//import net.covers1624.quack.util.CrashLock;
//import net.minecraft.network.FriendlyByteBuf;
//import net.minecraft.resources.ResourceLocation;
//import net.minecraft.util.GsonHelper;
//import net.minecraft.world.item.ItemStack;
//import net.minecraft.world.item.crafting.Ingredient;
//import net.minecraftforge.common.MinecraftForge;
//import net.minecraftforge.common.crafting.AbstractIngredient;
//import net.minecraftforge.common.crafting.CraftingHelper;
//import net.minecraftforge.common.crafting.IIngredientSerializer;
//import net.minecraftforge.common.crafting.conditions.ICondition;
//import net.minecraftforge.event.AddReloadListenerEvent;
//import org.jetbrains.annotations.Nullable;
//
//import java.util.LinkedList;
//import java.util.List;
//import java.util.function.Function;
//
//import static codechicken.lib.CodeChickenLib.MOD_ID;
//
///**
// * Created by covers1624 on 24/9/22.
// */
//public class ConditionalIngredient extends AbstractIngredient {
//
//    private static final ResourceLocation TYPE = new ResourceLocation(MOD_ID, "conditional");
//
//    private final List<ICondition> conditions;
//    private final Ingredient pass;
//    @Nullable
//    private final Ingredient fail;
//
//    private ConditionalIngredient(List<ICondition> conditions, Ingredient pass, @Nullable Ingredient fail) {
//        this.conditions = conditions;
//        this.pass = pass;
//        this.fail = fail;
//    }
//
//    public static ConditionalIngredient.Builder builder() {
//        return new ConditionalIngredient.Builder();
//    }
//
//    @Override
//    public JsonElement toJson() {
//        JsonObject obj = new JsonObject();
//        obj.addProperty("type", TYPE.toString());
//        JsonArray conditions = new JsonArray();
//        for (ICondition condition : this.conditions) {
//            conditions.add(CraftingHelper.serialize(condition));
//        }
//        obj.add("conditions", conditions);
//        obj.add("pass", pass.toJson());
//        if (fail != null) {
//            obj.add("fail", fail.toJson());
//        }
//        return obj;
//    }
//
//    // @formatter:off
//    @Override public ItemStack[] getItems() { throw new UnsupportedOperationException("Exists for DataGeneration only."); }
//    @Override public boolean isSimple() { throw new UnsupportedOperationException("Exists for DataGeneration only."); }
//    @Override public IIngredientSerializer<? extends Ingredient> getSerializer() { throw new UnsupportedOperationException("Exists for DataGeneration only."); }
//    // @formatter:on
//
//    public static class Builder {
//
//        private List<ICondition> conditions = new LinkedList<>();
//        private Ingredient pass = null;
//        private Ingredient fail = null;
//
//        private Builder() {
//        }
//
//        public Builder withCondition(Function<ConditionBuilder, ICondition> func) {
//            conditions.add(func.apply(ConditionBuilder.INSTANCE));
//            return this;
//        }
//
//        public Builder withPass(Ingredient ing) {
//            if (pass != null) throw new IllegalStateException("Pass Ingredient already specified.");
//            this.pass = ing;
//            return this;
//        }
//
//        public Builder withFail(Ingredient ing) {
//            if (fail != null) throw new IllegalStateException("Pass Ingredient already specified.");
//            this.fail = ing;
//            return this;
//        }
//
//        public ConditionalIngredient build() {
//            if (conditions.isEmpty()) throw new IllegalStateException("No conditions specified.");
//            if (pass == null) throw new IllegalStateException("Pass Ingredient is required.");
//
//            return new ConditionalIngredient(conditions, pass, fail);
//        }
//    }
//
//    public static class Serializer implements IIngredientSerializer<Ingredient> {
//
//        private static final CrashLock LOCK = new CrashLock("Already Initialized");
//        private static ICondition.IContext conditionContext = ICondition.IContext.EMPTY;
//
//        public static void init() {
//            LOCK.lock();
//            MinecraftForge.EVENT_BUS.addListener(Serializer::onAddReloadListenerEvent);
//            CraftingHelper.register(TYPE, new Serializer());
//        }
//
//        private static void onAddReloadListenerEvent(AddReloadListenerEvent event) {
//            conditionContext = event.getConditionContext();
//        }
//
//        @Override
//        public Ingredient parse(JsonObject json) {
//            if (!CraftingHelper.processConditions(json, "conditions", conditionContext)) {
//                if (json.has("fail")) {
//                    return CraftingHelper.getIngredient(GsonHelper.getAsJsonObject(json, "fail"), true);
//                }
//                return Ingredient.EMPTY;
//            }
//            return CraftingHelper.getIngredient(GsonHelper.getAsJsonObject(json, "pass"), false);
//        }
//
//        // We don't operate like this, we always return a different ingredient.
//        // @formatter:off
//        @Override public Ingredient parse(FriendlyByteBuf buffer) { throw new UnsupportedOperationException(); }
//        @Override public void write(FriendlyByteBuf buffer, Ingredient ingredient) { throw new UnsupportedOperationException(); }
//        // @formatter:on
//    }
//}
