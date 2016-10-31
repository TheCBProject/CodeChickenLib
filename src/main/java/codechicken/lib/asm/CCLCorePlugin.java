package codechicken.lib.asm;

import net.minecraftforge.fml.relauncher.IFMLCallHook;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.TransformerExclusions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

@TransformerExclusions({ "codechicken.lib.asm", "codechicken.lib.config" })
@Deprecated//To be fully phased out by 1.11. Kept to keep backwards compat.
public class CCLCorePlugin implements IFMLLoadingPlugin , IFMLCallHook{

    public static Logger logger = LogManager.getLogger("CodeChicken Lib");

    @Override
    public String[] getASMTransformerClass() {
        return new String[] { "codechicken.lib.asm.ClassHeirachyManager" };
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Override
    public String getSetupClass() {
        return getClass().getName();
    }

    @Override
    public void injectData(Map<String, Object> data) {

    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }

    @Override
    public Void call() throws Exception {
        new ASMHelper();
        return null;
    }
}
