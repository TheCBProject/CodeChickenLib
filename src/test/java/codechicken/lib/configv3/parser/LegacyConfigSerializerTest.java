package codechicken.lib.configv3.parser;

import codechicken.lib.configv3.ConfigCategoryImpl;
import codechicken.lib.configv3.ConfigFile;
import codechicken.lib.test.config.ConfigTests;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * Created by covers1624 on 21/4/22.
 */
public class LegacyConfigSerializerTest {

    @Test
    public void testParse() throws Throwable {
        Path file = Files.createTempFile("temp", ".cfg");
        file.toFile().deleteOnExit();
        copyTestFile(file);

        Path dest = Files.createTempFile("temp", ".cfg");
        dest.toFile().deleteOnExit();

        ConfigCategoryImpl rootTag = new ConfigCategoryImpl("rootTag", null);
        new LegacyConfigSerializer().parse(file, rootTag);
        assertNotEquals(0, rootTag.getChildren().size());

        new LegacyConfigSerializer().save(dest, rootTag);
        // TODO Re-parse dest and compare to rootTag.
    }

    @Test
    public void testThroughConfigFile() throws Throwable {
        Path file = Files.createTempFile("temp", ".cfg");
        file.toFile().deleteOnExit();
        copyTestFile(file);

        ConfigFile cFile = ConfigFile.builder("rootTag")
                .path(file)
                .load();

        assertNotEquals(0, cFile.getChildren().size());
    }

    private static void copyTestFile(Path dst) throws Throwable {
        Path resource = Paths.get(ConfigTests.class.getResource("/test.cfg").toURI());
        Files.copy(resource, dst, StandardCopyOption.REPLACE_EXISTING);
    }
}
