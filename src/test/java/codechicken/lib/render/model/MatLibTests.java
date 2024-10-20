package codechicken.lib.render.model;

import codechicken.lib.colour.ColourRGBA;
import codechicken.lib.test.TestResourceProvider;
import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Created by covers1624 on 14/4/22.
 */
public class MatLibTests {

    @Test
    public void testMatLibParse() {
        MatLib matLib = MatLib.parse(TestResourceProvider.INSTANCE, ResourceLocation.parse("test:model/test.mtl"));
        assertEquals(1, matLib.materials.size());
        ModelMaterial material = matLib.getMaterial("Material");
        assertNotNull(material);
        assertEquals(new ColourRGBA(0xB2997FFF), material.ambientColour);
        assertEquals("ka.png", material.ambientColourMap);
        assertEquals(new ColourRGBA(0xCCB299FF), material.diffuseColour);
        assertEquals("kd.png", material.diffuseColourMap);
        assertEquals(new ColourRGBA(0xCCB299FF), material.specularColour);
        assertEquals("ks.png", material.specularColourMap);
        assertEquals(360.0F, material.specularHighlight);
        assertEquals(1.5F, material.dissolve);
        assertEquals(5.0F, material.illumination);
    }
}
