package codechicken.lib.config.parser;

import codechicken.lib.config.*;
import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by covers1624 on 19/5/22.
 */
public class TomlConfigSerializer implements ConfigSerializer {

    @Override
    public void parse(Path file, ConfigCategoryImpl rootTag) throws IOException {
        try (CommentedFileConfig tomlConfig = CommentedFileConfig.builder(file).preserveInsertionOrder().build()) {
            tomlConfig.load();
            load("", rootTag, tomlConfig);
        }
    }

    private void load(String catName, ConfigCategoryImpl category, Config config) {
        for (Map.Entry<String, Object> entry : config.valueMap().entrySet()) {
            String path = join(catName, entry.getKey());
            Object value = entry.getValue();
            if (value instanceof Config conf) {
                load(path, category.getCategory(entry.getKey()), conf);
            } else if (value instanceof List<?> lst) {
                category.getValueList(entry.getKey()).setValue(lst);
            } else {
                category.getValue(entry.getKey()).setValue(value);
            }
        }
    }

    @Override
    public void save(Path file, ConfigCategoryImpl rootTag) throws IOException {
        try (CommentedFileConfig tomlConfig = CommentedFileConfig.builder(file)
                .preserveInsertionOrder()
                .sync()
                .writingMode(WritingMode.REPLACE)
                .build()) {
            for (ConfigTag child : rootTag.getChildren()) {
                write(tomlConfig, "", (AbstractConfigTag<?>) child);
            }
            tomlConfig.save();
        }
    }

    private void write(CommentedFileConfig config, String catName, AbstractConfigTag<?> tag) {
        String path = join(catName, tag.getName());

        if (!tag.getComment().isEmpty()) {
            config.setComment(path, String.join("\n", tag.getComment()));
        }
        if (tag instanceof ConfigCategoryImpl cat) {
            for (ConfigTag child : cat.getChildren()) {
                if (child.isNetworkTag()) continue;

                write(config, path, (AbstractConfigTag<?>) child);
            }
        } else if (tag instanceof ConfigValueImpl val) {
            config.set(path, convert(val.getRawValue(), val.getType()));
        } else if (tag instanceof ConfigValueListImpl lst) {
            config.set(path, convert(lst.getRawValue(), lst.getType()));
        }
    }

    private static List<?> convert(List<?> values, ValueType type) {
        if (type != ValueType.HEX) return values;

        List<Object> list = new ArrayList<>(values.size());
        for (Object value : values) {
            list.add(convert(value, type));
        }

        return list;
    }

    private static Object convert(Object value, ValueType type) {
        if (type != ValueType.HEX) return value;

        return "0x" + (Long.toString(((long) (Integer) value) << 32 >>> 32, 16)).toUpperCase();
    }

    private static String join(String a, String b) {
        if (a.isEmpty()) return b;
        return a + "." + b;
    }
}
