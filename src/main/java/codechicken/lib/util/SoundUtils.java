package codechicken.lib.util;

import codechicken.lib.vec.Vector3;
import net.minecraft.entity.Entity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

/**
 * Created by covers1624 on 17/10/2016.
 */
public class SoundUtils {

    public static void playSoundAt(Entity entity, SoundCategory category, SoundEvent sound, float volume, float pitch, boolean distanceDelay) {
        entity.worldObj.playSound(entity.posX, entity.posY, entity.posZ, sound, category, volume, pitch, distanceDelay);
    }

    public static void playSoundAt(Entity entity, SoundCategory category, SoundEvent sound, float volume, float pitch) {
        playSoundAt(entity, category, sound, volume, pitch, false);
    }

    public static void playSoundAt(Entity entity, SoundCategory category, SoundEvent sound) {
        playSoundAt(entity, category, sound, 1.0F, 1.0F);
    }

    public static void playSoundAt(Vector3 pos, World world, SoundCategory category, SoundEvent sound, float volume, float pitch, boolean distanceDelay) {
        world.playSound(pos.x, pos.y, pos.z, sound, category, volume, pitch, distanceDelay);
    }
    public static void playSoundAt(Vector3 pos, World world, SoundCategory category, SoundEvent sound, float volume, float pitch) {
        world.playSound(pos.x, pos.y, pos.z, sound, category, volume, pitch, false);
    }

}
