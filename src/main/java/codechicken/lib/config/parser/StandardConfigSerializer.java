package codechicken.lib.config.parser;

import codechicken.lib.config.ConfigTag;
import codechicken.lib.config.ConfigTagImpl;
import codechicken.lib.util.SneakyUtils;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by covers1624 on 5/2/20.
 */
public class StandardConfigSerializer implements ConfigSerializer {

    private static final Logger logger = LogManager.getLogger();

    public static final StandardConfigSerializer INSTANCE = new StandardConfigSerializer();

    private static final Pattern QUOTE_PATTERN = Pattern.compile("(?<=\")(.*)(?=\")");
    private static final Pattern STRING_MATCHER = Pattern.compile("(?<=.:\")(.*)(\"=\")(.*)(?=\")");

    private static final Set<String> validTrue = Sets.newHashSet("true", "yes", "1");
    private static final Set<String> validFalse = Sets.newHashSet("false", "no", "0");

    @Override
    public void parse(Path file, ConfigTagImpl rootTag) throws IOException {
        try (ConfigReader reader = new ConfigReader(Files.newBufferedReader(file))) {
            parse(rootTag, reader);
        }
    }

    @SuppressWarnings ({ "unchecked", "rawtypes" })
    private void parse(ConfigTagImpl thisTag, ConfigReader reader) throws IOException {
        while (true) {
            String line = readLine(reader);
            if (line == null) {
                break;
            }
            //Ignore comments and empty lines.
            if (line.isEmpty() || line.startsWith("//") || line.startsWith("#")) {
                continue;
            }
            //Check if we have reached the end of our tag.
            if (line.startsWith("}")) {
                break;
            }
            if (line.startsWith("~")) {
                thisTag.version = line.substring(1);
                continue;
            }
            //Check if we are the start of a new tag.
            if (line.startsWith("\"") && line.endsWith("{")) {
                Matcher matcher = QUOTE_PATTERN.matcher(line);
                if (!matcher.find()) {
                    throw new ConfigParseException("Malformed line! @%s, %s", reader.getCurrLine(), line);
                }
                String name = matcher.group();
                ConfigTagImpl tag = thisTag.getTag(name);
                parse(tag, reader);
                continue;
            }
            //Actual value parsing!
            boolean isList = line.endsWith("<");
            char first = line.charAt(0);
            ConfigTag.TagType type = ConfigTag.TagType.fromChar(first);
            if (type == null) {
                throw new ConfigParseException("Invalid value type %s, @Line:%s, '%s'", first, reader.getCurrLine(), line);
            }
            //If we are a string we need a custom matcher.
            Matcher matcher = (type != ConfigTag.TagType.STRING || isList ? QUOTE_PATTERN : STRING_MATCHER).matcher(line);
            if (!matcher.find()) {
                throw new ConfigParseException("Malformed line! @%s, '%s'", reader.getCurrLine(), line);
            }
            //Get the name of the tag.
            String name = matcher.group(1);
            ConfigTagImpl tag = thisTag.getTag(name);
            if (!isList) {
                //Get the value.
                String value;
                if (type == ConfigTag.TagType.STRING) {
                    value = matcher.group(3);
                } else {
                    int equals = line.indexOf('=');
                    if (equals == -1) {
                        throw new ConfigParseException("Malformed line! @%s, '%s', Expected equals sign.", reader.getCurrLine(), line);
                    }
                    value = line.substring(equals + 1);
                }
                switch (type) {
                    //Parse the types.
                    case BOOLEAN: {
                        tag.setBoolean(parseBoolean(value, reader.getCurrLine()));
                        continue;
                    }
                    case STRING: {
                        tag.setString(value);
                        continue;
                    }
                    case INT: {
                        try {
                            tag.setInt(Integer.parseInt(value));
                        } catch (NumberFormatException e) {
                            throw new ConfigParseException(e, "Malformed line!, @%s, '%s'", reader.getCurrLine(), line);
                        }
                        continue;
                    }
                    case HEX: {
                        try {
                            tag.setHex((int) Long.parseLong(value.replace("0x", ""), 16));
                        } catch (NumberFormatException e) {
                            throw new ConfigParseException(e, "Malformed line!, @%s, '%s'", reader.getCurrLine(), line);
                        }
                        continue;
                    }
                    case DOUBLE: {
                        try {
                            tag.setDouble(Double.parseDouble(value));
                        } catch (NumberFormatException e) {
                            throw new ConfigParseException(e, "Malformed line!, @%s, '%s'", reader.getCurrLine(), line);
                        }
                        continue;
                    }
                }
            }

            List list = new LinkedList<>();
            //We got this far, must be a list!
            while (true) {
                String listLine = readLine(reader);
                if (listLine == null) {
                    throw new EOFException("End of line reached whilst parsing list?");
                }
                if (listLine.isEmpty() || line.startsWith("//") || line.startsWith("#")) {
                    continue;
                }
                if (listLine.startsWith(">")) {
                    break;
                }
                switch (type) {
                    case BOOLEAN: {
                        list.add(parseBoolean(listLine, reader.getCurrLine()));
                        break;
                    }
                    case STRING: {
                        list.add(listLine);
                        break;
                    }
                    case INT: {
                        list.add(Integer.parseInt(listLine));
                        break;
                    }
                    case HEX: {
                        list.add((int) Long.parseLong(listLine.replace("0x", ""), 16));
                        break;
                    }
                    case DOUBLE: {
                        list.add(Double.parseDouble(listLine));
                        break;
                    }
                    default: {
                        //This should absolutely never happen ever.
                        throw new ConfigParseException("Invalid type state at list parsing?? %s", line);
                    }
                }
            }
            switch (type) {
                case BOOLEAN:
                    tag.setBooleanList(list);
                    break;
                case STRING:
                    tag.setStringList(list);
                    break;
                case INT:
                    tag.setIntList(list);
                    break;
                case HEX: {
                    tag.setHexList(list);
                    break;
                }
                case DOUBLE:
                    tag.setDoubleList(list);
                    break;
            }
        }
    }

    private static boolean parseBoolean(String value, int line) throws IOException {
        Boolean bool = null;
        if (validTrue.contains(value.toLowerCase(Locale.US))) {
            bool = true;
        } else if (validFalse.contains(value.toLowerCase(Locale.US))) {
            bool = false;
        }
        if (bool == null) {
            throw new ConfigParseException("Invalid Boolean qualifier! %s on line: %s, supported: %s", value, line, Joiner.on(", ").join(Iterables.concat(validTrue, validFalse)));
        }
        return bool;
    }

    protected static String readLine(BufferedReader reader) throws IOException {
        String line = reader.readLine();
        return line == null ? null : line.trim();
    }

    @Override
    public void save(Path file, ConfigTag tag) {
        if (Files.exists(file)) {
            SneakyUtils.sneaky(() -> Files.delete(file));
        }
        if (!Files.exists(file.getParent())) {
            SneakyUtils.sneaky(() -> Files.createDirectories(file.getParent()));
        }
        try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(file, StandardOpenOption.CREATE))) {
            writeTag((ConfigTagImpl) tag, writer, 0);
        } catch (IOException e) {
            logger.error("Failed to save config file: {}", file, e);
        }
    }

    @SuppressWarnings ("rawtypes")
    private void writeTag(ConfigTagImpl tag, PrintWriter writer, int depth) {
        if (tag.isCategory()) {
            if (tag.version != null) {
                writeLine(writer, depth, "~%s", tag.version);
                writer.println();
            }
            for (Iterator<Map.Entry<String, ConfigTagImpl>> iterator = tag.children.entrySet().iterator(); iterator.hasNext(); ) {
                Map.Entry<String, ConfigTagImpl> entry = iterator.next();
                int inc = 0;
                for (String comment : entry.getValue().comment) {
                    writeLine(writer, depth, "# " + comment);
                }
                if (entry.getValue().isCategory()) {
                    writeLine(writer, depth, "\"%s\" {", entry.getKey());
                    inc++;
                }
                writeTag(entry.getValue(), writer, depth + inc);
                if (entry.getValue().isCategory()) {
                    writeLine(writer, depth, "}");
                }
                if (iterator.hasNext()) {
                    writer.println();
                }
            }
        } else if (tag.isValue()) {
            switch (tag.type) {
                case STRING:
                    writeLine(writer, depth, "%s:\"%s\"=\"%s\"", tag.type.getChar(), tag.name, tag.value);
                    break;
                case BOOLEAN:
                case INT:
                case HEX:
                case DOUBLE:
                    writeLine(writer, depth, "%s:\"%s\"=%s", tag.type.getChar(), tag.name, tag.type.processLine(tag.value));
                    break;
                case LIST: {
                    writeLine(writer, depth, "%s:\"%s\" <", tag.listType.getChar(), tag.name);
                    for (Object object : ((List) tag.value)) {
                        writeLine(writer, depth + 1, "%s", tag.listType.processLine(object));
                    }
                    writeLine(writer, depth, ">");
                    break;
                }
            }
        } else {
            throw new IllegalStateException("Somehow a tag is not a category or a value..");
        }
    }

    private static void writeLine(PrintWriter writer, int tabs, String line, Object... data) {
        for (int i = 0; i < tabs; i++) {
            writer.print('\t');
        }
        writer.println(String.format(line, data));
    }
}
