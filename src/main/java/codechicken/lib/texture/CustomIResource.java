package codechicken.lib.texture;

import net.minecraft.resources.IResource;
import net.minecraft.resources.data.IMetadataSectionSerializer;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by covers1624 on 2/11/2016.
 * TODO Document.
 */
public class CustomIResource implements IResource {

    private ResourceLocation location;
    private InputStream stream;
    private IResource wrapped;

    public CustomIResource(ResourceLocation location, BufferedImage image, IResource wrapped) {
        try {
            this.location = location;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            stream = new ByteArrayInputStream(baos.toByteArray());
            this.wrapped = wrapped;

        } catch (Exception e) {
            throw new RuntimeException("Unable to create CustomIResource", e);
        }
    }

    @Override
    public ResourceLocation getLocation() {
        return location;
    }

    @Override
    public InputStream getInputStream() {
        return stream;
    }

    @Nullable
    @Override
    public <T> T getMetadata(IMetadataSectionSerializer<T> serializer) {
        return wrapped.getMetadata(serializer);
    }

    @Override
    public String getPackName() {
        return wrapped.getPackName();
    }

    @Override
    public void close() throws IOException {
        wrapped.close();
        stream.close();
    }
}
