package codechicken.lib.data;

import io.netty.handler.codec.EncoderException;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by covers1624 on 21/11/2017.
 */
public class MCDataInputWrapper implements MCDataInput {

    private DataInput input;

    public MCDataInputWrapper(DataInput input) {
        this.input = input;
    }

    public MCDataInputWrapper(InputStream input) {
        this.input = new DataInputStream(input);
    }

    @Override
    public long readLong() {
        try {
            return input.readLong();
        } catch (IOException e) {
            throw new EncoderException(e);
        }
    }

    @Override
    public int readInt() {
        try {
            return input.readInt();
        } catch (IOException e) {
            throw new EncoderException(e);
        }
    }

    @Override
    public short readShort() {
        try {
            return input.readShort();
        } catch (IOException e) {
            throw new EncoderException(e);
        }
    }

    @Override
    public int readUShort() {
        try {
            return input.readUnsignedShort();
        } catch (IOException e) {
            throw new EncoderException(e);
        }
    }

    @Override
    public byte readByte() {
        try {
            return input.readByte();
        } catch (IOException e) {
            throw new EncoderException(e);
        }
    }

    @Override
    public short readUByte() {
        try {
            return (short) input.readUnsignedByte();
        } catch (IOException e) {
            throw new EncoderException(e);
        }
    }

    @Override
    public double readDouble() {
        try {
            return input.readDouble();
        } catch (IOException e) {
            throw new EncoderException(e);
        }
    }

    @Override
    public float readFloat() {
        try {
            return input.readFloat();
        } catch (IOException e) {
            throw new EncoderException(e);
        }
    }

    @Override
    public boolean readBoolean() {
        try {
            return input.readBoolean();
        } catch (IOException e) {
            throw new EncoderException(e);
        }
    }

    @Override
    public char readChar() {
        try {
            return input.readChar();
        } catch (IOException e) {
            throw new EncoderException(e);
        }
    }

    @Override
    public byte[] readArray(int length) {
        try {
            byte[] bytes = new byte[length];
            input.readFully(bytes);
            return bytes;
        } catch (IOException e) {
            throw new EncoderException(e);
        }
    }
}
