package codechicken.lib.test;

import codechicken.lib.render.model.OBJParserTests;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceProvider;

import java.util.Objects;
import java.util.Optional;

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
    public Optional<Resource> getResource(ResourceLocation resource) {
        return Optional.of(new Resource(null, () -> Objects.requireNonNull(OBJParserTests.class.getResourceAsStream("/" + resource.getPath()), "Resource " + resource + " doesnt exist.")));
    }
}
