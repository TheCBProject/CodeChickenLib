package codechicken.lib.util;

import codechicken.lib.render.CCModelState;
import codechicken.lib.vec.Matrix4;
import codechicken.lib.vec.Vector3;
import com.google.common.collect.Maps;
import com.google.gson.*;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.model.ModelRotation;
import net.minecraft.util.JSONUtils;
import net.minecraftforge.client.model.ForgeBlockStateV1.TRSRDeserializer;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;

import javax.vecmath.Vector3f;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Created by covers1624 on 5/16/2016.
 * This is mostly just extracted from the ForgeBlockStateV1.
 * Credits to Rain Warrior.
 *
 * If you have an idea for another transform just make a pull request.
 */
//TODO, Pull updated values from the model jsons, These are good, but are slightly off from vanilla in some aspects.
public class TransformUtils {

    private static final TRSRTransformation flipX = new TRSRTransformation(null, null, new Vector3f(-1, 1, 1), null);

    public static final Gson GSON = new GsonBuilder().registerTypeAdapter(TRSRTransformation.class, TRSRDeserializer.INSTANCE).create();

    public static final CCModelState DEFAULT_BLOCK;
    public static final CCModelState DEFAULT_ITEM;
    public static final CCModelState DEFAULT_TOOL;
    public static final CCModelState DEFAULT_BOW;
    public static final CCModelState DEFAULT_HANDHELD_ROD;

    static {
        Map<TransformType, TRSRTransformation> map;
        TRSRTransformation thirdPerson;
        TRSRTransformation firstPerson;

        //@formatter:off
        map = new HashMap<>();
        thirdPerson =                                   create(0F,2.5F, 0F,75F, 45F, 0F,0.375F );
        map.put(TransformType.GUI,                      create(0F,  0F, 0F,30F,225F, 0F,0.625F));
        map.put(TransformType.GROUND,                   create(0F,  3F, 0F, 0F,  0F, 0F, 0.25F));
        map.put(TransformType.FIXED,                    create(0F,  0F, 0F, 0F,  0F, 0F,  0.5F));
        map.put(TransformType.THIRD_PERSON_RIGHT_HAND,  thirdPerson);
        map.put(TransformType.THIRD_PERSON_LEFT_HAND,   flipLeft(thirdPerson));
        map.put(TransformType.FIRST_PERSON_RIGHT_HAND,  create(0F, 0F, 0F, 0F, 45F, 0F, 0.4F));
        map.put(TransformType.FIRST_PERSON_LEFT_HAND,   create(0F, 0F, 0F, 0F, 225F, 0F, 0.4F));
        DEFAULT_BLOCK = new CCModelState(map);

        map = new HashMap<>();
        thirdPerson =                                    create(   0F,  3F,   1F, 0F,  0F, 0F, 0.55F);
        firstPerson =                                    create(1.13F,3.2F,1.13F, 0F,-90F,25F, 0.68F);
        map.put(TransformType.GROUND,                    create(   0F,  2F,   0F, 0F,  0F, 0F, 0.5F));
        map.put(TransformType.HEAD,                      create(   0F, 13F,   7F, 0F,180F, 0F,   1F));
        map.put(TransformType.THIRD_PERSON_RIGHT_HAND,   thirdPerson);
        map.put(TransformType.THIRD_PERSON_LEFT_HAND,    flipLeft(thirdPerson));
        map.put(TransformType.FIRST_PERSON_RIGHT_HAND,   firstPerson);
        map.put(TransformType.FIRST_PERSON_LEFT_HAND,    flipLeft(firstPerson));
        DEFAULT_ITEM = new CCModelState(map);

        map = new HashMap<>();
        map.put(TransformType.GROUND,                   create(   0F,  2F,   0F, 0F,  0F, 0F, 0.5F));
        map.put(TransformType.THIRD_PERSON_RIGHT_HAND,  create(   0F,  4F, 0.5F, 0F,-90F, 55,0.85F));
        map.put(TransformType.THIRD_PERSON_LEFT_HAND,   create(   0F,  4F, 0.5F, 0F, 90F,-55,0.85F));
        map.put(TransformType.FIRST_PERSON_RIGHT_HAND,  create(1.13F,3.2F,1.13F, 0F,-90F, 25,0.68F));
        map.put(TransformType.FIRST_PERSON_LEFT_HAND,   create(1.13F,3.2F,1.13F, 0F, 90F,-25,0.68F));
        DEFAULT_TOOL = new CCModelState(map);

        map = new HashMap<>();
        map.put(TransformType.GROUND,                   create(   0F,  2F,   0F,  0F,   0F,  0F, 0.5F));
        map.put(TransformType.THIRD_PERSON_RIGHT_HAND,  create(  -1F, -2F, 2.5F,-80F, 260F,-40F, 0.9F));
        map.put(TransformType.THIRD_PERSON_LEFT_HAND,   create(  -1F, -2F, 2.5F,-80F,-280F, 40F, 0.9F));
        map.put(TransformType.FIRST_PERSON_RIGHT_HAND,  create(1.13F,3.2F,1.13F,  0F, -90F, 25F,0.68F));
        map.put(TransformType.FIRST_PERSON_LEFT_HAND,   create(1.13F,3.2F,1.13F,  0F,  90F,-25F,0.68F));
        DEFAULT_BOW = new CCModelState(map);

        map = new HashMap<>();
        map.put(TransformType.GROUND,                   create(0F, 2F,   0F, 0F,  0F,  0F, 0.5F));
        map.put(TransformType.THIRD_PERSON_RIGHT_HAND,  create(0F,  4F,2.5F, 0F, 90F, 55F,0.85F));
        map.put(TransformType.THIRD_PERSON_LEFT_HAND,   create(0F,  4F,2.5F, 0F,-90F,-55F,0.85F));
        map.put(TransformType.FIRST_PERSON_RIGHT_HAND,  create(0F,1.6F,0.8F, 0F, 90F, 25F,0.68F));
        map.put(TransformType.FIRST_PERSON_LEFT_HAND,   create(0F,1.6F,0.8F, 0F,-90F,-25F,0.68F));
        DEFAULT_HANDHELD_ROD = new CCModelState(map);
        //@formatter:on
    }

    /**
     * Creates a new TRSRTransformation.
     *
     * @param tx The x transform.
     * @param ty The y transform.
     * @param tz The z transform.
     * @param rx The x Axis rotation.
     * @param ry The y Axis rotation.
     * @param rz The z Axis rotation.
     * @param s  The scale.
     * @return The new TRSRTransformation.
     */
    public static TRSRTransformation create(float tx, float ty, float tz, float rx, float ry, float rz, float s) {
        return create(new Vector3f(tx / 16, ty / 16, tz / 16), new Vector3f(rx, ry, rz), new Vector3f(s, s, s));
    }

    /**
     * Creates a new TRSRTransformation.
     *
     * @param transform The transform.
     * @param rotation  The rotation.
     * @param scale     The scale.
     * @return The new TRSRTransformation.
     */
    public static TRSRTransformation create(Vector3 transform, Vector3 rotation, Vector3 scale) {
        return create(transform.vector3f(), rotation.vector3f(), scale.vector3f());
    }

    /**
     * Creates a new TRSRTransformation.
     *
     * @param transform The transform.
     * @param rotation  The rotation.
     * @param scale     The scale.
     * @return The new TRSRTransformation.
     */
    public static TRSRTransformation create(Vector3f transform, Vector3f rotation, Vector3f scale) {
        return TRSRTransformation.blockCenterToCorner(new TRSRTransformation(transform, TRSRTransformation.quatFromXYZDegrees(rotation), scale, null));
    }

    /**
     * Flips the transform for the left hand.
     *
     * @param transform The right hand transform.
     * @return The new left hand transform.
     */
    public static TRSRTransformation flipLeft(TRSRTransformation transform) {
        return TRSRTransformation.blockCenterToCorner(flipX.compose(TRSRTransformation.blockCornerToCenter(transform)).compose(flipX));
    }

    //    //TODO, Support vanilla transforms in main Transform parser bellow.
    //    @Deprecated//This really doesnt need to exist, just adds complexity.
    //    public static void registerDefaultFactories() {
    //        registerTransformFactory(new ResourceLocation("minecraft:default"), new IStandardTransformFactory() {
    //
    //            @Override
    //            public TRSRTransformation getTransform(TransformType type, JsonObject object) {
    //                Vector3 rot = parseVec3(object, "rotation", Vector3.zero.copy());
    //                Vector3 trans = parseVec3(object, "translation", Vector3.zero.copy());
    //                trans.multiply(1D / 16D);
    //                trans.x = MathHelper.clip(trans.x, -5.0D, 5.0D);
    //                trans.y = MathHelper.clip(trans.y, -5.0D, 5.0D);
    //                trans.z = MathHelper.clip(trans.z, -5.0D, 5.0D);
    //                Vector3 scale = parseVec3(object, "scale", Vector3.one);
    //                scale.x = MathHelper.clip(scale.x, -4.0D, 4.0D);
    //                scale.y = MathHelper.clip(scale.y, -4.0D, 4.0D);
    //                scale.z = MathHelper.clip(scale.z, -4.0D, 4.0D);
    //                return create(trans, rot, scale);
    //            }
    //
    //            private Vector3 parseVec3(JsonObject object, String key, Vector3 defaultValue) {
    //                if (object.has(key)) {
    //                    JsonArray array = JsonUtils.getJsonArray(object, key);
    //                    if (array.size() == 3) {
    //                        float[] floats = new float[3];
    //                        for (int i = 0; i < 3; i++) {
    //                            floats[i] = JsonUtils.getFloat(array.get(i), key + "[ " + i + " ]");
    //                        }
    //                        return new Vector3(floats);
    //                    }
    //                    throw new JsonParseException("Expected 3 " + key + " values, found: " + array.size());
    //                }
    //                return defaultValue;
    //            }
    //        });
    //    }

    /**
     * Reimplementation from ForgeBlockStateV1's variant Deserializer.
     *
     * @param json The Json that contains either ModelRotation x,y,TRSRTransforms or CCL defaults.
     * @return A IModelState.
     */
    //TODO, Implement above vanilla transform parsing.
    public static Optional<IModelState> parseFromJson(JsonObject json) {
        Optional<IModelState> ret = Optional.empty();
        if (json.has("x") || json.has("y")) {
            int x = JSONUtils.getInt(json, "x", 0);
            int y = JSONUtils.getInt(json, "y", 0);
            ModelRotation rot = ModelRotation.getModelRotation(x, y);
            ret = Optional.of(rot);
            if (!ret.isPresent()) {
                throw new JsonParseException("Invalid BlockModelRotation x: " + x + " y: " + y);
            }
        }
        if (json.has("transform")) {
            JsonElement transformElement = json.get("transform");
            if (transformElement.isJsonPrimitive() && transformElement.getAsJsonPrimitive().isString()) {
                String transform = transformElement.getAsString();
                switch (transform) {
                    case "identity":
                        ret = Optional.of(TRSRTransformation.identity());
                        break;
                    case "ccl:default-block":
                        ret = Optional.of(DEFAULT_BLOCK);
                        break;
                    case "ccl:default-item":
                        ret = Optional.of(DEFAULT_ITEM);
                        break;
                    case "ccl:default-tool":
                        ret = Optional.of(DEFAULT_TOOL);
                        break;
                    case "ccl:default-bow":
                        ret = Optional.of(DEFAULT_BOW);
                        break;
                    case "ccl:default-handheld-rod":
                        ret = Optional.of(DEFAULT_HANDHELD_ROD);
                        break;
                }
            } else if (!transformElement.isJsonObject()) {
                try {
                    TRSRTransformation base = GSON.fromJson(transformElement, TRSRTransformation.class);
                    ret = Optional.of(TRSRTransformation.blockCenterToCorner(base));
                } catch (JsonParseException e) {
                    throw new JsonParseException("transform: expected a string, object or valid base transformation, got: " + transformElement);
                }
            } else {
                JsonObject transform = transformElement.getAsJsonObject();
                //TODO, move this to a factory.
                EnumMap<TransformType, TRSRTransformation> transforms = Maps.newEnumMap(TransformType.class);
                if (transform.has("thirdperson")) {
                    TRSRTransformation t = GSON.fromJson(transform.get("thirdperson"), TRSRTransformation.class);
                    transform.remove("thirdperson");
                    transforms.put(TransformType.THIRD_PERSON_RIGHT_HAND, TRSRTransformation.blockCenterToCorner(t));
                }
                if (transform.has("thirdperson_righthand")) {
                    TRSRTransformation t = GSON.fromJson(transform.get("thirdperson_righthand"), TRSRTransformation.class);
                    transform.remove("thirdperson_righthand");
                    transforms.put(TransformType.THIRD_PERSON_RIGHT_HAND, TRSRTransformation.blockCenterToCorner(t));
                }
                if (transform.has("thirdperson_lefthand")) {
                    TRSRTransformation t = GSON.fromJson(transform.get("thirdperson_lefthand"), TRSRTransformation.class);
                    transform.remove("thirdperson_lefthand");
                    transforms.put(TransformType.THIRD_PERSON_LEFT_HAND, TRSRTransformation.blockCenterToCorner(t));
                }
                if (transform.has("firstperson")) {
                    TRSRTransformation t = GSON.fromJson(transform.get("firstperson"), TRSRTransformation.class);
                    transform.remove("firstperson");
                    transforms.put(TransformType.FIRST_PERSON_RIGHT_HAND, TRSRTransformation.blockCenterToCorner(t));
                }
                if (transform.has("firstperson_righthand")) {
                    TRSRTransformation t = GSON.fromJson(transform.get("firstperson_righthand"), TRSRTransformation.class);
                    transform.remove("firstperson_righthand");
                    transforms.put(TransformType.FIRST_PERSON_RIGHT_HAND, TRSRTransformation.blockCenterToCorner(t));
                }
                if (transform.has("firstperson_lefthand")) {
                    TRSRTransformation t = GSON.fromJson(transform.get("firstperson_lefthand"), TRSRTransformation.class);
                    transform.remove("firstperson_lefthand");
                    transforms.put(TransformType.FIRST_PERSON_LEFT_HAND, TRSRTransformation.blockCenterToCorner(t));
                }
                if (transform.has("head")) {
                    TRSRTransformation t = GSON.fromJson(transform.get("head"), TRSRTransformation.class);
                    transform.remove("head");
                    transforms.put(TransformType.HEAD, TRSRTransformation.blockCenterToCorner(t));
                }
                if (transform.has("gui")) {
                    TRSRTransformation t = GSON.fromJson(transform.get("gui"), TRSRTransformation.class);
                    transform.remove("gui");
                    transforms.put(TransformType.GUI, TRSRTransformation.blockCenterToCorner(t));
                }
                if (transform.has("ground")) {
                    TRSRTransformation t = GSON.fromJson(transform.get("ground"), TRSRTransformation.class);
                    transform.remove("ground");
                    transforms.put(TransformType.GROUND, TRSRTransformation.blockCenterToCorner(t));
                }
                if (transform.has("fixed")) {
                    TRSRTransformation t = GSON.fromJson(transform.get("fixed"), TRSRTransformation.class);
                    transform.remove("fixed");
                    transforms.put(TransformType.FIXED, TRSRTransformation.blockCenterToCorner(t));
                }
                int k = transform.entrySet().size();
                if (transform.has("matrix")) {
                    k--;
                }
                if (transform.has("translation")) {
                    k--;
                }
                if (transform.has("rotation")) {
                    k--;
                }
                if (transform.has("scale")) {
                    k--;
                }
                if (transform.has("post-rotation")) {
                    k--;
                }
                if (k > 0) {
                    throw new JsonParseException("transform: allowed keys: 'thirdperson', 'firstperson', 'gui', 'head', 'matrix', 'translation', 'rotation', 'scale', 'post-rotation'");
                }
                TRSRTransformation base = TRSRTransformation.identity();
                if (!transform.entrySet().isEmpty()) {
                    base = GSON.fromJson(transform, TRSRTransformation.class);
                    base = TRSRTransformation.blockCenterToCorner(base);
                }
                IModelState state;
                if (transforms.isEmpty()) {
                    state = base;
                } else {
                    state = new CCModelState(Maps.immutableEnumMap(transforms), Optional.of(base));
                }
                ret = Optional.of(state);
            }
        }
        return ret;
    }

    public static TRSRTransformation fromMatrix4(Matrix4 matrix4) {
        return new TRSRTransformation(matrix4.toMatrix4f());
    }
}
