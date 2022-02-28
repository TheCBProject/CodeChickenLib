package codechicken.lib.render.pipeline.attribute;

import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.render.pipeline.IVertexSource;
import codechicken.lib.render.pipeline.VertexAttribute;
import net.covers1624.quack.util.SneakyUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.IntFunction;

/**
 * Used as a key for {@link VertexAttribute}'s
 *
 * @see VertexAttribute
 * @see IVertexSource#getAttributes
 * Created by covers1624 on 10/10/2016.
 */
public class AttributeKey<T> {

    private final String name;
    private final IntFunction<T> factory;
    public final int attributeKeyIndex;
    public final int operationIndex;

    public AttributeKey(String name, IntFunction<T> factory) {
        this.name = name;
        this.factory = factory;
        attributeKeyIndex = AttributeKeyRegistry.registerAttributeKey(this);
        operationIndex = IVertexOperation.registerOperation();
    }

    /**
     * Construct a new storage of attribute values with the given length.
     *
     * @param length The length.
     * @return The new sotorage.
     */
    public T newArray(int length) {
        return factory.apply(length);
    }

    public static class AttributeKeyRegistry {

        private static final Map<String, AttributeKey<?>> nameMap = new HashMap<>();
        private static final List<AttributeKey<?>> attributeKeys = new ArrayList<>();

        private static int registerAttributeKey(AttributeKey<?> attr) {
            if (nameMap.containsKey(attr.name)) {
                throw new IllegalArgumentException("Duplicate registration of attribute with name: " + attr.name);
            }
            nameMap.put(attr.name, attr);
            attributeKeys.add(attr);
            return attributeKeys.size() - 1;
        }

        public static <T> AttributeKey<T> getAttributeKey(int index) {
            return SneakyUtils.unsafeCast(attributeKeys.get(index));
        }

        public static int numAttributes() {
            return attributeKeys.size();
        }
    }

}
