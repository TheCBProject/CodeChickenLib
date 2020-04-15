package codechicken.lib.raytracer;

import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

/**
 * Created by covers1624 on 3/10/20.
 */
public class MergedVoxelShapeHolder<T> {

    private final Set<VoxelShape> shapeParts = new HashSet<>();
    private final Set<VoxelShape> partCache = new HashSet<>();

    private VoxelShape mergedShape;
    private Function<T, VoxelShape> extractor;
    private Function<VoxelShape, VoxelShape> postProcess;

    public MergedVoxelShapeHolder<T> setExtractor(Function<T, VoxelShape> extractor) {
        this.extractor = extractor;
        return this;
    }

    public MergedVoxelShapeHolder<T> setPostProcessHook(Function<VoxelShape, VoxelShape> postProcess) {
        this.postProcess = postProcess;
        return this;
    }

    public void clear() {
        shapeParts.clear();
        partCache.clear();
        mergedShape = null;
    }

    public VoxelShape update(Collection<T> things) {
        partCache.clear();
        for (T thing : things) {
            partCache.add(extractor.apply(thing));
        }
        if (!partCache.equals(shapeParts) || mergedShape == null) {
            shapeParts.clear();
            shapeParts.addAll(partCache);
            //Same as VoxelShapes.or(VoxelShapes.empty(), shapeParts.toArray()); Except we skip useless array creation.
            VoxelShape merged = shapeParts.stream().reduce(VoxelShapes.empty(), VoxelShapes::or);
            mergedShape = postProcess.apply(merged);
        }

        return mergedShape;
    }

}
