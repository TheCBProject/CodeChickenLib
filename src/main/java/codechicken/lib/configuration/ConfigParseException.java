package codechicken.lib.configuration;

import java.io.IOException;

/**
 * Created by covers1624 on 18/07/2017.
 */
public class ConfigParseException extends IOException {

    public ConfigParseException(String message) {
        super(message);
    }

    public ConfigParseException(String format, Object... data) {
        this(String.format(format, data));
    }

    public ConfigParseException(Throwable cause, String message) {
        super(message, cause);
    }

    public ConfigParseException(Throwable cause) {
        super(cause);
    }

}
