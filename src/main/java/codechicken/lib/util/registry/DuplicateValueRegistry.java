package codechicken.lib.util.registry;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.LegacyNamespacedRegistry;

import javax.annotation.Nullable;
import java.util.HashMap;

public class DuplicateValueRegistry extends LegacyNamespacedRegistry {

    private final LegacyNamespacedRegistry wrapped;
    private final HashMap<Object, ResourceLocation> classMap = new HashMap<>();

    public DuplicateValueRegistry(LegacyNamespacedRegistry wrapped) {
        this.wrapped = wrapped;
    }

    @SuppressWarnings ("unchecked")
    @Nullable
    @Override
    public ResourceLocation getNameForObject(Object value) {
        if (classMap.containsKey(value)) {
            return classMap.get(value);
        }
        return (ResourceLocation) wrapped.getNameForObject(value);
    }

    public void addMapping(Object clazz, ResourceLocation mapping) {
        classMap.put(clazz, mapping);
    }
}
