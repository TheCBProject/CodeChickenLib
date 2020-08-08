package codechicken.lib.render.lighting;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockDisplayReader;

public class PlanarLightMatrix extends PlanarLightModel {

    public static final int operationIndex = IVertexOperation.registerOperation();
    public static PlanarLightMatrix instance = new PlanarLightMatrix();

    public IBlockDisplayReader access;
    public BlockPos pos = BlockPos.ZERO;

    private int sampled = 0;
    public int[] brightness = new int[6];

    public PlanarLightMatrix() {
        super(PlanarLightModel.standardLightModel.colours);
    }

    public PlanarLightMatrix locate(IBlockDisplayReader a, BlockPos bPos) {
        access = a;
        pos = bPos;
        sampled = 0;
        return this;
    }

    public int brightness(int side) {
        if ((sampled & 1 << side) == 0) {
            brightness[side] = WorldRenderer.getCombinedLight(access, pos);
            sampled |= 1 << side;
        }
        return brightness[side];
    }

    @Override
    public boolean load(CCRenderState ccrs) {
        ccrs.pipeline.addDependency(ccrs.sideAttrib);
        return true;
    }

    @Override
    public void operate(CCRenderState ccrs) {
        super.operate(ccrs);
        ccrs.brightness = brightness(ccrs.side);
    }

    @Override
    public int operationID() {
        return operationIndex;
    }
}
