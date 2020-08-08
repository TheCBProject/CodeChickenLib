package codechicken.lib.data;

import io.netty.handler.codec.EncoderException;

import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * An {@link MCDataOutput} implementation writing all
 * its data to either a {@link OutputStream} or
 * {@link DataOutput} stream.
 * <p>
 * Created by covers1624 on 4/15/20.
 */
public class MCDataOutputStream implements MCDataOutput {

    private final DataOutput out;

    public MCDataOutputStream(OutputStream out) {
        this((DataOutput) new DataOutputStream(out));
    }

    public MCDataOutputStream(DataOutput out) {
        this.out = out;
    }

    @Override
    public MCDataOutput writeByte(int b) {
        try {
            out.writeByte(b);
        } catch (IOException e) {
            throw new EncoderException(e);
        }
        return this;
    }

    @Override
    public MCDataOutput writeChar(int c) {
        try {
            out.writeChar(c);
        } catch (IOException e) {
            throw new EncoderException(e);
        }
        return this;
    }

    @Override
    public MCDataOutput writeShort(int s) {
        try {
            out.writeShort(s);
        } catch (IOException e) {
            throw new EncoderException(e);
        }
        return this;
    }

    @Override
    public MCDataOutput writeInt(int i) {
        try {
            out.writeInt(i);
        } catch (IOException e) {
            throw new EncoderException(e);
        }
        return this;
    }

    @Override
    public MCDataOutput writeLong(long l) {
        try {
            out.writeLong(l);
        } catch (IOException e) {
            throw new EncoderException(e);
        }
        return this;
    }

    @Override
    public MCDataOutput writeFloat(float f) {
        try {
            out.writeFloat(f);
        } catch (IOException e) {
            throw new EncoderException(e);
        }
        return this;
    }

    @Override
    public MCDataOutput writeDouble(double d) {
        try {
            out.writeDouble(d);
        } catch (IOException e) {
            throw new EncoderException(e);
        }
        return this;
    }

    @Override
    public MCDataOutput writeBoolean(boolean b) {
        try {
            out.writeBoolean(b);
        } catch (IOException e) {
            throw new EncoderException(e);
        }
        return this;
    }
}
