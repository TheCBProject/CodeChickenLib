package codechicken.lib.util;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.server.packs.resources.ResourceProvider;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by covers1624 on 11/07/2017.
 */
public class ResourceUtils {

    /**
     * Retrieves an InputStream from the standard Vanilla IReloadableResourceManager.
     *
     * @param resource The resource.
     * @return The InputStream.
     * @throws IOException If the file is not found or some other IO error occurred.
     */
    public static InputStream getResourceAsStream(ResourceLocation resource) throws IOException {
        return getResource(resource).open();
    }

    /**
     * Grabs the resource manager.
     *
     * @return The resource manager.
     */
    public static ReloadableResourceManager getResourceManager() {
        return (ReloadableResourceManager) Minecraft.getInstance().getResourceManager();
    }

    /**
     * Get's an IResource from the resource manager.
     *
     * @param location The resource to get.
     * @return The gotten resource.
     * @throws IOException If the resource doesn't exist, or some other IO error occurred.
     */
    public static Resource getResource(ResourceLocation location) throws IOException {
        return getResourceManager().getResourceOrThrow(location);
    }

    /**
     * Registers a IResourceManagerReloadListener to MC's resource manager.
     *
     * @param reloadListener The listener.
     */
    public static void registerReloadListener(ResourceManagerReloadListener reloadListener) {
        getResourceManager().registerReloadListener(reloadListener);
    }

    /**
     * Loads the given {@link ResourceLocation} with the given {@link ResourceProvider} into
     * a list of UTF-8 Strings.
     *
     * @param resourceProvider The {@link ResourceProvider}.
     * @param loc              The {@link ResourceLocation}.
     * @return The UTF-8 lines of the resource.
     */
    public static List<String> loadResource(ResourceProvider resourceProvider, ResourceLocation loc) {
        try {
            Resource resource = resourceProvider.getResourceOrThrow(loc);
            try (BufferedReader reader = resource.openAsReader()) {
                return reader.lines().toList();
            }
        } catch (IOException ex) {
            throw new RuntimeException("Failed to load MTL file: " + loc, ex);
        }
    }

}
