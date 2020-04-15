package codechicken.lib.util;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

/**
 * Created by covers1624 on 3/10/20.
 */
public class MultiRegistryHelper {

    private final String modId;
    private final Multimap<Class<?>, IForgeRegistryEntry<?>> entries = MultimapBuilder//
            .hashKeys()//
            .arrayListValues()//
            .build();

    public MultiRegistryHelper(String modId) {
        this(modId, FMLJavaModLoadingContext.get().getModEventBus());
    }

    public MultiRegistryHelper(String modId, IEventBus eventBus) {
        this.modId = modId;
        eventBus.addListener(this::onRegister);
    }

    public void register(String name, IForgeRegistryEntry<?>... entries) {
        for (IForgeRegistryEntry<?> entry : entries) {
            register(name, entry);
        }
    }

    private void register(String name, IForgeRegistryEntry<?> entry) {
        if (entry.getRegistryName() == null) {
            entry.setRegistryName(new ResourceLocation(modId, name));
        }
        entries.put(entry.getRegistryType(), entry);
    }

    private void onRegister(RegistryEvent.Register<?> event) {
        IForgeRegistry registry = event.getRegistry();
        entries.get(((Class<?>) event.getGenericType())).forEach(registry::register);
    }

}
