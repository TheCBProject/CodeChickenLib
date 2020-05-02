package codechicken.lib.data;

import java.io.ByteArrayOutputStream;

/**
 * An {@link MCDataOutput} implementation that
 * provides a byte array of the data received.
 * <p>
 * Created by covers1624 on 4/16/20.
 */
public class MCByteStream extends MCDataOutputStream {

    private final ByteArrayOutputStream bos;

    public MCByteStream() {
        this(new ByteArrayOutputStream());
    }

    public MCByteStream(ByteArrayOutputStream bos) {
        super(bos);
        this.bos = bos;
    }

    /**
     * Get the data buffered.
     *
     * @return The bytes.
     */
    public byte[] getBytes() {
        return bos.toByteArray();
    }
}
