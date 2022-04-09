package codechicken.lib.render.shader;

/**
 * Provides methods to mutate specilization constants
 * before the shader is specialized.
 * <p>
 * Created by KitsuneAlex on 19/11/21.
 */
public interface ConstantCache {

    void constant1i(int id, int value);

    void constant1f(int id, float value);

    void constant1b(int id, boolean value);

}
