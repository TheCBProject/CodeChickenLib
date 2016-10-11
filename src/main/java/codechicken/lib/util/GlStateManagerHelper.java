package codechicken.lib.util;

import com.google.common.base.Objects;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.*;

/**
 * Created by covers1624 on 10/10/2016.
 * Dumps specific GL states from GlStateManager
 * TODO Ability to auto compare between different saved states. So basically a GlLeak analyser.
 */
public class GlStateManagerHelper {

    public static enum State {
        GL_ALPHA_TEST {
            @Override
            public String getState() {
                AlphaState alphaState = GlStateManager.alphaState;
                return Objects.toStringHelper(name()).add("Func", alphaState.func).add("Ref", alphaState.ref).add("Enabled", String.valueOf(alphaState.alphaTest.currentState).toUpperCase()).toString();
            }
        },
        GL_LIGHTING {
            @Override
            public String getState() {
                return Objects.toStringHelper(name()).add("Enabled", parseBoolState(GlStateManager.lightingState)).toString();
            }
        },
        GL_BLEND {
            @Override
            public String getState() {
                BlendState blendState = GlStateManager.blendState;
                //@formatter:off
                return Objects.toStringHelper(name())
                        .add("SrcFactor", parseFactor(blendState.srcFactor))          .add("DstFactor", parseFactor(blendState.dstFactor))
                        .add("SrcFactorAlpha", parseFactor(blendState.srcFactorAlpha)).add("DstFactorAlpha", parseFactor(blendState.dstFactorAlpha))
                        .add("Enabled", parseBoolState(blendState.blend)).toString();
                //@formatter:on
            }

            private String parseFactor(int factor) {
                switch (factor) {
                    case 32771:
                        return "CONSTANT_ALPHA";
                    case 32769:
                        return "CONSTANT_COLOR";
                    case 772:
                        return "DST_ALPHA";
                    case 774:
                        return "DST_COLOR";
                    case 1:
                        return "ONE";
                    case 32772:
                        return "ONE_MINUS_CONSTANT_ALPHA";
                    case 32770:
                        return "ONE_MINUS_CONSTANT_COLOR";
                    case 773:
                        return "ONE_MINUS_DST_ALPHA";
                    case 775:
                        return "ONE_MINUS_DST_COLOR";
                    case 771:
                        return "ONE_MINUS_SRC_ALPHA";
                    case 769:
                        return "ONE_MINUS_SRC_COLOR";
                    case 770:
                        return "SRC_ALPHA";
                    case 776:
                        return "SRC_ALPHA_SATURATE";
                    case 768:
                        return "SRC_COLOR";
                    case 0:
                        return "ZERO";
                    default:
                        return "UNKNOWN:" + factor;

                }
            }

        },
        GL_DEPTH_TEST {
            @Override
            public String getState() {
                DepthState depthState = GlStateManager.depthState;
                return Objects.toStringHelper(name()).add("Func", parseFunc(depthState.depthFunc)).add("Mask", String.valueOf(depthState.maskEnabled).toUpperCase()).add("Enabled", parseBoolState(depthState.depthTest)).toString();
            }

            private String parseFunc(int func) {
                switch (func) {
                    case 0x200:
                        return "GL_NEVER";
                    case 0x201:
                        return "GL_LESS";
                    case 0x202:
                        return "GL_EQUAL";
                    case 0x203:
                        return "GL_LEQUAL";
                    case 0x204:
                        return "GL_GREATER";
                    case 0x205:
                        return "GL_NOTEQUAL";
                    case 0x206:
                        return "GL_GEQUAL";
                    case 0x207:
                        return "GL_ALWAYS";
                    default:
                        return "UNKNOWN:" + func;

                }
            }
        },
        GL_CULL_FACE {
            @Override
            public String getState() {
                CullState cullState = GlStateManager.cullState;
                return Objects.toStringHelper(name()).add("Mode", parseMode(cullState.mode)).add("Enabled", parseBoolState(cullState.cullFace)).toString();
            }

            private String parseMode(int mode) {
                switch (mode) {
                    case 1028:
                        return "FRONT";
                    case 1029:
                        return "BACK";
                    case 1032:
                        return "FRONT_AND_BACK";
                    default:
                        return "UNKNOWN:" + mode;

                }
            }

        },
        GL_RESCALE_NORMAL {
            @Override
            public String getState() {
                return Objects.toStringHelper(name()).add("Enabled", parseBoolState(GlStateManager.rescaleNormalState)).toString();
            }
        };

        public abstract String getState();
    }

    private static String parseBoolState(BooleanState boolState) {
        return String.valueOf(boolState.currentState).toUpperCase();
    }

    public static String dumpGLState(State... states) {
        StringBuilder builder = new StringBuilder();
        builder.append("GlStateManager { ");
        for (int i = 0; i < states.length; i++) {
            State state = states[i];
            builder.append(state.getState());
            if (i != states.length - 1) {
                builder.append(", ");
            }
        }

        builder.append(" }");
        return builder.toString();
    }

}
