package codechicken.lib.math;

import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3i;

public class MathHelper {

    public static final double phi = 1.618033988749894;
    public static final double pi = Math.PI;
    public static final double todeg = 57.29577951308232;
    public static final double torad = 0.017453292519943;
    public static final double sqrt2 = 1.414213562373095;

    public static double[] SIN_TABLE = new double[65536];

    static {
        for (int i = 0; i < 65536; ++i) {
            SIN_TABLE[i] = Math.sin(i / 65536D * 2 * Math.PI);
        }

        SIN_TABLE[0] = 0;
        SIN_TABLE[16384] = 1;
        SIN_TABLE[32768] = 0;
        SIN_TABLE[49152] = 1;
    }

    public static double sin(double d) {
        return SIN_TABLE[(int) ((float) d * 10430.378F) & 65535];
    }

    public static double cos(double d) {
        return SIN_TABLE[(int) ((float) d * 10430.378F + 16384.0F) & 65535];
    }

    /**
     * @param a   The value
     * @param b   The value to approach
     * @param max The maximum step
     * @return the closed value to b no less than max from a
     */
    public static float approachLinear(float a, float b, float max) {
        return (a > b) ? (a - b < max ? b : a - max) : (b - a < max ? b : a + max);
    }

    /**
     * @param a   The value
     * @param b   The value to approach
     * @param max The maximum step
     * @return the closed value to b no less than max from a
     */
    public static double approachLinear(double a, double b, double max) {
        return (a > b) ? (a - b < max ? b : a - max) : (b - a < max ? b : a + max);
    }

    /**
     * @param a The first value
     * @param b The second value
     * @param d The interpolation factor, between 0 and 1
     * @return a+(b-a)*d
     */
    public static float interpolate(float a, float b, float d) {
        return a + (b - a) * d;
    }

    /**
     * @param a The first value
     * @param b The second value
     * @param d The interpolation factor, between 0 and 1
     * @return a+(b-a)*d
     */
    public static double interpolate(double a, double b, double d) {
        return a + (b - a) * d;
    }

    /**
     * @param a     The value
     * @param b     The value to approach
     * @param ratio The ratio to reduce the difference by
     * @return a+(b-a)*ratio
     */
    public static double approachExp(double a, double b, double ratio) {
        return a + (b - a) * ratio;
    }

    /**
     * @param a     The value
     * @param b     The value to approach
     * @param ratio The ratio to reduce the difference by
     * @param cap   The maximum amount to advance by
     * @return a+(b-a)*ratio
     */
    public static double approachExp(double a, double b, double ratio, double cap) {
        double d = (b - a) * ratio;
        if (Math.abs(d) > cap) {
            d = Math.signum(d) * cap;
        }
        return a + d;
    }

    /**
     * @param a     The value
     * @param b     The value to approach
     * @param ratio The ratio to reduce the difference by
     * @param c     The value to retreat from
     * @param kick  The difference when a == c
     * @return
     */
    public static double retreatExp(double a, double b, double c, double ratio, double kick) {
        double d = (Math.abs(c - a) + kick) * ratio;
        if (d > Math.abs(b - a)) {
            return b;
        }
        return a + Math.signum(b - a) * d;
    }

    /**
     * @param value The value
     * @param min   The min value
     * @param max   The max value
     * @return The clipped value between min and max
     */
    public static double clip(double value, double min, double max) {
        if (value > max) {
            value = max;
        }
        if (value < min) {
            value = min;
        }
        return value;
    }

    /**
     * @param value The value
     * @param min   The min value
     * @param max   The max value
     * @return The clipped value between min and max
     */
    public static float clip(float value, float min, float max) {
        if (value > max) {
            value = max;
        }
        if (value < min) {
            value = min;
        }
        return value;
    }

    /**
     * @param value The value
     * @param min   The min value
     * @param max   The max value
     * @return The clipped value between min and max
     */
    public static int clip(int value, int min, int max) {
        if (value > max) {
            value = max;
        }
        if (value < min) {
            value = min;
        }
        return value;
    }

    /**
     * Maps a value range to another value range.
     *
     * @param valueIn The value to map.
     * @param inMin   The minimum of the input value range.
     * @param inMax   The maximum of the input value range
     * @param outMin  The minimum of the output value range.
     * @param outMax  The maximum of the output value range.
     * @return The mapped value.
     */
    public static double map(double valueIn, double inMin, double inMax, double outMin, double outMax) {
        return (valueIn - inMin) * (outMax - outMin) / (inMax - inMin) + outMin;
    }

    /**
     * Maps a value range to another value range.
     *
     * @param valueIn The value to map.
     * @param inMin   The minimum of the input value range.
     * @param inMax   The maximum of the input value range
     * @param outMin  The minimum of the output value range.
     * @param outMax  The maximum of the output value range.
     * @return The mapped value.
     */
    public static float map(float valueIn, float inMin, float inMax, float outMin, float outMax) {
        return (valueIn - inMin) * (outMax - outMin) / (inMax - inMin) + outMin;
    }

    /**
     * Rounds the number of decimal places based on the given multiplier.<br>
     * e.g.<br>
     * Input: 17.5245743<br>
     * multiplier: 1000<br>
     * Output: 17.534<br>
     * multiplier: 10<br>
     * Output 17.5<br><br>
     *
     * @param number     The input value.
     * @param multiplier The multiplier.
     * @return The input rounded to a number of decimal places based on the multiplier.
     */
    public static double round(double number, double multiplier) {
        return Math.round(number * multiplier) / multiplier;
    }

    /**
     * Rounds the number of decimal places based on the given multiplier.<br>
     * e.g.<br>
     * Input: 17.5245743<br>
     * multiplier: 1000<br>
     * Output: 17.534<br>
     * multiplier: 10<br>
     * Output 17.5<br><br>
     *
     * @param number     The input value.
     * @param multiplier The multiplier.
     * @return The input rounded to a number of decimal places based on the multiplier.
     */
    public static float round(float number, float multiplier) {
        return Math.round(number * multiplier) / multiplier;
    }

    /**
     * @return min <= value <= max
     */
    public static boolean between(double min, double value, double max) {
        return min <= value && value <= max;
    }

    public static int approachExpI(int a, int b, double ratio) {
        int r = (int) Math.round(approachExp(a, b, ratio));
        return r == a ? b : r;
    }

    public static int retreatExpI(int a, int b, int c, double ratio, int kick) {
        int r = (int) Math.round(retreatExp(a, b, c, ratio, kick));
        return r == a ? b : r;
    }

    public static int floor(double d) {
        return net.minecraft.util.math.MathHelper.floor(d);
    }

    public static int floor(float d) {
        return net.minecraft.util.math.MathHelper.floor(d);
    }

    public static int ceil(double d) {
        return net.minecraft.util.math.MathHelper.ceil(d);
    }

    public static int ceil(float d) {
        return net.minecraft.util.math.MathHelper.ceil(d);
    }

    public static float sqrt(float f) {
        return net.minecraft.util.math.MathHelper.sqrt(f);
    }

    public static float sqrt(double f) {
        return net.minecraft.util.math.MathHelper.sqrt(f);
    }

    public static int roundAway(double d) {
        return (int) (d < 0 ? Math.floor(d) : Math.ceil(d));
    }

    public static int compare(int a, int b) {
        return Integer.compare(a, b);
    }

    public static int compare(double a, double b) {
        return Double.compare(a, b);
    }

    public static BlockPos min(Vector3i pos1, Vector3i pos2) {
        return new BlockPos(Math.min(pos1.getX(), pos2.getX()), Math.min(pos1.getY(), pos2.getY()), Math.min(pos1.getZ(), pos2.getZ()));
    }

    public static BlockPos max(Vector3i pos1, Vector3i pos2) {
        return new BlockPos(Math.max(pos1.getX(), pos2.getX()), Math.max(pos1.getY(), pos2.getY()), Math.max(pos1.getZ(), pos2.getZ()));
    }

    public static int absSum(BlockPos pos) {
        return Math.abs(pos.getX()) + Math.abs(pos.getY()) + Math.abs(pos.getZ());
    }

    public static boolean isAxial(BlockPos pos) {
        return pos.getX() == 0 ? (pos.getY() == 0 || pos.getZ() == 0) : (pos.getY() == 0 && pos.getZ() == 0);
    }

    public static int toSide(BlockPos pos) {
        Direction side = getSide(pos);
        return side == null ? -1 : side.getIndex();
    }

    public static Direction getSide(BlockPos pos) {
        if (!isAxial(pos)) {
            return null;
        }
        if (pos.getY() < 0) {
            return Direction.DOWN;
        }
        if (pos.getY() > 0) {
            return Direction.UP;
        }
        if (pos.getZ() < 0) {
            return Direction.NORTH;
        }
        if (pos.getZ() > 0) {
            return Direction.SOUTH;
        }
        if (pos.getX() < 0) {
            return Direction.WEST;
        }
        if (pos.getX() > 0) {
            return Direction.EAST;
        }

        return null;
    }
}
