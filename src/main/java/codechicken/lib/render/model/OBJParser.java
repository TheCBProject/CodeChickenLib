package codechicken.lib.render.model;

import codechicken.lib.render.CCModel;
import codechicken.lib.util.ResourceUtils;
import codechicken.lib.vec.SwapYZ;
import codechicken.lib.vec.Transformation;
import codechicken.lib.vec.Vector3;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceProvider;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Created by covers1624 on 11/4/22.
 */
public class OBJParser {

    private final ResourceLocation location;
    @Nullable
    private ResourceProvider provider;
    private VertexFormat.Mode vertexMode = VertexFormat.Mode.TRIANGLES;
    @Nullable
    private Transformation coordSystem;
    private boolean ignoreMtl;

    /**
     * Create a new instance of an OBJParser for a nice builder-like structure.
     *
     * @param location The {@link ResourceLocation} of the OBJ.
     */
    public OBJParser(ResourceLocation location) {
        this.location = location;
    }

    /**
     * Set the {@link ResourceProvider} used to locate assets.
     *
     * @param provider The {@link ResourceProvider}.
     * @return The same parser.
     */
    public OBJParser provider(ResourceProvider provider) {
        this.provider = provider;
        return this;
    }

    /**
     * Set the {@link VertexFormat.Mode} to parse the OBJ model into.
     *
     * @param mode The mode.
     * @return The same parser.
     */
    public OBJParser vertexMode(VertexFormat.Mode mode) {
        if (mode != VertexFormat.Mode.TRIANGLES && mode != VertexFormat.Mode.QUADS) {
            throw new IllegalStateException("Only Quads and Triangles are supported.");
        }
        vertexMode = mode;
        return this;
    }

    /**
     * Overload of {@link #vertexMode(VertexFormat.Mode)} passing {@link VertexFormat.Mode#QUADS}.
     *
     * @return The same parser.
     */
    public OBJParser quads() {
        return vertexMode(VertexFormat.Mode.QUADS);
    }

    /**
     * Set the coordinate system transformation to apply during parsing.
     *
     * @param coordSystem The coordinate transform.
     * @return The same parser.
     */
    public OBJParser coordSystem(@Nullable Transformation coordSystem) {
        this.coordSystem = coordSystem;
        return this;
    }

    /**
     * Overload of {@link #coordSystem(Transformation)} specifying the {@link SwapYZ}
     * coordinate system transformation.
     *
     * @return The same parser.
     */
    public OBJParser swapYZ() {
        return coordSystem(new SwapYZ());
    }

    /**
     * Sets the parser to ignore MTL definitions and usages within the OBJ.
     *
     * @return The same parser.
     */
    public OBJParser ignoreMtl() {
        ignoreMtl = true;
        return this;
    }

    /**
     * Actually perform the parsing.
     *
     * @return The parsed models.
     */
    public Map<String, CCModel> parse() {
        if (provider == null) {
            provider = Minecraft.getInstance().getResourceManager();
        }
        return parse(provider, location, vertexMode, coordSystem, ignoreMtl);
    }

    /**
     * Parse an OBJ model into a named map of {@link CCModel}s.
     *
     * @param provider    The {@link ResourceProvider} to locate assets.
     * @param loc         The {@link ResourceLocation} of the OBJ model.
     * @param vertexMode  The {@link VertexFormat.Mode} to parse the model into.
     * @param coordSystem The coordinate system transformation to apply during parsing.
     * @param ignoreMtl   If MTL files should be ignored.
     * @return The parsed models.
     */
    public static Map<String, CCModel> parse(ResourceProvider provider, ResourceLocation loc, VertexFormat.Mode vertexMode, @Nullable Transformation coordSystem, boolean ignoreMtl) {
        if (vertexMode != VertexFormat.Mode.QUADS && vertexMode != VertexFormat.Mode.TRIANGLES) throw new IllegalStateException("Only Quads and Triangles are supported.");

        Map<String, CCModel> builtModels = new HashMap<>();

        List<Vector3> vs = new ArrayList<>();
        List<Vector3> vts = new ArrayList<>();
        List<Vector3> vns = new ArrayList<>();

        MatLib matlib = null;

        ModelMaterial material = null;
        List<int[]> polys = null;

        String group = null;
        String name = null;
        String finishedName = null;

        boolean modelFinished = false;

        List<String> loadResource = ResourceUtils.loadResource(provider, loc);
        for (int ln = 0; ln < loadResource.size(); ln++) {
            String line = loadResource.get(ln);
            line = line.replaceAll("\\s+", " ").trim();
            if (line.isEmpty() || line.startsWith("#")) continue;

            String[] splits = line.split(" ", 2);
            switch (splits[0]) {
                case "mtllib" -> {
                    if (!ignoreMtl) {
                        matlib = MatLib.parse(provider, maybeRelative(loc, splits[1]));
                    }
                }
                case "usemtl" -> {
                    if (!ignoreMtl) {
                        ModelMaterial newMaterial = Objects.requireNonNull(matlib, "matlib definition not found").getMaterial(splits[1]);
                        if (material == newMaterial) break;
                        material = newMaterial;
                        if (material != null || polys != null) {
                            modelFinished = true;
                        }
                    }
                }
                case "v" -> vs.add(apply(coordSystem, parseVec3(splits[1], ln)));
                case "vt" -> vts.add(parseUV(splits[1], ln));
                case "vn" -> vns.add(applyN(coordSystem, parseVec3(splits[1], ln)));
                case "f" -> {
                    if (polys == null) {
                        polys = new ArrayList<>();
                        finishedName = name;
                    }
                    polys.addAll(parsePolys(splits[1], vertexMode));
                }
                case "g" -> {
                    name = group = splits[1];
                    modelFinished = true;
                }
                case "o" -> {
                    name = (group != null ? group + "/" : "") + splits[1];
                    modelFinished = true;
                }
            }

            if (modelFinished) {
                modelFinished = false;
                if (polys != null) {
                    CCModel builtModel = CCModel.createModel(vs, vts, vns, vertexMode, polys);
                    builtModel.setAttribute(ModelMaterial.MATERIAL_KEY, material);
                    polys = null;

                    builtModels.put(finishedName, builtModel);
                }
            }
        }
        if (polys != null) {
            CCModel builtModel = CCModel.createModel(vs, vts, vns, vertexMode, polys);
            builtModel.setAttribute(ModelMaterial.MATERIAL_KEY, material);

            builtModels.put(finishedName, builtModel);
        }
        return builtModels;
    }

    private static Vector3 parseUV(String s, int line) {
        double[] doubles = parseDoubles(s, " ");
        if (doubles.length < 2) throw new IllegalStateException("Expected u and v component. Line " + line + " " + s);

        return new Vector3(doubles[0], 1 - doubles[1], 0);
    }

    private static Vector3 parseVec3(String s, int line) {
        double[] doubles = parseDoubles(s, " ");
        if (doubles.length < 3) throw new IllegalStateException("Expected x, y and z component. Line " + line + " " + s);

        return new Vector3(doubles);
    }

    private static List<int[]> parsePolys(String s, VertexFormat.Mode vm) {
        String[] av = s.split(" ");
        int[][] polyVerts = new int[av.length][3];
        for (int i = 0; i < av.length; i++) {
            String[] as = av[i].split("/");
            for (int p = 0; p < as.length; p++) {
                if (!as[p].isEmpty()) {
                    polyVerts[i][p] = Integer.parseInt(as[p]);
                }
            }
        }
        List<int[]> polys = new ArrayList<>();
        if (vm == VertexFormat.Mode.TRIANGLES) {
            triangulate(polys, polyVerts);
        } else {
            quadulate(polys, polyVerts);
        }
        return polys;
    }

    private static void triangulate(List<int[]> polys, int[][] polyVerts) {
        for (int i = 2; i < polyVerts.length; i++) {
            polys.add(polyVerts[0]);
            polys.add(polyVerts[i]);
            polys.add(polyVerts[i - 1]);
        }
    }

    private static void quadulate(List<int[]> polys, int[][] polyVerts) {
        if (polyVerts.length == 4) {
            polys.add(polyVerts[0]);
            polys.add(polyVerts[3]);
            polys.add(polyVerts[2]);
            polys.add(polyVerts[1]);
        } else {
            for (int i = 2; i < polyVerts.length; i++) {
                polys.add(polyVerts[0]);
                polys.add(polyVerts[i]);
                polys.add(polyVerts[i - 1]);
                polys.add(polyVerts[i - 1]);
            }
        }
    }

    private static Vector3 apply(@Nullable Transformation transformation, Vector3 vec3) {
        if (transformation != null) {
            transformation.apply(vec3);
        }
        return vec3;
    }

    private static Vector3 applyN(@Nullable Transformation transformation, Vector3 vec3) {
        if (transformation != null) {
            transformation.applyN(vec3);
        }
        return vec3;
    }

    private static double[] parseDoubles(String s, String token) {
        String[] as = s.split(token);
        double[] values = new double[as.length];
        for (int i = 0; i < as.length; i++) {
            values[i] = Double.parseDouble(as[i]);
        }
        return values;
    }

    private static ResourceLocation maybeRelative(ResourceLocation other, String resource) {
        if (resource.contains(":")) {
            return new ResourceLocation(resource);
        }
        String path = other.getPath();
        int lastSlash = path.lastIndexOf("/");
        if (lastSlash != -1) {
            path = path.substring(0, lastSlash);
        } else {
            path = "";
        }
        return new ResourceLocation(other.getNamespace(), path + "/" + resource);
    }
}
