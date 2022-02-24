package codechicken.lib.render.shader;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by covers1624 on 24/5/20.
 */
public class AssetShaderObject extends AbstractShaderObject implements ResourceManagerReloadListener {

    private static final Logger LOGGER = LogManager.getLogger();

    private final ResourceLocation asset;
    private String source;

    public AssetShaderObject(String name, ShaderType type, Collection<Uniform> uniforms, ResourceLocation asset) {
        super(name, type, uniforms);
        this.asset = Objects.requireNonNull(asset);
    }

    @Override
    protected String getSource() {
        if (source == null) {
            try (Resource resource = Minecraft.getInstance().getResourceManager().getResource(asset)) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
                    source = reader.lines().collect(Collectors.joining("\n"));
                }
            } catch (IOException e) {
                LOGGER.error("Failed to read shader source.", e);
            }
        }

        return source;
    }

    @Override
    public void onResourceManagerReload(ResourceManager resourceManager) {
        source = null;
        dirty = true;
    }

}
