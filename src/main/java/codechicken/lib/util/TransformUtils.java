package codechicken.lib.util;

import codechicken.lib.vec.Vector3;
import com.google.common.collect.ImmutableMap;
import com.mojang.math.Quaternion;
import com.mojang.math.Transformation;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.resources.model.ModelState;
import net.minecraftforge.client.model.SimpleModelState;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by covers1624 on 5/16/2016.
 * This is mostly just extracted from the ForgeBlockStateV1.
 * Credits to Rain Warrior.
 * <p>
 * If you have an idea for another transform just make a pull request.
 */
public class TransformUtils {

    private static final Transformation flipX = new Transformation(null, null, new Vector3f(-1, 1, 1), null);

    public static final ModelState IDENTITY = new SimpleModelState(ImmutableMap.of());
    public static final ModelState DEFAULT_BLOCK;
    public static final ModelState DEFAULT_ITEM;
    public static final ModelState DEFAULT_TOOL;
    public static final ModelState DEFAULT_BOW;
    public static final ModelState DEFAULT_HANDHELD_ROD;

    static {
        Map<TransformType, Transformation> map;
        Transformation thirdPerson;
        Transformation firstPerson;

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
        DEFAULT_BLOCK = new SimpleModelState(ImmutableMap.copyOf(map));

        map = new HashMap<>();
        thirdPerson =                                    create(   0F,  3F,   1F, 0F,  0F, 0F, 0.55F);
        firstPerson =                                    create(1.13F,3.2F,1.13F, 0F,-90F,25F, 0.68F);
        map.put(TransformType.GROUND,                    create(   0F,  2F,   0F, 0F,  0F, 0F, 0.5F));
        map.put(TransformType.HEAD,                      create(   0F, 13F,   7F, 0F,180F, 0F,   1F));
        map.put(TransformType.THIRD_PERSON_RIGHT_HAND,   thirdPerson);
        map.put(TransformType.THIRD_PERSON_LEFT_HAND,    flipLeft(thirdPerson));
        map.put(TransformType.FIRST_PERSON_RIGHT_HAND,   firstPerson);
        map.put(TransformType.FIRST_PERSON_LEFT_HAND,    flipLeft(firstPerson));
        DEFAULT_ITEM = new SimpleModelState(ImmutableMap.copyOf(map));

        map = new HashMap<>();
        map.put(TransformType.GROUND,                   create(   0F,  2F,   0F, 0F,  0F, 0F, 0.5F));
        map.put(TransformType.FIXED,                    create(   0F,  0F,   0F, 0F,180F, 0F,   1F));
        map.put(TransformType.THIRD_PERSON_RIGHT_HAND,  create(   0F,  4F, 0.5F, 0F,-90F, 55,0.85F));
        map.put(TransformType.THIRD_PERSON_LEFT_HAND,   create(   0F,  4F, 0.5F, 0F, 90F,-55,0.85F));
        map.put(TransformType.FIRST_PERSON_RIGHT_HAND,  create(1.13F,3.2F,1.13F, 0F,-90F, 25,0.68F));
        map.put(TransformType.FIRST_PERSON_LEFT_HAND,   create(1.13F,3.2F,1.13F, 0F, 90F,-25,0.68F));
        DEFAULT_TOOL = new SimpleModelState(ImmutableMap.copyOf(map));

        map = new HashMap<>();
        map.put(TransformType.GROUND,                   create(   0F,  2F,   0F,  0F,   0F,  0F, 0.5F));
        map.put(TransformType.FIXED,                    create(   0F,  0F,   0F, 0F,  180F,  0F,   1F));
        map.put(TransformType.THIRD_PERSON_RIGHT_HAND,  create(  -1F, -2F, 2.5F,-80F, 260F,-40F, 0.9F));
        map.put(TransformType.THIRD_PERSON_LEFT_HAND,   create(  -1F, -2F, 2.5F,-80F,-280F, 40F, 0.9F));
        map.put(TransformType.FIRST_PERSON_RIGHT_HAND,  create(1.13F,3.2F,1.13F,  0F, -90F, 25F,0.68F));
        map.put(TransformType.FIRST_PERSON_LEFT_HAND,   create(1.13F,3.2F,1.13F,  0F,  90F,-25F,0.68F));
        DEFAULT_BOW = new SimpleModelState(ImmutableMap.copyOf(map));

        map = new HashMap<>();
        map.put(TransformType.GROUND,                   create(0F, 2F,   0F, 0F,  0F,  0F, 0.5F));
        map.put(TransformType.THIRD_PERSON_RIGHT_HAND,  create(0F,  4F,2.5F, 0F, 90F, 55F,0.85F));
        map.put(TransformType.THIRD_PERSON_LEFT_HAND,   create(0F,  4F,2.5F, 0F,-90F,-55F,0.85F));
        map.put(TransformType.FIRST_PERSON_RIGHT_HAND,  create(0F,1.6F,0.8F, 0F, 90F, 25F,0.68F));
        map.put(TransformType.FIRST_PERSON_LEFT_HAND,   create(0F,1.6F,0.8F, 0F,-90F,-25F,0.68F));
        DEFAULT_HANDHELD_ROD = new SimpleModelState(ImmutableMap.copyOf(map));
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
    public static Transformation create(float tx, float ty, float tz, float rx, float ry, float rz, float s) {
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
    public static Transformation create(Vector3 transform, Vector3 rotation, Vector3 scale) {
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
    public static Transformation create(Vector3f transform, Vector3f rotation, Vector3f scale) {
        return new Transformation(transform, new Quaternion(rotation.x(), rotation.y(), rotation.z(), true), scale, null);
    }

    @SuppressWarnings ("deprecation")
    public static Transformation create(ItemTransform transform) {
        if (ItemTransform.NO_TRANSFORM.equals(transform)) return Transformation.identity();

        return create(transform.translation, transform.rotation, transform.scale);
    }

    /**
     * Flips the transform for the left hand.
     *
     * @param transform The right hand transform.
     * @return The new left hand transform.
     */
    public static Transformation flipLeft(Transformation transform) {
        return flipX.compose(transform).compose(flipX);
    }

    @SuppressWarnings ("deprecation")
    public static ModelState stateFromItemTransforms(ItemTransforms itemTransforms) {
        if (itemTransforms == ItemTransforms.NO_TRANSFORMS) return IDENTITY;

        ImmutableMap.Builder<ItemTransforms.TransformType, Transformation> map = ImmutableMap.builder();

        for (TransformType value : TransformType.values()) {
            map.put(value, create(itemTransforms.getTransform(value)));
        }

        return new SimpleModelState(map.build());
    }
}
