package codechicken.lib.configv3.parser;

import codechicken.lib.configv3.ConfigCategoryImpl;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Created by covers1624 on 18/4/22.
 */
public interface ConfigSerializer {

    void parse(Path file, ConfigCategoryImpl rootTag) throws IOException;

    void save(Path file, ConfigCategoryImpl tag) throws IOException;
}
