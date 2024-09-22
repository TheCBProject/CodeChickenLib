package codechicken.lib.util;

import codechicken.lib.math.MathHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;

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
        int facing = MathHelper.floor((entity.getYRot() * 4F) / 360F + 0.5D) & 3;
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
        int entityRotation = (int) Math.floor(entity.getYRot() * 4.0F / 360.0F + 0.5D) & 3;
        if (Math.abs(entity.getX() - pos.getX()) < 2.0D && Math.abs(entity.getZ() - pos.getZ()) < 2.0D) {

            double eyeDistance = entity.getY() + 1.82D - pos.getY();

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
        return switch (facing) {
            case NORTH -> Direction.WEST;
            case EAST -> Direction.NORTH;
            case SOUTH -> Direction.EAST;
            case WEST -> Direction.SOUTH;
            default -> throw new IllegalStateException("Unable to get CCW facing of " + facing);
        };
    }

    /**
     * Rotate this Facing around the Y axis counter-clockwise (NORTH => EAST => SOUTH => WEST => NORTH)
     *
     * @param facing Current facing.
     * @return Next facing.
     */
    public static Direction rotateClockwise(Direction facing) {
        return switch (facing) {
            case NORTH -> Direction.EAST;
            case EAST -> Direction.SOUTH;
            case SOUTH -> Direction.WEST;
            case WEST -> Direction.NORTH;
            default -> throw new IllegalStateException("Unable to get CW facing of " + facing);
        };
    }

    /**
     * Rotate this Facing around all axises counter-clockwise (NORTH => SOUTH => EAST => WEST => UP => DOWN => NORTH)
     *
     * @param facing Current facing.
     * @return Next facing.
     */
    public static Direction rotateForward(Direction facing) {
        return switch (facing) {
            case NORTH -> Direction.DOWN;
            case DOWN -> Direction.UP;
            case UP -> Direction.WEST;
            case WEST -> Direction.EAST;
            case EAST -> Direction.SOUTH;
            case SOUTH -> Direction.NORTH;
        };
    }

    /**
     * Rotate this Facing around all axises counter-clockwise (NORTH => DOWN => UP => WEST => EAST => SOUTH => NORTH)
     *
     * @param facing Current facing.
     * @return Next facing.
     */
    public static Direction rotateBackwards(Direction facing) {
        return switch (facing) {
            case NORTH -> Direction.SOUTH;
            case SOUTH -> Direction.EAST;
            case EAST -> Direction.WEST;
            case WEST -> Direction.UP;
            case UP -> Direction.DOWN;
            case DOWN -> Direction.NORTH;
        };
    }

    /**
     * Turns Entity rotation in to Direction.
     *
     * @param rotation The entity rotation, Generally {@code MathHelper.floor_double((entity.rotationYaw * 4F) / 360F + 0.5D) & 3};
     * @return The rotation in Direction.
     */
    public static Direction entityRotationToSide(int rotation) {
        return switch (rotation) {
            case 0 -> Direction.SOUTH;
            case 1 -> Direction.WEST;
            case 2 -> Direction.NORTH;
            default -> Direction.EAST;
        };
    }

}
