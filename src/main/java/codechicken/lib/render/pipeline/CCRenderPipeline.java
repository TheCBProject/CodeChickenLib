package codechicken.lib.render.pipeline;

import codechicken.lib.render.CCRenderState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings ("ForLoopReplaceableByForEach")
public class CCRenderPipeline {

    private final CCRenderState renderState;

    @Deprecated//Hack removed.
    public boolean forceFormatAttributes = true;

    private final List<VertexAttribute<?>> attribs = new ArrayList<>();
    private final List<IVertexOperation> ops = new ArrayList<>();
    private final List<PipelineNode> nodes = new ArrayList<>();
    private final List<IVertexOperation> sorted = new ArrayList<>();
    private @Nullable PipelineNode loading;

    public CCRenderPipeline(CCRenderState renderState) {
        this.renderState = renderState;
    }

    public void setPipeline(IVertexOperation... ops) {
        this.ops.clear();
        Collections.addAll(this.ops, ops);
        rebuild();
    }

    public void reset() {
        ops.clear();
        unbuild();
    }

    private void unbuild() {
        for (int i = 0; i < attribs.size(); i++) {
            VertexAttribute<?> attrib = attribs.get(i);
            attrib.active = false;
        }
        attribs.clear();
        sorted.clear();
    }

    public void rebuild() {
        if (renderState.model == null || renderState.fmt == null) return;

        unbuild();

        assert renderState.cFmt != null;
        if (renderState.cFmt.hasNormal) {
            addAttribute(renderState.normalAttrib);
        }
        if (renderState.cFmt.hasColor) {
            addAttribute(renderState.colourAttrib);
        }
        if (renderState.computeLighting) {
            addAttribute(renderState.lightingAttrib);
        }

        if (ops.isEmpty()) {
            return;
        }

        //ensure enough nodes for all ops
        while (nodes.size() < IVertexOperation.operationCount()) {
            nodes.add(new PipelineNode());
        }

        // addDependency adds things to this.
        //noinspection ForLoopReplaceableByForEach
        for (int i = 0; i < ops.size(); i++) {
            IVertexOperation op = ops.get(i);
            loading = nodes.get(op.operationID());
            assert loading != null;
            boolean loaded = op.load(renderState);
            if (loaded) {
                loading.op = op;
            }

            if (op instanceof VertexAttribute) {
                if (loaded) {
                    attribs.add((VertexAttribute<?>) op);
                } else {
                    ((VertexAttribute<?>) op).active = false;
                }
            }
        }

        for (int i = 0; i < nodes.size(); i++) {
            PipelineNode node = nodes.get(i);
            node.add();
        }
    }

    public void addRequirement(int opRef) {
        assert loading != null;
        loading.deps.add(nodes.get(opRef));
    }

    public void addDependency(VertexAttribute<?> attrib) {
        assert loading != null;
        loading.deps.add(nodes.get(attrib.operationID()));
        addAttribute(attrib);
    }

    public void addAttribute(VertexAttribute<?> attrib) {
        if (!attrib.active) {
            ops.add(attrib);
            attrib.active = true;
        }
    }

    public void operate() {
        for (int i = 0; i < sorted.size(); i++) {
            IVertexOperation aSorted = sorted.get(i);
            aSorted.operate(renderState);
        }
    }

    private class PipelineNode {

        public ArrayList<PipelineNode> deps = new ArrayList<>();
        public @Nullable IVertexOperation op;

        public void add() {
            if (op == null) {
                return;
            }

            for (int i = 0; i < deps.size(); i++) {
                PipelineNode dep = deps.get(i);
                dep.add();
            }
            deps.clear();
            sorted.add(op);
            op = null;
        }
    }
}
