package codechicken.lib.util;

import codechicken.lib.math.MathHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

/**
 * Contains a bunch of stuff to do with rotation.
 * Created by covers1624 on 6/30/2016.
 */
public class RotationUtils {

    /**
     * Gets the rotation for placing a block only on the horizon.
     *
     * @param entity Entity placing block.
     * @return Direction placed.
     */
    public static Direction getPlacedRotationHorizontal(LivingEntity entity) {
        int facing = MathHelper.floor((entity.rotationYaw * 4F) / 360F + 0.5D) & 3;
        return entityRotationToSide(facing).getOpposite();
    }

    /**
     * Gets rotation for placing a block, Will use Up and Down.
     *
     * @param pos    Pos placement is happening.
     * @param entity Entity placing block.
     * @return Direction placed.
     */
    public static Direction getPlacedRotation(BlockPos pos, LivingEntity entity) {
        int entityRotation = (int) Math.floor(entity.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
        if (Math.abs(entity.posX - pos.getX()) < 2.0D && Math.abs(entity.posZ - pos.getZ()) < 2.0D) {

            double eyeDistance = entity.posY + 1.82D - pos.getY();

            if (eyeDistance > 2.0D) {
                return Direction.DOWN;
            }

            if (eyeDistance < 0.0D) {
                return Direction.UP;
            }
        }

        return entityRotationToSide(entityRotation);
    }

    /**
     * Short hand for getPlacedRotationHorizontal and getPlacedRotation.
     *
     * @param pos         Pos placement is happening.
     * @param entity      Entity placing block.
     * @param onlyHorizon True if should only obey the horizon.
     * @return Direction placed.
     */
    public static Direction getPlacedRotation(BlockPos pos, LivingEntity entity, boolean onlyHorizon) {
        if (onlyHorizon) {
            return getPlacedRotationHorizontal(entity);
        }
        return getPlacedRotation(pos, entity);
    }

    /**
     * Rotate this Facing around the Y axis counter-clockwise (NORTH => WEST => SOUTH => EAST => NORTH)
     *
     * @param facing Current facing.
     * @return Next facing.
     */
    public static Direction rotateCounterClockwise(Direction facing) {
        switch (facing) {
            case NORTH:
                return Direction.WEST;
            case EAST:
                return Direction.NORTH;
            case SOUTH:
                return Direction.EAST;
            case WEST:
                return Direction.SOUTH;
            default:
                throw new IllegalStateException("Unable to get CCW facing of " + facing);
        }
    }

    /**
     * Rotate this Facing around the Y axis counter-clockwise (NORTH => EAST => SOUTH => WEST => NORTH)
     *
     * @param facing Current facing.
     * @return Next facing.
     */
    public static Direction rotateClockwise(Direction facing) {
        switch (facing) {
            case NORTH:
                return Direction.EAST;
            case EAST:
                return Direction.SOUTH;
            case SOUTH:
                return Direction.WEST;
            case WEST:
                return Direction.NORTH;
            default:
                throw new IllegalStateException("Unable to get CW facing of " + facing);
        }
    }

    /**
     * Rotate this Facing around all axises counter-clockwise (NORTH => SOUTH => EAST => WEST => UP => DOWN => NORTH)
     *
     * @param facing Current facing.
     * @return Next facing.
     */
    public static Direction rotateForward(Direction facing) {
        switch (facing) {
            case NORTH:
                return Direction.DOWN;
            case DOWN:
                return Direction.UP;
            case UP:
                return Direction.WEST;
            case WEST:
                return Direction.EAST;
            case EAST:
                return Direction.SOUTH;
            case SOUTH:
                return Direction.NORTH;
        }
        return Direction.NORTH;
    }

    /**
     * Rotate this Facing around all axises counter-clockwise (NORTH => DOWN => UP => WEST => EAST => SOUTH => NORTH)
     *
     * @param facing Current facing.
     * @return Next facing.
     */
    public static Direction rotateBackwards(Direction facing) {
        switch (facing) {
            case NORTH:
                return Direction.SOUTH;
            case SOUTH:
                return Direction.EAST;
            case EAST:
                return Direction.WEST;
            case WEST:
                return Direction.UP;
            case UP:
                return Direction.DOWN;
            case DOWN:
                return Direction.NORTH;
        }
        return Direction.NORTH;
    }

    /**
     * Turns Entity rotation in to Direction.
     *
     * @param rotation The entity rotation, Generally MathHelper.floor_double((entity.rotationYaw * 4F) / 360F + 0.5D) & 3;
     * @return The rotation in Direction.
     */
    public static Direction entityRotationToSide(int rotation) {
        switch (rotation) {
            case 0:
                return Direction.SOUTH;
            case 1:
                return Direction.WEST;
            case 2:
                return Direction.NORTH;
            default:
                return Direction.EAST;
        }
    }

}
