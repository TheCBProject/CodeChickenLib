package codechicken.lib.vec;

import codechicken.lib.raytracer.IndexedCuboid6;

import java.util.List;

/**
 * Created by covers1624 on 4/13/2016.
 * TODO Polish.
 */
public interface IIndexedCuboidProvider {

    IndexedCuboid6 getBlockBounds();

    List<IndexedCuboid6> getIndexedCuboids();

}
