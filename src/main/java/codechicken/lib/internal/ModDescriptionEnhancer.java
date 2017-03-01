package codechicken.lib.internal;

import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.ModContainer;


public class ModDescriptionEnhancer {

    public static void enhanceMod(Object mod) {
        ModContainer mc = FMLCommonHandler.instance().findContainerFor(mod);
        mc.getMetadata().description = enhanceDesc(mc.getMetadata().description);
    }

    public static String enhanceDesc(String desc) {
        int supportersIdx = desc.indexOf("Supporters:");
        if (supportersIdx < 0) {
            return desc;
        }

        String supportersList = desc.substring(supportersIdx);
        supportersList = supportersList.replaceAll("\\b(\\w+)\\b", TextFormatting.AQUA + "$1");
        return desc.substring(0, supportersIdx) + supportersList;
    }
}
