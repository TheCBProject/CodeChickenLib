package codechicken.lib.render.shader;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Objects;

/**
 * Created by covers1624 on 24/5/20.
 */
public class AssetShaderObject extends AbstractShaderObject implements ResourceManagerReloadListener {

    private static final Logger LOGGER = LogManager.getLogger();

    private final ResourceLocation asset;
    @Nullable
    private String source;

    public AssetShaderObject(String name, ShaderType type, Collection<UniformPair> uniforms, ResourceLocation asset) {
        super(name, type, uniforms);
        this.asset = Objects.requireNonNull(asset);
    }

    @Override
    protected String getSource() {
        if (source == null) {
            source = new GlslProcessor(asset).process().processedSource();
        }

        return source;
    }

    @Override
    public void onResourceManagerReload(ResourceManager resourceManager) {
        source = null;
        dirty = true;
    }

}
