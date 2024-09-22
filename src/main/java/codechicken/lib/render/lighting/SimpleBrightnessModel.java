package codechicken.lib.render.lighting;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import org.jetbrains.annotations.Nullable;

import static java.util.Objects.requireNonNull;

/**
 * Simple brightness model that only works for axis planar sides
 */
public class SimpleBrightnessModel implements IVertexOperation {

    public static final int operationIndex = IVertexOperation.registerOperation();
    public static SimpleBrightnessModel instance = new SimpleBrightnessModel();

    public @Nullable BlockAndTintGetter access;
    public BlockPos pos = BlockPos.ZERO;

    private int sampled = 0;
    private final int[] samples = new int[6];

    public void locate(BlockAndTintGetter a, BlockPos bPos) {
        access = a;
        pos = bPos;
        sampled = 0;
    }

    public int sample(int side) {
        requireNonNull(access, "SimpleBrightnessModel must be located first.");
        if ((sampled & 1 << side) == 0) {
            samples[side] = LevelRenderer.getLightColor(access, pos.relative(Direction.BY_3D_DATA[side]));
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
