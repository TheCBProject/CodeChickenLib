package codechicken.lib.vec;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class TransformationList extends Transformation {

    private ArrayList<Transformation> transformations = new ArrayList<>();
    private Matrix4 mat;

    public TransformationList(Transformation... transforms) {
        this(Arrays.asList(transforms));
    }

    public TransformationList(List<Transformation> transforms) {
        for (Transformation t : transforms) {
            if (t instanceof TransformationList) {
                transformations.addAll(((TransformationList) t).transformations);
            } else {
                transformations.add(t);
            }
        }

        compact();
    }

    public Matrix4 compile() {
        if (mat == null) {
            mat = new Matrix4();
            for (int i = transformations.size() - 1; i >= 0; i--) {
                transformations.get(i).apply(mat);
            }
        }
        return mat;
    }

    /**
     * Returns a global space matrix as opposed to an object space matrix (reverse application order)
     *
     * @return The matrix.
     */
    public Matrix4 reverseCompile() {
        Matrix4 mat = new Matrix4();
        for (Transformation t : transformations) {
            t.apply(mat);
        }
        return mat;
    }

    @Override
    public void apply(Vector3 vec) {
        if (mat != null) {
            mat.apply(vec);
        } else {
            for (Transformation transformation : transformations) {
                transformation.apply(vec);
            }
        }
    }

    @Override
    public void applyN(Vector3 normal) {
        if (mat != null) {
            mat.applyN(normal);
        } else {
            for (Transformation transformation : transformations) {
                transformation.applyN(normal);
            }
        }
    }

    @Override
    public void apply(Matrix4 mat) {
        mat.multiply(compile());
    }

    @Override
    public TransformationList with(Transformation t) {
        if (t.isRedundant()) {
            return this;
        }

        mat = null;//matrix invalid
        if (t instanceof TransformationList) {
            transformations.addAll(((TransformationList) t).transformations);
        } else {
            transformations.add(t);
        }

        compact();
        return this;
    }

    public TransformationList prepend(Transformation t) {
        if (t.isRedundant()) {
            return this;
        }

        mat = null;//matrix invalid
        if (t instanceof TransformationList) {
            transformations.addAll(0, ((TransformationList) t).transformations);
        } else {
            transformations.add(0, t);
        }

        compact();
        return this;
    }

    private void compact() {
        ArrayList<Transformation> newList = new ArrayList<>(transformations.size());
        Iterator<Transformation> iterator = transformations.iterator();
        Transformation prev = null;
        while (iterator.hasNext()) {
            Transformation t = iterator.next();
            if (t.isRedundant()) {
                continue;
            }

            if (prev != null) {
                Transformation m = prev.merge(t);
                if (m == null) {
                    newList.add(prev);
                } else if (m.isRedundant()) {
                    t = null;
                } else {
                    t = m;
                }
            }
            prev = t;
        }
        if (prev != null) {
            newList.add(prev);
        }

        if (newList.size() < transformations.size()) {
            transformations = newList;
            mat = null;
        }

        if (transformations.size() > 3 && mat == null) {
            compile();
        }
    }

    @Override
    public boolean isRedundant() {
        return transformations.size() == 0;
    }

    @Override
    @OnlyIn (Dist.CLIENT)
    public void glApply() {
        for (int i = transformations.size() - 1; i >= 0; i--) {
            transformations.get(i).glApply();
        }
    }

    @Override
    public Transformation inverse() {
        TransformationList rev = new TransformationList();
        for (int i = transformations.size() - 1; i >= 0; i--) {
            rev.with(transformations.get(i).inverse());
        }
        return rev;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        for (Transformation t : transformations) {
            s.append("\n").append(t.toString());
        }
        return s.toString().trim();
    }
}
