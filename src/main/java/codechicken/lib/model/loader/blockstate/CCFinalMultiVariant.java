//package codechicken.lib.model.loader.blockstate;
//
//import codechicken.lib.model.bakedmodels.ModelProperties;
//import codechicken.lib.model.forgemodel.CCMultiModel;
//import codechicken.lib.model.forgemodel.StateOverrideIModel;
//import net.minecraft.client.renderer.block.model.ModelRotation;
//import net.minecraft.client.renderer.block.model.Variant;
//import net.minecraft.util.ResourceLocation;
//import net.minecraftforge.client.model.IModel;
//import net.minecraftforge.client.model.ModelLoaderRegistry;
//import net.minecraftforge.common.model.IModelState;
//
//import java.util.LinkedList;
//import java.util.List;
//import java.util.Map;
//
///**
// * Created by covers1624 on 16/12/2016.
// */
//public class CCFinalMultiVariant extends Variant {
//
//    private Variant baseVariant;
//    private ModelProperties baseProperties;
//    private List<Variant> finalVariants = new LinkedList<>();
//    private IModelState state;
//
//    public CCFinalMultiVariant(CCVariant baseVariant, String textureDomain, Map<String, CCVariant> subModels) {
//        super(baseVariant.model == null ? new ResourceLocation("builtin/missing") : baseVariant.model, baseVariant.state.get() instanceof ModelRotation ? ((ModelRotation) baseVariant.state.get()) : ModelRotation.X0_Y0, baseVariant.uvLock.orElse(false), baseVariant.weight.orElse(1));
//        state = baseVariant.state.get();
//        this.baseVariant = makeFinalVariant(baseVariant, textureDomain);
//        this.baseProperties = new ModelProperties(baseVariant.smooth.orElse(true), baseVariant.gui3d.orElse(true));
//        for (CCVariant subModel : subModels.values()) {
//            finalVariants.add(makeFinalVariant(baseVariant.copy().with(subModel), textureDomain));
//        }
//    }
//
//    @Override
//    public IModel process(IModel base) {
//        boolean hasBase = base != ModelLoaderRegistry.getMissingModel();
//        if (hasBase) {
//            base = baseVariant.process(base);
//        }
//
//        List<IModel> subModels = new LinkedList<>();
//        for (Variant variant : finalVariants) {
//            if (!variant.getModelLocation().equals(new ResourceLocation("builtin/missing"))) {
//                IModel subModel = ModelLoaderRegistry.getModelOrLogError(variant.getModelLocation(), "Unable to load subModel's Model: " + variant.getModelLocation());
//                subModels.add(variant.process(new StateOverrideIModel(subModel, variant.getState())));
//            }
//        }
//
//        return new CCMultiModel(hasBase ? base : null, baseProperties, subModels);
//    }
//
//    @Override
//    public IModelState getState() {
//        return state;
//    }
//
//    private static Variant makeFinalVariant(CCVariant variant, String textureDomain) {
//        boolean uvLock = variant.uvLock.orElse(false);
//        boolean smooth = variant.smooth.orElse(true);
//        boolean gui3d = variant.gui3d.orElse(true);
//        int weight = variant.weight.orElse(1);
//        if (variant.hasModel() && !variant.hasTextures() && !variant.hasCustomData() && variant.state.get() instanceof ModelRotation) {
//            return new Variant(variant.model, ((ModelRotation) variant.state.get()), uvLock, weight);
//        } else {
//            return new CCFinalVariant(variant.model, variant.state, uvLock, smooth, gui3d, weight, variant.textures, textureDomain, variant.customData);
//        }
//    }
//}
