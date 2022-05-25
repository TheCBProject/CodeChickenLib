package codechicken.lib.config.parser;

import codechicken.lib.config.*;
import it.unimi.dsi.fastutil.chars.Char2ObjectArrayMap;
import it.unimi.dsi.fastutil.chars.Char2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2CharArrayMap;
import it.unimi.dsi.fastutil.objects.Object2CharMap;
import net.covers1624.quack.io.IOUtils;
import net.minecraft.Util;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * {@link ConfigSerializer} for the legacy ConfigV2 format.
 * <p>
 * Created by covers1624 on 18/4/22.
 */
public class LegacyConfigSerializer implements ConfigSerializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(LegacyConfigSerializer.class);

    private static final Pattern QUOTE_PATTERN = Pattern.compile("(?<=\")(.*)(?=\")");
    private static final Pattern STRING_MATCHER = Pattern.compile("(?<=.:\")(.*)(\"=\")(.*)(?=\")");

    private static final Char2ObjectMap<ValueType> valueTypeLookup = Util.make(new Char2ObjectArrayMap<>(), e -> {
        e.put('B', ValueType.BOOLEAN);
        e.put('S', ValueType.STRING);
        e.put('I', ValueType.INT);
        e.put('L', ValueType.LONG);
        e.put('H', ValueType.HEX);
        e.put('D', ValueType.DOUBLE);
    });
    private static final Object2CharMap<ValueType> charLookup = Util.make(new Object2CharArrayMap<>(), e -> {
        for (Char2ObjectMap.Entry<ValueType> entry : valueTypeLookup.char2ObjectEntrySet()) {
            e.put(entry.getValue(), entry.getCharKey());
        }
    });

    @Override
    public void parse(Path file, ConfigCategoryImpl rootTag) throws IOException {
        try (ConfigReader reader = new ConfigReader(Files.newBufferedReader(file))) {
            parse(rootTag, reader);
        }
    }

    private void parse(ConfigCategoryImpl category, ConfigReader reader) throws IOException {
        while (true) {
            String line = readLine(reader);
            if (line == null) {
                break;
            }
            // Ignore comments and empty lines.
            if (line.isEmpty() || line.startsWith("//") || line.startsWith("#")) {
                continue;
            }
            // Check if we have reached the end of our tag.
            if (line.startsWith("}")) {
                break;
            }
            if (line.startsWith("~")) {
                category.getValue("~version").setString(line.substring(1));
                continue;
            }
            // Check if we are the start of a new tag.
            if (line.startsWith("\"") && line.endsWith("{")) {
                Matcher matcher = QUOTE_PATTERN.matcher(line);
                if (!matcher.find()) {
                    throw new ConfigParseException("Malformed line! @%s, %s", reader.line, line);
                }
                String name = matcher.group();
                parse(category.getCategory(name), reader);
                continue;
            }
            // Actual value parsing!
            boolean isList = line.endsWith("<");
            char first = line.charAt(0);
            ValueType type = valueTypeLookup.get(first);
            if (type == null) {
                throw new ConfigParseException("Invalid value type %s, @Line:%s, '%s'", first, reader.line, line);
            }
            // If we are a string we need a custom matcher.
            Matcher matcher = (type != ValueType.STRING || isList ? QUOTE_PATTERN : STRING_MATCHER).matcher(line);
            if (!matcher.find()) {
                throw new ConfigParseException("Malformed line! @%s, '%s'", reader.line, line);
            }
            // Get the name of the tag.
            String name = matcher.group(1);
            if (!isList) {
                ConfigValueImpl tag = category.getValue(name);
                tag.setKnownType(type);

                // Get the value.
                String value;
                if (type == ValueType.STRING) {
                    value = matcher.group(3);
                } else {
                    int equals = line.indexOf('=');
                    if (equals == -1) {
                        throw new ConfigParseException("Malformed line! @%s, '%s', Expected equals sign.", reader.line, line);
                    }
                    value = line.substring(equals + 1);
                }
                tag.setValue(value);
            } else {
                ConfigValueListImpl tag = category.getValueList(name);
                tag.setKnownType(type);

                List<String> list = new LinkedList<>();
                // We got this far, must be a list!
                while (true) {
                    String listLine = readLine(reader);
                    if (listLine == null) {
                        throw new EOFException("End of line reached whilst parsing list?");
                    }
                    if (listLine.startsWith(">")) {
                        break;
                    }
                    list.add(listLine);
                }

                tag.setValue(list);
            }
        }
    }

    @Nullable
    protected static String readLine(BufferedReader reader) throws IOException {
        String line = reader.readLine();
        return line == null ? null : line.trim();
    }

    @Override
    public void save(Path file, ConfigCategoryImpl tag) throws IOException {
        try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(IOUtils.makeParents(file)))) {
            writeTag(tag, writer, -1);
        }
    }

    private void writeTag(ConfigTag tag, PrintWriter writer, int depth) {
        if (tag instanceof ConfigCategoryImpl cat) {
            boolean isRootTag = depth == -1;
            if (!isRootTag) {
                writeLine(writer, depth, "\"%s\" {", tag.getName());
            }
            depth++;
            boolean first = true;
            for (ConfigTag child : cat.getChildren()) {
                if (child.isNetworkTag()) continue;

                if (!first) {
                    writer.println();
                }

                List<String> comment = child.getComment();
                if (!comment.isEmpty()) {
                    for (String line : comment) {
                        writeLine(writer, depth, "# " + line);
                    }
                }
                first = false;
                writeTag(child, writer, depth);
            }
            if (!isRootTag) {
                writeLine(writer, depth - 1, "}");
            }
        } else if (tag instanceof ConfigValueImpl val) {
            Object value = val.getRawValue();
            if (val.getType() == ValueType.STRING) {
                value = "\"" + value + "\"";
            }
            writeLine(writer, depth, "%s:\"%s\"=%s", charLookup.getChar(val.getType()), val.getName(), toString(value, val.getType()));
        } else if (tag instanceof ConfigValueListImpl list) {
            List<?> lst = list.getRawValue();
            writeLine(writer, depth, "%s:\"%s\" <", charLookup.getChar(list.getType()), list.getName());
            for (Object e : lst) {
                writeLine(writer, depth + 1, "%s", toString(e, list.getType()));
            }
            writeLine(writer, depth, ">");
        } else {
            throw new IllegalStateException("Unknown tag type: " + tag.getClass().getName());
        }
    }

    private static String toString(Object val, ValueType type) {
        if (type == ValueType.HEX) {
            return "0x" + (Long.toString(((long) (Integer) val) << 32 >>> 32, 16)).toUpperCase();
        }
        return val.toString();
    }

    private static void writeLine(PrintWriter writer, int tabs, String line, Object... data) {
        for (int i = 0; i < tabs; i++) {
            writer.print('\t');
        }
        writer.println(String.format(line, data));
    }

    private static class ConfigReader extends BufferedReader {

        public int line;

        public ConfigReader(Reader in) {
            super(in);
        }

        @Override
        public String readLine() throws IOException {
            line++;
            return super.readLine();
        }
    }
}
