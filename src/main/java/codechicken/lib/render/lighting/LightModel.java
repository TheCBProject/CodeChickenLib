package codechicken.lib.render.lighting;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Rotation;
import codechicken.lib.vec.Vector3;

public class LightModel implements IVertexOperation {

    public static final int operationIndex = IVertexOperation.registerOperation();

    public static LightModel standardLightModel;

    static {
        //@formatter:off
        standardLightModel = new LightModel()
		        .setAmbient(new Vector3(0.4, 0.4, 0.4))
		        .addLight(new Light(new Vector3(0.2, 1, -0.7))
				        .setDiffuse(new Vector3(0.6, 0.6, 0.6)))
		        .addLight(new Light(new Vector3(-0.2, 1, 0.7))
				        .setDiffuse(new Vector3(0.6, 0.6, 0.6)));
        //@formatter:on
    }

    private final Vector3 ambient = new Vector3();
    private final Light[] lights = new Light[8];
    private int lightCount;

    public LightModel addLight(Light light) {
        lights[lightCount++] = light;
        return this;
    }

    public LightModel setAmbient(Vector3 vec) {
        ambient.set(vec);
        return this;
    }

    /**
     * @param colour The pre-lighting vertex colour. RGBA format
     * @param normal The normal at the vertex
     * @return The lighting applied colour
     */
    public int apply(int colour, Vector3 normal) {
        Vector3 n_colour = ambient.copy();
        for (int l = 0; l < lightCount; l++) {
            Light light = lights[l];
            double n_l = light.position.dotProduct(normal);
            double f = n_l > 0 ? 1 : 0;
            n_colour.x += light.ambient.x + f * light.diffuse.x * n_l;
            n_colour.y += light.ambient.y + f * light.diffuse.y * n_l;
            n_colour.z += light.ambient.z + f * light.diffuse.z * n_l;
        }

        if (n_colour.x > 1) {
            n_colour.x = 1;
        }
        if (n_colour.y > 1) {
            n_colour.y = 1;
        }
        if (n_colour.z > 1) {
            n_colour.z = 1;
        }

        n_colour.multiply((colour >>> 24) / 255D, (colour >> 16 & 0xFF) / 255D, (colour >> 8 & 0xFF) / 255D);
        return (int) (n_colour.x * 255) << 24 | (int) (n_colour.y * 255) << 16 | (int) (n_colour.z * 255) << 8 | colour & 0xFF;
    }

    @Override
    public boolean load(CCRenderState ccrs) {
        if (!ccrs.computeLighting) {
            return false;
        }

        ccrs.pipeline.addDependency(ccrs.normalAttrib);
        ccrs.pipeline.addDependency(ccrs.colourAttrib);
        return true;
    }

    @Override
    public void operate(CCRenderState ccrs) {
        ccrs.colour = apply(ccrs.colour, ccrs.normal);
    }

    @Override
    public int operationID() {
        return operationIndex;
    }

    public PlanarLightModel reducePlanar() {
        int[] colours = new int[6];
        for (int i = 0; i < 6; i++) {
            colours[i] = apply(-1, Rotation.axes[i]);
        }
        return new PlanarLightModel(colours);
    }

    public static class Light {

        public Vector3 ambient = new Vector3();
        public Vector3 diffuse = new Vector3();
        public Vector3 position;

        public Light(Vector3 pos) {
            position = pos.copy().normalize();
        }

        public Light setDiffuse(Vector3 vec) {
            diffuse.set(vec);
            return this;
        }

        public Light setAmbient(Vector3 vec) {
            ambient.set(vec);
            return this;
        }
    }
}
