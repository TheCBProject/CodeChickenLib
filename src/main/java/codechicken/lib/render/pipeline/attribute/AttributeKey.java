package codechicken.lib.render.pipeline.attribute;

import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.render.pipeline.IVertexSource;
import codechicken.lib.render.pipeline.VertexAttribute;
import codechicken.lib.util.ArrayUtils;
import codechicken.lib.util.Copyable;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.IntFunction;

import static net.covers1624.quack.util.SneakyUtils.unsafeCast;

/**
 * Represents a Key for abstract data storage on an {@link IVertexSource}
 *
 * @see VertexAttribute
 * @see IVertexSource#getAttribute
 * Created by covers1624 on 10/10/2016.
 */
public abstract class AttributeKey<T> {

    private final String name;
    public final int attributeKeyIndex;
    public final int operationIndex;

    public AttributeKey(String name) {
        this.name = name;

        attributeKeyIndex = AttributeKeyRegistry.registerAttributeKey(this);
        operationIndex = IVertexOperation.registerOperation();
    }

    public static <T> AttributeKey<T> create(String name, IntFunction<T> factory) {
        return new AttributeKey<>(name) {
            @Override
            public T createDefault(int length) {
                return factory.apply(length);
            }

            @Override
            public T copy(T src, int length) {
                T dst = createDefault(length);
                ArrayUtils.arrayCopy(src, 0, dst, 0, Array.getLength(src));
                return dst;
            }

            @Override
            public T copyRange(T src, int srcpos, T dest, int destpos, int length) {
                ArrayUtils.arrayCopy(src, srcpos, dest, destpos, length);
                return dest;
            }
        };
    }

    /**
     * Construct a new default instance of the storage.
     *
     * @param length The vertex length.
     * @return The new storage.
     */
    public abstract T createDefault(int length);

    /**
     * Copy and resize the attribute.
     * <p>
     * The attribute will either be {@link Copyable} or an array,
     * which may also hold objects which are {@link Copyable}.
     *
     * @param src    The object to copy.
     * @param length The new length of vertices.
     * @return The copied attribute.
     */
    public abstract T copy(T src, int length);

    /**
     * Copies the range of the source attribute into the destination attribute.
     *
     * @param src     The attribute instance to copy from.
     * @param srcpos  The starting vertex position to copy from.
     * @param dest    The attribute instance to copy into.
     * @param destpos The starting vertex position to copy into.
     * @param length  The number of vertex elements to copy.
     * @return The object to set in the destination's attribute slot. Usually just {@code dest}.`
     */
    public abstract T copyRange(T src, int srcpos, T dest, int destpos, int length);

    public static class AttributeKeyRegistry {

        private static final Map<String, AttributeKey<?>> nameMap = new HashMap<>();
        private static final List<AttributeKey<?>> attributeKeys = new ArrayList<>();

        private static synchronized int registerAttributeKey(AttributeKey<?> attr) {
            if (nameMap.containsKey(attr.name)) {
                throw new IllegalArgumentException("Duplicate registration of attribute with name: " + attr.name);
            }
            nameMap.put(attr.name, attr);
            attributeKeys.add(attr);
            return attributeKeys.size() - 1;
        }

        public static <T> AttributeKey<T> getAttributeKey(int index) {
            return unsafeCast(attributeKeys.get(index));
        }

        public static int numAttributes() {
            return attributeKeys.size();
        }
    }
}
