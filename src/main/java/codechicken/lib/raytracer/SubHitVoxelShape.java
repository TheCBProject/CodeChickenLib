package codechicken.lib.raytracer;

import codechicken.lib.vec.Vector3;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A VoxelShape implementation for handling SubHit's on IndexedCuboid6's
 *
 * Created by covers1624 on 31/10/19.
 */
public class SubHitVoxelShape extends VoxelShape {

    private final VoxelShape shape;
    private final List<Pair<IndexedCuboid6, VoxelShape>> cuboidShapes;

    /**
     * @param shape   The base advertised VoxelShape. (Collision / w/e)
     * @param cuboids Any SubHit's.
     */
    public SubHitVoxelShape(VoxelShape shape, List<IndexedCuboid6> cuboids) {
        super(shape.part);
        this.shape = shape;
        cuboidShapes = cuboids.stream()//
                .map(e -> Pair.of(e, VoxelShapes.create(e.aabb())))//
                .collect(Collectors.toList());
    }

    @Override
    public DoubleList getValues(Direction.Axis axis) {
        return shape.getValues(axis);
    }

    @Nullable
    @Override
    public BlockRayTraceResult rayTrace(Vec3d start, Vec3d end, BlockPos pos) {
        CuboidRayTraceResult closest = null;
        double dist = Double.MAX_VALUE;
        for (Pair<IndexedCuboid6, VoxelShape> cuboidShape : cuboidShapes) {
            CuboidRayTraceResult hit = rayTrace(start, end, pos, cuboidShape.getLeft(), cuboidShape.getRight());
            if (hit != null && dist > hit.dist) {
                closest = hit;
                dist = hit.dist;
            }
        }
        return closest;
    }

    private CuboidRayTraceResult rayTrace(Vec3d start, Vec3d end, BlockPos pos, IndexedCuboid6 cuboid, VoxelShape shape) {
        BlockRayTraceResult hit = shape.rayTrace(start, end, pos);
        if (hit != null) {
            Vector3 hitVec = new Vector3(hit.getHitVec());
            return new CuboidRayTraceResult(hitVec, hit.getFace(), pos, hit.isInside(), cuboid, hitVec.copy().subtract(start).magSquared());
        }
        return null;
    }
}
