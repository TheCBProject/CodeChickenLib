package codechicken.lib.configv3;

import codechicken.lib.configv3.parser.ConfigSerializer;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.apache.commons.io.FilenameUtils.removeExtension;

/**
 * Created by covers1624 on 27/4/22.
 */
public final class ConfigFile {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigFile.class);

    private final String rootName;
    @Nullable
    private Path path;
    @Nullable
    private ConfigSerializer serializer;

    public ConfigFile(String rootName) {
        this.rootName = rootName;
    }

    public ConfigFile path(Path path) {
        this.path = path;
        serializer = pickForExtension(path);
        return this;
    }

    public ConfigFile serializer(ConfigSerializer serializer) {
        this.serializer = serializer;
        return this;
    }

    public ConfigCategory load() {
        if (path == null) throw new IllegalStateException("Path has not been set.");
        if (serializer == null) throw new IllegalStateException("Serializer was not automatically detected from file extension.");

        ConfigCategoryImpl rootTag = new ConfigCategoryImpl(rootName, null) {
            @Override
            public void save() {
                if (isDirty()) {
                    try {
                        serializer.save(path, this);
                    } catch (IOException ex) {
                        LOGGER.error("Failed to save config file! {}", path, ex);
                    }
                    clearDirty();
                }
            }
        };
        if (Files.exists(path)) {
            try {
                serializer.parse(path, rootTag);
            } catch (Throwable ex) {
                rootTag.clear();
                rootTag.clearDirty();
                Path backup = moveToBackup(path);
                LOGGER.warn("Failed to load config {} - Backing up to '{}' and generating a new one.", path, backup, ex);
            }
        }
        return rootTag;
    }

    @Nullable
    @VisibleForTesting
    static ConfigSerializer pickForExtension(Path path) {
        String ext = FilenameUtils.getExtension(path.getFileName().toString());
        return switch (ext) {
            case "cfg" -> ConfigSerializer.LEGACY;
            case "json", "toml" -> throw new NotImplementedException();
            default -> null;
        };
    }

    // ccl.cfg -> ccl.cfg.bak1 -> ... -> ccl.cfg.bak6(delete)
    @Nullable
    @VisibleForTesting
    static Path moveToBackup(Path path) {
        try {
            String fName = path.getFileName().toString();
            String ext = FilenameUtils.getExtension(fName);
            int curr = ext.startsWith("bak") ? Integer.parseInt(ext.substring(3)) : 0;
            int next = curr + 1;
            Path newFile = path.resolveSibling((ext.startsWith("bak") ? removeExtension(fName) : fName) + ".bak" + next);
            if (next == 6) { // Only keep 5 backups, delete if 5th file would be moved to 6th.
                Files.delete(path);
                return null;
            }
            if (Files.exists(newFile)) {
                moveToBackup(newFile);
            }
            Files.move(path, newFile);
            return newFile;
        } catch (IOException ex) {
            throw new RuntimeException("Failed to move config file.", ex);
        }
    }
}
