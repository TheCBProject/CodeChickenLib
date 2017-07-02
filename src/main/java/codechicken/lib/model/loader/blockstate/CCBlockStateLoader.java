package codechicken.lib.model.loader.blockstate;

import codechicken.lib.reflect.ObfMapping;
import codechicken.lib.internal.CCLLog;
import codechicken.lib.reflect.ReflectionManager;
import codechicken.lib.texture.TextureUtils;
import codechicken.lib.util.ArrayUtils;
import com.google.common.base.Joiner;
import com.google.common.collect.*;
import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.block.statemap.BlockStateMapper;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.item.Item;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.fml.common.ProgressManager;
import net.minecraftforge.fml.common.ProgressManager.ProgressBar;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.Level;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Created by covers1624 on 17/11/2016.
 * TODO, Vanilla Predicate support inside json. Maybe allow variants inside predicates??
 * TODO, Allow all unhandled BlockState Json data to be passed to custom IModels.
 * TODO, Custom Sided particle system from json maybe / support for sided particles loaded from this loader.
 */
public class CCBlockStateLoader {

    public static CCBlockStateLoader INSTANCE = new CCBlockStateLoader();

    public VariantLoader VARIANT_LOADER = new VariantLoader();

    public static final Gson GSON = new GsonBuilder().registerTypeAdapter(CCVariant.class, new CCVariant.Deserializer()).create();

    public Map<ResourceLocation, ModelBlockDefinition> blockDefinitions = new HashMap<>();
    public Map<ModelResourceLocation, IModel> toBake = new LinkedHashMap<>();

    private Map<ResourceLocation, Exception> exceptions;
    private ModelLoader modelLoader;


    public static void initialize() {
        MinecraftForge.EVENT_BUS.register(INSTANCE);
    }

    @SubscribeEvent (priority = EventPriority.HIGHEST)
    public void onTextureStitchPre(TextureStitchEvent.Pre event) {
        if (!event.getMap().getBasePath().equals("textures")) {
            CCLLog.log(Level.WARN, "Someone is calling the TextureStitchEvent.Pre for a texture map that is NOT vanillas.");
            CCLLog.log(Level.WARN, "This is a bug. There is no sense of different atlas's in vanilla so this event is NOT generic and specific to the vanilla atlas.");
            CCLLog.log(Level.WARN, "Im catching this so things don't explode. Fix your shit!");
            CCLLog.big(Level.WARN, 100, "");
            return;
        }
        grabLoader();

        loadBakery(modelLoader.blockModelShapes.getBlockStateMapper(), modelLoader.resourceManager);

        for (IModel model : toBake.values()) {
            model.getTextures().forEach(event.getMap()::registerSprite);
        }
    }

    //This is bullshit..
    //But allas, Loads all compatiable block and item BlockState jsons from /assets/mod/cc_blockstates/
    public void loadBakery(BlockStateMapper mapper, IResourceManager manager) {
        blockDefinitions.clear();
        toBake.clear();

        List<Block> blocks = StreamSupport.stream(ForgeRegistries.BLOCKS.spliterator(), false).filter(block -> block.getRegistryName() != null).collect(Collectors.toList());
        blocks.sort(Comparator.comparing(b -> b.getRegistryName().toString()));
        ProgressBar bar = ProgressManager.push("CCL ModelLoading: Blocks", blocks.size());

        for (Block block : blocks) {
            bar.step(block.getRegistryName().toString());

            for (ResourceLocation location : mapper.getBlockstateLocations(block)) {
                if (canLoad(manager, location)) {
                    ModelBlockDefinition definition = blockDefinitions.computeIfAbsent(getBlockStateLocation(location), file -> loadMBD(manager, file));
                    if (definition != null) {
                        if (definition.hasMultipartData()) {
                            throw new RuntimeException("BlockState file parsed by CCL appears to have Multipart data.. " + location.toString());
                        }
                        Map<IBlockState, ModelResourceLocation> map = mapper.getVariants(block);

                        for (Entry<IBlockState, ModelResourceLocation> entry : map.entrySet()) {
                            ModelResourceLocation modelLocation = entry.getValue();

                            if (location.equals(modelLocation)) {
                                IModel model;
                                WrappedMRL wrapped = WrappedMRL.from(modelLocation);
                                try {
                                    model = VARIANT_LOADER.loadModel(wrapped);
                                } catch (Exception e) {
                                    model = ModelLoaderRegistry.getMissingModel();
                                    storeException(modelLocation, e);
                                }
                                toBake.put(modelLocation, model);
                            }
                        }
                    }
                }
            }

        }

        ProgressManager.pop(bar);

        List<Item> items = StreamSupport.stream(ForgeRegistries.ITEMS.spliterator(), false).filter(block -> block.getRegistryName() != null).collect(Collectors.toList());
        items.sort(Comparator.comparing(item -> item.getRegistryName().toString()));
        bar = ProgressManager.push("CCL ModelLoading: Items", items.size());
        for (Item item : items) {
            bar.step(item.getRegistryName().toString());
            for (String s : modelLoader.getVariantNames(item)) {
                ModelResourceLocation invLoc = ModelLoader.getInventoryVariant(s);
                if (canLoad(manager, invLoc)) {
                    WrappedMRL wrapped = WrappedMRL.from(invLoc);
                    IModel model;
                    try {
                        model = VARIANT_LOADER.loadModel(wrapped);
                    } catch (Exception e) {
                        model = ModelLoaderRegistry.getMissingModel();
                        storeException(wrapped, e);
                    }
                    toBake.put(wrapped.to(), model);
                }
            }
        }
        ProgressManager.pop(bar);
    }



    @SubscribeEvent (priority = EventPriority.HIGHEST)
    public void onModelBake(ModelBakeEvent event) {

        IModel missingIModel = ModelLoaderRegistry.getMissingModel();
        IBakedModel missingModel = missingIModel.bake(missingIModel.getDefaultState(), DefaultVertexFormats.ITEM, TextureUtils.bakedTextureGetter);
        Map<IModel, IBakedModel> bakedModels = new HashMap<>();
        HashMultimap<IModel, ModelResourceLocation> models = HashMultimap.create();
        Multimaps.invertFrom(Multimaps.forMap(toBake), models);

        ProgressBar bar = ProgressManager.push("CCL ModelLoading: Baking", models.keySet().size());

        for (IModel model : models.keySet()) {
            bar.step(String.format("[%s]", Joiner.on(", ").join(models.get(model))));
            if (model == missingIModel) {
                bakedModels.put(model, missingModel);
                continue;
            }
            bakedModels.put(model, model.bake(model.getDefaultState(), DefaultVertexFormats.ITEM, TextureUtils.bakedTextureGetter));
        }
        ProgressManager.pop(bar);

        for (Entry<ModelResourceLocation, IModel> entry : toBake.entrySet()) {
            event.getModelRegistry().putObject(entry.getKey(), bakedModels.get(entry.getValue()));
        }

    }

    private void storeException(ResourceLocation location, Exception exception) {
        exceptions.put(location, exception);
    }

    @SuppressWarnings ("unchecked")
    //This is total bullshit.
    private void grabLoader() {
        ObfMapping mapping = new ObfMapping("net/minecraftforge/client/model/ModelLoader$VanillaLoader", "INSTANCE");
        Object object = ReflectionManager.getField(mapping, null, Object.class);
        mapping = new ObfMapping("net/minecraftforge/client/model/ModelLoader$VanillaLoader", "getLoader", "()Lnet/minecraftforge/client/model/ModelLoader;");
        modelLoader =  ReflectionManager.callMethod(mapping, ModelLoader.class, object);
        mapping = new ObfMapping("net/minecraftforge/client/model/ModelLoader", "loadingExceptions");
        exceptions = ReflectionManager.getField(mapping, modelLoader, Map.class);
    }

    private static ResourceLocation getBlockStateLocation(ResourceLocation location) {
        return new ResourceLocation(location.getResourceDomain(), "cc_blockstates/" + location.getResourcePath() + ".json");
    }

    private static boolean canLoad(IResourceManager resourceManager, ResourceLocation location) {
        ResourceLocation fileLocation = getBlockStateLocation(location);
        try {
            resourceManager.getAllResources(fileLocation);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static ModelBlockDefinition loadMBD(IResourceManager manager, ResourceLocation file) {
        List<ModelBlockDefinition> list = new ArrayList<>();

        try {
            for (IResource resource : manager.getAllResources(file)) {
                list.add(load(resource.getInputStream()));
            }
        } catch (FileNotFoundException ignore) {
        } catch (IOException e) {
            throw new RuntimeException("Encountered an exception when loading model definition of model " + file, e);
        }
        if (list.isEmpty()) {
            return null;
        }
        return new ModelBlockDefinition(list);
    }

    public static ModelBlockDefinition load(InputStream stream) {
        try {
            JsonParser parser = new JsonParser();
            JsonReader reader = new JsonReader(new InputStreamReader(stream));
            reader.setLenient(true);
            JsonObject object = parser.parse(reader).getAsJsonObject();
            if (JsonUtils.hasField(object, "ccl_marker")) {
                int marker = JsonUtils.getInt(object, "ccl_marker");
                if (marker == 1) {
                    List<String> variantSets = new ArrayList<>();
                    for (JsonElement element : object.getAsJsonArray("variant_sets")) {
                        variantSets.add(element.getAsString());
                    }

                    String textureDomain = "";
                    if (object.has("texture_domain")) {
                        textureDomain = object.get("texture_domain").getAsString();
                    }

                    CCVariant defaultVariant = null;
                    if (object.has("defaults")) {
                        defaultVariant = GSON.fromJson(object.get("defaults"), CCVariant.class);
                    }
                    Map<String, Map<String, CCVariant>> variants = parseVariants(object.getAsJsonObject("variants"));

                    Map<String, Map<String, Map<String, CCVariant>>> subModels = parseSubModels(object.getAsJsonObject("sub_model"));

                    Map<String, CCVariant> compiledVariants = new LinkedHashMap<>();
                    Map<String, Map<String, CCVariant>> compiledSubModelVariants = new LinkedHashMap<>();

                    List<String> possibleCombos = new ArrayList<>();

                    for (String variantSet : variantSets) {
                        Map<String, List<String>> variantValueMap = generateVariantValueMap(Arrays.asList(variantSet.split(",")), variants, subModels);
                        possibleCombos.addAll(generatePossibleCombos(variantValueMap));
                    }

                    for (String var : possibleCombos) {
                        Map<String, String> kvArray = ArrayUtils.convertKeyValueArrayToMap(var.split(","));
                        CCVariant finalVariant = new CCVariant();
                        if (defaultVariant != null) {
                            finalVariant = defaultVariant.copy();
                        }
                        compiledVariants.put(var, compileVariant(finalVariant.copy(), kvArray, variants));

                    }
                    for (Entry<String, Map<String, Map<String, CCVariant>>> subModelVariantEntry : subModels.entrySet()) {
                        Map<String, CCVariant> compiledVariants2 = new LinkedHashMap<>();
                        for (String var : possibleCombos) {
                            Map<String, String> kvArray = ArrayUtils.convertKeyValueArrayToMap(var.split(","));
                            CCVariant finalVariant = new CCVariant();
                            if (defaultVariant != null) {
                                finalVariant = defaultVariant.copy();
                            }
                            compiledVariants2.put(var, compileVariant(finalVariant.copy(), kvArray, subModelVariantEntry.getValue()));
                        }
                        compiledSubModelVariants.put(subModelVariantEntry.getKey(), compiledVariants2);
                    }

                    Map<String, VariantList> variantList = new HashMap<>();
                    for (Entry<String, CCVariant> entry : compiledVariants.entrySet()) {
                        Map<String, CCVariant> subModelVariants = getSubModelsForKey(entry.getKey(), compiledSubModelVariants);
                        List<Variant> vars = new ArrayList<>();
                        CCVariant variant = entry.getValue();

                        boolean uvLock = variant.uvLock.orElse(false);
                        boolean smooth = variant.smooth.orElse(true);
                        boolean gui3d = variant.gui3d.orElse(true);
                        int weight = variant.weight.orElse(1);

                        if (variant.model != null && subModelVariants.size() == 0 && variant.textures.size() == 0 && variant.customData.size() == 0 && variant.state.orElse(null) instanceof ModelRotation) {
                            vars.add(new Variant(variant.model, ((ModelRotation) variant.state.get()), uvLock, weight));
                        } else if (subModelVariants.size() == 0) {
                            vars.add(new CCFinalVariant(variant.model, variant.state.orElse(TRSRTransformation.identity()), uvLock, smooth, gui3d, weight, variant.textures, textureDomain, variant.customData));
                        } else {
                            vars.add(new CCFinalMultiVariant(variant, textureDomain, subModelVariants));
                        }
                        variantList.put(entry.getKey(), new VariantList(vars));
                    }
                    return new ModelBlockDefinition(variantList, null);
                }

            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public static Map<String, Map<String, CCVariant>> parseVariants(JsonObject variantElement) {
        Map<String, Map<String, CCVariant>> variants = new LinkedHashMap<>();
        for (Entry<String, JsonElement> variantsEntry : variantElement.entrySet()) {
            String variantName = variantsEntry.getKey();
            Map<String, CCVariant> variantValues = variants.computeIfAbsent(variantName, k -> new LinkedHashMap<>());
            for (Entry<String, JsonElement> variantEntry : variantsEntry.getValue().getAsJsonObject().entrySet()) {
                String variantValue = variantEntry.getKey();
                CCVariant variant = GSON.fromJson(variantEntry.getValue(), CCVariant.class);
                variantValues.put(variantValue, variant);
            }
        }
        return variants;
    }

    public static Map<String, Map<String, Map<String, CCVariant>>> parseSubModels(JsonObject object) {
        Map<String, Map<String, Map<String, CCVariant>>> subModels = new LinkedHashMap<>();

        if (object != null) {
            for (Entry<String, JsonElement> subModelEntry : object.entrySet()) {
                subModels.put(subModelEntry.getKey(), parseVariants(subModelEntry.getValue().getAsJsonObject().getAsJsonObject("variants")));
            }
        }

        return subModels;
    }

    public static CCVariant compileVariant(CCVariant finalVariant, Map<String, String> kvArray, Map<String, Map<String, CCVariant>> variants) {
        for (Entry<String, String> entry : kvArray.entrySet()) {
            for (Entry<String, Map<String, CCVariant>> variantsEntry : variants.entrySet()) {
                if (entry.getKey().equals(variantsEntry.getKey())) {
                    Map<String, CCVariant> variantMap = variantsEntry.getValue();
                    if (variantMap.containsKey(entry.getValue())) {
                        finalVariant = finalVariant.with(variantMap.get(entry.getValue()));
                    }
                }
            }
        }
        for (Entry<String, String> entry : kvArray.entrySet()) {
            for (Entry<String, Map<String, CCVariant>> variantsEntry : variants.entrySet()) {
                if (entry.getKey().equals(variantsEntry.getKey())) {
                    Map<String, CCVariant> variantMap = variantsEntry.getValue();
                    if (variantMap.containsKey(entry.getValue())) {
                        finalVariant = variantMap.get(entry.getValue()).applySubOverrides(finalVariant, kvArray);
                    }
                }
            }
        }
        return finalVariant;
    }

    public static Map<String, CCVariant> getSubModelsForKey(String key, Map<String, Map<String, CCVariant>> subModels) {
        Map<String, CCVariant> subModelVariants = new LinkedHashMap<>();
        for (Entry<String, Map<String, CCVariant>> subModelEntry : subModels.entrySet()) {
            for (Entry<String, CCVariant> variantEntry : subModelEntry.getValue().entrySet()) {
                if (variantEntry.getKey().equals(key)) {
                    subModelVariants.put(subModelEntry.getKey(), variantEntry.getValue());
                }
            }
        }
        return subModelVariants;
    }

    /**
     * Generates a list of strings for all possible combos of Key=Value.
     * Per key value list can have more than 1 valid value.
     *
     * Credits to brandon3055 who wrote this for me!
     *
     * @param variantValueMap A map of all possible Keys to all possible values per key.
     * @return A compiled list of all possible combos.
     */
    public static List<String> generatePossibleCombos(Map<String, List<String>> variantValueMap) {
        List<String> possibleCombos = new ArrayList<>();

        List<String> keys = Lists.newArrayList(variantValueMap.keySet());

        int comboCount = 1;
        for (String key : variantValueMap.keySet()) {
            comboCount *= variantValueMap.get(key).size();
        }

        int[] indexes = new int[variantValueMap.size()];
        for (int l = 0; l < comboCount; l++) {
            for (int in = 0; in < indexes.length; in++) {
                indexes[in]++;
                if (indexes[in] >= variantValueMap.get(keys.get(in)).size()) {
                    indexes[in] = 0;
                } else {
                    break;
                }
            }

            String combo = "";
            for (int i = 0; i < indexes.length; i++) {
                combo += keys.get(i) + "=" + variantValueMap.get(keys.get(i)).get(indexes[i]) + ",";
            }
            possibleCombos.add(combo.substring(0, combo.length() - 1));
        }
        return possibleCombos;
    }

    /**
     * Generates a map of Key -> List(Value) from the provided CCVariants.
     *
     * @param keys     The keys to get all values for.
     * @param variants The CCVariants parsed from json.
     * @return Map of Key to value lists.
     */
    public static Map<String, List<String>> generateVariantValueMap(List<String> keys, Map<String, Map<String, CCVariant>> variants, Map<String, Map<String, Map<String, CCVariant>>> subModels) {
        Map<String, List<String>> variantValueMap = new LinkedHashMap<>();
        for (String variant : keys) {
            List<String> variantValues = new ArrayList<>();
            for (String variantName : variants.keySet()) {
                if (variantName.equals(variant) && variants.containsKey(variant)) {
                    variantValues.addAll(variants.get(variant).keySet());
                }

                for (CCVariant subVariant : variants.get(variantName).values()) {
                    variantValues.addAll(subVariant.getPossibleVariantValues(variant));
                }
            }
            for (Map<String, Map<String, CCVariant>> subModelVariants : subModels.values()) {
                for (String variantName : subModelVariants.keySet()) {
                    if (variantName.equals(variant) && subModelVariants.containsKey(variant)) {
                        variantValues.addAll(subModelVariants.get(variant).keySet());
                    }

                    for (CCVariant subVariant : subModelVariants.get(variantName).values()) {
                        variantValues.addAll(subVariant.getPossibleVariantValues(variant));
                    }
                }
            }
            variantValueMap.put(variant, variantValues);
        }
        return variantValueMap;
    }

    //Used for bypassing some of Forges model loading bs.
    private static class WrappedMRL extends ResourceLocation {

        private final String variant;

        public WrappedMRL(ModelResourceLocation modelResourceLocation) {
            super(modelResourceLocation.getResourceDomain(), modelResourceLocation.getResourcePath());
            variant = modelResourceLocation.getVariant();
        }

        public ModelResourceLocation to() {
            return new ModelResourceLocation(getResourceDomain() + ":" + getResourcePath(), variant);
        }

        public static WrappedMRL from(ModelResourceLocation loc) {
            return new WrappedMRL(loc);
        }

        public String getVariant() {
            return this.variant;
        }

        public boolean equals(Object other) {
            if (this == other) {
                return true;
            } else if (other instanceof ModelResourceLocation && super.equals(other)) {
                ModelResourceLocation modelresourcelocation = (ModelResourceLocation) other;
                return this.variant.equals(modelresourcelocation.getVariant());
            } else if (other instanceof WrappedMRL && super.equals(other)) {
                WrappedMRL modelResourceLocation = ((WrappedMRL) other);
                return this.variant.equalsIgnoreCase(modelResourceLocation.variant);
            } else {
                return false;
            }
        }

        public int hashCode() {
            return 31 * super.hashCode() + this.variant.hashCode();
        }

        public String toString() {
            return super.toString() + '#' + this.variant;
        }
    }

    private class VariantLoader implements ICustomModelLoader {

        @Override
        public void onResourceManagerReload(IResourceManager resourceManager) {
        }

        @Override
        public boolean accepts(ResourceLocation modelLocation) {
            return modelLocation instanceof WrappedMRL;
        }

        @Override
        public IModel loadModel(ResourceLocation modelLocation) throws Exception {
            ModelResourceLocation location = ((WrappedMRL) modelLocation).to();
            ResourceLocation file = getBlockStateLocation(location);
            ModelBlockDefinition definition = blockDefinitions.get(file);
            VariantList variants = definition.getVariant(location.getVariant());
            return new WeightedRandomModel(variants);
        }
    }

    //Credits to fry, This is private in Forge, have to copy.
    public static final class WeightedRandomModel implements IModel {

        private final List<Variant> variants;
        private final List<ResourceLocation> locations = new ArrayList<>();
        private final Set<ResourceLocation> textures = Sets.newHashSet();
        private final List<IModel> models = new ArrayList<>();
        private final IModelState defaultState;

        public WeightedRandomModel(VariantList variants) throws Exception {
            this.variants = variants.getVariantList();
            ImmutableList.Builder<Pair<IModel, IModelState>> builder = ImmutableList.builder();
            for (Variant v : this.variants) {
                ResourceLocation loc = v.getModelLocation();
                locations.add(loc);

                IModel model;
                if (loc.equals(ModelBakery.MODEL_MISSING)) {
                    model = ModelLoaderRegistry.getMissingModel();
                } else {
                    model = ModelLoaderRegistry.getModel(loc);
                }

                model = v.process(model);
                for (ResourceLocation location : model.getDependencies()) {
                    ModelLoaderRegistry.getModelOrMissing(location);
                }
                textures.addAll(model.getTextures());

                models.add(model);
                builder.add(Pair.of(model, v.getState()));
            }

            if (models.size() == 0) {
                IModel missing = ModelLoaderRegistry.getMissingModel();
                models.add(missing);
                builder.add(Pair.of(missing, TRSRTransformation.identity()));
            }

            defaultState = new MultiModelState(builder.build());
        }

        @Override
        public Collection<ResourceLocation> getDependencies() {
            return ImmutableList.copyOf(locations);
        }

        @Override
        public Collection<ResourceLocation> getTextures() {
            return ImmutableSet.copyOf(textures);
        }

        @Override
        public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
            if (!Attributes.moreSpecific(format, Attributes.DEFAULT_BAKED_FORMAT)) {
                throw new IllegalArgumentException("can't bake vanilla weighted models to the format that doesn't fit into the default one: " + format);
            }
            if (variants.size() == 1) {
                IModel model = models.get(0);
                return model.bake(MultiModelState.getPartState(state, model, 0), format, bakedTextureGetter);
            }
            WeightedBakedModel.Builder builder = new WeightedBakedModel.Builder();
            for (int i = 0; i < variants.size(); i++) {
                IModel model = models.get(i);
                builder.add(model.bake(MultiModelState.getPartState(state, model, i), format, bakedTextureGetter), variants.get(i).getWeight());
            }
            return builder.build();
        }

        @Override
        public IModelState getDefaultState() {
            return defaultState;
        }
    }

}
