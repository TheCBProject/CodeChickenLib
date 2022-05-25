package codechicken.lib.config.parser;

import codechicken.lib.config.*;
import com.google.gson.internal.LazilyParsedNumber;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import net.covers1624.quack.io.IndentPrintWriter;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by covers1624 on 20/5/22.
 */
public class JsonConfigSerializer implements ConfigSerializer {

    @Override
    public void parse(Path file, ConfigCategoryImpl rootTag) throws IOException {
        try (JsonReader reader = new JsonReader(Files.newBufferedReader(file))) {
            reader.setLenient(true);
            readCategory(reader, rootTag);
        }
    }

    private void readCategory(JsonReader reader, ConfigCategoryImpl cat) throws IOException {
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            JsonToken next = reader.peek();
            if (next == JsonToken.BEGIN_OBJECT) {
                readCategory(reader, cat.getCategory(name));
            } else if (next == JsonToken.BEGIN_ARRAY) {
                readList(reader, cat.getValueList(name));
            } else {
                cat.getValue(name).setValue(readObject(reader));
            }
        }

        reader.endObject();
    }

    private void readList(JsonReader reader, ConfigValueListImpl val) throws IOException {
        reader.beginArray();

        List<Object> list = new LinkedList<>();
        while (reader.hasNext()) {
            list.add(readObject(reader));
        }
        val.setValue(list);

        reader.endArray();
    }

    private Object readObject(JsonReader reader) throws IOException {
        JsonToken next = reader.peek();
        return switch (next) {
            case STRING -> reader.nextString();
            case NUMBER -> new LazilyParsedNumber(reader.nextString());
            case BOOLEAN -> reader.nextBoolean();
            default -> throw new IllegalStateException("Unknown Token for object type: " + next);
        };
    }

    @Override
    public void save(Path file, ConfigCategoryImpl rootTag) throws IOException {
        try (IndentPrintWriter pw = new IndentPrintWriter(new PrintWriter(Files.newOutputStream(file), true), "\t")) {
            writeCategory(pw, rootTag);
            pw.println();
            pw.flush();
        }
    }

    private void writeTag(IndentPrintWriter pw, ConfigTag tag) {
        if (tag instanceof ConfigCategoryImpl cat) {
            writeCategory(pw, cat);
        } else if (tag instanceof ConfigValueImpl val) {
            writeValue(pw, val);
        } else if (tag instanceof ConfigValueListImpl lst) {
            writeValueList(pw, lst);
        }
    }

    private void writeCategory(IndentPrintWriter pw, ConfigCategoryImpl cat) {
        pw.println("{");
        pw.pushIndent();

        boolean first = true;
        for (ConfigTag child : cat.getChildren()) {
            if (child.isNetworkTag()) continue;
            if (!first) {
                pw.print(",");
                pw.println();// Finish previous line.
                pw.println();// Spacer.
            }
            for (String s : child.getComment()) {
                pw.println("// " + s);
            }
            pw.print("\"" + child.getName() + "\": ");
            writeTag(pw, child);
            first = false;
        }

        if (!first) {
            pw.println();// Finish previous line.
        }

        pw.popIndent();
        pw.print("}");
    }

    private void writeValue(IndentPrintWriter pw, ConfigValueImpl val) {
        pw.print(toString(val.getRawValue(), val.getType()));
    }

    private void writeValueList(IndentPrintWriter pw, ConfigValueListImpl lst) {
        pw.println("[");
        pw.pushIndent();
        boolean first = true;
        for (Object o : lst.getRawValue()) {
            if (!first) {
                pw.println(",");
            }
            pw.print(toString(o, lst.getType()));
            first = false;
        }
        if (!first) {
            pw.println();
        }
        pw.popIndent();
        pw.print("]");
    }

    private static String toString(Object val, ValueType type) {
        if (type == ValueType.HEX) {
            return "\"0x" + (Long.toString(((long) (Integer) val) << 32 >>> 32, 16)).toUpperCase() + "\"";
        }
        if (type == ValueType.STRING) {
            return "\"" + val + "\"";
        }
        return val.toString();
    }
}
