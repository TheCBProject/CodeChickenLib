package codechicken.lib.vec;

public abstract class VariableTransformation extends Transformation {

    public final Matrix4 mat;

    public VariableTransformation(Matrix4 mat) {
        this.mat = mat;
    }

    @Override
    public void applyN(Vector3 normal) {
        apply(normal);
    }

    @Override
    public void apply(Matrix4 mat) {
        mat.multiply(this.mat);
    }

    @Override
    public Transformation copy() {
        return this;
    }
}
