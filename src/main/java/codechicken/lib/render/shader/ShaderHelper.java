package codechicken.lib.render.shader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by covers1624 on 22/06/2017.
 * TODO, Maybe cache read files here to avoid hitting the disk for dynamic shaders.
 */
public class ShaderHelper {

    /**
     * Appends a shader to another shader.
     * This is essentially a crude "#Include X" for shaders.
     * The literal contents of toAppend is added to the bottom of your shader.
     * So be sure not to have any conflicts or you will experience linkage errors.
     *
     * @param parent The parent shader.
     * @param toAppend The shader to append.
     * @return The new shader with the appendage.
     * @throws IOException if an I/O error occurs.
     */
    public static String appendShader(String parent, InputStream toAppend) throws IOException {
        String appendage = readShader(toAppend);
        return parent + "\n" + appendage;
    }

    /**
     * Reads a shader from an InputStream.
     *
     * @param stream The stream to read.
     * @return The shader read from disk.
     * @throws IOException if an I/O error occurs.
     */
    public static String readShader(InputStream stream) throws IOException{
        StringBuilder sb = new StringBuilder();
        BufferedReader bin = new BufferedReader(new InputStreamReader(stream));
        String line;
        while ((line = bin.readLine()) != null) {
            sb.append(line).append('\n');
        }
        stream.close();
        return sb.toString();
    }

    /**
     * Helper for getting an input stream.
     *
     * @param location InputStream to get.
     * @return The InputStream.
     */
    public static InputStream getStream(String location) {
        return ShaderHelper.class.getResourceAsStream(location);
    }

}
