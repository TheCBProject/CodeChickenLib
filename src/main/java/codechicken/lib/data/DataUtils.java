package codechicken.lib.data;

import java.io.OutputStream;

/**
 * Created by covers1624 on 4/15/20.
 */
class DataUtils {

    /**
     * {@link OutputStream#write(byte[], int, int)}
     */
    static void checkLen(int arrLen, int off, int len) {
        if ((off < 0) || (off > arrLen) || (len < 0) || ((off + len) > arrLen) || ((off + len) < 0)) {
            throw new IndexOutOfBoundsException();
        }
    }
}
