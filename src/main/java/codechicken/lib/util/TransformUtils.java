package codechicken.lib.util;

import codechicken.lib.render.CCModelState;
import codechicken.lib.vec.Matrix4;
import com.google.common.collect.ImmutableMap;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraftforge.common.model.TRSRTransformation;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

/**
 * Created by covers1624 on 5/16/2016.
 * This is mostly just extracted from the ForgeBlockStateV1.
 * <p>
 * If you have an idea for another transform just make a pull request.
 */
public class TransformUtils {
    private static final TRSRTransformation flipX = new TRSRTransformation(null, null, new Vector3f(-1, 1, 1), null);

    public static final CCModelState DEFAULT_BLOCK;
    public static final CCModelState DEFAULT_ITEM;
    public static final CCModelState DEFAULT_TOOL;
    public static final CCModelState DEFAULT_BOW;
    public static final CCModelState DEFAULT_HANDHELD_ROD;

    static {
        TRSRTransformation thirdPerson = get(0, 2.5f, 0, 75, 45, 0, 0.375f);
        TRSRTransformation firstPerson;

        ImmutableMap.Builder<TransformType, TRSRTransformation> defaultBlockBuilder = ImmutableMap.builder();
        defaultBlockBuilder.put(TransformType.GUI, get(0, 0, 0, 30, 225, 0, 0.625f));
        defaultBlockBuilder.put(TransformType.GROUND, get(0, 3, 0, 0, 0, 0, 0.25f));
        defaultBlockBuilder.put(TransformType.FIXED, get(0, 0, 0, 0, 0, 0, 0.5f));
        defaultBlockBuilder.put(TransformType.THIRD_PERSON_RIGHT_HAND, thirdPerson);
        defaultBlockBuilder.put(TransformType.THIRD_PERSON_LEFT_HAND, leftify(thirdPerson));
        defaultBlockBuilder.put(TransformType.FIRST_PERSON_RIGHT_HAND, get(0, 0, 0, 0, 45, 0, 0.4f));
        defaultBlockBuilder.put(TransformType.FIRST_PERSON_LEFT_HAND, get(0, 0, 0, 0, 225, 0, 0.4f));
        DEFAULT_BLOCK = new CCModelState(defaultBlockBuilder.build());

        thirdPerson = get(0, 3, 1, 0, 0, 0, 0.55f);
        firstPerson = get(1.13f, 3.2f, 1.13f, 0, -90, 25, 0.68f);
        ImmutableMap.Builder<TransformType, TRSRTransformation> defaultItemBuilder = ImmutableMap.builder();
        defaultItemBuilder.put(TransformType.GROUND, get(0, 2, 0, 0, 0, 0, 0.5f));
        defaultItemBuilder.put(TransformType.HEAD, get(0, 13, 7, 0, 180, 0, 1));
        defaultItemBuilder.put(TransformType.THIRD_PERSON_RIGHT_HAND, thirdPerson);
        defaultItemBuilder.put(TransformType.THIRD_PERSON_LEFT_HAND, leftify(thirdPerson));
        defaultItemBuilder.put(TransformType.FIRST_PERSON_RIGHT_HAND, firstPerson);
        defaultItemBuilder.put(TransformType.FIRST_PERSON_LEFT_HAND, leftify(firstPerson));
        DEFAULT_ITEM = new CCModelState(defaultItemBuilder.build());

        ImmutableMap.Builder<TransformType, TRSRTransformation> defaultToolBuilder = ImmutableMap.builder();
        defaultToolBuilder.put(TransformType.THIRD_PERSON_RIGHT_HAND, get(0, 4, 0.5F, 0, -90, 55, 0.85F));
        defaultToolBuilder.put(TransformType.THIRD_PERSON_LEFT_HAND, get(0, 4, 0.5f, 0, 90, -55, 0.85f));
        defaultToolBuilder.put(TransformType.FIRST_PERSON_RIGHT_HAND, get(1.13f, 3.2f, 1.13f, 0, -90, 25, 0.68f));
        defaultToolBuilder.put(TransformType.FIRST_PERSON_LEFT_HAND, get(1.13f, 3.2f, 1.13f, 0, 90, -25, 0.68f));
        DEFAULT_TOOL = new CCModelState(defaultToolBuilder.build());

        ImmutableMap.Builder<TransformType, TRSRTransformation> defaultBowBuilder = ImmutableMap.builder();
        defaultBowBuilder.put(TransformType.THIRD_PERSON_RIGHT_HAND, get(-1F, -2F, 2.5F, -80, 260, -40, 0.9F));
        defaultBowBuilder.put(TransformType.THIRD_PERSON_LEFT_HAND, get(-1F, -2F, 2.5F, -80, -280, 40, 0.9f));
        defaultBowBuilder.put(TransformType.FIRST_PERSON_RIGHT_HAND, get(1.13F, 3.2F, 1.13F, 0, -90, 25, 0.68f));
        defaultBowBuilder.put(TransformType.FIRST_PERSON_LEFT_HAND, get(1.13f, 3.2f, 1.13f, 0, 90, -25, 0.68f));
        DEFAULT_BOW = new CCModelState(defaultBowBuilder.build());

        ImmutableMap.Builder<TransformType, TRSRTransformation> defaultRodBuilder = ImmutableMap.builder();
        defaultRodBuilder.put(TransformType.THIRD_PERSON_RIGHT_HAND, get(0F, 4F, 2.5F, 0, 90, 55, 0.85F));
        defaultRodBuilder.put(TransformType.THIRD_PERSON_LEFT_HAND, get(0F, 4F, 2.5F, 0, -90, -55, 0.85f));
        defaultRodBuilder.put(TransformType.FIRST_PERSON_RIGHT_HAND, get(0F, 1.6F, 0.8F, 0, 90, 25, 0.68f));
        defaultRodBuilder.put(TransformType.FIRST_PERSON_LEFT_HAND, get(0F, 1.6F, 0.8F, 0, -90, -25, 0.68f));
        DEFAULT_HANDHELD_ROD = new CCModelState(defaultRodBuilder.build());
    }

    public static TRSRTransformation get(float tx, float ty, float tz, float rx, float ry, float rz, float s) {
        return TRSRTransformation.blockCenterToCorner(new TRSRTransformation(new Vector3f(tx / 16, ty / 16, tz / 16), TRSRTransformation.quatFromXYZDegrees(new Vector3f(rx, ry, rz)), new Vector3f(s, s, s), null));
    }

    public static TRSRTransformation leftify(TRSRTransformation transform) {
        return TRSRTransformation.blockCenterToCorner(flipX.compose(TRSRTransformation.blockCornerToCenter(transform)).compose(flipX));
    }

    public static TRSRTransformation fromMatrix4(Matrix4 matrix4){
        TRSRTransformation transformation = new TRSRTransformation(matrix4.toMatrix4f());
        transformation.getLeftRot();//Call something to cause it to gen properly.
        return transformation;
    }
}
