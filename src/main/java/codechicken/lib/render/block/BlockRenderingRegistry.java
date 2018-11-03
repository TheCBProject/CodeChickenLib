package codechicken.lib.render.block;

import codechicken.lib.model.bakedmodels.ModelProperties;
import codechicken.lib.model.bakedmodels.PerspectiveAwareBakedModel;
import codechicken.lib.reflect.ObfMapping;
import codechicken.lib.reflect.ReflectionManager;
import codechicken.lib.render.buffer.BakingVertexBuffer;
import codechicken.lib.texture.TextureUtils;
import codechicken.lib.texture.TextureUtils.IIconRegister;
import codechicken.lib.util.TransformUtils;
import com.google.common.collect.ImmutableList;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelRenderer;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.HashMap;
import java.util.Map;

/**
 * TODO, Individual ICCBlockRenderer's should implement IIconRegister and register it themselves.
 * Created by covers1624 on 8/09/2016.
 */
public class BlockRenderingRegistry {

    private static final Map<EnumBlockRenderType, ICCBlockRenderer> blockRendererList = new HashMap<>();
    private static final ImmutableList<EnumBlockRenderType> vanillaRenderTypes = ImmutableList.copyOf(EnumBlockRenderType.values());

    private static boolean initialized = false;

    @SideOnly (Side.CLIENT)
    public static void init() {
        if (!initialized) {
            Minecraft mc = Minecraft.getMinecraft();
            BlockRendererDispatcher parentDispatcher = mc.getBlockRendererDispatcher();
            CCBlockRendererDispatcher newDispatcher = new CCBlockRendererDispatcher(parentDispatcher, mc.getBlockColors());

            ObfMapping mapping = new ObfMapping("net/minecraft/client/Minecraft", "field_175618_aM");
            ReflectionManager.setField(mapping, mc, newDispatcher);

            TextureUtils.addIconRegister(newDispatcher);

            initialized = true;
        }
    }

    /**
     * Used to create a new EnumBlockRenderType, make sure you return this on both client and server.
     *
     * @param name The name for the enum.
     * @return The new enum.
     */
    public static EnumBlockRenderType createRenderType(String name) {
        return EnumHelper.addEnum(EnumBlockRenderType.class, name, new Class[0]);
    }

    @SideOnly (Side.CLIENT)
    public static boolean canHandle(EnumBlockRenderType type) {
        return blockRendererList.containsKey(type);
    }

    @SideOnly (Side.CLIENT)
    public static void registerRenderer(EnumBlockRenderType type, ICCBlockRenderer renderer) {
        if (vanillaRenderTypes.contains(type)) {
            throw new IllegalArgumentException("Invalid EnumBlockRenderType! " + type.name());
        }
        if (blockRendererList.containsKey(type)) {
            throw new IllegalArgumentException("Unable to register duplicate render type!" + type.name());
        }
        blockRendererList.put(type, renderer);
        if (renderer instanceof IIconRegister) {
            TextureUtils.addIconRegister((IIconRegister) renderer);
        }
    }

    @SideOnly (Side.CLIENT)
    static void renderBlockDamage(IBlockAccess world, BlockPos pos, IBlockState state, TextureAtlasSprite sprite) {
        ICCBlockRenderer renderer = blockRendererList.get(state.getRenderType());
        if (renderer != null) {
            state = state.getActualState(world, pos);
            //TODO This needs to be optimized, probably not the most efficient thing in the world..
            BufferBuilder parent = Tessellator.getInstance().getBuffer();
            BakingVertexBuffer buffer = BakingVertexBuffer.create();
            buffer.setTranslation(-pos.getX(), -pos.getY(), -pos.getZ());
            buffer.begin(7, parent.getVertexFormat());
            renderer.handleRenderBlockDamage(world, pos, state, sprite, buffer);
            buffer.finishDrawing();
            buffer.setTranslation(0, 0, 0);
            BlockModelRenderer modelRenderer = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelRenderer();
            modelRenderer.renderModel(world, new PerspectiveAwareBakedModel(buffer.bake(), TransformUtils.DEFAULT_BLOCK, new ModelProperties(true, true, null)), state, pos, parent, true);
        }
    }

    @SideOnly (Side.CLIENT)
    static boolean renderBlock(IBlockAccess world, BlockPos pos, IBlockState state, BufferBuilder buffer) {
        ICCBlockRenderer renderer = blockRendererList.get(state.getRenderType());
        return renderer != null && renderer.renderBlock(world, pos, state, buffer);
    }

    @SideOnly (Side.CLIENT)
    static void renderBlockBrightness(IBlockState state, float brightness) {
        ICCBlockRenderer renderer = blockRendererList.get(state.getRenderType());
        if (renderer != null) {
            renderer.renderBrightness(state, brightness);
        }
    }

    @Deprecated
    @SideOnly (Side.CLIENT)
    static void registerTextures(TextureMap map) {
        for (ICCBlockRenderer renderer : blockRendererList.values()) {
            renderer.registerTextures(map);
        }
    }
}
