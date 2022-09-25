package codechicken.lib.render.shader;

import codechicken.lib.render.shader.GlslProcessor.ProcessedShader;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.shaders.Program;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.Util;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ChainedJsonException;
import net.minecraft.server.packs.resources.ResourceProvider;
import net.minecraft.util.GsonHelper;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import java.io.IOException;
import java.util.*;

/**
 * A Slightly extended ShaderInstance supporting:
 * - Better Uniform caching.
 * - All OpenGL Uniform types.
 * - Global Uniform callbacks.
 * - Better import/includes in shaders.
 * <p>
 * Created by covers1624 on 8/4/22.
 */
public class CCShaderInstance extends ShaderInstance {

    private final List<Runnable> applyCallbacks = new LinkedList<>();

    protected CCShaderInstance(ResourceProvider resourceProvider, ResourceLocation loc, VertexFormat format) throws IOException {
        super(resourceProvider, loc, format);
    }

    public static CCShaderInstance create(ResourceProvider resourceProvider, ResourceLocation loc, VertexFormat format) {
        try {
            return new CCShaderInstance(resourceProvider, loc, format);
        } catch (IOException ex) {
            throw new RuntimeException("Failed to initialize shader.", ex);
        }
    }

    /**
     * Add a callback for when this {@link CCShaderInstance} is applied.
     * <p>
     * Use this for global uniforms, or whatever else.
     *
     * @param callback The callback.
     */
    public void onApply(Runnable callback) {
        applyCallbacks.add(callback);
    }

    @Override
    public void apply() {
        for (Runnable callback : applyCallbacks) {
            callback.run();
        }
        super.apply();
    }

    @Nullable
    @Override
    public CCUniform getUniform(String name) {
        return (CCUniform) super.getUniform(name);
    }

    @Override
    protected void parseUniformNode(JsonElement json) throws ChainedJsonException {
        JsonObject obj = GsonHelper.convertToJsonObject(json, "uniform");
        String name = GsonHelper.getAsString(obj, "name");
        String typeStr = GsonHelper.getAsString(obj, "type");
        UniformType type = UniformType.parse(typeStr);
        if (type == null) {
            throw new ChainedJsonException("Invalid type '%s'. See UniformType enum. All vanilla types supported.".formatted(typeStr));
        }

        int count = GsonHelper.getAsInt(obj, "count");
        // Hack for vanilla compat. Vanilla specifies 'float, count 4' for vec4, we need ot to be a vec4
        switch (type) {
            case FLOAT -> {
                switch(count) {
                    case 2 -> type = UniformType.VEC2;
                    case 3 -> type = UniformType.VEC3;
                    case 4 -> type = UniformType.VEC4;
                }
            }
            case INT -> {
                switch(count) {
                    case 2 -> type = UniformType.I_VEC2;
                    case 3 -> type = UniformType.I_VEC3;
                    case 4 -> type = UniformType.I_VEC4;
                }
            }
            case U_INT -> {
                switch(count) {
                    case 2 -> type = UniformType.U_VEC2;
                    case 3 -> type = UniformType.U_VEC3;
                    case 4 -> type = UniformType.U_VEC4;
                }
            }
        }
        CCUniform uniform = CCUniform.makeUniform(name, type, count, this);

        JsonArray jsonValues = GsonHelper.getAsJsonArray(obj, "values");
        if (jsonValues.size() != count && jsonValues.size() > 1) {
            throw new ChainedJsonException("Invalid amount of values specified (expected " + count + ", found " + jsonValues.size() + ")");
        }
        switch (type.getCarrier()) {
            case INT, U_INT -> uniform.glUniformI(parseInts(count, jsonValues));
            case FLOAT, MATRIX -> uniform.glUniformF(false, parseFloats(count, jsonValues));
            case DOUBLE, D_MATRIX -> uniform.glUniformD(false, parseDoubles(count, jsonValues));
        }
        uniforms.add(uniform);
    }

    public Program compileProgram(ResourceProvider resourceProvider, Program.Type programType, ResourceLocation loc) throws IOException {
        ResourceLocation adjustedLoc = new ResourceLocation(loc.getNamespace(), "shaders/core/" + loc.getPath() + programType.getExtension());
        String cacheString = "ccl__" + adjustedLoc;
        Program program = programType.getPrograms().get(cacheString);
        if (program != null) return program;

        ProcessedShader processedShader = new GlslProcessor(resourceProvider, adjustedLoc).process();

        int id = GL20.glCreateShader(programType.getGlType());
        GL20.glShaderSource(id, processedShader.processedSource());
        GL20.glCompileShader(id);
        if (GL20.glGetShaderi(id, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
            String s1 = GL20.glGetShaderInfoLog(id);
            throw new IOException("Couldn't compile " + programType.getName() + " program (" + processedShader.sourceName() + ", " + adjustedLoc + ") : " + s1);
        }
        program = new Program(programType, id, cacheString);
        programType.getPrograms().put(cacheString, program);
        return program;
    }

    private static float[] parseFloats(int count, JsonArray jsonValues) throws ChainedJsonException {
        int i = 0;
        float[] values = new float[Math.max(count, 16)];
        for (JsonElement jsonValue : jsonValues) {
            try {
                values[i++] = GsonHelper.convertToFloat(jsonValue, "value");
            } catch (Exception ex) {
                ChainedJsonException chainedjsonexception = ChainedJsonException.forException(ex);
                chainedjsonexception.prependJsonKey("values[" + i + "]");
                throw chainedjsonexception;
            }
        }

        // If only one value is specified, copy to all in array.
        if (count > 1 && jsonValues.size() == 1) {
            Arrays.fill(values, 1, values.length, values[0]);
        }
        return Arrays.copyOfRange(values, 0, count);
    }

    private static int[] parseInts(int count, JsonArray jsonValues) throws ChainedJsonException {
        int i = 0;
        int[] values = new int[Math.max(count, 16)];
        for (JsonElement jsonValue : jsonValues) {
            try {
                values[i++] = GsonHelper.convertToInt(jsonValue, "value");
            } catch (Exception ex) {
                ChainedJsonException chainedjsonexception = ChainedJsonException.forException(ex);
                chainedjsonexception.prependJsonKey("values[" + i + "]");
                throw chainedjsonexception;
            }
        }

        // If only one value is specified, copy to all in array.
        if (count > 1 && jsonValues.size() == 1) {
            Arrays.fill(values, 1, values.length, values[0]);
        }
        return Arrays.copyOfRange(values, 0, count);
    }

    private static double[] parseDoubles(int count, JsonArray jsonValues) throws ChainedJsonException {
        int i = 0;
        double[] values = new double[Math.max(count, 16)];
        for (JsonElement jsonValue : jsonValues) {
            try {
                values[i++] = GsonHelper.convertToDouble(jsonValue, "value");
            } catch (Exception ex) {
                ChainedJsonException chainedjsonexception = ChainedJsonException.forException(ex);
                chainedjsonexception.prependJsonKey("values[" + i + "]");
                throw chainedjsonexception;
            }
        }

        // If only one value is specified, copy to all in array.
        if (count > 1 && jsonValues.size() == 1) {
            Arrays.fill(values, 1, values.length, values[0]);
        }
        return Arrays.copyOfRange(values, 0, count);
    }
}
