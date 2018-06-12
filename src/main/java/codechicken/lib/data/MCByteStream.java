package codechicken.lib.data;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

/**
 * Created by covers1624 on 11/06/18.
 */
public class MCByteStream extends MCDataOutputWrapper {

    private ByteArrayOutputStream out;

    public MCByteStream(ByteArrayOutputStream out) {
        super(new DataOutputStream(out));
        this.out = out;
    }

    public byte[] getBytes() {
        return out.toByteArray();
    }
}
