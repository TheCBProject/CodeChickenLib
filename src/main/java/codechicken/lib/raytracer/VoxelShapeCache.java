package codechicken.lib.raytracer;

import codechicken.lib.vec.Cuboid6;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableSet;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.concurrent.TimeUnit;

/**
 * Created by covers1624 on 3/10/20.
 */
public class VoxelShapeCache {

    private static final Cache<AABB, VoxelShape> bbToShapeCache = CacheBuilder.newBuilder()
            .expireAfterAccess(2, TimeUnit.HOURS)
            .build();
    private static final Cache<Cuboid6, VoxelShape> cuboidToShapeCache = CacheBuilder.newBuilder()
            .expireAfterAccess(2, TimeUnit.HOURS)
            .build();

    private static final Cache<ImmutableSet<VoxelShape>, VoxelShape> mergeShapeCache = CacheBuilder.newBuilder()
            .expireAfterAccess(2, TimeUnit.HOURS)
            .build();

    public static VoxelShape getShape(AABB aabb) {
        VoxelShape shape = bbToShapeCache.getIfPresent(aabb);
        if (shape == null) {
            shape = Shapes.create(aabb);
            bbToShapeCache.put(aabb, shape);
        }
        return shape;
    }

    public static VoxelShape getShape(Cuboid6 cuboid) {
        VoxelShape shape = cuboidToShapeCache.getIfPresent(cuboid);
        if (shape == null) {
            shape = Shapes.box(cuboid.min.x, cuboid.min.y, cuboid.min.z, cuboid.max.x, cuboid.max.y, cuboid.max.z);
            cuboidToShapeCache.put(cuboid, shape);
        }
        return shape;
    }

    public static VoxelShape merge(ImmutableSet<VoxelShape> shapes) {
        VoxelShape shape = mergeShapeCache.getIfPresent(shapes);
        if (shape == null) {
            shape = shapes.stream().reduce(Shapes.empty(), Shapes::or);
            mergeShapeCache.put(shapes, shape);
        }
        return shape;
    }

}
