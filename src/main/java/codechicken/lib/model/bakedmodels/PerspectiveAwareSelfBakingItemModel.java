package codechicken.lib.model.bakedmodels;

import codechicken.lib.model.ItemQuadBakery;
import codechicken.lib.texture.TextureUtils;
import codechicken.lib.util.TransformUtils;
import com.google.common.collect.ImmutableList;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IPerspectiveAwareModel;
import net.minecraftforge.client.model.ItemLayerModel;
import net.minecraftforge.common.model.IModelState;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by covers1624 on 7/18/2016.
 */
public class PerspectiveAwareSelfBakingItemModel implements IPerspectiveAwareModel {

    private List<BakedQuad> quads;
    private TextureAtlasSprite sprite;
    private ResourceLocation spriteName;
    private IModelState modelState;

    public PerspectiveAwareSelfBakingItemModel(ResourceLocation sprite) {
        this(sprite, TransformUtils.DEFAULT_ITEM);
    }

    public PerspectiveAwareSelfBakingItemModel(ResourceLocation sprite, IModelState modelState) {
        spriteName = sprite;
        this.modelState = modelState;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
        if (state == null && side == null) {
            if (quads == null) {
                bake();
            }
            return quads;
        }
        return new ArrayList<>();
    }

    @Override
    public boolean isAmbientOcclusion() {
        return false;
    }

    @Override
    public boolean isGui3d() {
        return false;
    }

    @Override
    public boolean isBuiltInRenderer() {
        return false;
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        return sprite;
    }

    @Override
    public ItemCameraTransforms getItemCameraTransforms() {
        return ItemCameraTransforms.DEFAULT;
    }

    @Override
    public ItemOverrideList getOverrides() {
        return ItemOverrideList.NONE;
    }

    @Override
    public Pair<? extends IBakedModel, Matrix4f> handlePerspective(TransformType cameraTransformType) {
        return MapWrapper.handlePerspective(this, modelState, cameraTransformType);
    }

    private void bake() {
        IBakedModel layerModel = new ItemLayerModel(ImmutableList.of(spriteName)).bake(TransformUtils.DEFAULT_ITEM, DefaultVertexFormats.ITEM, TextureUtils.bakedTextureGetter);
        ImmutableList.Builder<BakedQuad> quadBuilder = ImmutableList.builder();
        quadBuilder.addAll(layerModel.getQuads(null, null, 0));
        this.sprite = TextureUtils.bakedTextureGetter.apply(spriteName);
        this.quads = ItemQuadBakery.bakeItem(ImmutableList.of(sprite));
    }

}
