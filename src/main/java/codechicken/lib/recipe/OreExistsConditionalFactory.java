package codechicken.lib.recipe;

import com.google.gson.JsonObject;
import net.minecraft.util.JsonUtils;
import net.minecraftforge.common.crafting.IConditionFactory;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.oredict.OreDictionary;

import java.util.function.BooleanSupplier;

/**
 * Created by covers1624 on 18/10/2017.
 */
public class OreExistsConditionalFactory implements IConditionFactory {

    @Override
    public BooleanSupplier parse(JsonContext context, JsonObject json) {
        String ore = JsonUtils.getString(json, "ore");
        return () -> OreDictionary.doesOreNameExist(ore);
    }
}
