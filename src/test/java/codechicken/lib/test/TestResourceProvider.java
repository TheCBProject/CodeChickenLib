package codechicken.lib.test;

import codechicken.lib.render.model.OBJParserTests;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceProvider;
import net.minecraft.server.packs.resources.SimpleResource;

import java.io.IOException;
import java.util.Objects;

/**
 * Simple singleton {@link ResourceProvider} instance for unit tests.
 * <p>
 * Created by covers1624 on 14/4/22.
 */
public class TestResourceProvider implements ResourceProvider {

    public static final TestResourceProvider INSTANCE = new TestResourceProvider();

    private TestResourceProvider() {
    }

    @Override
    public Resource getResource(ResourceLocation resource) throws IOException {
        return new SimpleResource(
                "test",
                resource,
                Objects.requireNonNull(OBJParserTests.class.getResourceAsStream("/" + resource.getPath()), "Resource " + resource + " doesnt exist."),
                null
        );
    }
}
