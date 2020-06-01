package codechicken.lib.render.lighting;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IEnviromentBlockReader;

/**
 * Simple brightness model that only works for axis planar sides
 */
public class SimpleBrightnessModel implements IVertexOperation {

    public static final int operationIndex = IVertexOperation.registerOperation();
    public static SimpleBrightnessModel instance = new SimpleBrightnessModel();

    public IEnviromentBlockReader access;
    public BlockPos pos = BlockPos.ZERO;

    private int sampled = 0;
    private int[] samples = new int[6];
    private BlockPos c = BlockPos.ZERO;

    public void locate(IEnviromentBlockReader a, BlockPos bPos) {
        access = a;
        pos = bPos;
        sampled = 0;
    }

    public int sample(int side) {
        if ((sampled & 1 << side) == 0) {
            c = pos.offset(Direction.BY_INDEX[side]);
            BlockState b = access.getBlockState(c);
            samples[side] = access.getCombinedLight(c, b.getBlock().getLightValue(b, access, c));
            sampled |= 1 << side;
        }
        return samples[side];
    }

    @Override
    public boolean load(CCRenderState ccrs) {

        ccrs.pipeline.addDependency(ccrs.sideAttrib);
        return true;
    }

    @Override
    public void operate(CCRenderState ccrs) {
        ccrs.brightness = sample(ccrs.side);
    }

    @Override
    public int operationID() {
        return operationIndex;
    }
}
