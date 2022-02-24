package codechicken.lib.vec;

import codechicken.lib.raytracer.VoxelShapeCache;
import codechicken.lib.util.Copyable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class Cuboid6 implements Copyable<Cuboid6> {

    public static Cuboid6 full = new Cuboid6(0, 0, 0, 1, 1, 1);

    public Vector3 min;
    public Vector3 max;

    public Cuboid6() {
        this(new Vector3(), new Vector3());
    }

    public Cuboid6(Vector3 min, Vector3 max) {
        this.min = min;
        this.max = max;
    }

    public Cuboid6(Vec3i min, Vec3i max) {
        this.min = Vector3.fromVec3i(min);
        this.max = Vector3.fromVec3i(max);
    }

    public Cuboid6(AABB aabb) {
        min = new Vector3(aabb.minX, aabb.minY, aabb.minZ);
        max = new Vector3(aabb.maxX, aabb.maxY, aabb.maxZ);
    }

    public Cuboid6(CompoundTag tag) {
        this(Vector3.fromNBT(tag.getCompound("min")), Vector3.fromNBT(tag.getCompound("max")));
    }

    public Cuboid6(Cuboid6 cuboid) {
        min = cuboid.min.copy();
        max = cuboid.max.copy();
    }

    public Cuboid6(double minx, double miny, double minz, double maxx, double maxy, double maxz) {
        min = new Vector3(minx, miny, minz);
        max = new Vector3(maxx, maxy, maxz);
    }

    public AABB aabb() {
        return new AABB(min.x, min.y, min.z, max.x, max.y, max.z);
    }

    public VoxelShape shape() {
        return VoxelShapeCache.getShape(this);
    }

    public CompoundTag writeToNBT(CompoundTag tag) {
        tag.put("min", min.writeToNBT(new CompoundTag()));
        tag.put("max", max.writeToNBT(new CompoundTag()));
        return tag;
    }

    public Cuboid6 set(double minx, double miny, double minz, double maxx, double maxy, double maxz) {
        min.set(minx, miny, minz);
        max.set(maxx, maxy, maxz);
        return this;
    }

    public Cuboid6 set(Vector3 min, Vector3 max) {
        return set(min.x, min.y, min.z, max.x, max.y, max.z);
    }

    public Cuboid6 set(Vec3i min, Vec3i max) {
        return set(min.getX(), min.getY(), min.getZ(), max.getX(), max.getY(), max.getZ());
    }

    public Cuboid6 set(Cuboid6 c) {
        return set(c.min.x, c.min.y, c.min.z, c.max.x, c.max.y, c.max.z);
    }

    public Cuboid6 set(AABB bb) {
        return set(bb.minX, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ);
    }

    public Cuboid6 add(double dx, double dy, double dz) {
        min.add(dx, dy, dz);
        max.add(dx, dy, dz);
        return this;
    }

    public Cuboid6 add(double d) {
        return add(d, d, d);
    }

    public Cuboid6 add(Vector3 vec) {
        return add(vec.x, vec.y, vec.z);
    }

    public Cuboid6 add(Vec3i vec) {
        return add(vec.getX(), vec.getY(), vec.getZ());
    }

    public Cuboid6 add(BlockPos pos) {
        return add(pos.getX(), pos.getY(), pos.getZ());
    }

    public Cuboid6 subtract(double dx, double dy, double dz) {
        min.subtract(dx, dy, dz);
        max.subtract(dx, dy, dz);
        return this;
    }

    public Cuboid6 subtract(double d) {
        return subtract(d, d, d);
    }

    public Cuboid6 subtract(Vector3 vec) {
        return subtract(vec.x, vec.y, vec.z);
    }

    public Cuboid6 subtract(Vec3i vec) {
        return subtract(vec.getX(), vec.getY(), vec.getZ());
    }

    public Cuboid6 subtract(Vec3 vec) {
        return subtract(vec.x, vec.y, vec.z);
    }

    public Cuboid6 subtract(BlockPos pos) {
        return subtract(pos.getX(), pos.getY(), pos.getZ());
    }

    public Cuboid6 expand(double dx, double dy, double dz) {
        min.subtract(dx, dy, dz);
        max.add(dx, dy, dz);
        return this;
    }

    public Cuboid6 expand(double d) {
        return expand(d, d, d);
    }

    public Cuboid6 expand(Vector3 vec) {
        return expand(vec.x, vec.y, vec.z);
    }

    public Cuboid6 expandSide(Direction side, int amount) {
        switch (side.getAxisDirection()) {
            case NEGATIVE -> min.add(Vector3.fromVec3i(side.getNormal()).multiply(amount));
            case POSITIVE -> max.add(Vector3.fromVec3i(side.getNormal()).multiply(amount));
        }
        return this;
    }

    public Cuboid6 shrinkSide(Direction side, int amount) {
        expandSide(side, -amount);
        return this;
    }

    public Cuboid6 offset(Cuboid6 o) {
        min.add(o.min);
        max.add(o.max);
        return this;
    }

    public Cuboid6 enclose(double minx, double miny, double minz, double maxx, double maxy, double maxz) {
        if (min.x > minx) {
            min.x = minx;
        }
        if (min.y > miny) {
            min.y = miny;
        }
        if (min.z > minz) {
            min.z = minz;
        }
        if (max.x < maxx) {
            max.x = maxx;
        }
        if (max.y < maxy) {
            max.y = maxy;
        }
        if (max.z < maxz) {
            max.z = maxz;
        }
        return this;
    }

    public Cuboid6 enclose(double x, double y, double z) {
        return enclose(x, y, z, x, y, z);
    }

    public Cuboid6 enclose(Vector3 vec) {
        return enclose(vec.x, vec.y, vec.z, vec.x, vec.y, vec.z);
    }

    public Cuboid6 enclose(Cuboid6 c) {
        return enclose(c.min.x, c.min.y, c.min.z, c.max.x, c.max.y, c.max.z);
    }

    public boolean contains(double x, double y, double z) {
        return min.x - 1E-5 <= x && min.y - 1E-5 <= y && min.z - 1E-5 <= z && max.x + 1E-5 >= x && max.y + 1E-5 >= y && max.z + 1E-5 >= z;
    }

    public boolean contains(Vector3 vec) {
        return contains(vec.x, vec.y, vec.z);
    }

    public boolean intersects(Cuboid6 b) {
        return max.x - 1E-5 > b.min.x && max.y - 1E-5 > b.min.y && max.z - 1E-5 > b.min.z && b.max.x - 1E-5 > min.x && b.max.y - 1E-5 > min.y && b.max.z - 1E-5 > min.z;
    }

    public double volume() {
        return (max.x - min.x + 1) * (max.y - min.y + 1) * (max.z - min.z + 1);
    }

    public Vector3 center() {
        return min.copy().add(max).multiply(0.5);
    }

    public double getSideSize(Direction side) {
        return switch (side.getAxis()) {
            case X -> (max.x - min.x) + 1;
            case Y -> (max.y - min.y) + 1;
            case Z -> (max.z - min.z) + 1;
        };
    }

    public double getSide(int side) {
        return switch (side) {
            case 0 -> min.y;
            case 1 -> max.y;
            case 2 -> min.z;
            case 3 -> max.z;
            case 4 -> min.x;
            case 5 -> max.x;
            default -> 0;
        };
    }

    public double getSide(Direction side) {
        return getSide(side.ordinal());
    }

    public Cuboid6 setSide(int side, double d) {
        switch (side) {
            case 0 -> min.y = d;
            case 1 -> max.y = d;
            case 2 -> min.z = d;
            case 3 -> max.z = d;
            case 4 -> min.x = d;
            case 5 -> max.x = d;
        }
        return this;
    }

    public Cuboid6 setSide(Direction side, double d) {
        return setSide(side.ordinal(), d);
    }

    public int hashCode() {
        long i = Double.doubleToLongBits(min.x);
        int j = (int) (i ^ i >>> 32);
        i = Double.doubleToLongBits(min.y);
        j = 31 * j + (int) (i ^ i >>> 32);
        i = Double.doubleToLongBits(min.z);
        j = 31 * j + (int) (i ^ i >>> 32);
        i = Double.doubleToLongBits(max.x);
        j = 31 * j + (int) (i ^ i >>> 32);
        i = Double.doubleToLongBits(max.y);
        j = 31 * j + (int) (i ^ i >>> 32);
        i = Double.doubleToLongBits(max.z);
        j = 31 * j + (int) (i ^ i >>> 32);
        return j;
    }

    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj)) {
            return true;
        }
        if (!(obj instanceof Cuboid6)) {
            return false;
        }
        Cuboid6 c = (Cuboid6) obj;
        return min.equals(c.min) && max.equals(c.max);
    }

    public boolean equalsT(Cuboid6 c) {
        return min.equalsT(c.min) && max.equalsT(c.max);
    }

    public Cuboid6 copy() {
        return new Cuboid6(this);
    }

    public String toString() {
        MathContext cont = new MathContext(4, RoundingMode.HALF_UP);
        return "Cuboid: (" + new BigDecimal(min.x, cont) + ", " + new BigDecimal(min.y, cont) + ", " + new BigDecimal(min.z, cont) + ") -> (" + new BigDecimal(max.x, cont) + ", " + new BigDecimal(max.y, cont) + ", " + new BigDecimal(max.z, cont) + ")";
    }

    public Cuboid6 apply(Transformation t) {
        t.apply(min);
        t.apply(max);
        double temp;
        if (min.x > max.x) {
            temp = min.x;
            min.x = max.x;
            max.x = temp;
        }
        if (min.y > max.y) {
            temp = min.y;
            min.y = max.y;
            max.y = temp;
        }
        if (min.z > max.z) {
            temp = min.z;
            min.z = max.z;
            max.z = temp;
        }
        return this;
    }
}
