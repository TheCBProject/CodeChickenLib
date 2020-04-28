package codechicken.lib.render.pipeline;

import codechicken.lib.colour.ColourRGBA;
import codechicken.lib.render.CCRenderState;

public class ColourMultiplier implements IVertexOperation {

    private static final ThreadLocal<ColourMultiplier> instances = ThreadLocal.withInitial(() -> new ColourMultiplier(-1));

    public static ColourMultiplier instance(int colour) {
        ColourMultiplier instance = instances.get();
        instance.colour = colour;
        return instance;
    }

    public static final int operationIndex = IVertexOperation.registerOperation();
    public int colour;

    public ColourMultiplier(int colour) {
        this.colour = colour;
    }

    @Override
    public boolean load(CCRenderState ccrs) {
        if (colour == -1) {
            return false;
        }

        ccrs.pipeline.addDependency(ccrs.colourAttrib);
        return true;
    }

    @Override
    public void operate(CCRenderState ccrs) {
        ccrs.colour = ColourRGBA.multiply(ccrs.colour, colour);
    }

    @Override
    public int operationID() {
        return operationIndex;
    }
}
