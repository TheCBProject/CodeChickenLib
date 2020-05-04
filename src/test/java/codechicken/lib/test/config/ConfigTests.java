package codechicken.lib.test.config;

import codechicken.lib.config.ConfigTag;
import codechicken.lib.config.StandardConfigFile;
import codechicken.lib.config.parser.ConfigFile;
import codechicken.lib.config.parser.StandardConfigSerializer;
import codechicken.lib.data.MCDataByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.Assert;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Created by covers1624 on 16/06/18.
 */

public class ConfigTests {

    @Test
    public void testReadWriteRead() throws Throwable {
        Path dir = Files.createTempDirectory("read_write_back_test");
        Path before = dir.resolve("before.cfg");
        Path after = dir.resolve("after.cfg");
        copyTestFile(before);
        ConfigFile beforeCFile = new StandardConfigFile(before);
        ConfigTag beforeConfig = beforeCFile.load();
        Assert.assertFalse(beforeCFile.didError());
        StandardConfigSerializer.INSTANCE.save(after, beforeConfig);
        ConfigTag afterConfig = new StandardConfigFile(after).load();
        ensureSame(beforeConfig, afterConfig);
    }

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
        ensureSame(staticConfig, generated_config);
    }

    @Test
    public void testCopy() throws Throwable {
        Path dir = Files.createTempDirectory("copy_test");
        Path testFile = dir.resolve("test.cfg");
        copyTestFile(testFile);
        ConfigTag configA = new StandardConfigFile(testFile).load();
        ConfigTag configB = configA.copy();
        ensureSame(configA, configB);
    }

    @Test (expected = ConfigTestException.class)
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
        ensureSame(configA, configB);
    }

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
        ensureSame(configA, configB);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testCopyFromFail() throws Throwable {
        Path dir = Files.createTempDirectory("copy_from_fail_test");
        Path testFile = dir.resolve("test.cfg");
        copyTestFile(testFile);
        ConfigTag configA = new StandardConfigFile(testFile).load();
        ConfigTag configB = configA.copy();
        configA.deleteTag("Tag1");
        configB.copyFrom(configA);
        ensureSame(configA, configB);
    }

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
        ensureSame(configA, configB);
    }

    @Test (expected = IllegalArgumentException.class)
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
        ensureSame(configA, configB);
    }

    private static void copyTestFile(Path dst) throws Throwable {
        Path resource = Paths.get(ConfigTests.class.getResource("/test.cfg").toURI());
        Files.copy(resource, dst, StandardCopyOption.REPLACE_EXISTING);
    }

    private static void ensureSame(ConfigTag a, ConfigTag b) throws ConfigTestException {
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
                ensureSame(a.getTag(child), b.getTag(child));
            }
        }
    }
}
