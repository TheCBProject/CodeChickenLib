package codechicken.lib.config;

import codechicken.lib.config.parser.ConfigSerializer;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;

/**
 * Created by covers1624 on 19/5/22.
 */
public class TomlConfigSerializerTests extends ConfigRoundTripTest {

    @Test
    public void testRoundTrip() throws Throwable {
        Path dir = Files.createTempDirectory("toml_test_");
        dir.toFile().deleteOnExit();
        Path config = dir.resolve("config.toml");

        ConfigCategoryImpl rootTag = generateTestTag(new Random());
        ConfigSerializer.TOML.save(config, rootTag);

        ConfigCategoryImpl readTag = new ConfigCategoryImpl("rootTag", null);
        ConfigSerializer.TOML.parse(config, readTag);

        assertTagsParseSame(rootTag, readTag);
    }
}
