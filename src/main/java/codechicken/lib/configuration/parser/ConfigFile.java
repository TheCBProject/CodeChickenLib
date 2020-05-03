package codechicken.lib.configuration.parser;

import codechicken.lib.configuration.ConfigTag;

/**
 * Created by covers1624 on 5/3/20.
 */
public interface ConfigFile {

    ConfigTag deferLoad();

    ConfigTag load();

    void save(ConfigTag tag);

    boolean didError();

}
