package codechicken.lib.render.shader;

import codechicken.lib.vec.Matrix4;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;

/**
 * Created by covers1624 on 8/4/22.
 */
public interface ICCUniform {

    // region Int
    default void glUniform1i(int i0) { glUniformI(i0); }

    default void glUniform2i(int i0, int i1) { glUniformI(i0, i1); }

    default void glUniform3i(int i0, int i1, int i2) { glUniformI(i0, i1, i2); }

    default void glUniform4i(int i0, int i1, int i2, int i3) { glUniformI(i0, i1, i2, i3); }
    // endregion

    // region Unsigned Int
    default void glUniform1ui(int i0) { glUniformI(i0); }

    default void glUniform2ui(int i0, int i1) { glUniformI(i0, i1); }

    default void glUniform3ui(int i0, int i1, int i2) { glUniformI(i0, i1, i2); }

    default void glUniform4ui(int i0, int i1, int i2, int i3) { glUniformI(i0, i1, i2, i3); }
    // endregion

    // region Float
    default void glUniform1f(float f0) { glUniformF(false, f0); }

    default void glUniform2f(float f0, float f1) { glUniformF(false, f0, f1); }

    default void glUniform3f(float f0, float f1, float f2) { glUniformF(false, f0, f1, f2); }

    default void glUniform4f(float f0, float f1, float f2, float f3) { glUniformF(false, f0, f1, f2, f3); }
    // endregion

    // region Double
    default void glUniform1d(float d0) { glUniformD(false, d0); }

    default void glUniform2d(float d0, float d1) { glUniformD(false, d0, d1); }

    default void glUniform3d(float d0, float d1, float d2) { glUniformD(false, d0, d1, d2); }

    default void glUniform4d(float d0, float d1, float d2, float d3) { glUniformD(false, d0, d1, d2, d3); }
    // endregion

    // region Boolean
    default void glUniform1b(boolean b0) { glUniformI(b0 ? 1 : 0); }

    default void glUniform2b(boolean b0, boolean b1) { glUniformI(b0 ? 1 : 0, b1 ? 1 : 0); }

    default void glUniform3b(boolean b0, boolean b1, boolean b2) { glUniformI(b0 ? 1 : 0, b1 ? 1 : 0, b2 ? 1 : 0); }

    default void glUniform4b(boolean b0, boolean b1, boolean b2, boolean b3) { glUniformI(b0 ? 1 : 0, b1 ? 1 : 0, b2 ? 1 : 0, b3 ? 1 : 0); }
    // endregion

    // region 2x Float Matrix
    default void glUniformMatrix2f(float[] matrix) { glUniformF(false, matrix); }

    default void glUniformMatrix2f(boolean transpose, float[] matrix) { glUniformF(transpose, matrix); }

    default void glUniformMatrix2x3f(float[] matrix) { glUniformF(false, matrix); }

    default void glUniformMatrix2x3f(boolean transpose, float[] matrix) { glUniformF(transpose, matrix); }

    default void glUniformMatrix2x4f(float[] matrix) { glUniformF(false, matrix); }

    default void glUniformMatrix2x4f(boolean transpose, float[] matrix) { glUniformF(transpose, matrix); }
    // endregion

    // region 3x Float Matrix
    default void glUniformMatrix3f(float[] matrix) { glUniformF(false, matrix); }

    default void glUniformMatrix3f(boolean transpose, float[] matrix) { glUniformF(transpose, matrix); }

    default void glUniformMatrix3f(Matrix3f matrix) { glUniformF(false, toArrayF(matrix)); }

    default void glUniformMatrix3f(boolean transpose, Matrix3f matrix) { glUniformF(transpose, toArrayF(matrix)); }

    default void glUniformMatrix3x2f(float[] matrix) { glUniformF(false, matrix); }

    default void glUniformMatrix3x2f(boolean transpose, float[] matrix) { glUniformF(transpose, matrix); }

    default void glUniformMatrix3x4f(float[] matrix) { glUniformF(false, matrix); }

    default void glUniformMatrix3x4f(boolean transpose, float[] matrix) { glUniformF(transpose, matrix); }
    // endregion

    // region 4x Float Matrix
    default void glUniformMatrix4f(float[] matrix) { glUniformF(false, matrix); }

    default void glUniformMatrix4f(boolean transpose, float[] matrix) { glUniformF(transpose, matrix); }

    default void glUniformMatrix4f(Matrix4 matrix) { glUniformF(false, matrix.toArrayF()); }

    default void glUniformMatrix4f(boolean transpose, Matrix4 matrix) { glUniformF(transpose, matrix.toArrayF()); }

    default void glUniformMatrix4f(Matrix4f matrix) { glUniformF(false, toArrayF(matrix)); }

    default void glUniformMatrix4f(boolean transpose, Matrix4f matrix) { glUniformF(transpose, toArrayF(matrix)); }

    default void glUniformMatrix4x2f(float[] matrix) { glUniformF(false, matrix); }

    default void glUniformMatrix4x2f(boolean transpose, float[] matrix) { glUniformF(transpose, matrix); }

    default void glUniformMatrix4x3f(float[] matrix) { glUniformF(false, matrix); }

    default void glUniformMatrix4x3f(boolean transpose, float[] matrix) { glUniformF(transpose, matrix); }
    // endregion

    // region 2x Double matrix
    default void glUniformMatrix2d(double[] matrix) { glUniformD(false, matrix); }

    default void glUniformMatrix2d(boolean transpose, double[] matrix) { glUniformD(transpose, matrix); }

    default void glUniformMatrix2x3d(double[] matrix) { glUniformD(false, matrix); }

    default void glUniformMatrix2x3d(boolean transpose, double[] matrix) { glUniformD(transpose, matrix); }

    default void glUniformMatrix2x4d(double[] matrix) { glUniformD(false, matrix); }

    default void glUniformMatrix2x4d(boolean transpose, double[] matrix) { glUniformD(transpose, matrix); }
    // endregion

    // region 3x Double Matrix
    default void glUniformMatrix3d(double[] matrix) { glUniformD(false, matrix); }

    default void glUniformMatrix3d(boolean transpose, double[] matrix) { glUniformD(transpose, matrix); }

    default void glUniformMatrix3d(Matrix3f matrix) { glUniformD(false, toArrayD(matrix)); }

    default void glUniformMatrix3d(boolean transpose, Matrix3f matrix) { glUniformD(transpose, toArrayD(matrix)); }

    default void glUniformMatrix3x2d(double[] matrix) { glUniformD(false, matrix); }

    default void glUniformMatrix3x2d(boolean transpose, double[] matrix) { glUniformD(transpose, matrix); }

    default void glUniformMatrix3x4d(double[] matrix) { glUniformD(false, matrix); }

    default void glUniformMatrix3x4d(boolean transpose, double[] matrix) { glUniformD(transpose, matrix); }
    // endregion

    // region 4x Double Matrix
    default void glUniformMatrix4d(double[] matrix) { glUniformD(false, matrix); }

    default void glUniformMatrix4d(boolean transpose, double[] matrix) { glUniformD(transpose, matrix); }

    default void glUniformMatrix4d(Matrix4 matrix) { glUniformD(false, matrix.toArrayD()); }

    default void glUniformMatrix4d(boolean transpose, Matrix4 matrix) { glUniformD(transpose, matrix.toArrayD()); }

    default void glUniformMatrix4d(Matrix4f matrix) { glUniformD(false, toArrayD(matrix)); }

    default void glUniformMatrix4d(boolean transpose, Matrix4f matrix) { glUniformD(transpose, toArrayD(matrix)); }

    default void glUniformMatrix4x2d(double[] matrix) { glUniformD(false, matrix); }

    default void glUniformMatrix4x2d(boolean transpose, double[] matrix) { glUniformD(transpose, matrix); }

    default void glUniformMatrix4x3d(double[] matrix) { glUniformD(false, matrix); }

    default void glUniformMatrix4x3d(boolean transpose, double[] matrix) { glUniformD(transpose, matrix); }
    // endregion

    // region Raw
    void glUniformI(int... values);

    void glUniformF(boolean transpose, float... values);

    void glUniformD(boolean transpose, double... values);
    // endregion

    private static float[] toArrayF(Matrix3f matrix) {
        return new float[] {
                matrix.m00,
                matrix.m10,
                matrix.m20,
                matrix.m01,
                matrix.m11,
                matrix.m21,
                matrix.m02,
                matrix.m12,
                matrix.m22,
        };
    }

    private static float[] toArrayF(Matrix4f matrix) {
        return new float[] {
                matrix.m00,
                matrix.m10,
                matrix.m20,
                matrix.m30,
                matrix.m01,
                matrix.m11,
                matrix.m21,
                matrix.m31,
                matrix.m02,
                matrix.m12,
                matrix.m22,
                matrix.m32,
                matrix.m03,
                matrix.m13,
                matrix.m23,
                matrix.m33,
        };
    }

    private static double[] toArrayD(Matrix3f matrix) {
        return new double[] {
                matrix.m00,
                matrix.m10,
                matrix.m20,
                matrix.m01,
                matrix.m11,
                matrix.m21,
                matrix.m02,
                matrix.m12,
                matrix.m22,
        };
    }

    private static double[] toArrayD(Matrix4f matrix) {
        return new double[] {
                matrix.m00,
                matrix.m10,
                matrix.m20,
                matrix.m30,
                matrix.m01,
                matrix.m11,
                matrix.m21,
                matrix.m31,
                matrix.m02,
                matrix.m12,
                matrix.m22,
                matrix.m32,
                matrix.m03,
                matrix.m13,
                matrix.m23,
                matrix.m33,
        };
    }
}
