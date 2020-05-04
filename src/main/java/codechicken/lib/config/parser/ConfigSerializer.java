package codechicken.lib.config.parser;

import codechicken.lib.config.ConfigTag;
import codechicken.lib.config.ConfigTagImpl;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Created by covers1624 on 5/2/20.
 */
public interface ConfigSerializer {

    void parse(Path file, ConfigTagImpl rootTag) throws IOException;

    void save(Path file, ConfigTag tag) throws IOException;

}
