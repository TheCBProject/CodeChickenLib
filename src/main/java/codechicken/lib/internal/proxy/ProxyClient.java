package codechicken.lib.internal.proxy;

import codechicken.lib.CodeChickenLib;
import codechicken.lib.configuration.ConfigTag;
import codechicken.lib.model.ModelRegistryHelper;
import codechicken.lib.model.bakery.ModelBakery;
import codechicken.lib.render.CCRenderEventHandler;
import codechicken.lib.render.block.BlockRenderingRegistry;
import codechicken.lib.render.item.map.MapRenderRegistry;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import java.lang.reflect.Field;

/**
 * Created by covers1624 on 30/10/19.
 */
public class ProxyClient extends Proxy {

    public static ModelRegistryHelper modelHelper = new ModelRegistryHelper();
    public static boolean catchBlockRenderExceptions;
    public static boolean catchItemRenderExceptions;
    public static boolean attemptRecoveryOnItemRenderException;
    public static boolean messagePlayerOnRenderExceptionCaught;
    private static boolean hasSanitized;

    @Override
    public void clientSetup(FMLClientSetupEvent event) {
        loadClientConfig();
        //OpenGLUtils.loadCaps();
        //        CustomParticleHandler.init();
        //        CCBlockStateLoader.initialize();
        BlockRenderingRegistry.init();
        //CCRenderItem.init();
        ModelBakery.init();
        CCRenderEventHandler.init();
        //Minecraft.getInstance().ingameGUI.itemRenderer = CCRenderItem.getOverridenItemRender();

        MinecraftForge.EVENT_BUS.register(new MapRenderRegistry());
        //        ModelLoaderRegistry.registerLoader(CCCubeLoader.INSTANCE);
        //        ModelLoaderRegistry.registerLoader(CCBakeryModelLoader.INSTANCE);

        //        PacketCustom.assignHandler(PacketDispatcher.NET_CHANNEL, new ClientPacketHandler());
        //
        //        ClientCommandHandler.instance.registerCommand(new CCLClientCommand());

        RenderingRegistry.registerEntityRenderingHandler(DummyEntity.class, manager -> {
            sanitizeEntityRenderers(manager);
            return new EntityRenderer<DummyEntity>(manager) {
                protected ResourceLocation getEntityTexture(DummyEntity entity) {
                    return null;
                }
            };
        });
    }

    @OnlyIn (Dist.CLIENT)
    public static void sanitizeEntityRenderers(EntityRendererManager renderManager) {
        if (!hasSanitized) {
            try {
                for (EntityRenderer<? extends Entity> render : renderManager.renderers.values()) {
                    if (render != null) {
                        for (Field field : render.getClass().getDeclaredFields()) {
                            if (field.getType().equals(ItemRenderer.class)) {
                                field.setAccessible(true);
                                //field.set(render, CCRenderItem.getOverridenItemRender());
                            }
                        }
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException("Unable to reflect an EntityRenderer!", e);
            }
            renderManager.renderers.remove(DummyEntity.class);
            hasSanitized = true;
        }
    }

    private void loadClientConfig() {
        ConfigTag tag;
        ConfigTag clientTag = CodeChickenLib.config.getTag("client");
        clientTag.deleteTag("block_renderer_dispatcher_misc");

        tag = clientTag.getTag("catchBlockRenderExceptions")//
                .setComment(//
                        "With this enabled, CCL will catch all exceptions thrown whilst rendering blocks.",//
                        "If an exception is caught, the block will not be rendered."//
                );
        catchBlockRenderExceptions = tag.setDefaultBoolean(true).getBoolean();
        tag = clientTag.getTag("catchItemRenderExceptions")//
                .setComment(//
                        "With this enabled, CCL will catch all exceptions thrown whilst rendering items.",//
                        "By default CCL will only enhance the crash report, but with 'attemptRecoveryOnItemRenderException' enabled",//
                        " CCL will attempt to recover after the exception."//
                );
        catchItemRenderExceptions = tag.setDefaultBoolean(true).getBoolean();
        tag = clientTag.getTag("attemptRecoveryOnItemRenderException")//
                .setComment(//
                        "With this enabled, CCL will attempt to recover item rendering after an exception is thrown.",//
                        "It is recommended to only enable this when a mod has a known bug and a fix has not been released yet.",//
                        "WARNING: This might cause issues with some mods, Some mods modify the GL state rendering items,",//
                        "  CCL does not recover the GL state, as a result a GL leak /may/ occur. However, CCL will remember",//
                        "  and pop the GL ModelView matrix stack depth, this might incur a bit of a performance hit.",//
                        "  Some mods might also have custom BufferBuilders, CCL has no way of recovering the state of those.",//
                        "  this /can/ result in 'Already Building' exceptions being thrown. CCL will however recover the vanilla BufferBuilder."//
                );
        attemptRecoveryOnItemRenderException = tag.setDefaultBoolean(false).getBoolean();
        tag = clientTag.getTag("messagePlayerOnRenderCrashCaught")//
                .setComment(//
                        "With this enabled, CCL will message the player upon an exception from rendering blocks or items.",//
                        "Messages are Rate-Limited to one per 5 seconds in the event that the exception continues."//
                );
        messagePlayerOnRenderExceptionCaught = tag.setDefaultBoolean(true).getBoolean();

        clientTag.save();
    }

    //@formatter:off
    @OnlyIn (Dist.CLIENT)
    public class DummyEntity extends Entity {
        public DummyEntity(EntityType<?> entityTypeIn, World worldIn) { super(entityTypeIn, worldIn); }
        @Override protected void registerData() { }
        @Override protected void readAdditional(CompoundNBT compound) { }
        @Override protected void writeAdditional(CompoundNBT compound) { }
        @Override public IPacket<?> createSpawnPacket() { return null; }
    }
    //@formatter:on
}
