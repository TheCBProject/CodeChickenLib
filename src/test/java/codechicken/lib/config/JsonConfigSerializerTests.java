package codechicken.lib.config;

import codechicken.lib.config.parser.ConfigSerializer;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;

/**
 * Created by covers1624 on 19/5/22.
 */
public class JsonConfigSerializerTests extends ConfigRoundTripTest {

    @Test
    public void testRoundTrip() throws Throwable {
        Path dir = Files.createTempDirectory("json_test_");
        dir.toFile().deleteOnExit();
        Path config = dir.resolve("config.json");

        ConfigCategoryImpl rootTag = generateTestTag(new Random());
        ConfigSerializer.JSON.save(config, rootTag);

        ConfigCategoryImpl readTag = new ConfigCategoryImpl("rootTag", null);
        ConfigSerializer.JSON.parse(config, readTag);

        assertTagsParseSame(rootTag, readTag);
    }
}
