package codechicken.lib.config;

import codechicken.lib.config.parser.ConfigSerializer;
import org.apache.commons.lang3.NotImplementedException;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by covers1624 on 5/5/22.
 */
public class ConfigFileTests {

    @Test
    public void testPickForExtension() {
        assertEquals(ConfigSerializer.LEGACY, ConfigFile.pickForExtension(Path.of("test.cfg")));
        assertEquals(ConfigSerializer.JSON, ConfigFile.pickForExtension(Path.of("test.json")));
        assertNull(ConfigFile.pickForExtension(Path.of("test.tar.gz")));
        assertThrows(NotImplementedException.class, () -> ConfigFile.pickForExtension(Path.of("test.toml")));
    }

    @Test
    public void testMoveToBackup() throws IOException {
        Path tempDir = Files.createTempDirectory("config_tests");
        tempDir.toFile().deleteOnExit();

        Path config = tempDir.resolve("test.cfg");
        Path bak1 = tempDir.resolve("test.cfg.bak1");
        Path bak2 = tempDir.resolve("test.cfg.bak2");
        Path bak3 = tempDir.resolve("test.cfg.bak3");
        Path bak4 = tempDir.resolve("test.cfg.bak4");
        Path bak5 = tempDir.resolve("test.cfg.bak5");
        Path bak6 = tempDir.resolve("test.cfg.bak6");

        Files.writeString(config, "File 1");
        ConfigFile.moveToBackup(config);

        assertFalse(Files.exists(config));
        assertTrue(Files.exists(bak1));
        assertEquals("File 1", Files.readString(bak1));

        Files.writeString(config, "File 2");
        ConfigFile.moveToBackup(config);

        assertFalse(Files.exists(config));
        assertTrue(Files.exists(bak1));
        assertTrue(Files.exists(bak2));
        assertEquals("File 2", Files.readString(bak1));
        assertEquals("File 1", Files.readString(bak2));

        Files.writeString(config, "File 3");
        ConfigFile.moveToBackup(config);

        assertFalse(Files.exists(config));
        assertTrue(Files.exists(bak1));
        assertTrue(Files.exists(bak2));
        assertTrue(Files.exists(bak3));
        assertEquals("File 3", Files.readString(bak1));
        assertEquals("File 2", Files.readString(bak2));
        assertEquals("File 1", Files.readString(bak3));

        Files.writeString(config, "File 4");
        ConfigFile.moveToBackup(config);

        assertFalse(Files.exists(config));
        assertTrue(Files.exists(bak1));
        assertTrue(Files.exists(bak2));
        assertTrue(Files.exists(bak3));
        assertTrue(Files.exists(bak4));
        assertEquals("File 4", Files.readString(bak1));
        assertEquals("File 3", Files.readString(bak2));
        assertEquals("File 2", Files.readString(bak3));
        assertEquals("File 1", Files.readString(bak4));

        Files.writeString(config, "File 5");
        ConfigFile.moveToBackup(config);

        assertFalse(Files.exists(config));
        assertTrue(Files.exists(bak1));
        assertTrue(Files.exists(bak2));
        assertTrue(Files.exists(bak3));
        assertTrue(Files.exists(bak4));
        assertTrue(Files.exists(bak5));
        assertEquals("File 5", Files.readString(bak1));
        assertEquals("File 4", Files.readString(bak2));
        assertEquals("File 3", Files.readString(bak3));
        assertEquals("File 2", Files.readString(bak4));
        assertEquals("File 1", Files.readString(bak5));

        Files.writeString(config, "File 6");
        ConfigFile.moveToBackup(config);

        assertFalse(Files.exists(config));
        assertTrue(Files.exists(bak1));
        assertTrue(Files.exists(bak2));
        assertTrue(Files.exists(bak3));
        assertTrue(Files.exists(bak4));
        assertTrue(Files.exists(bak5));
        assertFalse(Files.exists(bak6));
        assertEquals("File 6", Files.readString(bak1));
        assertEquals("File 5", Files.readString(bak2));
        assertEquals("File 4", Files.readString(bak3));
        assertEquals("File 3", Files.readString(bak4));
        assertEquals("File 2", Files.readString(bak5));
    }

    @Test
    public void testCustomSerializer() throws IOException {
        Path file = Files.createTempFile("test", ".notcfg");
        file.toFile().deleteOnExit();
        boolean[] wasSaveCalled = { false };
        ConfigCategory category = new ConfigFile("rootTag")
                .path(file)
                .serializer(new ConfigSerializer() {
                    @Override
                    public void parse(Path file, ConfigCategoryImpl rootTag) throws IOException {
                        rootTag.getValue("value").setString("Test");
                    }

                    @Override
                    public void save(Path file, ConfigCategoryImpl tag) throws IOException {
                        assertEquals("Real test", tag.getValue("value").getString());
                        wasSaveCalled[0] = true;
                    }
                })
                .load();

        ConfigValue value = category.getValue("value");
        assertEquals("Test", value.getString());

        value.setString("Real test");
        assertTrue(value.isDirty());
        value.save();
        assertFalse(value.isDirty());
        assertTrue(wasSaveCalled[0]);
    }

    @Test
    public void testCustomSerializerLoadFail() throws IOException {
        Path dir = Files.createTempDirectory("config_test");
        dir.toFile().deleteOnExit();
        Path config = dir.resolve("test.notcfg");
        Path bak1 = dir.resolve("test.notcfg.bak1");

        Files.writeString(config, "Config");

        ConfigCategory category = new ConfigFile("rootTag")
                .path(config)
                .serializer(new ConfigSerializer() {
                    @Override
                    public void parse(Path file, ConfigCategoryImpl rootTag) throws IOException {
                        throw new IOException("Load failed");
                    }

                    @Override
                    public void save(Path file, ConfigCategoryImpl tag) throws IOException {
                    }
                })
                .load();

        assertFalse(Files.exists(config));
        assertTrue(Files.exists(bak1));
        assertEquals("Config", Files.readString(bak1));
    }
}
