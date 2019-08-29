package codechicken.lib.model.loader.blockstate;

import codechicken.lib.util.Copyable;
import codechicken.lib.util.TransformUtils;
import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;
import com.google.gson.*;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.model.IModelState;

import java.lang.reflect.Type;
import java.util.*;
import java.util.Map.Entry;

/**
 * Created by covers1624 on 18/11/2016.
 */
public class CCVariant implements Copyable<CCVariant> {

    protected ResourceLocation model;
    protected Optional<IModelState> state = Optional.empty();
    protected Optional<Boolean> uvLock = Optional.empty();
    protected Optional<Boolean> smooth = Optional.empty();
    protected Optional<Boolean> gui3d = Optional.empty();
    protected Optional<Integer> weight = Optional.empty();
    protected Map<String, String> textures = new HashMap<>();
    protected Map<String, String> customData = new HashMap<>();
    protected Map<String, Map<String, CCVariant>> subVariants = new LinkedHashMap<>();

    public CCVariant() {
    }

    public CCVariant(CCVariant variant) {
        this.model = variant.model;
        this.state = variant.state;
        this.uvLock = variant.uvLock;
        this.smooth = variant.smooth;
        this.gui3d = variant.gui3d;
        this.weight = variant.weight;
        this.textures = new HashMap<>(variant.textures);
        this.customData = new HashMap<>(customData);
        this.subVariants = new LinkedHashMap<>(variant.subVariants);
    }

    public CCVariant with(CCVariant other) {
        if (this.model == null || other.model != null) {
            this.model = other.model;
        }

        if (other.state.isPresent()) {
            this.state = other.state;
        }
        if (other.uvLock.isPresent()) {
            this.uvLock = other.uvLock;
        }
        if (other.smooth.isPresent()) {
            this.smooth = other.smooth;
        }
        if (other.gui3d.isPresent()) {
            this.gui3d = other.gui3d;
        }
        if (other.weight.isPresent()) {
            this.weight = other.weight;
        }
        HashMap<String, String> newTextures = new HashMap<>();
        newTextures.putAll(textures);
        newTextures.putAll(other.textures);
        this.textures = new LinkedHashMap<>(newTextures);

        HashMap<String, String> newCustomData = new HashMap<>();
        newCustomData.putAll(customData);
        newCustomData.putAll(other.customData);
        this.customData = new LinkedHashMap<>(newCustomData);
        return this;
    }

    public boolean hasModel() {
        return model != null;
    }

    public boolean hasTextures() {
        return textures.size() != 0;
    }

    public boolean hasCustomData() {
        return customData.size() != 0;
    }

    public List<String> getPossibleVariantNames() {
        List<String> variantNames = new ArrayList<>();
        for (String variantName : subVariants.keySet()) {
            variantNames.add(variantName);
            for (CCVariant subVariant : subVariants.get(variantName).values()) {
                variantNames.addAll(subVariant.getPossibleVariantNames());
            }
        }
        return variantNames;
    }

    public List<String> getPossibleVariantValues(String variant) {
        List<String> variantValues = new ArrayList<>();
        for (String variantName : subVariants.keySet()) {
            if (variantName.equals(variant) && subVariants.containsKey(variant)) {
                variantValues.addAll(subVariants.get(variant).keySet());
            }

            for (CCVariant subVariant : subVariants.get(variantName).values()) {
                variantValues.addAll(subVariant.getPossibleVariantValues(variant));
            }
        }
        return variantValues;
    }

    public CCVariant applySubOverrides(CCVariant parent, Map<String, String> kvArray) {
        CCVariant finalVariant = parent;
        for (Entry<String, String> entry : kvArray.entrySet()) {
            for (Entry<String, Map<String, CCVariant>> variantsEntry : subVariants.entrySet()) {
                if (entry.getKey().equals(variantsEntry.getKey())) {
                    Map<String, CCVariant> variantMap = variantsEntry.getValue();
                    if (variantMap.containsKey(entry.getValue())) {
                        finalVariant = finalVariant.with(variantMap.get(entry.getValue()));
                    }
                }
            }
        }

        for (Entry<String, String> entry : kvArray.entrySet()) {
            for (Entry<String, Map<String, CCVariant>> variantsEntry : subVariants.entrySet()) {
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

    @Override
    public CCVariant copy() {
        return new CCVariant(this);
    }

    public static class Deserializer implements JsonDeserializer<CCVariant> {

        public static ResourceLocation getBlockLocation(String location) {
            ResourceLocation tmp = new ResourceLocation(location);
            return new ResourceLocation(tmp.getNamespace(), "block/" + tmp.getPath());
        }

        @Override
        public CCVariant deserialize(JsonElement element, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            CCVariant variant = new CCVariant();
            JsonObject json = element.getAsJsonObject();

            if (json.has("model")) {
                JsonElement modelElement = json.get("model");
                if (modelElement.isJsonNull()) {
                    variant.model = null;
                } else {
                    variant.model = getBlockLocation(modelElement.getAsString());
                }
            }

            if (json.has("textures")) {
                for (Entry<String, JsonElement> entry : json.get("textures").getAsJsonObject().entrySet()) {
                    if (entry.getValue().isJsonNull()) {
                        variant.textures.put(entry.getKey(), "");
                    } else {
                        variant.textures.put(entry.getKey(), entry.getValue().getAsString());
                    }
                }
            }

            variant.state = TransformUtils.parseFromJson(json);

            if (json.has("uvlock")) {
                variant.uvLock = Optional.of(JsonUtils.getBoolean(json, "uvlock"));
            }

            if (json.has("smooth_lighting")) {
                variant.smooth = Optional.of(JsonUtils.getBoolean(json, "smooth_lighting"));
            }

            if (json.has("gui3d")) {
                variant.gui3d = Optional.of(JsonUtils.getBoolean(json, "gui3d"));
            }

            if (json.has("weight")) {
                variant.weight = Optional.of(JsonUtils.getInt(json, "weight"));
            }

            if (json.has("variants")) {
                variant.subVariants.putAll(CCBlockStateLoader.parseVariants(new LinkedHashMap<>(), json.getAsJsonObject("variants")));
            }

            if (json.has("custom")) {
                for (Entry<String, JsonElement> e : json.get("custom").getAsJsonObject().entrySet()) {
                    if (e.getValue().isJsonNull()) {
                        variant.customData.put(e.getKey(), null);
                    } else {
                        variant.customData.put(e.getKey(), e.getValue().toString());
                    }
                }
            }

            return variant;
        }
    }

    @Override
    public String toString() {
        ToStringHelper helper = MoreObjects.toStringHelper("CCVariant");
        helper.add("Model", model);
        helper.add("IModelState", state);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        helper.add("Textures:", gson.toJson(textures, Map.class));
        return helper.toString();
    }
}
