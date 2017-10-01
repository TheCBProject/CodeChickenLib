package codechicken.lib.test;

import codechicken.lib.configuration.ConfigFile;
import codechicken.lib.configuration.ConfigTag;
import codechicken.lib.configuration.IConfigTag.TagType;
import codechicken.lib.util.ResourceUtils;
import com.google.common.collect.Lists;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Objects;

/**
 * Created by covers1624 on 26/08/2017.
 */
@Mod (modid = "config_test", name = "Config Tests", version = "1.0", acceptableRemoteVersions = "*")
public class ConfigTests {

    private static final Logger logger = LogManager.getLogger("CCL Config Tests");

    @EventHandler
    @SuppressWarnings ("ResultOfMethodCallIgnored")
    public void preInit(FMLPreInitializationEvent event) {
        File configDir = new File(event.getModConfigurationDirectory(), "ccl_config_test");
        logger.info("Running config tests..");
        {
            logger.info("Running read -> write -> read comparison test.");
            runTest(() -> {
                File dir = new File(configDir, "read_back_comparison");
                File before = new File(dir, "before.cfg");
                File after = new File(dir, "after.cfg");
                before.delete();
                after.delete();
                {
                    ResourceUtils.tryCreateFile(before);
                    InputStream in = ConfigTests.class.getResourceAsStream("/assets/config_test/test.cfg");
                    OutputStream out = new FileOutputStream(before);
                    IOUtils.copy(in, out);
                    IOUtils.closeQuietly(in, out);
                }
                ConfigFile beforeConfig = new ConfigFile(before);
                beforeConfig.write(after);
                ConfigFile afterConfig = new ConfigFile(after);
                ensureSame(beforeConfig, afterConfig);
            });
            logger.info("Running config generation test.");
            runTest(() -> {
                File dir = new File(configDir, "generation");
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
                logger.info("Generation Passed.");
                logger.info("Testing against static file.");
                File staticFile = new File(dir, "static.cfg");
                staticFile.delete();
                {
                    ResourceUtils.tryCreateFile(staticFile);
                    InputStream in = ConfigTests.class.getResourceAsStream("/assets/config_test/test.cfg");
                    OutputStream out = new FileOutputStream(staticFile);
                    IOUtils.copy(in, out);
                    IOUtils.closeQuietly(in, out);
                }
                ConfigFile staticConfig = new ConfigFile(staticFile);
                ensureSame(staticConfig, generated_config);
            });
        }
    }

    private void runTest(IThrowingRunnable<Exception> runnable) {
        try {
            runnable.run();
            logger.info("Test Passed.");
        } catch (Exception e) {
            logger.info("Test failed!", e);
        }
    }

    public static void ensureSame(ConfigTag a, ConfigTag b) throws Exception {
        String aName = a.getUnlocalizedName();
        String bName = b.getUnlocalizedName();
        if (a.isValue()) {
            if (!b.isValue()) {
                throw new Exception(String.format("Tag B '%s' is not a value like tag A '%s'.", bName, aName));
            }
            TagType aType = a.getType();
            if (aType != b.getType()) {
                throw new Exception(String.format("Tag B '%s' is not the same type as A '%s'. A: '%s', B: '%s'.", bName, aName, aType, b.getType()));
            }
            if (aType == TagType.LIST) {
                if (a.getListType() != b.getListType()) {
                    throw new Exception(String.format("Tag B '%s' is not of the same list type as A '%s'. A: '%s', B: '%s'.", bName, aName, a.getListType(), b.getListType()));
                }
            }
            if (!a.getRawValue().equals(b.getRawValue())) {
                throw new Exception(String.format("Tab B '%s' does not have the same value as tag A '%s', A: '%s', B: '%s'.", bName, aName, a.getRawValue(), b.getRawValue()));
            }
        }
        if (a.isCategory()) {
            if (!b.isCategory()) {
                throw new Exception(String.format("Tag B '%s' is not a category like tag A '%s'.", bName, aName));
            }
            if (!Objects.equals(a.getTagVersion(), b.getTagVersion())) {
                throw new Exception(String.format("Tag B '%s' does not have the same version flag as Tag A '%s'. A: '%s', B: '%s'.", bName, aName, a.getTagVersion(), b.getTagVersion()));
            }
            List<String> aChildren = a.getChildNames();
            List<String> bChildren = b.getChildNames();
            for (String aChild : aChildren) {
                if (!bChildren.contains(aChild)) {
                    throw new Exception(String.format("Tag B '%s' does not have a child '%s' that Tag A '%s' does.", bName, aChild, aName));
                }
            }

            for (String bChild : bChildren) {
                if (!aChildren.contains(bChild)) {
                    throw new Exception(String.format("Tag A '%s' does not have a child '%s' that Tag B '%s' does.", aName, bChild, bName));
                }
            }

            for (String child : aChildren) {
                ensureSame(a.getTag(child), b.getTag(child));
            }
        }
    }

    public interface IThrowingRunnable<E extends Throwable> {

        void run() throws E;
    }
}
