package codechicken.lib.config.parser;

import codechicken.lib.config.ConfigCategoryImpl;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Represents a serializer capable of reading the provided file into
 * a tag tree.
 * <p>
 * Created by covers1624 on 18/4/22.
 */
public interface ConfigSerializer {

    ConfigSerializer LEGACY = new LegacyConfigSerializer();

    void parse(Path file, ConfigCategoryImpl rootTag) throws IOException;

    void save(Path file, ConfigCategoryImpl rootTag) throws IOException;
}
