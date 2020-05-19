package codechicken.lib.config.parser;

import codechicken.lib.config.ConfigTag;
import codechicken.lib.config.ConfigTagImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

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
                rootTag.clear();
                didError = true;
                Path backupFile = null;
                try {
                    backupFile = backupFile(file);
                } catch (IOException ioException) {
                    logger.warn("Failed to backup config file: ", e);
                }
                logger.error("Failed to load config '{}', Backing up config to '{}' and generating a fresh one.", file, backupFile, e);
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

    private static Path backupFile(Path file) throws IOException {
        String fName = file.getFileName().toString();
        int lastDot = fName.lastIndexOf(".");
        if (lastDot != -1) {
            String backupName = fName.substring(0, lastDot) + "-backup-" + TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
            Path backup = file.resolveSibling(backupName + fName.substring(lastDot));
            Files.move(file, backup);
            return backup;
        }
        return null;
    }
}
