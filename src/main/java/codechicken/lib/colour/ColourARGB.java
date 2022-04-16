package codechicken.lib.colour;

public class ColourARGB extends Colour {

    public ColourARGB(int colour) {
        super((colour >> 16) & 0xFF, (colour >> 8) & 0xFF, colour & 0xFF, (colour >> 24) & 0xFF);
    }

    public ColourARGB(int a, int r, int g, int b) {
        super(r, g, b, a);
    }

    public ColourARGB(ColourARGB colour) {
        super(colour);
    }

    @Override
    public ColourARGB copy() {
        return new ColourARGB(this);
    }

    @Override
    public Colour set(int colour) {
        return set(new ColourARGB(colour));
    }

    @Override
    public int pack() {
        return pack(this);
    }

    @Override
    public float[] packArray() {
        return new float[] { (a & 0xFF) / 255f, (r & 0xFF) / 255f, (g & 0xFF) / 255f, (b & 0xFF) / 255f };
    }

    public static int pack(Colour colour) {
        return (colour.a & 0xFF) << 24 | (colour.r & 0xFF) << 16 | (colour.g & 0xFF) << 8 | (colour.b & 0xFF);
    }
}
