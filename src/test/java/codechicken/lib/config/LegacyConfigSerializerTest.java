package codechicken.lib.config;

import codechicken.lib.config.parser.ConfigSerializer;
import codechicken.lib.test.config.ConfigTests;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Created by covers1624 on 21/4/22.
 */
public class LegacyConfigSerializerTest {

    @Test
    public void testParseSerializeRondTrip() throws Throwable {
        Path dir = Files.createTempDirectory("config_test");
        dir.toFile().deleteOnExit();

        Path initial = dir.resolve("initial.cfg");
        Path written = dir.resolve("written.cfg");
        copyTestFile(initial);

        ConfigCategoryImpl rootTag = new ConfigCategoryImpl("rootTag", null);
        ConfigSerializer.LEGACY.parse(initial, rootTag);
        assertNotEquals(0, rootTag.getChildren().size());

        ConfigSerializer.LEGACY.save(written, rootTag);

        ConfigCategoryImpl readRoot = new ConfigCategoryImpl("rootTag", null);
        ConfigSerializer.LEGACY.parse(written, readRoot);

        assertTrue(ConfigV3Tests.equals(rootTag, readRoot));
    }

    @Test
    public void testThroughConfigFile() throws Throwable {
        Path file = Files.createTempFile("temp", ".cfg");
        file.toFile().deleteOnExit();
        copyTestFile(file);

        ConfigCategory config = new ConfigFile("rootTag")
                .path(file)
                .load();

        assertNotEquals(0, config.getChildren().size());
    }

    private static void copyTestFile(Path dst) throws Throwable {
        Path resource = Paths.get(ConfigTests.class.getResource("/test.cfg").toURI());
        Files.copy(resource, dst, StandardCopyOption.REPLACE_EXISTING);
    }
}
