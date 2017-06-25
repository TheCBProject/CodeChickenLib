package codechicken.lib.model.bakery.sub;

import codechicken.lib.model.bakery.ModelBakery;
import codechicken.lib.model.bakery.generation.IBlockBakery;
import codechicken.lib.model.bakery.generation.ILayeredBlockBakery;
import codechicken.lib.model.bakery.generation.ISimpleBlockBakery;
import codechicken.lib.model.bakery.key.IBlockStateKeyGenerator;
import codechicken.lib.model.bakery.key.IItemStackKeyGenerator;
import codechicken.lib.texture.TextureUtils.IIconRegister;
import com.google.common.collect.ImmutableList;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by covers1624 on 26/12/2016.
 * TODO Document.
 */
public class SubBlockBakery implements ILayeredBlockBakery, IIconRegister {

    private final Map<Integer, IBlockBakery> subBakeries = new HashMap<>();
    private final SubBlockStateKeyGenerator blockKeyGenerator = new SubBlockStateKeyGenerator();
    private final SubItemStackKeyGenerator itemKeyGenerator = new SubItemStackKeyGenerator();

    public void registerSubBakery(int meta, IBlockBakery bakery) {
        subBakeries.put(meta, bakery);
    }

    public void registerSubBakery(int meta, IBlockBakery bakery, IBlockStateKeyGenerator blockKeyGen) {
        registerSubBakery(meta, bakery, blockKeyGen, null);
    }

    public void registerSubBakery(int meta, IBlockBakery bakery, IItemStackKeyGenerator itemKeyGen) {
        registerSubBakery(meta, bakery, null, itemKeyGen);
    }

    public void registerSubBakery(int meta, IBlockBakery bakery, IBlockStateKeyGenerator blockKeyGen, IItemStackKeyGenerator itemKeyGen) {
        subBakeries.put(meta, bakery);
        if (blockKeyGen != null) {
            blockKeyGenerator.register(meta, blockKeyGen);
        }
        if (itemKeyGen != null) {
            itemKeyGenerator.register(meta, itemKeyGen);
        }
    }

    public SubBlockBakery registerKeyGens(Block block) {
        registerBlockKeyGen(block);
        registerItemKeyGen(Item.getItemFromBlock(block));
        return this;
    }

    public SubBlockBakery registerBlockKeyGen(Block block) {
        ModelBakery.registerBlockKeyGenerator(block, blockKeyGenerator);
        return this;
    }

    public SubBlockBakery registerItemKeyGen(Item block) {
        ModelBakery.registerItemKeyGenerator(block, itemKeyGenerator);
        return this;
    }

    public SubBlockStateKeyGenerator getBlockKeyGen() {
        return blockKeyGenerator;
    }

    public SubItemStackKeyGenerator getItemKeyGen() {
        return itemKeyGenerator;
    }

    @Override
    @SideOnly (Side.CLIENT)
    public List<BakedQuad> bakeLayerFace(EnumFacing face, BlockRenderLayer layer, IExtendedBlockState state) {
        IBlockBakery bakery = subBakeries.get(state.getBlock().getMetaFromState(state));
        if (bakery instanceof ISimpleBlockBakery) {
            if (state.getBlock().canRenderInLayer(state, layer)) {
                return ((ISimpleBlockBakery) bakery).bakeQuads(face, state);
            }
        } else if (bakery instanceof ILayeredBlockBakery) {
            return ((ILayeredBlockBakery) bakery).bakeLayerFace(face, layer, state);
        }
        return ImmutableList.of();
    }

    @Override
    @SideOnly (Side.CLIENT)
    public IExtendedBlockState handleState(IExtendedBlockState state, IBlockAccess world, BlockPos pos) {
        IBlockBakery bakery = subBakeries.get(state.getBlock().getMetaFromState(state));
        if (bakery == null) {
            return state;
        }
        return bakery.handleState(state, world, pos);
    }

    @Override
    @SideOnly (Side.CLIENT)
    public List<BakedQuad> bakeItemQuads(EnumFacing face, ItemStack stack) {
        IBlockBakery bakery = subBakeries.get(stack.getMetadata());
        if (bakery == null) {
            return ImmutableList.of();
        }
        return bakery.bakeItemQuads(face, stack);
    }

    @Override
    @SideOnly (Side.CLIENT)
    public void registerIcons(TextureMap textureMap) {
        for (IBlockBakery bakery : subBakeries.values()) {
            if (bakery instanceof IIconRegister) {
                ((IIconRegister) bakery).registerIcons(textureMap);
            }
        }
    }
}
