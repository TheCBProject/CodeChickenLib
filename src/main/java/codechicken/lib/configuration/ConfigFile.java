package codechicken.lib.configuration;

import codechicken.lib.util.ResourceUtils;
import org.apache.commons.io.IOUtils;

import java.io.*;

public class ConfigFile extends ConfigTag {

    protected File file;
    protected boolean isLoading;

    /**
     * A standard config file!
     *
     * @param file The file.
     */
    public ConfigFile(File file) {
        this(file, true);
    }

    /**
     * A special config file.
     *
     * @param file The file.
     * @param load If load should be called immediately.
     */
    public ConfigFile(File file, boolean load) {
        this(file.getName(), file, load);
    }

    /**
     * A special config file.
     *
     * @param prefix The prefix used for unlocalized names.
     * @param file   The file.
     * @param load   If load should be called immediately.
     */
    public ConfigFile(String prefix, File file, boolean load) {
        super(prefix, null);
        this.file = file;
        if (load) {
            load();
        }
    }

    /**
     * Loads the config from disk.
     * This WILL override the state in the config.
     * Make sure this is only called at load time.
     */
    public void load() {
        ResourceUtils.tryCreateFile(file);
        isLoading = true;
        clear();
        ConfigReader reader = null;
        try {
            reader = new ConfigReader(new FileReader(file));
            parseTag(reader);
        } catch (IOException e) {
            throw new ConfigException(e);
        } finally {
            IOUtils.closeQuietly(reader);
            isLoading = false;
        }
    }

    /**
     * Called to write the config to a file.
     * This is internal.
     *
     * @param file The file to write to.
     */
    public void write(File file) {

        PrintWriter writer = null;
        try {
            writer = new PrintWriter(new FileOutputStream(file));
            for (String comment : comment) {
                writeLine(writer, 0, "#" + comment);
            }
            writeTag(writer, 0);
        } catch (IOException e) {
            throw new ConfigException(e);
        } finally {
            IOUtils.closeQuietly(writer);
        }

    }

    @Override
    public void save() {
        if (isDirty()) {
            write(file);
            onSave();
        }
    }

    protected class ConfigReader extends BufferedReader {

        private int line;

        public ConfigReader(Reader in, int sz) {
            super(in, sz);
        }

        public ConfigReader(Reader in) {
            super(in);
        }

        @Override
        public String readLine() throws IOException {
            line++;
            return super.readLine();
        }

        public int getCurrLine() {
            return line;
        }
    }

    public class ConfigException extends RuntimeException {

        public ConfigException() {
            super();
        }

        public ConfigException(String message) {
            super(message);
        }

        public ConfigException(Throwable throwable) {
            super(throwable);
        }

        public ConfigException(String message, Throwable throwable) {
            super(message, throwable);
        }
    }
}
