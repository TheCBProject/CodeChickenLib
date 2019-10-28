//package codechicken.lib.recipe;
//
//import com.google.gson.JsonObject;
//import com.google.gson.JsonParseException;
//import net.minecraft.util.JsonUtils;
//import net.minecraftforge.common.config.ConfigCategory;
//import net.minecraftforge.common.config.Configuration;
//import net.minecraftforge.common.crafting.IConditionFactory;
//import net.minecraftforge.common.crafting.JsonContext;
//
//import java.util.function.BooleanSupplier;
//
///**
// * Created by covers1624 on 8/11/2017.
// */
//public abstract class AbstractForgeConfigConditionalFactory implements IConditionFactory {
//
//    private Configuration config;
//
//    protected AbstractForgeConfigConditionalFactory(Configuration config) {
//        this.config = config;
//    }
//
//    @Override
//    public BooleanSupplier parse(JsonContext context, JsonObject json) {
//        String category = JsonUtils.getString(json, "category");
//        String key = JsonUtils.getString(json, "key");
//        boolean flip = JsonUtils.getBoolean(json, "flip", false);
//
//        if (config.hasCategory(category)) {
//            ConfigCategory cat = config.getCategory(category);
//            if (cat.containsKey(key) && cat.get(key).isBooleanValue()) {
//                return () -> flip != cat.get(key).getBoolean();
//            } else {
//                throw new JsonParseException(String.format("Key doesn't exist on category or is not of a boolean type. Category: %s, Key: %s", category, key));
//            }
//        } else {
//            throw new JsonParseException(String.format("Category doesn't exist on config file. Category: %s, Config: %s", category, config.getConfigFile().getAbsolutePath()));
//        }
//    }
//}
