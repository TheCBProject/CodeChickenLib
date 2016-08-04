package codechicken.lib.model.loader;

import net.minecraft.block.Block;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by covers1624 on 8/4/2016.
 */
public class CCBakedBlockModelFaceExclusions {

    private static Map<ResourceLocation, List<EnumFacing>> resourceFaceExclusionsMap = new HashMap<ResourceLocation, List<EnumFacing>>();
    private static ArrayList<ResourceLocation> onlyObeyNullList = new ArrayList<ResourceLocation>();

    public static void addFaceExclusion(Block block, EnumFacing face) {
        List<EnumFacing> exclusions = new ArrayList<EnumFacing>();
        if (resourceFaceExclusionsMap.containsKey(block.getRegistryName())) {
            exclusions = resourceFaceExclusionsMap.get(block.getRegistryName());
        }
        exclusions.add(face);
        resourceFaceExclusionsMap.put(block.getRegistryName(), exclusions);
    }

    public static void addAllFaceExclusions(Block block) {
        onlyObeyNullList.add(block.getRegistryName());
        for (EnumFacing face : EnumFacing.VALUES) {
            addFaceExclusion(block, face);
        }
    }

    public static boolean shouldBake(Block block, EnumFacing face) {
        if (face == null && onlyObeyNullList.contains(block.getRegistryName())) {
            return true;
        }
        if (resourceFaceExclusionsMap.containsKey(block.getRegistryName())) {
            List<EnumFacing> exclusions = resourceFaceExclusionsMap.get(block.getRegistryName());
            return !exclusions.contains(face);
        }
        return true;
    }

}
