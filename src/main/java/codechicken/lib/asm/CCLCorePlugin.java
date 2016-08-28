package codechicken.lib.asm;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.TransformerExclusions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

@TransformerExclusions({ "codechicken.lib.asm", "codechicken.lib.config" })
public class CCLCorePlugin implements IFMLLoadingPlugin {

    public static Logger logger = LogManager.getLogger("CodeChicken Lib");

    @Override
    public String[] getASMTransformerClass() {
        return new String[] { "codechicken.lib.asm.ClassHeirachyManager", "codechicken.lib.asm.RenderHookTransformer" };
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {

    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}
