package codechicken.lib.util;

import net.minecraft.client.resources.I18n;

@Deprecated
public class LangProxy {

    public final String namespace;

    public LangProxy(String namespace) {
        this.namespace = namespace + ".";
    }

    public String translate(String key) {
        return I18n.format(namespace + key);
    }

    public String format(String key, Object... params) {
        return I18n.format(namespace + key, params);
    }
}
