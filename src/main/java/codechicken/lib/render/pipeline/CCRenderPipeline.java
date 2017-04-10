package codechicken.lib.render.pipeline;

import codechicken.lib.render.CCRenderState;

import java.util.ArrayList;
import java.util.Collections;

public class CCRenderPipeline {

    public class PipelineBuilder {

        CCRenderState renderState;

        public PipelineBuilder(CCRenderState renderState) {
            this.renderState = renderState;
        }

        public PipelineBuilder add(IVertexOperation op) {
            ops.add(op);
            return this;
        }

        public PipelineBuilder add(IVertexOperation... ops) {
            Collections.addAll(CCRenderPipeline.this.ops, ops);
            return this;
        }

        public void build() {
            rebuild();
        }

        public void render() {
            rebuild();
            renderState.render();
        }
    }

    private class PipelineNode {

        public ArrayList<PipelineNode> deps = new ArrayList<>();
        public IVertexOperation op;

        public void add() {
            if (op == null) {
                return;
            }

            for (PipelineNode dep : deps) {
                dep.add();
            }
            deps.clear();
            sorted.add(op);
            op = null;
        }
    }

    private CCRenderState renderState;

    //By default we want to force format attributes.
    public boolean forceFormatAttributes = true;

    private ArrayList<VertexAttribute> attribs = new ArrayList<>();
    private ArrayList<IVertexOperation> ops = new ArrayList<>();
    private ArrayList<PipelineNode> nodes = new ArrayList<>();
    private ArrayList<IVertexOperation> sorted = new ArrayList<>();
    private PipelineNode loading;
    private PipelineBuilder builder;

    public CCRenderPipeline(CCRenderState renderState) {
        this.renderState = renderState;
        builder = new PipelineBuilder(renderState);
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
        for (VertexAttribute attrib : attribs) {
            attrib.active = false;
        }
        attribs.clear();
        sorted.clear();
    }

    public void rebuild() {

        if (forceFormatAttributes) {
            if (renderState.fmt.hasNormal()) {
                addAttribute(renderState.normalAttrib);
            }
            if (renderState.fmt.hasColor()) {
                addAttribute(renderState.colourAttrib);
            }
            if (renderState.computeLighting) {
                addAttribute(renderState.lightingAttrib);
            }
        }

        if (ops.isEmpty() || renderState.model == null) {
            return;
        }

        //ensure enough nodes for all ops
        while (nodes.size() < CCRenderState.operationCount()) {
            nodes.add(new PipelineNode());
        }
        unbuild();

        //This needs to be an indexed loop as things are added to ops from VertexAttributes.
        //noinspection ForLoopReplaceableByForEach
        for (int i = 0; i < ops.size(); i++) {
            IVertexOperation op = ops.get(i);
            loading = nodes.get(op.operationID());
            boolean loaded = op.load(renderState);
            if (loaded) {
                loading.op = op;
            }

            if (op instanceof VertexAttribute) {
                if (loaded) {
                    attribs.add((VertexAttribute) op);
                } else {
                    ((VertexAttribute) op).active = false;
                }
            }
        }

        for (PipelineNode node : nodes) {
            node.add();
        }
    }

    public void addRequirement(int opRef) {
        loading.deps.add(nodes.get(opRef));
    }

    public void addDependency(VertexAttribute attrib) {
        loading.deps.add(nodes.get(attrib.operationID()));
        addAttribute(attrib);
    }

    public void addAttribute(VertexAttribute attrib) {
        if (!attrib.active) {
            ops.add(attrib);
            attrib.active = true;
        }
    }

    public void operate() {
        for (IVertexOperation aSorted : sorted) {
            aSorted.operate(renderState);
        }
    }

    public PipelineBuilder builder() {
        ops.clear();
        return builder;
    }
}
