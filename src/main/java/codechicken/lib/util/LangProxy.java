package codechicken.lib.util;

import net.minecraftforge.fml.common.registry.LanguageRegistry;

public class LangProxy {
    public final String namespace;

    public LangProxy(String namespace) {
        this.namespace = namespace + ".";
    }

    public String translate(String key) {
        return LanguageRegistry.instance().getStringLocalization(namespace + key);
    }

    public String format(String key, Object... params) {
        return String.format(LanguageRegistry.instance().getStringLocalization(namespace + key), params);
    }
}
