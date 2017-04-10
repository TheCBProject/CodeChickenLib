package codechicken.lib.util.registry;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.LegacyNamespacedRegistry;

import javax.annotation.Nullable;
import java.util.HashMap;

public class DuplicateValueRegistry<V> extends LegacyNamespacedRegistry<V> {

    private final LegacyNamespacedRegistry<V> wrapped;
    private final HashMap<V, ResourceLocation> classMap = new HashMap<>();

    public DuplicateValueRegistry(LegacyNamespacedRegistry<V> wrapped) {
        this.wrapped = wrapped;
    }

    @Nullable
    @Override
    public ResourceLocation getNameForObject(V value) {
        if (classMap.containsKey(value)) {
            return classMap.get(value);
        }
        return wrapped.getNameForObject(value);
    }

    public void addMapping(V clazz, ResourceLocation mapping) {
        classMap.put(clazz, mapping);
    }
}
