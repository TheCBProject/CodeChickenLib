package codechicken.lib.render.shader;

import it.unimi.dsi.fastutil.ints.Int2IntArrayMap;

/**
 * Created by KitsuneAlex on 19/11/21.
 */
public class ShaderConstantCache implements ConstantCache {

    private final Int2IntArrayMap values = new Int2IntArrayMap();

    @Override
    public void constant1i(int id, int value) {
        if(id < 0) {
            throw new IllegalArgumentException("Constant location ID must be >= 0");
        }
        final int oldValue = values.get(id);
        if(oldValue == value) {
            return;
        }
        values.put(id, value);
    }

    @Override
    public void constant1f(int id, float value) {
        if(id < 0) {
            throw new IllegalArgumentException("Constant location ID must be >= 0");
        }
        final float oldValue = Float.intBitsToFloat(values.get(id));
        if(oldValue == value) {
            return;
        }
        values.put(id, Float.floatToIntBits(value));
    }

    @Override
    public void constant1b(int id, boolean value) {
        if(id < 0) {
            throw new IllegalArgumentException("Constant location ID must be >= 0");
        }
        final boolean oldValue = values.get(id) == 1;
        if(oldValue == value) {
            return;
        }
        values.put(id, value ? 1 : 0);
    }

    public int[] getIndices() {
        return values.keySet().toIntArray();
    }

    public int[] getValues() {
        return values.values().toIntArray();
    }

}
