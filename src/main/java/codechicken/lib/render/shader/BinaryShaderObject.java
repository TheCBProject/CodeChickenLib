package codechicken.lib.render.shader;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.resource.IResourceType;
import net.minecraftforge.resource.ISelectiveResourceReloadListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL46;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Allows using pre-compiled shader binaries with {@link ShaderProgram}.
 * <p>
 * Created by KitsuneAlex on 18/11/21.
 */
public class BinaryShaderObject implements ShaderObject, ISelectiveResourceReloadListener {

    private static final Logger LOGGER = LogManager.getLogger();

    private final String name;
    private final ResourceLocation asset;
    private final ShaderType type;
    private final BinaryType binaryType;
    private final String entryPoint;
    private final ImmutableList<Uniform> uniforms;
    private final ShaderConstantCache constantCache = new ShaderConstantCache();
    private final Consumer<ConstantCache> specializationCallback;
    private boolean dirty = false;
    private int shaderId = -1;

    public BinaryShaderObject(String name, ResourceLocation asset, ShaderType type, BinaryType binaryType, String entryPoint, Collection<Uniform> uniforms, Consumer<ConstantCache> specializationCallback) {
        this.name = name;
        this.asset = asset;
        this.type = type;
        this.binaryType = binaryType;
        this.entryPoint = entryPoint;
        this.uniforms = ImmutableList.copyOf(uniforms);
        this.specializationCallback = specializationCallback;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ShaderType getShaderType() {
        return type;
    }

    @Override
    public ImmutableList<Uniform> getUniforms() {
        return uniforms;
    }

    @Override
    public boolean isDirty() {
        return dirty;
    }

    @Override
    public void alloc() {
        if (dirty || shaderId == -1) {
            if (shaderId == -1) {
                shaderId = GL20.glCreateShader(type.getGLCode());
                if (shaderId == 0) {
                    throw new IllegalStateException("Allocation of ShaderObject failed.");
                }
            }

            byte[] source = getRawSource();
            ByteBuffer sourceBuffer = BufferUtils.createByteBuffer(source.length).order(ByteOrder.nativeOrder());
            sourceBuffer.put(source);
            sourceBuffer.flip();
            GL46.glShaderBinary(new int[] { shaderId }, binaryType.getGLCode(), sourceBuffer);

            specializationCallback.accept(constantCache);
            GL46.glSpecializeShader(shaderId, entryPoint, constantCache.getIndices(), constantCache.getValues());

            if (GL20.glGetShaderi(shaderId, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
                throw new RuntimeException("ShaderProgram linkage failure. \n" + GL20.glGetShaderInfoLog(shaderId));
            }

            dirty = false;
        }
    }

    @Override
    public int getShaderID() {
        return shaderId;
    }

    @Override
    public void onLink(int programId) {
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager, Predicate<IResourceType> resourcePredicate) {
        dirty = true;
    }

    private byte[] getRawSource() {
        try (IResource resource = Minecraft.getInstance().getResourceManager().getResource(asset)) {
            try (InputStream istream = resource.getInputStream()) {
                ByteArrayOutputStream ostream = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int read;
                while ((read = istream.read(buffer)) != -1) {
                    ostream.write(buffer, 0, read);
                }
                return ostream.toByteArray();
            }
        } catch (Throwable t) {
            LOGGER.error("Failed to read shader asset.", t);
            return new byte[0];
        }
    }

}
