package codechicken.lib.internal.proxy;

import codechicken.lib.internal.command.client.DumpModelLocationsCommand;
import codechicken.lib.internal.command.client.NukeCCModelCacheCommand;
import codechicken.lib.internal.network.ClientPacketHandler;
import codechicken.lib.internal.network.PacketDispatcher;
import codechicken.lib.model.ModelRegistryHelper;
import codechicken.lib.model.bakery.ModelBakery;
import codechicken.lib.model.loader.bakery.CCBakeryModelLoader;
import codechicken.lib.model.loader.blockstate.CCBlockStateLoader;
import codechicken.lib.model.loader.cube.CCCubeLoader;
import codechicken.lib.packet.PacketCustom;
import codechicken.lib.render.CCRenderEventHandler;
import codechicken.lib.render.OpenGLUtils;
import codechicken.lib.render.block.BlockRenderingRegistry;
import codechicken.lib.render.item.CCItemRenderer;
import codechicken.lib.render.item.CCRenderItem;
import codechicken.lib.render.item.EntityRendererHooks;
import codechicken.lib.render.item.map.MapRenderRegistry;
import codechicken.lib.render.particle.CustomParticleHandler;
import codechicken.lib.texture.TextureUtils;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.MinecraftForge;

/**
 * Created by covers1624 on 23/11/2016.
 */
public class ProxyClient extends Proxy {

    @Override
    public void preInit() {
        super.preInit();

        OpenGLUtils.loadCaps();
        CustomParticleHandler.init();
        CCBlockStateLoader.initialize();
        ModelBakery.init();
        CCRenderEventHandler.init();

        MinecraftForge.EVENT_BUS.register(new TextureUtils());
        MinecraftForge.EVENT_BUS.register(new MapRenderRegistry());
        MinecraftForge.EVENT_BUS.register(new ModelRegistryHelper());

        ModelLoaderRegistry.registerLoader(CCCubeLoader.INSTANCE);
        ModelLoaderRegistry.registerLoader(CCBakeryModelLoader.INSTANCE);

        PacketCustom.assignHandler(PacketDispatcher.NET_CHANNEL, new ClientPacketHandler());

        ClientCommandHandler.instance.registerCommand(new DumpModelLocationsCommand());
        ClientCommandHandler.instance.registerCommand(new NukeCCModelCacheCommand());
        //TODO, assess if this is enough for a properly modded environment.
        //TODO, Concerns are that this will be registered wayy too late and some mod will fuck it up.
	    EntityRendererHooks.ClientProxy.registerEntitySanitizer();
    }

    @Override
    public void init() {
        BlockRenderingRegistry.init();
        CCItemRenderer.initialize();
        CCRenderItem.init();
    }
}
