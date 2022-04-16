package codechicken.lib.render.pipeline;

import codechicken.lib.render.CCRenderState;

/**
 * Represents an operation to be run for each vertex that operates on and modifies the current state
 */
public interface IVertexOperation {

    static int registerOperation() {
        return VertexOperationRegistry.nextOperationIndex++;
    }

    static int operationCount() {
        return VertexOperationRegistry.nextOperationIndex;
    }

    /**
     * Load any required references and add dependencies to the pipeline based on the current model (may be null)
     * Return false if this operation is redundant in the pipeline with the given model
     */
    boolean load(CCRenderState ccrs);

    /**
     * Perform the operation on the current render state
     */
    void operate(CCRenderState ccrs);

    /**
     * Get the unique id representing this type of operation. Duplicate operation IDs within the pipeline may have unexpected results.
     * ID should be obtained from CCRenderState.registerOperation() and stored in a static variable
     */
    int operationID();

    class VertexOperationRegistry {

        static int nextOperationIndex;
    }
}
