package codechicken.lib.test.config;

import codechicken.lib.config.ConfigTag;
import codechicken.lib.config.StandardConfigFile;
import codechicken.lib.config.parser.ConfigFile;
import codechicken.lib.config.parser.StandardConfigSerializer;
import codechicken.lib.data.MCDataByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Created by covers1624 on 16/06/18.
 */
@Disabled
public class ConfigTests {

    //Loads the config, saves it to a new file, loads it again, and compares the first loaded and the save-loaded.
    @Test
    public void testReadWriteRead() throws Throwable {
        Path dir = Files.createTempDirectory("read_write_back_test");
        Path before = dir.resolve("before.cfg");
        Path after = dir.resolve("after.cfg");
        copyTestFile(before);
        ConfigFile beforeCFile = new StandardConfigFile(before);
        ConfigTag beforeConfig = beforeCFile.load();
        assertFalse(beforeCFile.didError());
        StandardConfigSerializer.INSTANCE.save(after, beforeConfig);
        ConfigTag afterConfig = new StandardConfigFile(after).load();
        ensureSameRaw(beforeConfig, afterConfig);
    }

    //Should produce a config identical to our test file.
    @Test
    public void testGeneration() throws Throwable {
        Path dir = Files.createTempDirectory("generation_test");
        ConfigTag generated_config = new StandardConfigFile(dir.resolve("generated.cfg")).load();
        generated_config.setTagVersion("1.1");
        generated_config.setComment("This is a config comment.");

        ConfigTag tag = generated_config.getTag("Tag1").setComment("Specifies a new ConfigTag");
        tag.setTagVersion("1.2.3.4");
        ConfigTag bool = tag.getTag("boolean").setDefaultBoolean(false);
        ConfigTag string = tag.getTag("string").setDefaultString("This is a string with data, Cannot be Multi Line.");
        ConfigTag integer = tag.getTag("integer").setDefaultInt(123456789);
        ConfigTag doubl_e = tag.getTag("double").setDefaultDouble(1.2345);
        ConfigTag hex = tag.getTag("hex").setDefaultHex(0xFFFFFFFF);
        ConfigTag boolArray = tag.getTag("boolean_array").setDefaultBooleanList(Arrays.asList(true, false, true, false));
        ConfigTag stringArray = tag.getTag("string_array").setDefaultStringList(Arrays.asList("value", "value2", "value33"));
        ConfigTag intArray = tag.getTag("integer_array").setDefaultIntList(Arrays.asList(1, 2, 3, 4, 5, 6));
        ConfigTag doubleArray = tag.getTag("double_array").setDefaultDoubleList(Arrays.asList(1.2, 3.4, 5.6, 7.8));
        ConfigTag hexArray = tag.getTag("hex_array").setDefaultHexList(Arrays.asList(0xFFFF, 0x00FF));

        ConfigTag tag2 = generated_config.getTag("Tag2");
        ConfigTag tag3 = tag2.getTag("Tag3");
        ConfigTag tag4 = tag3.getTag("Tag4");
        ConfigTag depthTest = tag4.getTag("depth_test").setDefaultBoolean(true);
        generated_config.save();
        Path staticFile = dir.resolve("static.cfg");
        copyTestFile(staticFile);
        ConfigTag staticConfig = new StandardConfigFile(staticFile).load();
        ensureSameRaw(staticConfig, generated_config);
    }

    //Copes the config and ensures its the same.
    @Test
    public void testCopy() throws Throwable {
        Path dir = Files.createTempDirectory("copy_test");
        Path testFile = dir.resolve("test.cfg");
        copyTestFile(testFile);
        ConfigTag configA = new StandardConfigFile(testFile).load();
        ConfigTag configB = configA.copy();
        ensureSameRaw(configA, configB);
    }

    //Copies the config, modifies it then ensures they are different.
    @Test
    public void testCopyFail() throws Throwable {
        Path dir = Files.createTempDirectory("copy_test_fail");
        Path testFile = dir.resolve("test.cfg");
        copyTestFile(testFile);
        ConfigTag configA = new StandardConfigFile(testFile).load();
        ConfigTag configB = configA.copy();
        configB.setTagVersion("boop");
        ConfigTag tag1 = configB.getTag("Tag1");
        tag1.getTag("string").setString("nope");
        tag1.getTag("boolean_array").getBooleanList().clear();
        assertThrows(ConfigTestException.class, () -> ensureSameRaw(configA, configB));
    }

    //Copies the config, modifies it, then 'resets' it with copyFrom, ensures they are the same.
    @Test
    public void testCopyFrom() throws Throwable {
        Path dir = Files.createTempDirectory("copy_from_test");
        Path testFile = dir.resolve("test.cfg");
        copyTestFile(testFile);
        ConfigTag configA = new StandardConfigFile(testFile).load();
        ConfigTag configB = configA.copy();
        ConfigTag tag1 = configB.getTag("Tag1");
        tag1.getTag("string").setString("nope");
        tag1.getTag("boolean_array").getBooleanList().clear();
        configB.copyFrom(configA);
        ensureSameRaw(configA, configB);
    }

    //Copies the config, deletes a tag from it, copyFrom should fail.
    @Test
    public void testCopyFromFail() throws Throwable {
        Path dir = Files.createTempDirectory("copy_from_fail_test");
        Path testFile = dir.resolve("test.cfg");
        copyTestFile(testFile);
        ConfigTag configA = new StandardConfigFile(testFile).load();
        ConfigTag configB = configA.copy();
        configA.deleteTag("Tag1");
        configB.copyFrom(configA);
        assertThrows(ConfigTestException.class, () -> ensureSameRaw(configA, configB));
    }

    //Copes the config, writes the original to the stream, modifies the copy, reads stream to copy, ensures they are the same.
    @Test
    public void testWriteReadStream() throws Throwable {
        Path dir = Files.createTempDirectory("write_read_stream_test");
        Path testFile = dir.resolve("test.cfg");
        copyTestFile(testFile);
        ConfigTag configA = new StandardConfigFile(testFile).load();
        ConfigTag configB = configA.copy();
        MCDataByteBuf byteStream = new MCDataByteBuf(Unpooled.buffer());
        configA.write(byteStream);
        ConfigTag tag1 = configB.getTag("Tag1");
        tag1.getTag("string").setString("nope");
        tag1.getTag("boolean_array").getBooleanList().clear();
        configB.read(byteStream);
        ensureSameRaw(configA, configB);
    }

    //Copes the config, writes the original to the stream, deletes a tag from the copy, read should fail.
    @Test
    public void testWriteReadStreamFail() throws Throwable {
        Path dir = Files.createTempDirectory("write_read_stream_test_fail");
        Path testFile = dir.resolve("test.cfg");
        copyTestFile(testFile);
        ConfigTag configA = new StandardConfigFile(testFile).load();
        ConfigTag configB = configA.copy();
        MCDataByteBuf byteStream = new MCDataByteBuf(Unpooled.buffer());
        configA.write(byteStream);
        configB.deleteTag("Tag1");
        configB.read(byteStream);
        assertThrows(ConfigTestException.class, () -> ensureSameRaw(configA, configB));
    }

    //Copes the config, modifies it, writes it to a stream, reads with readNetwork, ensures they are 'effectively' the same.
    @Test
    public void testReadNetwork() throws Throwable {
        Path dir = Files.createTempDirectory("read_network");

        Path testFile = dir.resolve("test.cfg");
        copyTestFile(testFile);
        ConfigTag testConfig = new StandardConfigFile(testFile).load();
        ConfigTag testClone = testConfig.copy();
        testClone.getTag("Tag1").getTag("integer").setInt(333333);
        testClone.getTag("Tag1").getTag("string").setString("MAGIIIIC");
        MCDataByteBuf buf = new MCDataByteBuf(Unpooled.buffer());
        testClone.write(buf);
        testConfig.readNetwork(buf);
        ensureSameSynced(testConfig, testClone);
    }

    //Copes the config, modifies it, writes to stream, reads from stream with 'readNetwork', ensures effectively the same, calls 'netowrkRestore'
    // ensures the config is the same as the unmodified version.
    @Test
    public void testReadNetworkRevert() throws Throwable {
        Path dir = Files.createTempDirectory("read_network");

        Path testFile = dir.resolve("test.cfg");
        copyTestFile(testFile);
        ConfigTag testConfig = new StandardConfigFile(testFile).load();
        ConfigTag baseConfig = testConfig.copy();
        ConfigTag testClone = testConfig.copy();
        testClone.getTag("Tag1").getTag("integer").setInt(333333);
        testClone.getTag("Tag1").getTag("string").setString("MAGIIIIC");
        MCDataByteBuf buf = new MCDataByteBuf(Unpooled.buffer());
        testClone.write(buf);
        testConfig.readNetwork(buf);
        ensureSameSynced(testConfig, testClone);
        testConfig.networkRestore();
        ensureSameSynced(testConfig, baseConfig);
    }

    //Copes the config, modifies it, writes it to a stream, reads using 'readNetwork', saves the file back, loads it and enures the same as original.
    @Test
    public void testReadNetworkWriteFile() throws Throwable {
        Path dir = Files.createTempDirectory("read_stream_network_read");

        Path testFile = dir.resolve("test.cfg");
        copyTestFile(testFile);
        ConfigTag testConfig = new StandardConfigFile(testFile).load();
        ConfigTag baseConfig = testConfig.copy();
        ConfigTag testClone = testConfig.copy();
        testClone.getTag("Tag1").getTag("integer").setInt(333333);
        testClone.getTag("Tag1").getTag("string").setString("MAGIIIIC");
        MCDataByteBuf buf = new MCDataByteBuf(Unpooled.buffer());
        testClone.write(buf);
        testConfig.readNetwork(buf);
        testConfig.save();
        ConfigTag loaded = new StandardConfigFile(testFile).load();
        ensureSameRaw(loaded, baseConfig);
    }

    private static void copyTestFile(Path dst) throws Throwable {
        Path resource = Paths.get(ConfigTests.class.getResource("/test.cfg").toURI());
        Files.copy(resource, dst, StandardCopyOption.REPLACE_EXISTING);
    }

    private static void ensureSameRaw(ConfigTag a, ConfigTag b) throws ConfigTestException {
        String aName = a.getQualifiedName();
        String bName = b.getQualifiedName();
        if (a.isValue()) {
            if (!b.isValue()) {
                throw new ConfigTestException(String.format("Tag B '%s' is not a value like tag A '%s'.", bName, aName));
            }
            ConfigTag.TagType aType = a.getTagType();
            if (aType != b.getTagType()) {
                throw new ConfigTestException(String.format("Tag B '%s' is not the same type as A '%s'. A: '%s', B: '%s'.", bName, aName, aType, b.getTagType()));
            }
            if (aType == ConfigTag.TagType.LIST) {
                if (a.getListType() != b.getListType()) {
                    throw new ConfigTestException(String.format("Tag B '%s' is not of the same list type as A '%s'. A: '%s', B: '%s'.", bName, aName, a.getListType(), b.getListType()));
                }
            }
            if (!a.getRawValue().equals(b.getRawValue())) {
                throw new ConfigTestException(String.format("Tab B '%s' does not have the same value as tag A '%s', A: '%s', B: '%s'.", bName, aName, a.getRawValue(), b.getRawValue()));
            }
        }
        if (a.isCategory()) {
            if (!b.isCategory()) {
                throw new ConfigTestException(String.format("Tag B '%s' is not a category like tag A '%s'.", bName, aName));
            }
            if (!Objects.equals(a.getTagVersion(), b.getTagVersion())) {
                throw new ConfigTestException(String.format("Tag B '%s' does not have the same version flag as Tag A '%s'. A: '%s', B: '%s'.", bName, aName, a.getTagVersion(), b.getTagVersion()));
            }
            List<String> aChildren = a.getChildNames();
            List<String> bChildren = b.getChildNames();
            for (String aChild : aChildren) {
                if (!bChildren.contains(aChild)) {
                    throw new ConfigTestException(String.format("Tag B '%s' does not have a child '%s' that Tag A '%s' does.", bName, aChild, aName));
                }
            }

            for (String bChild : bChildren) {
                if (!aChildren.contains(bChild)) {
                    throw new ConfigTestException(String.format("Tag A '%s' does not have a child '%s' that Tag B '%s' does.", aName, bChild, bName));
                }
            }

            for (String child : aChildren) {
                ensureSameRaw(a.getTag(child), b.getTag(child));
            }
        }
    }

    private static void ensureSameSynced(ConfigTag a, ConfigTag b) throws ConfigTestException {
        String aName = a.getQualifiedName();
        String bName = b.getQualifiedName();
        if (a.isValue()) {
            if (!b.isValue()) {
                throw new ConfigTestException(String.format("Tag B '%s' is not a value like tag A '%s'.", bName, aName));
            }
            ConfigTag.TagType aType = a.getTagType();
            if (aType != b.getTagType()) {
                throw new ConfigTestException(String.format("Tag B '%s' is not the same type as A '%s'. A: '%s', B: '%s'.", bName, aName, aType, b.getTagType()));
            }
            if (aType == ConfigTag.TagType.LIST) {
                if (a.getListType() != b.getListType()) {
                    throw new ConfigTestException(String.format("Tag B '%s' is not of the same list type as A '%s'. A: '%s', B: '%s'.", bName, aName, a.getListType(), b.getListType()));
                }
            }
            if (!a.getSyncedValue().equals(b.getSyncedValue())) {
                throw new ConfigTestException(String.format("Tab B '%s' does not have the same value as tag A '%s', A: '%s', B: '%s'.", bName, aName, a.getRawValue(), b.getRawValue()));
            }
        }
        if (a.isCategory()) {
            if (!b.isCategory()) {
                throw new ConfigTestException(String.format("Tag B '%s' is not a category like tag A '%s'.", bName, aName));
            }
            if (!Objects.equals(a.getTagVersion(), b.getTagVersion())) {
                throw new ConfigTestException(String.format("Tag B '%s' does not have the same version flag as Tag A '%s'. A: '%s', B: '%s'.", bName, aName, a.getTagVersion(), b.getTagVersion()));
            }
            List<String> aChildren = a.getChildNames();
            List<String> bChildren = b.getChildNames();
            for (String aChild : aChildren) {
                if (!bChildren.contains(aChild)) {
                    throw new ConfigTestException(String.format("Tag B '%s' does not have a child '%s' that Tag A '%s' does.", bName, aChild, aName));
                }
            }

            for (String bChild : bChildren) {
                if (!aChildren.contains(bChild)) {
                    throw new ConfigTestException(String.format("Tag A '%s' does not have a child '%s' that Tag B '%s' does.", aName, bChild, bName));
                }
            }

            for (String child : aChildren) {
                ensureSameSynced(a.getTag(child), b.getTag(child));
            }
        }
    }
}
