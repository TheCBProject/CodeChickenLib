package codechicken.lib.configuration.parser;

import codechicken.lib.configuration.ConfigTag;
import codechicken.lib.configuration.ConfigTagImpl;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Created by covers1624 on 5/2/20.
 */
public interface ConfigSerializer {

    void parse(Path file, ConfigTagImpl rootTag) throws IOException;

    void save(Path file, ConfigTag tag) throws IOException;

}
