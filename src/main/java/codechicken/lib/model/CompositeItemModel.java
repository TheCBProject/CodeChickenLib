package codechicken.lib.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.*;
import com.mojang.math.Transformation;
import net.covers1624.quack.collection.FastStream;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.model.CompositeModel;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.minecraftforge.client.model.geometry.IGeometryLoader;
import net.minecraftforge.client.model.geometry.IUnbakedGeometry;
import net.minecraftforge.client.model.geometry.UnbakedGeometryHelper;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * Similar to Forge's {@link CompositeModel}, with support for children to have overrides.
 * <p>
 * Created by covers1624 on 8/9/23.
 */
public class CompositeItemModel implements IGeometryLoader<CompositeItemModel.Geometry> {

    @Override
    public Geometry read(JsonObject jsonObject, JsonDeserializationContext ctx) throws JsonParseException {
        JsonObject childrenObj = jsonObject.getAsJsonObject("children");
        if (childrenObj == null) throw new JsonParseException("Composite model requires children.");

        Map<String, BlockModel> children = FastStream.of(childrenObj.entrySet())
                .toImmutableMap(Map.Entry::getKey, e -> ctx.deserialize(e.getValue(), BlockModel.class));

        List<String> itemPasses;
        JsonArray itemPassesArr = jsonObject.getAsJsonArray("item_render_order");
        if (itemPassesArr != null) {
            itemPasses = FastStream.of(itemPassesArr)
                    .map(JsonElement::getAsString)
                    .peek(e -> {
                        if (!children.containsKey(e)) {
                            throw new JsonParseException("Invalid child in item_render_order." + e + " does not exist.");
                        }
                    })
                    .toImmutableList();
        } else {
            itemPasses = ImmutableList.copyOf(children.keySet());
        }

        return new Geometry(children, itemPasses);
    }

    public record Geometry(Map<String, BlockModel> children, List<String> itemPasses) implements IUnbakedGeometry<Geometry> {

        @Override
        public BakedModel bake(IGeometryBakingContext context, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides, ResourceLocation modelLocation) {
            TextureAtlasSprite particle = spriteGetter.apply(context.getMaterial("particle"));
            ModelState effectiveState = composeState(context.getRootTransform(), modelState);

            ImmutableMap<String, BakedModel> children = FastStream.of(this.children.entrySet())
                    .filter(e -> context.isComponentVisible(e.getKey(), true))
                    .toImmutableMap(Map.Entry::getKey, e -> e.getValue().bake(baker, e.getValue(), spriteGetter, effectiveState, modelLocation, true));

            ImmutableList<BakedModel> itemPasses = FastStream.of(this.itemPasses)
                    .map(children::get)
                    .toImmutableList();

            class Overrides extends ItemOverrides {

                @Nullable
                @Override
                public BakedModel resolve(BakedModel pModel, ItemStack pStack, @Nullable ClientLevel pLevel, @Nullable LivingEntity pEntity, int pSeed) {
                    ImmutableList<BakedModel> iPasses = FastStream.of(itemPasses)
                            .map(e -> e.getOverrides().resolve(e, pStack, pLevel, pEntity, pSeed))
                            .toImmutableList();

                    return new CompositeModel.Baked(
                            context.isGui3d(),
                            context.useBlockLight(),
                            context.useAmbientOcclusion(),
                            particle,
                            context.getTransforms(),
                            this,
                            children,
                            iPasses
                    );
                }
            }

            return new CompositeModel.Baked(
                    context.isGui3d(),
                    context.useBlockLight(),
                    context.useAmbientOcclusion(),
                    particle,
                    context.getTransforms(),
                    new Overrides(),
                    children,
                    itemPasses
            );
        }

        @Override
        public void resolveParents(Function<ResourceLocation, UnbakedModel> modelGetter, IGeometryBakingContext context) {
            children.values().forEach(value -> value.resolveParents(modelGetter));
        }

        @Override
        public Set<String> getConfigurableComponentNames() {
            return children.keySet();
        }

        private static ModelState composeState(Transformation rootTransform, ModelState state) {
            if (rootTransform.isIdentity()) return state;

            return UnbakedGeometryHelper.composeRootTransformIntoModelState(state, rootTransform);
        }
    }
}
