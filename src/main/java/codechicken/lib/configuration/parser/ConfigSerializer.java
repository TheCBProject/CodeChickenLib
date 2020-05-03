package codechicken.lib.configuration.parser;

import codechicken.lib.configuration.ConfigTag;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Created by covers1624 on 5/2/20.
 */
public interface ConfigSerializer {

    ConfigTag parse(Path file, ConfigFile configFile) throws IOException;

    void save(Path file, ConfigTag tag) throws IOException;

}
