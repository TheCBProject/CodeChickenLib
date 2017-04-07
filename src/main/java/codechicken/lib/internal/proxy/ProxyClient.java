package codechicken.lib.internal.proxy;

import codechicken.lib.internal.command.client.DumpModelLocationsCommand;
import codechicken.lib.internal.command.client.NukeCCModelCacheCommand;
import codechicken.lib.model.ModelRegistryHelper;
import codechicken.lib.model.bakery.ModelBakery;
import codechicken.lib.model.loader.bakery.CCBakeryModelLoader;
import codechicken.lib.model.loader.cube.CCCubeLoader;
import codechicken.lib.render.CCRenderEventHandler;
import codechicken.lib.render.block.BlockRenderingRegistry;
import codechicken.lib.render.item.CCRenderItem;
import codechicken.lib.render.item.map.MapRenderRegistry;
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
        ModelBakery.init();
        CCRenderEventHandler.init();
        MinecraftForge.EVENT_BUS.register(new MapRenderRegistry());
        MinecraftForge.EVENT_BUS.register(new ModelRegistryHelper());
        ModelLoaderRegistry.registerLoader(CCCubeLoader.INSTANCE);
        ModelLoaderRegistry.registerLoader(CCBakeryModelLoader.INSTANCE);
        ClientCommandHandler.instance.registerCommand(new DumpModelLocationsCommand());
        ClientCommandHandler.instance.registerCommand(new NukeCCModelCacheCommand());
    }

    @Override
    public void init() {
        BlockRenderingRegistry.init();
        CCRenderItem.init();
    }
}
