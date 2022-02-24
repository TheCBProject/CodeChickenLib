package codechicken.lib.render.shader;

import codechicken.lib.vec.Matrix4;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;

/**
 * All supported Uniform types.
 * Names here represent the same name as the GL api.
 *
 * @see UniformType
 * Created by covers1624 on 24/5/20.
 */
public interface UniformCache {

    void glUniform1i(String name, int i0);

    void glUniform2i(String name, int i0, int i1);

    void glUniform3i(String name, int i0, int i1, int i2);

    void glUniform4i(String name, int i0, int i1, int i2, int i3);

    void glUniform1ui(String name, int i0);

    void glUniform2ui(String name, int i0, int i1);

    void glUniform3ui(String name, int i0, int i1, int i2);

    void glUniform4ui(String name, int i0, int i1, int i2, int i3);

    void glUniform1f(String name, float f0);

    void glUniform2f(String name, float f0, float f1);

    void glUniform3f(String name, float f0, float f1, float f2);

    void glUniform4f(String name, float f0, float f1, float f2, float f3);

    void glUniform1d(String name, float d0);

    void glUniform2d(String name, float d0, float d1);

    void glUniform3d(String name, float d0, float d1, float d2);

    void glUniform4d(String name, float d0, float d1, float d2, float d3);

    void glUniform1b(String name, boolean b0);

    void glUniform2b(String name, boolean b0, boolean b1);

    void glUniform3b(String name, boolean b0, boolean b1, boolean b2);

    void glUniform4b(String name, boolean b0, boolean b1, boolean b2, boolean b3);

    void glUniformMatrix2f(String name, float[] matrix);

    void glUniformMatrix2f(String name, boolean transpose, float[] matrix);

    void glUniformMatrix2x3f(String name, float[] matrix);

    void glUniformMatrix2x3f(String name, boolean transpose, float[] matrix);

    void glUniformMatrix2x4f(String name, float[] matrix);

    void glUniformMatrix2x4f(String name, boolean transpose, float[] matrix);

    void glUniformMatrix3f(String name, float[] matrix);

    void glUniformMatrix3f(String name, boolean transpose, float[] matrix);

    void glUniformMatrix3f(String name, Matrix3f matrix);

    void glUniformMatrix3f(String name, boolean transpose, Matrix3f matrix);

    void glUniformMatrix3x2f(String name, float[] matrix);

    void glUniformMatrix3x2f(String name, boolean transpose, float[] matrix);

    void glUniformMatrix3x4f(String name, float[] matrix);

    void glUniformMatrix3x4f(String name, boolean transpose, float[] matrix);

    void glUniformMatrix4f(String name, float[] matrix);

    void glUniformMatrix4f(String name, boolean transpose, float[] matrix);

    void glUniformMatrix4f(String name, Matrix4 matrix);

    void glUniformMatrix4f(String name, boolean transpose, Matrix4 matrix);

    void glUniformMatrix4f(String name, Matrix4f matrix);

    void glUniformMatrix4f(String name, boolean transpose, Matrix4f matrix);

    void glUniformMatrix4x2f(String name, float[] matrix);

    void glUniformMatrix4x2f(String name, boolean transpose, float[] matrix);

    void glUniformMatrix4x3f(String name, float[] matrix);

    void glUniformMatrix4x3f(String name, boolean transpose, float[] matrix);

    void glUniformMatrix2d(String name, double[] matrix);

    void glUniformMatrix2d(String name, boolean transpose, double[] matrix);

    void glUniformMatrix2x3d(String name, double[] matrix);

    void glUniformMatrix2x3d(String name, boolean transpose, double[] matrix);

    void glUniformMatrix2x4d(String name, double[] matrix);

    void glUniformMatrix2x4d(String name, boolean transpose, double[] matrix);

    void glUniformMatrix3d(String name, double[] matrix);

    void glUniformMatrix3d(String name, boolean transpose, double[] matrix);

    void glUniformMatrix3d(String name, Matrix3f matrix);

    void glUniformMatrix3d(String name, boolean transpose, Matrix3f matrix);

    void glUniformMatrix3x2d(String name, double[] matrix);

    void glUniformMatrix3x2d(String name, boolean transpose, double[] matrix);

    void glUniformMatrix3x4d(String name, double[] matrix);

    void glUniformMatrix3x4d(String name, boolean transpose, double[] matrix);

    void glUniformMatrix4d(String name, double[] matrix);

    void glUniformMatrix4d(String name, boolean transpose, double[] matrix);

    void glUniformMatrix4d(String name, Matrix4 matrix);

    void glUniformMatrix4d(String name, boolean transpose, Matrix4 matrix);

    void glUniformMatrix4d(String name, Matrix4f matrix);

    void glUniformMatrix4d(String name, boolean transpose, Matrix4f matrix);

    void glUniformMatrix4x2d(String name, double[] matrix);

    void glUniformMatrix4x2d(String name, boolean transpose, double[] matrix);

    void glUniformMatrix4x3d(String name, double[] matrix);

    void glUniformMatrix4x3d(String name, boolean transpose, double[] matrix);

}
