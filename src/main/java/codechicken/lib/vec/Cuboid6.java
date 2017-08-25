package codechicken.lib.vec;

import codechicken.lib.util.Copyable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

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

    public Cuboid6(AxisAlignedBB aabb) {
        min = new Vector3(aabb.minX, aabb.minY, aabb.minZ);
        max = new Vector3(aabb.maxX, aabb.maxY, aabb.maxZ);
    }

    public Cuboid6(NBTTagCompound tag) {
        this(Vector3.fromNBT(tag.getCompoundTag("min")), Vector3.fromNBT(tag.getCompoundTag("max")));
    }

    public Cuboid6(Cuboid6 cuboid) {
        min = cuboid.min.copy();
        max = cuboid.max.copy();
    }

    public Cuboid6(double minx, double miny, double minz, double maxx, double maxy, double maxz) {
        min = new Vector3(minx, miny, minz);
        max = new Vector3(maxx, maxy, maxz);
    }

    public AxisAlignedBB aabb() {
        return new AxisAlignedBB(min.x, min.y, min.z, max.x, max.y, max.z);
    }

    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        tag.setTag("min", min.writeToNBT(new NBTTagCompound()));
        tag.setTag("max", max.writeToNBT(new NBTTagCompound()));
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

    public Cuboid6 expandSide(EnumFacing side, int amount) {
        switch (side.getAxisDirection()) {
            case NEGATIVE:
                min.add(side.getDirectionVec().getX() * amount, side.getDirectionVec().getY() * amount, side.getDirectionVec().getZ() * amount);
                break;
            case POSITIVE:
                max.add(side.getDirectionVec().getX() * amount, side.getDirectionVec().getY() * amount, side.getDirectionVec().getZ() * amount);
                break;
        }
        return this;
    }
    
    public Cuboid6 shrinkSide(EnumFacing side, int amount) {
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

    public double getSideSize(EnumFacing side) {
        switch (side.getAxis()) {
            case X:
                return (max.x - min.x) + 1;
            case Y:
                return (max.y - min.y) + 1;
            case Z:
                return (max.z - min.z) + 1;
        }
        return 0;
    }
    
    public double getSide(EnumFacing side) {
        switch (side) {
            case DOWN:
                return min.y;
            case UP:
                return max.y;
            case NORTH:
                return min.z;
            case SOUTH:
                return max.z;
            case WEST:
                return min.x;
            case EAST:
                return max.x;
        }
        return 0;
    }

    @Deprecated
    public double getSide(int s)
    {
        return getSide(EnumFacing.values()[s]);
    }

    public Cuboid6 setSide(EnumFacing side, double d) {
        switch (side) {
            case DOWN:
                min.y = d;
                break;
            case UP:
                max.y = d;
                break;
            case NORTH:
                min.z = d;
                break;
            case SOUTH:
                max.z = d;
                break;
            case WEST:
                min.x = d;
                break;
            case EAST:
                max.x = d;
                break;
        }
        return this;
    }

    @Deprecated
    public Cuboid6 setSide(int s, double d)
    {
        return setSide(EnumFacing.values()[s], d);
    }

    @Override
    public boolean equals(Object obj) {
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
