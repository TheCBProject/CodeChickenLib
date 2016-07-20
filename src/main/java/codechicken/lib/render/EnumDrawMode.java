package codechicken.lib.render;

import org.lwjgl.opengl.GL11;

/**
 * Created by covers1624 on 7/20/2016.
 */
public enum EnumDrawMode {
    POINTS(GL11.GL_POINTS),
    LINES(GL11.GL_LINES),
    LINE_LOOP(GL11.GL_LINE_LOOP),
    LINE_STRIP(GL11.GL_LINE_STRIP),
    TRIANGLES(GL11.GL_TRIANGLES),
    TRIANGLE_STRIP(GL11.GL_TRIANGLE_STRIP),
    TRIANGLE_FAN(GL11.GL_TRIANGLE_FAN),
    QUADS(GL11.GL_QUADS),
    QUAD_STRIP(GL11.GL_QUAD_STRIP),
    POLYGON(GL11.GL_POLYGON);

    private int glMode;

    EnumDrawMode(int glMode) {
        this.glMode = glMode;
    }

    public static EnumDrawMode fromGL(int glMode){
        for (EnumDrawMode drawMode : values()){
            if (drawMode.getDrawMode() == glMode){
                return drawMode;
            }
        }
        return QUADS;//When in doubt, quads.
    }

    public int getDrawMode() {
        return glMode;
    }

    @Override
    public String toString() {
        String name = "DrawMode: ";
        String mode;

        switch (this) {

        case POINTS:
            mode = "Points";
            break;
        case LINES:
            mode = "Lines";
            break;
        case LINE_LOOP:
            mode = "Line Loop";
            break;
        case LINE_STRIP:
            mode = "Line Strip";
            break;
        case TRIANGLES:
            mode = "Triangles";
            break;
        case TRIANGLE_STRIP:
            mode = "Triangle Strip";
            break;
        case TRIANGLE_FAN:
            mode = "Triangle Fan";
            break;
        case QUADS:
            mode = "Quads";
            break;
        case QUAD_STRIP:
            mode = "Quad Strip";
            break;
        case POLYGON:
            mode = "Polygon";
            break;
        default:
            mode = "UNKNOWN";
            break;
        }

        return name + mode;
    }
}
