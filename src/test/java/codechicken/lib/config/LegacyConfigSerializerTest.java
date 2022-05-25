package codechicken.lib.config;

import codechicken.lib.config.parser.ConfigSerializer;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Created by covers1624 on 21/4/22.
 */
public class LegacyConfigSerializerTest extends ConfigRoundTripTest {

    @Test
    public void testParseSerializeRondTrip() throws Throwable {
        Path dir = Files.createTempDirectory("legacy_test_");
        dir.toFile().deleteOnExit();
        Path config = dir.resolve("config.cfg");

        ConfigCategoryImpl rootTag = generateTestTag(new Random());
        ConfigSerializer.LEGACY.save(config, rootTag);

        ConfigCategoryImpl readTag = new ConfigCategoryImpl("rootTag", null);
        ConfigSerializer.LEGACY.parse(config, readTag);

        assertTagsParseSame(rootTag, readTag);
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
        Path resource = Paths.get(LegacyConfigSerializerTest.class.getResource("/tests/legacy_serializer/test.cfg").toURI());
        Files.copy(resource, dst, StandardCopyOption.REPLACE_EXISTING);
    }
}
