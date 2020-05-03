package codechicken.lib.configuration.parser;

import codechicken.lib.configuration.ConfigTag;
import codechicken.lib.configuration.ConfigTagImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Created by covers1624 on 5/3/20.
 */
public class AbstractConfigFile implements ConfigFile {

    private static final Logger logger = LogManager.getLogger();

    private final Path file;
    private final ConfigSerializer serializer;
    private final ConfigTagImpl rootTag;
    private boolean didError = false;

    protected AbstractConfigFile(Path file, ConfigSerializer serializer) {
        this.file = file;
        this.serializer = serializer;
        rootTag = new ConfigTagImpl(this, file.getFileName().toString(), null);
    }

    @Override
    public ConfigTag deferLoad() {
        return rootTag;
    }

    @Override
    public ConfigTag load() {
        if (Files.exists(file)) {
            try {
                serializer.parse(file, rootTag);
                return rootTag;
            } catch (IOException e) {
                didError = true;
                logger.error("Failed to load config '{}', creating default.", file, e);
            }
        }
        return rootTag;
    }

    @Override
    public void save(ConfigTag tag) {
        try {
            serializer.save(file, tag);
        } catch (IOException e) {
            logger.error("Unable to save config file '{}'.", file, e);
        }
    }

    @Override
    public boolean didError() {
        return didError;
    }
}
