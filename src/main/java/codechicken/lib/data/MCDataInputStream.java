package codechicken.lib.data;

import io.netty.handler.codec.EncoderException;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * An {@link MCDataInput} implementation, reading
 * its data from either a {@link InputStream} or
 * {@link DataInput} stream.
 * <p>
 * Created by covers1624 on 4/16/20.
 */
public class MCDataInputStream implements MCDataInput {

    private final DataInput in;

    public MCDataInputStream(InputStream is) {
        this((DataInput) new DataInputStream(is));
    }

    public MCDataInputStream(DataInput in) {
        this.in = in;
    }

    @Override
    public byte readByte() {
        try {
            return in.readByte();
        } catch (IOException e) {
            throw new EncoderException(e);
        }
    }

    @Override
    public short readUByte() {
        try {
            return (short) in.readUnsignedByte();
        } catch (IOException e) {
            throw new EncoderException(e);
        }
    }

    @Override
    public char readChar() {
        try {
            return in.readChar();
        } catch (IOException e) {
            throw new EncoderException(e);
        }
    }

    @Override
    public short readShort() {
        try {
            return in.readShort();
        } catch (IOException e) {
            throw new EncoderException(e);
        }
    }

    @Override
    public int readUShort() {
        try {
            return in.readUnsignedShort();
        } catch (IOException e) {
            throw new EncoderException(e);
        }
    }

    @Override
    public int readInt() {
        try {
            return in.readInt();
        } catch (IOException e) {
            throw new EncoderException(e);
        }
    }

    @Override
    public long readLong() {
        try {
            return in.readLong();
        } catch (IOException e) {
            throw new EncoderException(e);
        }
    }

    @Override
    public float readFloat() {
        try {
            return in.readFloat();
        } catch (IOException e) {
            throw new EncoderException(e);
        }
    }

    @Override
    public double readDouble() {
        try {
            return in.readDouble();
        } catch (IOException e) {
            throw new EncoderException(e);
        }
    }

    @Override
    public boolean readBoolean() {
        try {
            return in.readBoolean();
        } catch (IOException e) {
            throw new EncoderException(e);
        }
    }
}
