package codechicken.lib.recipe;

import codechicken.lib.configuration.ConfigFile;
import codechicken.lib.configuration.ConfigTag;
import codechicken.lib.configuration.IConfigTag.TagType;
import com.google.gson.JsonObject;
import net.minecraft.util.JsonUtils;
import net.minecraftforge.common.crafting.IConditionFactory;
import net.minecraftforge.common.crafting.JsonContext;

import java.util.function.BooleanSupplier;

/**
 * Created by covers1624 on 8/11/2017.
 */
public abstract class AbstractConfigConditionalFactory implements IConditionFactory {

    private ConfigFile config;

    protected AbstractConfigConditionalFactory(ConfigFile config) {
        this.config = config;
    }

    @Override
    public BooleanSupplier parse(JsonContext context, JsonObject json) {
        String tag = JsonUtils.getString(json, "tag");
        boolean flip = tag.startsWith("!");
        if (flip) {
            tag = tag.substring(1);
        }
        String[] parts;
        if (tag.contains(".")) {
            parts = tag.split("\\.");
        } else {
            parts = new String[] { tag };
        }

        ConfigTag t = config;
        for (String p : parts) {
            if (t.hasTag(p)) {
                t = t.getTagIfPresent(p);
            } else {
                throw new RuntimeException("Tag: " + tag + " does not exist in the specified config.");
            }
        }
        if (t.getType() != TagType.BOOLEAN) {
            throw new RuntimeException("Unable to use non boolean tag as conditional.");
        }
        boolean value = t.getBoolean();

        return () -> flip != value;
    }
}
