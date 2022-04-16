package codechicken.lib.colour;

import codechicken.lib.math.MathHelper;
import codechicken.lib.util.Copyable;

import static java.lang.Math.max;

public abstract class Colour implements Copyable<Colour> {

    public byte r;
    public byte g;
    public byte b;
    public byte a;

    public Colour(int r, int g, int b, int a) {
        this.r = (byte) r;
        this.g = (byte) g;
        this.b = (byte) b;
        this.a = (byte) a;
    }

    public Colour(Colour colour) {
        r = colour.r;
        g = colour.g;
        b = colour.b;
        a = colour.a;
    }

    public abstract int pack();

    public abstract float[] packArray();

    public Colour add(Colour colour2) {
        a += colour2.a;
        r += colour2.r;
        g += colour2.g;
        b += colour2.b;
        return this;
    }

    public Colour sub(Colour colour2) {
        int ia = (a & 0xFF) - (colour2.a & 0xFF);
        int ir = (r & 0xFF) - (colour2.r & 0xFF);
        int ig = (g & 0xFF) - (colour2.g & 0xFF);
        int ib = (b & 0xFF) - (colour2.b & 0xFF);
        a = (byte) max(ia, 0);
        r = (byte) max(ir, 0);
        g = (byte) max(ig, 0);
        b = (byte) max(ib, 0);
        return this;
    }

    public Colour invert() {
        a = (byte) (0xFF - (a & 0xFF));
        r = (byte) (0xFF - (r & 0xFF));
        g = (byte) (0xFF - (g & 0xFF));
        b = (byte) (0xFF - (b & 0xFF));
        return this;
    }

    public Colour multiply(Colour colour2) {
        a = (byte) ((a & 0xFF) * ((colour2.a & 0xFF) / 255D));
        r = (byte) ((r & 0xFF) * ((colour2.r & 0xFF) / 255D));
        g = (byte) ((g & 0xFF) * ((colour2.g & 0xFF) / 255D));
        b = (byte) ((b & 0xFF) * ((colour2.b & 0xFF) / 255D));
        return this;
    }

    public Colour scale(double d) {
        a = (byte) ((a & 0xFF) * d);
        r = (byte) ((r & 0xFF) * d);
        g = (byte) ((g & 0xFF) * d);
        b = (byte) ((b & 0xFF) * d);
        return this;
    }

    public Colour interpolate(Colour colour2, double d) {
        return this.add(colour2.copy().sub(this).scale(d));
    }

    public Colour multiplyC(double d) {
        r = (byte) MathHelper.clip((r & 0xFF) * d, 0, 255);
        g = (byte) MathHelper.clip((g & 0xFF) * d, 0, 255);
        b = (byte) MathHelper.clip((b & 0xFF) * d, 0, 255);

        return this;
    }

    public abstract Colour copy();

    public int rgb() {
        return (r & 0xFF) << 16 | (g & 0xFF) << 8 | (b & 0xFF);
    }

    public int argb() {
        return (a & 0xFF) << 24 | (r & 0xFF) << 16 | (g & 0xFF) << 8 | (b & 0xFF);
    }

    public int rgba() {
        return (r & 0xFF) << 24 | (g & 0xFF) << 16 | (b & 0xFF) << 8 | (a & 0xFF);
    }

    public abstract Colour set(int colour);

    public Colour set(Colour colour) {
        r = colour.r;
        g = colour.g;
        b = colour.b;
        a = colour.a;
        return this;
    }

    public Colour set(double r, double g, double b, double a) {
        return set((int) (255 * r), (int) (255 * g), (int) (255 * b), (int) (255 * a));
    }

    public Colour set(float r, float g, float b, float a) {
        return set((int) (255F * r), (int) (255F * g), (int) (255F * b), (int) (255F * a));
    }

    public Colour set(int r, int g, int b, int a) {
        this.r = (byte) r;
        this.g = (byte) g;
        this.b = (byte) b;
        this.a = (byte) a;
        return this;
    }

    public Colour set(double[] doubles) {
        return set(doubles[0], doubles[1], doubles[2], doubles[3]);
    }

    public Colour set(float[] floats) {
        return set(floats[0], floats[1], floats[2], floats[3]);
    }

    /**
     * Flips a color between ABGR and RGBA.
     *
     * @param colour The input either ABGR or RGBA.
     * @return The flipped color.
     */
    public static int flipABGR(int colour) {
        int a = (colour >> 24) & 0xFF;
        int b = (colour >> 16) & 0xFF;
        int c = (colour >> 8) & 0xFF;
        int d = colour & 0xFF;
        return (d & 0xFF) << 24 | (c & 0xFF) << 16 | (b & 0xFF) << 8 | (a & 0xFF);
    }

    public static int[] unpack(int colour) {
        return new int[] { (colour >> 24) & 0xFF, (colour >> 16) & 0xFF, (colour >> 8) & 0xFF, colour & 0xFF };
    }

    public static int pack(int[] data) {
        return (data[0] & 0xFF) << 24 | (data[1] & 0xFF) << 16 | (data[2] & 0xFF) << 8 | (data[3] & 0xFF);
    }

    public float[] getRGBA() {
        return new float[] { r / 255F, g / 255F, b / 255F, a / 255F };
    }

    public float[] getARGB() {
        return new float[] { a / 255F, r / 255F, g / 255F, b / 255F };
    }

    public static int packRGBA(byte r, byte g, byte b, byte a) {
        return (r & 0xFF) << 24 | (g & 0xFF) << 16 | (b & 0xFF) << 8 | (a & 0xFF);
    }

    public static int packARGB(byte r, byte g, byte b, byte a) {
        return (a & 0xFF) << 24 | (r & 0xFF) << 16 | (g & 0xFF) << 8 | (b & 0xFF);
    }

    public static int packRGBA(int r, int g, int b, int a) {
        return r << 24 | g << 16 | b << 8 | a;
    }

    public static int packARGB(int r, int g, int b, int a) {
        return a << 24 | r << 16 | g << 8 | b;
    }

    public static int packRGBA(double r, double g, double b, double a) {
        return (int) (r * 255) << 24 | (int) (g * 255) << 16 | (int) (b * 255) << 8 | (int) (a * 255);
    }

    public static int packARGB(double r, double g, double b, double a) {
        return (int) (a * 255) << 24 | (int) (r * 255) << 16 | (int) (g * 255) << 8 | (int) (b * 255);
    }

    public static int packRGBA(float[] data) {
        return packRGBA(data[0], data[1], data[2], data[3]);
    }

    public static int packARGB(float[] data) {
        return packARGB(data[0], data[1], data[2], data[3]);
    }

    public boolean equals(Colour colour) {
        return colour != null && rgba() == colour.rgba();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Colour colour)) return false;

        if (r != colour.r) return false;
        if (g != colour.g) return false;
        if (b != colour.b) return false;
        return a == colour.a;
    }

    @Override
    public int hashCode() {
        int result = r;
        result = 31 * result + (int) g;
        result = 31 * result + (int) b;
        result = 31 * result + (int) a;
        return result;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[0x" + Integer.toHexString(pack()).toUpperCase() + "]";
    }
}
