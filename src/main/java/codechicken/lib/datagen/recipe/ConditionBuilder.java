package codechicken.lib.datagen.recipe;

import net.minecraftforge.common.crafting.conditions.IConditionBuilder;

/**
 * Created by covers1624 on 23/9/22.
 */
public interface ConditionBuilder extends IConditionBuilder {

    ConditionBuilder INSTANCE = new ConditionBuilder() { };
}
