package codechicken.lib.render.item;

import codechicken.lib.render.item.entity.WrappedEntityItemRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.lang.reflect.Field;

/**
 * Created by covers1624 on 31/10/2016.
 */
//Used to make sure all references to RenderItem are of CCL's overridden renderer.
//@Mod (modid = "ccl-entityhook", dependencies = "before:*")
@Deprecated
public class EntityRendererHooks {

    private static boolean hasSanitized;

    @SidedProxy
    public static ServerProxy proxy;

    @EventHandler
    public void preInt(FMLPreInitializationEvent event) {
        proxy.preInit(event);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }

    @SideOnly (Side.CLIENT)
    public static class ClientProxy extends ServerProxy {

        @Override
        @SideOnly (Side.CLIENT)
        public void preInit(FMLPreInitializationEvent event) {
            registerEntitySanitizer();
        }

        @SideOnly (Side.CLIENT)
        public static void registerEntitySanitizer() {
            RenderingRegistry.registerEntityRenderingHandler(DummyEntity.class, manager -> {
                sanitizeEntityRenderers(manager);
                return new Render<DummyEntity>(manager) {
                    @Override
                    protected ResourceLocation getEntityTexture(DummyEntity entity) {
                        return null;
                    }
                };
            });
        }

        @Override
        @SideOnly (Side.CLIENT)
        @SuppressWarnings ("unchecked")
        public void postInit(FMLPostInitializationEvent event) {
            RenderManager manager = Minecraft.getMinecraft().getRenderManager();

            Render<EntityItem> render = (Render<EntityItem>) manager.entityRenderMap.get(EntityItem.class);
            if (render == null) {
                throw new RuntimeException("EntityItem does not have a Render bound... This is likely a bug..");
            }
            manager.entityRenderMap.put(EntityItem.class, new WrappedEntityItemRenderer(manager, render));
        }

        @SideOnly (Side.CLIENT)
        public static void sanitizeEntityRenderers(RenderManager renderManager) {
            if (!hasSanitized) {
                try {
                    for (Render<? extends Entity> render : renderManager.entityRenderMap.values()) {
                        if (render != null) {
                            for (Field field : render.getClass().getDeclaredFields()) {
                                if (field.getType().equals(RenderItem.class)) {
                                    field.setAccessible(true);
                                    field.set(render, CCRenderItem.getOverridenRenderItem());
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    throw new RuntimeException("Unable to reflect an EntityRenderer!", e);
                }
                hasSanitized = true;
            }
        }
    }

    //@formatter:off
    public static class ServerProxy {
        public void preInit(FMLPreInitializationEvent event) {}
        public void postInit(FMLPostInitializationEvent event) {}
    }

    @SideOnly (Side.CLIENT)
    public class DummyEntity extends Entity {
        public DummyEntity(World worldIn) {super(worldIn);}
        @Override protected void entityInit() {}
        @Override protected void readEntityFromNBT(NBTTagCompound compound) {}
        @Override protected void writeEntityToNBT(NBTTagCompound compound) {}
    }
    //@formatter:on

}
