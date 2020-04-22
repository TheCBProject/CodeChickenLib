package codechicken.lib.raytracer;

import codechicken.lib.vec.Cuboid6;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import org.apache.commons.lang3.tuple.MutablePair;

import java.util.concurrent.TimeUnit;

/**
 * TODO, Maybe defensive copy Cuboid6 instances?
 * Created by covers1624 on 3/10/20.
 */
public class VoxelShapeCache {

    private static final Cache<AxisAlignedBB, VoxelShape> bbToShapeCache = CacheBuilder.newBuilder()//
            .expireAfterAccess(2, TimeUnit.HOURS).build();
    private static final Cache<Cuboid6, VoxelShape> cuboidToShapeCache = CacheBuilder.newBuilder()//
            .expireAfterAccess(2, TimeUnit.HOURS).build();

    private static final Cache<VoxelShape, MutablePair<AxisAlignedBB, Cuboid6>> shapeToBBCuboid = CacheBuilder.newBuilder()//
            //Weak keys, as we inherently use identity hashcode, as VoxelShape doesn't have a hashCode.
            .expireAfterAccess(2, TimeUnit.HOURS).weakKeys().build();

    public static VoxelShape getShape(AxisAlignedBB aabb) {
        VoxelShape shape = bbToShapeCache.getIfPresent(aabb);
        if (shape == null) {
            shape = VoxelShapes.create(aabb);
            bbToShapeCache.put(aabb, shape);
            MutablePair<AxisAlignedBB, Cuboid6> entry = getReverse(shape);
            if (entry.getLeft() == null) {
                entry.setLeft(aabb);
            }
        }
        return shape;
    }

    public static VoxelShape getShape(Cuboid6 cuboid) {
        VoxelShape shape = cuboidToShapeCache.getIfPresent(cuboid);
        if (shape == null) {
            shape = VoxelShapes.create(cuboid.min.x, cuboid.min.y, cuboid.min.z, cuboid.max.x, cuboid.max.y, cuboid.max.z);
            cuboidToShapeCache.put(cuboid, shape);
            MutablePair<AxisAlignedBB, Cuboid6> entry = getReverse(shape);
            if (entry.getRight() == null) {
                entry.setRight(cuboid);
            }
        }
        return shape;
    }

    public static AxisAlignedBB getAABB(VoxelShape shape) {
        MutablePair<AxisAlignedBB, Cuboid6> entry = getReverse(shape);
        if (entry.getLeft() == null) {
            entry.setLeft(shape.getBoundingBox());
        }
        return entry.getLeft();
    }

    public static Cuboid6 getCuboid(VoxelShape shape) {
        MutablePair<AxisAlignedBB, Cuboid6> entry = getReverse(shape);
        if (entry.getRight() == null) {
            entry.setRight(new Cuboid6(// I hope this is okay, don't want to rely on AABB cache.
                    shape.getStart(Direction.Axis.X), shape.getStart(Direction.Axis.Y), shape.getStart(Direction.Axis.Z),//
                    shape.getEnd(Direction.Axis.X), shape.getEnd(Direction.Axis.Y), shape.getEnd(Direction.Axis.Z)//
            ));
        }
        return entry.getRight();
    }

    private static MutablePair<AxisAlignedBB, Cuboid6> getReverse(VoxelShape shape) {
        MutablePair<AxisAlignedBB, Cuboid6> entry = shapeToBBCuboid.getIfPresent(shape);
        if (entry == null) {
            entry = new MutablePair<>();
            shapeToBBCuboid.put(shape, entry);
        }
        return entry;
    }

}
