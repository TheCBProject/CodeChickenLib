package codechicken.lib.internal.proxy;

import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

/**
 * Created by covers1624 on 23/11/2016.
 */
public class Proxy {

    public void preInit() {
    }

    public void init() {
    }

    public void postInit() {
    }

    public void loadConfig() {
    }

    public void serverStarting(FMLServerStartingEvent event) {
        //if (!ObfMapping.obfuscated) {
        //    event.registerServerCommand(new DevEnvCommand());
        //}
    }

    public boolean isClient() {
        return false;
    }

}
