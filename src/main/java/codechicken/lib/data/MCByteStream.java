package codechicken.lib.data;

import java.io.ByteArrayOutputStream;

/**
 * Created by covers1624 on 4/16/20.
 */
public class MCByteStream extends MCDataOutputStream {

    private final ByteArrayOutputStream bos;

    public MCByteStream(ByteArrayOutputStream bos) {
        super(bos);
        this.bos = bos;
    }

    public byte[] getBytes() {
        return bos.toByteArray();
    }
}
