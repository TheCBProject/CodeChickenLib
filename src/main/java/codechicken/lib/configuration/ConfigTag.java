package codechicken.lib.configuration;

import codechicken.lib.configuration.ConfigFile.ConfigReader;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by covers1624 on 18/07/2017.
 */
public class ConfigTag implements IConfigTag {

    private static Pattern QUOTE_PATTERN = Pattern.compile("(?<=\")(.*)(?=\")");
    private static Pattern STRING_MATCHER = Pattern.compile("(?<=.:\")(.*)(\"=\")(.*)(?=\")");

    private static final Set<String> validTrue = Sets.newHashSet("true", "yes", "1");
    private static final Set<String> validFalse = Sets.newHashSet("false", "no", "0");

    protected String name;
    protected String version;
    protected Map<String, ConfigTag> children;
    protected ConfigTag parent;
    protected boolean dirty;

    protected List<String> comment;
    private TagType type;

    protected TagType listType;

    protected Object value;

    protected ConfigTag(String name, ConfigTag parent) {
        this.name = name;
        this.parent = parent;
        children = new LinkedHashMap<>();
        comment = new LinkedList<>();
    }

    protected void parseTag(ConfigReader reader) throws IOException {
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
                version = line.substring(1);
                continue;
            }
            //Check if we are the start of a new tag.
            if (line.startsWith("\"") && line.endsWith("{")) {
                Matcher matcher = QUOTE_PATTERN.matcher(line);
                if (!matcher.find()) {
                    throw new ConfigParseException("Malformed line! @%s, %s", reader.getCurrLine(), line);
                }
                String name = matcher.group();
                ConfigTag tag = getTag(name);
                tag.parseTag(reader);
                continue;
            }
            //Actual value parsing!
            boolean isList = line.endsWith("<");
            char first = line.charAt(0);
            TagType type = TagType.fromChar(first);
            if (type == null) {
                throw new ConfigParseException("Invalid value type %s, @Line:%s, %s", first, reader.getCurrLine(), line);
            }
            //If we are a string we need a custom matcher.
            Matcher matcher = (type != TagType.STRING || isList ? QUOTE_PATTERN : STRING_MATCHER).matcher(line);
            if (!matcher.find()) {
                throw new ConfigParseException("Malformed line! @%s, %s", reader.getCurrLine(), line);
            }
            //Get the name of the tag.
            String name = matcher.group(1);
            ConfigTag tag = getTag(name);
            if (!isList) {
                //Get the value.
                String value;
                if (type == TagType.STRING) {
                    value = matcher.group(3);
                } else {
                    int equals = line.indexOf('=');
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
                        tag.setInt(Integer.parseInt(value));
                        continue;
                    }
                    case HEX: {
                        tag.setHex((int) Long.parseLong(value.replace("0x", ""), 16));
                        continue;
                    }
                    case DOUBLE: {
                        tag.setDouble(Double.parseDouble(value));
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
                case HEX:
                    tag.setHexList(list);
                    break;
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
        return line == null ? null : line.replace("\t", "");
    }

    protected void writeTag(PrintWriter writer, int depth) {
        for (String comment : comment) {
            writeLine(writer, depth, "#" + comment);
        }

        if (isCategory()) {
            if (version != null) {
                writeLine(writer, depth, "~%s", version);
            }
            for (Entry<String, ConfigTag> entry : children.entrySet()) {
                int inc = 0;
                if (entry.getValue().isCategory()) {
                    writeLine(writer, depth, "\"%s\" {", entry.getKey());
                    inc++;
                }
                entry.getValue().writeTag(writer, depth + inc);
                if (entry.getValue().isCategory()) {
                    writeLine(writer, depth, "}");
                }
                writer.println();
            }
        } else if (isValue()) {
            switch (type) {
                case STRING:
                    writeLine(writer, depth, "%s:\"%s\"=\"%s\"", type.getChar(), name, value);
                    break;
                case BOOLEAN:
                case INT:
                case HEX:
                case DOUBLE:
                    writeLine(writer, depth, "%s:\"%s\"=%s", type.getChar(), name, type.processLine(value));
                    break;
                case LIST: {
                    writeLine(writer, depth, "%s:\"%s\" <", listType.getChar(), name);
                    for (Object object : ((List) value)) {
                        writeLine(writer, depth + 1, "%s", listType.processLine(object));
                    }
                    writeLine(writer, depth, ">");
                    break;
                }
            }
        } else {
            throw new IllegalStateException("Somehow a tag is not a category or a value..");
        }
    }

    protected void writeLine(PrintWriter writer, int tabs, String line, Object... data) {
        for (int i = 0; i < tabs; i++) {
            writer.print('\t');
        }
        writer.println(String.format(line, data));
    }

    @Override
    public boolean hasParent() {
        return parent != null;
    }

    @Nullable
    @Override
    public ConfigTag getParent() {
        return parent;
    }

    @Override
    public boolean isCategory() {
        return value == null || !children.isEmpty();
    }

    @Override
    public boolean isValue() {
        return children.isEmpty() && value != null;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isDirty() {
        return dirty || (hasParent() && parent.isDirty());
    }

    @Override
    public ConfigTag markDirty() {
        dirty = true;//Mark us a dirty.
        if (hasParent()) {//If we are a child, we need to mark the parent dirty.
            parent.markDirty();
        }
        return this;
    }

    @Override
    public void clear() {
        if (isCategory()) {
            children.clear();
            markDirty();
        }
    }

    protected void onSave() {
        dirty = false;
        children.values().forEach(ConfigTag::onSave);
    }

    @Override
    public boolean hasTag(String name) {
        return value == null && children.containsKey(name);
    }

    @Override
    public ConfigTag getTag(String name) {
        addTagCheck();
        return children.computeIfAbsent(name, s -> new ConfigTag(s, this));
    }

    @Override
    @Nullable
    public ConfigTag getTagIfPresent(String name) {
        addTagCheck();
        return children.get(name);
    }

    @Override
    public ConfigTag deleteTag(String name) {
        if (isCategory()) {
            children.remove(name);
        }
        return this;
    }

    @Override
    public String getTagVersion() {
        return version;
    }

    @Override
    public ConfigTag setTagVersion(String version) {
        this.version = version;
        return this;
    }

    @Override
    public TagType getTagType() {
        return type;
    }

    @Override
    public Object getRawValue() {
        return value;
    }

    @Override
    public ConfigTag setComment(String comment) {
        this.comment = new LinkedList<>();
        this.comment.add(comment);
        return this;
    }

    @Override
    public ConfigTag setComment(List<String> lines) {
        this.comment = new LinkedList<>(lines);
        return null;
    }

    //region Getters.
    @Override
    public boolean getBoolean(boolean defaultValue) {
        setValueCheck();

        if (value == null) {
            setBoolean(defaultValue);
        } else if (type != TagType.BOOLEAN) {
            throw new UnsupportedOperationException("ConfigTag is not of a Boolean type, Actual: " + type);
        } else if (!(value instanceof Boolean)) {
            throw new IllegalStateException(String.format("Tag appears to be in an invalid state.. Requested: %s, Current %s.", type, value.getClass()));
        }

        return (Boolean) value;
    }

    @Override
    public String getString(String defaultValue) {
        setValueCheck();

        if (value == null) {
            setString(defaultValue);
        } else if (type != TagType.STRING) {
            throw new UnsupportedOperationException("ConfigTag is not of a String type, Actual: " + type);
        } else if (!(value instanceof String)) {
            throw new IllegalStateException(String.format("Tag appears to be in an invalid state.. Requested: %s, Current %s.", type, value.getClass()));
        }

        return (String) value;
    }

    @Override
    public int getInt(int defaultValue) {
        setValueCheck();

        if (value == null) {
            setInt(defaultValue);
        } else if (type != TagType.INT) {
            throw new UnsupportedOperationException("ConfigTag is not of a Integer type, Actual: " + type);
        } else if (!(value instanceof Integer)) {
            throw new IllegalStateException(String.format("Tag appears to be in an invalid state.. Requested: %s, Current %s.", type, value.getClass()));
        }

        return (Integer) value;
    }

    @Override
    public int getHex(int defaultValue) {
        setValueCheck();

        if (value == null) {
            setHex(defaultValue);
        } else if (type != TagType.HEX) {
            throw new UnsupportedOperationException("ConfigTag is not of a Hex type, Actual: " + type);
        } else if (!(value instanceof String)) {
            throw new IllegalStateException(String.format("Tag appears to be in an invalid state.. Requested: %s, Current %s.", type, value.getClass()));
        }

        return (int) Long.parseLong(((String) value).replace("0x", ""), 16);
    }

    @Override
    public double getDouble(double defaultValue) {
        setValueCheck();

        if (value == null) {
            setDouble(defaultValue);
        } else if (type != TagType.DOUBLE) {
            throw new UnsupportedOperationException("ConfigTag is not of a Double type, Actual: " + type);
        } else if (!(value instanceof Double)) {
            throw new IllegalStateException(String.format("Tag appears to be in an invalid state.. Requested: %s, Current %s.", type, value.getClass()));
        }

        return (Integer) value;
    }

    @Override
    public ConfigTag setBoolean(boolean value) {
        setValueCheck();
        type = TagType.BOOLEAN;
        this.value = value;
        markDirty();
        return this;
    }

    @Override
    public ConfigTag setString(String value) {
        setValueCheck();
        type = TagType.STRING;
        this.value = value;
        markDirty();
        return this;
    }

    @Override
    public ConfigTag setInt(int value) {
        setValueCheck();
        type = TagType.INT;
        this.value = value;
        markDirty();
        return this;
    }

    @Override
    public ConfigTag setHex(int value) {
        setValueCheck();
        setString("0x" + Long.toString(((long) value) << 32 >>> 32, 16));
        type = TagType.HEX;
        markDirty();
        return this;
    }

    @Override
    public ConfigTag setDouble(double value) {
        setValueCheck();
        type = TagType.DOUBLE;
        this.value = value;
        markDirty();
        return this;
    }
    //endregion

    //region Lists
    @Override
    @SuppressWarnings ("unchecked")
    public List<Boolean> getBooleanList(List<Boolean> defaultValues) {
        setValueCheck();

        if (value == null) {
            setBooleanList(defaultValues);
        } else if (type != TagType.LIST) {
            throw new UnsupportedOperationException("ConfigTag is not of a List type, Actual: " + type);
        } else if (listType != TagType.BOOLEAN) {
            throw new UnsupportedOperationException("List is not of a Boolean type, Actual: " + type);
        } else if (!(value instanceof List)) {
            throw new IllegalStateException(String.format("Tag appears to be in an invalid state.. Requested: %s, Current %s.", type, value.getClass()));
        }

        return (List) value;
    }

    @Override
    @SuppressWarnings ("unchecked")
    public List<String> getStringList(List<String> defaultValues) {
        setValueCheck();

        if (value == null) {
            setStringList(defaultValues);
        } else if (type != TagType.LIST) {
            throw new UnsupportedOperationException("ConfigTag is not of a List type, Actual: " + type);
        } else if (listType != TagType.STRING) {
            throw new UnsupportedOperationException("List is not of a String type, Actual: " + type);
        } else if (!(value instanceof List)) {
            throw new IllegalStateException(String.format("Tag appears to be in an invalid state.. Requested: %s, Current %s.", type, value.getClass()));
        }

        return (List) value;
    }

    @Override
    @SuppressWarnings ("unchecked")
    public List<Integer> getIntList(List<Integer> defaultValues) {
        setValueCheck();

        if (value == null) {
            setIntList(defaultValues);
        } else if (type != TagType.LIST) {
            throw new UnsupportedOperationException("ConfigTag is not of a List type, Actual: " + type);
        } else if (listType != TagType.INT) {
            throw new UnsupportedOperationException("List is not of a Integer type, Actual: " + type);
        } else if (!(value instanceof List)) {
            throw new IllegalStateException(String.format("Tag appears to be in an invalid state.. Requested: %s, Current %s.", type, value.getClass()));
        }

        return (List) value;
    }

    @Override
    @SuppressWarnings ("unchecked")
    public List<Integer> getHexList(List<Integer> defaultValues) {
        setValueCheck();

        if (value == null) {
            setHexList(defaultValues);
        } else if (type != TagType.LIST) {
            throw new UnsupportedOperationException("ConfigTag is not of a List type, Actual: " + type);
        } else if (listType != TagType.HEX) {
            throw new UnsupportedOperationException("List is not of a Hex type, Actual: " + type);
        } else if (!(value instanceof List)) {
            throw new IllegalStateException(String.format("Tag appears to be in an invalid state.. Requested: %s, Current %s.", type, value.getClass()));
        }

        return (List) value;
    }

    @Override
    @SuppressWarnings ("unchecked")
    public List<Double> getDoubleList(List<Double> defaultValues) {
        setValueCheck();

        if (value == null) {
            setDoubleList(defaultValues);
        } else if (type != TagType.LIST) {
            throw new UnsupportedOperationException("ConfigTag is not of a List type, Actual: " + type);
        } else if (listType != TagType.DOUBLE) {
            throw new UnsupportedOperationException("List is not of a Double type, Actual: " + type);
        } else if (!(value instanceof List)) {
            throw new IllegalStateException(String.format("Tag appears to be in an invalid state.. Requested: %s, Current %s.", type, value.getClass()));
        }

        return (List) value;
    }

    protected void setList(List<?> value) {
        setValueCheck();
        type = TagType.LIST;
        this.value = value;
        markDirty();
    }

    @Override
    public ConfigTag setBooleanList(List<Boolean> value) {
        setList(value);
        listType = TagType.BOOLEAN;
        return this;
    }

    @Override
    public ConfigTag setStringList(List<String> value) {
        setList(value);
        listType = TagType.STRING;
        return this;
    }

    @Override
    public ConfigTag setIntList(List<Integer> value) {
        setList(value);
        listType = TagType.INT;
        return this;
    }

    @Override
    public ConfigTag setHexList(List<Integer> value) {
        setList(value);
        listType = TagType.HEX;
        return this;
    }

    @Override
    public ConfigTag setDoubleList(List<Double> value) {
        setList(value);
        listType = TagType.DOUBLE;
        return this;
    }
    //endregion

    protected void addTagCheck() {
        if (value != null) {
            throw new UnsupportedOperationException("Unable to get a sub tag for a tag that has a value");
        }
    }

    protected void setValueCheck() {
        if (!children.isEmpty()) {
            throw new UnsupportedOperationException("Unable to set the value for a tag that has children.");
        }
    }

}
