package codechicken.lib.configuration.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

/**
 * Created by covers1624 on 5/2/20.
 */
public class ConfigReader extends BufferedReader {

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
