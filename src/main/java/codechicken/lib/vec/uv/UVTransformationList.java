package codechicken.lib.vec.uv;

import codechicken.lib.util.Copyable;
import net.covers1624.quack.collection.FastStream;
import net.covers1624.quack.collection.StreamableIterable;

import java.util.ArrayList;

public class UVTransformationList extends UVTransformation {

    private ArrayList<UVTransformation> transformations = new ArrayList<>();

    public UVTransformationList(UVTransformation... transforms) {
        for (UVTransformation t : transforms) {
            if (t instanceof UVTransformationList) {
                transformations.addAll(((UVTransformationList) t).transformations);
            } else {
                transformations.add(t);
            }
        }

        compact();
    }

    public UVTransformationList(UVTransformationList other) {
        transformations = FastStream.of(other.transformations)
                .map(Copyable::copy)
                .toList();

        compact();
    }

    @Override
    public void apply(UV uv) {
        for (UVTransformation transformation : transformations) {
            transformation.apply(uv);
        }
    }

    @Override
    public UVTransformationList with(UVTransformation t) {
        if (t.isRedundant()) {
            return this;
        }

        if (t instanceof UVTransformationList) {
            transformations.addAll(((UVTransformationList) t).transformations);
        } else {
            transformations.add(t);
        }

        compact();
        return this;
    }

    public UVTransformationList prepend(UVTransformation t) {
        if (t.isRedundant()) {
            return this;
        }

        if (t instanceof UVTransformationList) {
            transformations.addAll(0, ((UVTransformationList) t).transformations);
        } else {
            transformations.add(0, t);
        }

        compact();
        return this;
    }

    private void compact() {
        ArrayList<UVTransformation> newList = new ArrayList<>(transformations.size());
        UVTransformation prev = null;
        for (UVTransformation t : transformations) {
            if (t.isRedundant()) {
                continue;
            }

            if (prev != null) {
                UVTransformation m = prev.merge(t);
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
        }
    }

    @Override
    public boolean isRedundant() {
        return transformations.isEmpty();
    }

    @Override
    public UVTransformation inverse() {
        UVTransformationList rev = new UVTransformationList();
        for (int i = transformations.size() - 1; i >= 0; i--) {
            rev.with(transformations.get(i).inverse());
        }
        return rev;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        for (UVTransformation t : transformations) {
            s.append("\n").append(t.toString());
        }
        return s.toString().trim();
    }

    @Override
    public UVTransformation copy() {
        return new UVTransformationList(this);
    }
}
