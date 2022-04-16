package codechicken.lib.render.model;

import codechicken.lib.render.CCModel;
import codechicken.lib.test.TestResourceProvider;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Created by covers1624 on 14/4/22.
 */
public class OBJParserTests {

    @Test
    public void testObject() {
        Map<String, CCModel> modelMap = OBJParser.parse(TestResourceProvider.INSTANCE, new ResourceLocation("test:model/test.obj"), VertexFormat.Mode.QUADS, null, false);
        CCModel model = modelMap.get("Cube");
        assertNotNull(model);
        ModelMaterial material = model.material();
        assertNotNull(material);
    }

    @Test
    public void testGroup() {
        Map<String, CCModel> modelMap = OBJParser.parse(TestResourceProvider.INSTANCE, new ResourceLocation("test:model/test_group.obj"), VertexFormat.Mode.QUADS, null, false);
        CCModel model = modelMap.get("Cube");
        assertNotNull(model);
        ModelMaterial material = model.material();
        assertNotNull(material);
    }

    @Test
    public void testGroupObject() {
        Map<String, CCModel> modelMap = OBJParser.parse(TestResourceProvider.INSTANCE, new ResourceLocation("test:model/test_group_object.obj"), VertexFormat.Mode.QUADS, null, false);
        CCModel model = modelMap.get("Group/Cube");
        assertNotNull(model);
        ModelMaterial material = model.material();
        assertNotNull(material);
    }
}
