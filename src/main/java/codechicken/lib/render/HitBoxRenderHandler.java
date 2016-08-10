package codechicken.lib.render;

import codechicken.lib.raytracer.CuboidRayTraceResult;
import codechicken.lib.vec.Vector3;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by covers1624 on 5/16/2016.
 * Make sure to call init if you want to use this and CCC is not available.
 */
public class HitBoxRenderHandler {
    private static boolean hasInit = false;

    public static void init() {
        if (!hasInit) {
            MinecraftForge.EVENT_BUS.register(new HitBoxRenderHandler());
            hasInit = true;
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onBlockHighlight(DrawBlockHighlightEvent event) {
        World world = event.getPlayer().worldObj;
        EntityPlayer player = event.getPlayer();
        BlockPos pos = event.getTarget().getBlockPos();

        //We have found a CuboidRayTraceResult, Lets render it properly..
        if (event.getTarget().typeOfHit == Type.BLOCK && event.getTarget() instanceof CuboidRayTraceResult) {
            event.setCanceled(true);
            RenderUtils.renderHitBox(event.getPlayer(), ((CuboidRayTraceResult) event.getTarget()).cuboid6.copy().add(new Vector3(pos)), event.getPartialTicks());
        }
    }

}
