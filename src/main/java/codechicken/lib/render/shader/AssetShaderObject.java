package codechicken.lib.render.shader;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.resource.IResourceType;
import net.minecraftforge.resource.ISelectiveResourceReloadListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Created by covers1624 on 24/5/20.
 */
public class AssetShaderObject extends AbstractShaderObject implements ISelectiveResourceReloadListener {

    private final ResourceLocation asset;
    private String source;

    public AssetShaderObject(String name, ShaderType type, Collection<Uniform> uniforms, ResourceLocation asset) {
        super(name, type, uniforms);
        this.asset = Objects.requireNonNull(asset);
    }

    @Override
    protected String getSource() {
        if (source == null) {
            try (IResource resource = Minecraft.getInstance().getResourceManager().getResource(asset)) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
                    source = reader.lines().collect(Collectors.joining("\n"));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return source;
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager, Predicate<IResourceType> resourcePredicate) {
        source = null;
        dirty = true;
    }
}
