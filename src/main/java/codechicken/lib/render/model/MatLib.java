package codechicken.lib.render.model;

import codechicken.lib.util.ResourceUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceProvider;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Material Template Library.
 * <p>
 * Created by covers1624 on 11/4/22.
 */
public class MatLib {

    public final Map<String, ModelMaterial> materials = new HashMap<>();

    public static MatLib parse(ResourceProvider resourceProvider, ResourceLocation loc) {
        MatLib matLib = new MatLib();
        ModelMaterial curr = null;
        for (String line : ResourceUtils.loadResource(resourceProvider, loc)) {
            line = line.replaceAll("\\s+", " ").trim();
            if (line.isEmpty() || line.startsWith("#")) continue;
            String[] splits = line.split(" ", 2);
            if (splits[0].equals("newmtl")) {
                curr = new ModelMaterial();
                curr.name = splits[1];
                matLib.materials.put(curr.name, curr);
            }
            assert curr != null : "newmtl line not found yet";

            switch (splits[0]) {
                case "Ka" -> curr.ambientColour.set(parseDoubles(splits[1], " "));
                case "map_Ka" -> curr.ambientColourMap = splits[1];
                case "Kd" -> curr.diffuseColour.set(parseDoubles(splits[1], " "));
                case "map_Kd" -> curr.diffuseColourMap = splits[1];
                case "Ks" -> curr.specularColour.set(parseDoubles(splits[1], " "));
                case "Ns" -> curr.specularHighlight = Float.parseFloat(splits[1]);
                case "map_Ks" -> curr.specularColourMap = splits[1];
                case "d" -> curr.dissolve = Float.parseFloat(splits[1]);
                case "illum" -> curr.illumination = Float.parseFloat(splits[1]);
            }

        }
        return matLib;
    }

    @Nullable
    public ModelMaterial getMaterial(String name) {
        return materials.get(name);
    }

    private static double[] parseDoubles(String s, String token) {
        String[] as = s.split(token);
        assert as.length < 4 : "Too many values, expected 4 max, Got: " + as.length;
        double[] values = new double[] { 0, 0, 0, 1 };
        for (int i = 0; i < as.length; i++) {
            values[i] = Double.parseDouble(as[i]);
        }
        return values;
    }
}
