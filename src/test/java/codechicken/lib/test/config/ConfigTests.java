package codechicken.lib.test.config;

import codechicken.lib.configuration.ConfigFile;
import codechicken.lib.configuration.ConfigTag;
import codechicken.lib.configuration.IConfigTag.TagType;
import codechicken.lib.data.MCByteStream;
import codechicken.lib.data.MCDataInputWrapper;
import codechicken.lib.util.ResourceUtils;
import com.google.common.collect.Lists;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.*;
import java.util.List;
import java.util.Objects;

/**
 * Created by covers1624 on 16/06/18.
 */

public class ConfigTests {

    @Test
    public void testReadWriteRead() throws Throwable {
        File dir = new File(".", "read_write_test").getAbsoluteFile();
        File before = new File(dir, "before.cfg");
        File after = new File(dir, "after.cfg");
        before.delete();
        after.delete();
        copyTestFile(before);
        ConfigFile beforeConfig = new ConfigFile(before);
        beforeConfig.write(after);
        ConfigFile afterConfig = new ConfigFile(after);
        ensureSame(beforeConfig, afterConfig);
    }

    @Test
    public void testGeneration() throws Throwable {
        File dir = new File(".", "generation_test").getAbsoluteFile();
        ConfigFile generated_config = new ConfigFile(new File(dir, "generated.cfg"));
        generated_config.setTagVersion("1.1");
        generated_config.setComment("This is a config comment.");

        ConfigTag tag = generated_config.getTag("Tag1").setComment("Specifies a new ConfigTag");
        tag.setTagVersion("1.2.3.4");
        ConfigTag bool = tag.getTag("boolean").setDefaultBoolean(false);
        ConfigTag string = tag.getTag("string").setDefaultString("This is a string with data, Cannot be Multi Line.");
        ConfigTag integer = tag.getTag("integer").setDefaultInt(123456789);
        ConfigTag doubl_e = tag.getTag("double").setDefaultDouble(1.2345);
        ConfigTag hex = tag.getTag("hex").setDefaultHex(0xFFFFFFFF);
        ConfigTag boolArray = tag.getTag("boolean_array").setDefaultBooleanList(Lists.newArrayList(true, false, true, false));
        ConfigTag stringArray = tag.getTag("string_array").setDefaultStringList(Lists.newArrayList("value", "value2", "value33"));
        ConfigTag intArray = tag.getTag("integer_array").setDefaultIntList(Lists.newArrayList(1, 2, 3, 4, 5, 6));
        ConfigTag doubleArray = tag.getTag("double_array").setDefaultDoubleList(Lists.newArrayList(1.2, 3.4, 5.6, 7.8));
        ConfigTag hexArray = tag.getTag("hex_array").setDefaultHexList(Lists.newArrayList(0xFFFF, 0x00FF));

        ConfigTag tag2 = generated_config.getTag("Tag2");
        ConfigTag tag3 = tag2.getTag("Tag3");
        ConfigTag tag4 = tag3.getTag("Tag4");
        ConfigTag depthTest = tag4.getTag("depth_test").setDefaultBoolean(true);
        generated_config.save();
        File staticFile = new File(dir, "static.cfg");
        staticFile.delete();
        copyTestFile(staticFile);
        ConfigFile staticConfig = new ConfigFile(staticFile);
        ensureSame(staticConfig, generated_config);
    }

    @Test
    public void testCopy() throws Throwable {
        File dir = new File(".", "copy_test").getAbsoluteFile();
        File testFile = new File(dir, "test.cfg");
        testFile.delete();
        copyTestFile(testFile);
        ConfigFile configA = new ConfigFile(testFile);
        ConfigTag configB = configA.copy();
        ensureSame(configA, configB);
    }

    @Test (expected = ConfigTestException.class)
    public void testCopyFail() throws Throwable {
        File dir = new File(".", "copy_test_fail").getAbsoluteFile();
        File testFile = new File(dir, "test.cfg");
        testFile.delete();
        copyTestFile(testFile);
        ConfigFile configA = new ConfigFile(testFile);
        ConfigTag configB = configA.copy();
        configB.setTagVersion("boop");
        ConfigTag tag1 = configB.getTag("Tag1");
        tag1.getTag("string").setString("nope");
        tag1.getTag("boolean_array").getBooleanList().clear();
        ensureSame(configA, configB);
    }

    @Test
    public void testCopyFrom() throws Throwable {
        File dir = new File(".", "copy_from_test").getAbsoluteFile();
        File testFile = new File(dir, "test.cfg");
        testFile.delete();
        copyTestFile(testFile);
        ConfigFile configA = new ConfigFile(testFile);
        ConfigTag configB = configA.copy();
        ConfigTag tag1 = configB.getTag("Tag1");
        tag1.getTag("string").setString("nope");
        tag1.getTag("boolean_array").getBooleanList().clear();
        configB.copyFrom(configA);
        ensureSame(configA, configB);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCopyFromFail() throws Throwable {
        File dir = new File(".", "copy_from_fail_test").getAbsoluteFile();
        File testFile = new File(dir, "test.cfg");
        testFile.delete();
        copyTestFile(testFile);
        ConfigFile configA = new ConfigFile(testFile);
        ConfigTag configB = configA.copy();
        configA.deleteTag("Tag1");
        configB.copyFrom(configA);
        ensureSame(configA, configB);
    }

    @Test
    public void testWriteReadStream() throws Throwable {
        File dir = new File(".", "write_read_stream_test").getAbsoluteFile();
        File testFile = new File(dir, "test.cfg");
        testFile.delete();
        copyTestFile(testFile);
        ConfigFile configA = new ConfigFile(testFile);
        ConfigTag configB = configA.copy();
        MCByteStream byteStream = new MCByteStream(new ByteArrayOutputStream());
        configA.write(byteStream);
        ConfigTag tag1 = configB.getTag("Tag1");
        tag1.getTag("string").setString("nope");
        tag1.getTag("boolean_array").getBooleanList().clear();
        configB.read(new MCDataInputWrapper(new ByteArrayInputStream(byteStream.getBytes())));
        ensureSame(configA, configB);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWriteReadStreamFail() throws Throwable {
        File dir = new File(".", "write_read_stream_test").getAbsoluteFile();
        File testFile = new File(dir, "test.cfg");
        testFile.delete();
        copyTestFile(testFile);
        ConfigFile configA = new ConfigFile(testFile);
        ConfigTag configB = configA.copy();
        MCByteStream byteStream = new MCByteStream(new ByteArrayOutputStream());
        configA.write(byteStream);
        configB.deleteTag("Tag1");
        configB.read(new MCDataInputWrapper(new ByteArrayInputStream(byteStream.getBytes())));
        ensureSame(configA, configB);
    }

    private static void copyTestFile(File dst) throws Throwable {
        InputStream in = ConfigTests.class.getResourceAsStream("/test.cfg");
        OutputStream out = new FileOutputStream(ResourceUtils.ensureExists(dst));
        IOUtils.copy(in, out);
        IOUtils.closeQuietly(in, out);
    }

    private static void ensureSame(ConfigTag a, ConfigTag b) throws ConfigTestException {
        String aName = a.getUnlocalizedName();
        String bName = b.getUnlocalizedName();
        if (a.isValue()) {
            if (!b.isValue()) {
                throw new ConfigTestException(String.format("Tag B '%s' is not a value like tag A '%s'.", bName, aName));
            }
            TagType aType = a.getType();
            if (aType != b.getType()) {
                throw new ConfigTestException(String.format("Tag B '%s' is not the same type as A '%s'. A: '%s', B: '%s'.", bName, aName, aType, b.getType()));
            }
            if (aType == TagType.LIST) {
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
