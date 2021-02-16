package engine.render;

import org.lwjgl.BufferUtils;
import rutils.Logger;

import java.nio.*;
import java.util.Objects;

import static org.lwjgl.opengl.GL45.*;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public class GLBuffer
{
    private static final Logger LOGGER = new Logger();
    
    private final int id;
    private final GL  type;
    
    private GL usage = GL.STATIC_DRAW;
    
    private int bufferSize, dataSize;
    
    public GLBuffer(GL type)
    {
        this.id   = glGenBuffers();
        this.type = type;
        
        GLBuffer.LOGGER.fine("%s: Generated", this);
    }
    
    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GLBuffer that = (GLBuffer) o;
        return this.id == that.id;
    }
    
    @Override
    public int hashCode()
    {
        return Objects.hash(this.id);
    }
    
    @Override
    public String toString()
    {
        return "GLBuffer{" + "id=" + this.id + ", type=" + this.type + ", size=" + this.dataSize + "/" + this.bufferSize + '}';
    }
    
    /**
     * @return The buffer type.
     */
    public GL type()
    {
        return this.type;
    }
    
    /**
     * @return The buffer usage.
     */
    public GL usage()
    {
        return this.usage;
    }
    
    /**
     * Sets the buffer usage.
     *
     * @return This instance for call chaining.
     */
    public GLBuffer usage(GL usage)
    {
        GLBuffer.LOGGER.finer("%s: Setting Usage '%s'", this, usage);
        
        this.bufferSize = 0;
        
        this.usage = usage;
        
        return this;
    }
    
    /**
     * @return The size in bytes of the buffer.
     */
    public int bufferSize()
    {
        return this.bufferSize;
    }
    
    /**
     * @return The size in bytes of the data in the buffer buffer.
     */
    public int dataSize()
    {
        return this.dataSize;
    }
    
    /**
     * Binds the buffer for reading/writing.
     * <p>
     * Make sure to bind the buffer.
     *
     * @return This instance for call chaining.
     */
    public GLBuffer base(int index)
    {
        if (this.type != GL.ATOMIC_COUNTER_BUFFER && this.type != GL.TRANSFORM_FEEDBACK_BUFFER && this.type != GL.UNIFORM_BUFFER && this.type != GL.SHADER_STORAGE_BUFFER)
        {
            GLBuffer.LOGGER.warning("%s: Base is not supported for this buffer type '%s'", this, this.type);
            return this;
        }
        GLBuffer.LOGGER.fine("%s: Binding to Base: %s", this, index);
    
        glBindBufferBase(this.type.ref(), index, this.id);
    
        return this;
    }
    
    /**
     * Binds the buffer for reading/writing.
     *
     * @return This instance for call chaining.
     */
    public GLBuffer bind()
    {
        GLBuffer.LOGGER.finer("%s: Binding", this);
        
        glBindBuffer(this.type.ref(), this.id);
    
        return this;
    }
    
    /**
     * Unbinds the buffer from reading/writing.
     *
     * @return This instance for call chaining.
     */
    public GLBuffer unbind()
    {
        GLBuffer.LOGGER.finer("%s: Unbinding", this);
        
        glBindBuffer(this.type.ref(), 0);
    
        return this;
    }
    
    /**
     * Deletes the contents of the buffer and free's its memory.
     */
    public void delete()
    {
        GLBuffer.LOGGER.fine("%s: Deleting", this);
        
        this.bufferSize = 0;
        this.dataSize   = 0;
        
        glDeleteBuffers(this.id);
    }
    
    /**
     * Reallocates the Buffer at a specific size.
     * <p>
     * Make sure to bind the buffer first.
     *
     * @param size The new size in bytes
     * @return This instance for call chaining.
     */
    public GLBuffer set(int size)
    {
        this.bufferSize = this.dataSize = size;
    
        GLBuffer.LOGGER.finer("%s: Setting buffer size to %s", this, size);
    
        glBufferData(this.type.ref(), this.bufferSize, this.usage.ref());
    
        return this;
    }
    
    /**
     * Sets the contents of the buffer. If the data is larger than the buffer, then it will be resized.
     * <p>
     * Make sure to bind the buffer first.
     *
     * @param data The data.
     * @return This instance for call chaining.
     */
    public GLBuffer set(short... data)
    {
        int dataLength = data.length * Short.BYTES;
    
        if (dataLength > this.bufferSize)
        {
            this.bufferSize = this.dataSize = dataLength;
    
            GLBuffer.LOGGER.finer("%s: Setting from short array", this);
    
            glBufferData(this.type.ref(), data, this.usage.ref());
        }
        else
        {
            this.dataSize = dataLength;
    
            GLBuffer.LOGGER.finer("%s: Filling from short array", this);
    
            glBufferSubData(this.type.ref(), 0, data);
        }
        
        return this;
    }
    
    /**
     * Sets the contents of the buffer. If the data is larger than the buffer, then it will be resized.
     * <p>
     * Make sure to bind the buffer first.
     *
     * @param data The data.
     * @return This instance for call chaining.
     */
    public GLBuffer set(int... data)
    {
        int dataLength = data.length * Integer.BYTES;
    
        if (dataLength > this.bufferSize)
        {
            this.bufferSize = this.dataSize = dataLength;
    
            GLBuffer.LOGGER.finer("%s: Setting from int array", this);
    
            glBufferData(this.type.ref(), data, this.usage.ref());
        }
        else
        {
            this.dataSize = dataLength;
    
            GLBuffer.LOGGER.finer("%s: Filling from int array", this);
    
            glBufferSubData(this.type.ref(), 0, data);
        }
        
        return this;
    }
    
    /**
     * Sets the contents of the buffer. If the data is larger than the buffer, then it will be resized.
     * <p>
     * Make sure to bind the buffer first.
     *
     * @param data The data.
     * @return This instance for call chaining.
     */
    public GLBuffer set(long... data)
    {
        int dataLength = data.length * Long.BYTES;
    
        if (dataLength > this.bufferSize)
        {
            this.bufferSize = this.dataSize = dataLength;
    
            GLBuffer.LOGGER.finer("%s: Setting from long array", this);
    
            glBufferData(this.type.ref(), data, this.usage.ref());
        }
        else
        {
            this.dataSize = dataLength;
    
            GLBuffer.LOGGER.finer("%s: Filling from long array", this);
    
            glBufferSubData(this.type.ref(), 0, data);
        }
        
        return this;
    }
    
    /**
     * Sets the contents of the buffer. If the data is larger than the buffer, then it will be resized.
     * <p>
     * Make sure to bind the buffer first.
     *
     * @param data The data.
     * @return This instance for call chaining.
     */
    public GLBuffer set(float... data)
    {
        int dataLength = data.length * Float.BYTES;
    
        if (dataLength > this.bufferSize)
        {
            this.bufferSize = this.dataSize = dataLength;
    
            GLBuffer.LOGGER.finer("%s: Setting from float array", this);
    
            glBufferData(this.type.ref(), data, this.usage.ref());
        }
        else
        {
            this.dataSize = dataLength;
    
            GLBuffer.LOGGER.finer("%s: Filling from float array", this);
    
            glBufferSubData(this.type.ref(), 0, data);
        }
        
        return this;
    }
    
    /**
     * Sets the contents of the buffer. If the data is larger than the buffer, then it will be resized.
     * <p>
     * Make sure to bind the buffer first.
     *
     * @param data The data.
     * @return This instance for call chaining.
     */
    public GLBuffer set(double... data)
    {
        int dataLength = data.length * Double.BYTES;
    
        if (dataLength > this.bufferSize)
        {
            this.bufferSize = this.dataSize = dataLength;
    
            GLBuffer.LOGGER.finer("%s: Setting from double array", this);
    
            glBufferData(this.type.ref(), data, this.usage.ref());
        }
        else
        {
            this.dataSize = dataLength;
    
            GLBuffer.LOGGER.finer("%s: Filling from double array", this);
    
            glBufferSubData(this.type.ref(), 0, data);
        }
        
        return this;
    }
    
    /**
     * Sets the contents of the buffer. If the data is larger than the buffer, then it will be resized.
     * <p>
     * Make sure to bind the buffer first.
     *
     * @param data The data.
     * @return This instance for call chaining.
     */
    public GLBuffer set(ByteBuffer data)
    {
        int dataLength = data.remaining();
    
        if (dataLength > this.bufferSize)
        {
            this.bufferSize = this.dataSize = dataLength;
    
            GLBuffer.LOGGER.finer("%s: Setting from ByteBuffer", this);
    
            glBufferData(this.type.ref(), data, this.usage.ref());
        }
        else
        {
            this.dataSize = dataLength;
    
            GLBuffer.LOGGER.finer("%s: Filling from ByteBuffer", this);
    
            glBufferSubData(this.type.ref(), 0, data);
        }
        
        return this;
    }
    
    /**
     * Sets the contents of the buffer. If the data is larger than the buffer, then it will be resized.
     * <p>
     * Make sure to bind the buffer first.
     *
     * @param data The data.
     * @return This instance for call chaining.
     */
    public GLBuffer set(ShortBuffer data)
    {
        int dataLength = data.remaining() * Short.BYTES;
    
        if (dataLength > this.bufferSize)
        {
            this.bufferSize = this.dataSize = dataLength;
    
            GLBuffer.LOGGER.finer("%s: Setting from ShortBuffer", this);
    
            glBufferData(this.type.ref(), data, this.usage.ref());
        }
        else
        {
            this.dataSize = dataLength;
    
            GLBuffer.LOGGER.finer("%s: Filling from ShortBuffer", this);
    
            glBufferSubData(this.type.ref(), 0, data);
        }
        
        return this;
    }
    
    /**
     * Sets the contents of the buffer. If the data is larger than the buffer, then it will be resized.
     * <p>
     * Make sure to bind the buffer first.
     *
     * @param data The data.
     * @return This instance for call chaining.
     */
    public GLBuffer set(IntBuffer data)
    {
        int dataLength = data.remaining() * Integer.BYTES;
    
        if (dataLength > this.bufferSize)
        {
            this.bufferSize = this.dataSize = dataLength;
    
            GLBuffer.LOGGER.finer("%s: Setting from IntBuffer", this);
    
            glBufferData(this.type.ref(), data, this.usage.ref());
        }
        else
        {
            this.dataSize = dataLength;
    
            GLBuffer.LOGGER.finer("%s: Filling from IntBuffer", this);
    
            glBufferSubData(this.type.ref(), 0, data);
        }
        
        return this;
    }
    
    /**
     * Sets the contents of the buffer. If the data is larger than the buffer, then it will be resized.
     * <p>
     * Make sure to bind the buffer first.
     *
     * @param data The data.
     * @return This instance for call chaining.
     */
    public GLBuffer set(LongBuffer data)
    {
        int dataLength = data.remaining() * Long.BYTES;
    
        if (dataLength > this.bufferSize)
        {
            this.bufferSize = this.dataSize = dataLength;
    
            GLBuffer.LOGGER.finer("%s: Setting from LongBuffer", this);
    
            glBufferData(this.type.ref(), data, this.usage.ref());
        }
        else
        {
            this.dataSize = dataLength;
    
            GLBuffer.LOGGER.finer("%s: Filling from LongBuffer", this);
    
            glBufferSubData(this.type.ref(), 0, data);
        }
        
        return this;
    }
    
    /**
     * Sets the contents of the buffer. If the data is larger than the buffer, then it will be resized.
     * <p>
     * Make sure to bind the buffer first.
     *
     * @param data The data.
     * @return This instance for call chaining.
     */
    public GLBuffer set(FloatBuffer data)
    {
        int dataLength = data.remaining() * Float.BYTES;
    
        if (dataLength > this.bufferSize)
        {
            this.bufferSize = this.dataSize = dataLength;
    
            GLBuffer.LOGGER.finer("%s: Setting from FloatBuffer", this);
    
            glBufferData(this.type.ref(), data, this.usage.ref());
        }
        else
        {
            this.dataSize = dataLength;
    
            GLBuffer.LOGGER.finer("%s: Filling from FloatBuffer", this);
    
            glBufferSubData(this.type.ref(), 0, data);
        }
        
        return this;
    }
    
    /**
     * Sets the contents of the buffer. If the data is larger than the buffer, then it will be resized.
     * <p>
     * Make sure to bind the buffer first.
     *
     * @param data The data.
     * @return This instance for call chaining.
     */
    public GLBuffer set(DoubleBuffer data)
    {
        int dataLength = data.remaining() * Double.BYTES;
    
        if (dataLength > this.bufferSize)
        {
            this.bufferSize = this.dataSize = dataLength;
    
            GLBuffer.LOGGER.finer("%s: Setting from DoubleBuffer", this);
    
            glBufferData(this.type.ref(), data, this.usage.ref());
        }
        else
        {
            this.dataSize = dataLength;
    
            GLBuffer.LOGGER.finer("%s: Filling from DoubleBuffer", this);
    
            glBufferSubData(this.type.ref(), 0, data);
        }
    
        return this;
    }
    
    /**
     * Gets the data in the buffer.
     * <p>
     * Make sure to bind the buffer first.
     *
     * @return The data in the buffer.
     */
    public short[] getShort()
    {
        GLBuffer.LOGGER.finer("%s: Getting to short array", this);
    
        short[] shortData = new short[this.dataSize / Short.BYTES];
        glGetBufferSubData(this.type.ref(), 0, shortData);
        return shortData;
    }
    
    /**
     * Gets the data in the buffer.
     * <p>
     * Make sure to bind the buffer first.
     *
     * @return The data in the buffer.
     */
    public int[] getInt()
    {
        GLBuffer.LOGGER.finer("%s: Getting to int array", this);
    
        int[] intData = new int[this.dataSize / Integer.BYTES];
        glGetBufferSubData(this.type.ref(), 0, intData);
        return intData;
    }
    
    /**
     * Gets the data in the buffer.
     * <p>
     * Make sure to bind the buffer first.
     *
     * @return The data in the buffer.
     */
    public long[] getLong()
    {
        GLBuffer.LOGGER.finer("%s: Getting to long array", this);
    
        long[] longData = new long[this.dataSize / Long.BYTES];
        glGetBufferSubData(this.type.ref(), 0, longData);
        return longData;
    }
    
    /**
     * Gets the data in the buffer.
     * <p>
     * Make sure to bind the buffer first.
     *
     * @return The data in the buffer.
     */
    public float[] getFloat()
    {
        GLBuffer.LOGGER.finer("%s: Getting to float array", this);
    
        float[] floatData = new float[this.dataSize / Float.BYTES];
        glGetBufferSubData(this.type.ref(), 0, floatData);
        return floatData;
    }
    
    /**
     * Gets the data in the buffer.
     * <p>
     * Make sure to bind the buffer first.
     *
     * @return The data in the buffer.
     */
    public double[] getDouble()
    {
        GLBuffer.LOGGER.finer("%s: Getting to double array", this);
    
        double[] doubleData = new double[this.dataSize / Double.BYTES];
        glGetBufferSubData(this.type.ref(), 0, doubleData);
        return doubleData;
    }
    
    /**
     * Gets the data in the buffer.
     * <p>
     * Make sure to bind the buffer first.
     *
     * @param buffer The destination buffer.
     * @return The data in the buffer.
     */
    public ByteBuffer getByteBuffer(ByteBuffer buffer)
    {
        GLBuffer.LOGGER.finer("%s: Getting to ByteBuffer", this);
        
        assert buffer.remaining() >= this.bufferSize;
        glGetBufferSubData(this.type.ref(), 0, buffer);
        return buffer;
    }
    
    /**
     * Gets the data in the buffer.
     * <p>
     * Make sure to bind the buffer first.
     *
     * @param buffer The destination buffer.
     * @return The data in the buffer.
     */
    public ShortBuffer getShortBuffer(ShortBuffer buffer)
    {
        GLBuffer.LOGGER.finer("%s: Getting to ShortBuffer", this);
        
        assert buffer.remaining() >= this.bufferSize;
        glGetBufferSubData(this.type.ref(), 0, buffer);
        return buffer;
    }
    
    /**
     * Gets the data in the buffer.
     * <p>
     * Make sure to bind the buffer first.
     *
     * @param buffer The destination buffer.
     * @return The data in the buffer.
     */
    public IntBuffer getIntBuffer(IntBuffer buffer)
    {
        GLBuffer.LOGGER.finer("%s: Getting to IntBuffer", this);
        
        assert buffer.remaining() >= this.bufferSize;
        glGetBufferSubData(this.type.ref(), 0, buffer);
        return buffer;
    }
    
    /**
     * Gets the data in the buffer.
     * <p>
     * Make sure to bind the buffer first.
     *
     * @param buffer The destination buffer.
     * @return The data in the buffer.
     */
    public LongBuffer getLongBuffer(LongBuffer buffer)
    {
        GLBuffer.LOGGER.finer("%s: Getting to LongBuffer", this);
        
        assert buffer.remaining() >= this.bufferSize;
        glGetBufferSubData(this.type.ref(), 0, buffer);
        return buffer;
    }
    
    /**
     * Gets the data in the buffer.
     * <p>
     * Make sure to bind the buffer first.
     *
     * @param buffer The destination buffer.
     * @return The data in the buffer.
     */
    public FloatBuffer getFloatBuffer(FloatBuffer buffer)
    {
        GLBuffer.LOGGER.finer("%s: Getting to FloatBuffer", this);
        
        assert buffer.remaining() >= this.bufferSize;
        glGetBufferSubData(this.type.ref(), 0, buffer);
        return buffer;
    }
    
    /**
     * Gets the data in the buffer.
     * <p>
     * Make sure to bind the buffer first.
     *
     * @param buffer The destination buffer.
     * @return The data in the buffer.
     */
    public DoubleBuffer getDoubleBuffer(DoubleBuffer buffer)
    {
        GLBuffer.LOGGER.finer("%s: Getting to DoubleBuffer", this);
        
        assert buffer.remaining() >= this.bufferSize;
        glGetBufferSubData(this.type.ref(), 0, buffer);
        return buffer;
    }
    
    /**
     * Gets the data in the buffer.
     * <p>
     * Make sure to bind the buffer first.
     *
     * @return The data in the buffer.
     */
    public ByteBuffer getByteBuffer()
    {
        return getByteBuffer(BufferUtils.createByteBuffer(this.dataSize));
    }
    
    /**
     * Gets the data in the buffer.
     * <p>
     * Make sure to bind the buffer first.
     *
     * @return The data in the buffer.
     */
    public ShortBuffer getShortBuffer()
    {
        return getShortBuffer(BufferUtils.createByteBuffer(this.dataSize).asShortBuffer());
    }
    
    /**
     * Gets the data in the buffer.
     * <p>
     * Make sure to bind the buffer first.
     *
     * @return The data in the buffer.
     */
    public IntBuffer getIntBuffer()
    {
        return getIntBuffer(BufferUtils.createByteBuffer(this.dataSize).asIntBuffer());
    }
    
    /**
     * Gets the data in the buffer.
     * <p>
     * Make sure to bind the buffer first.
     *
     * @return The data in the buffer.
     */
    public LongBuffer getLongBuffer()
    {
        return getLongBuffer(BufferUtils.createByteBuffer(this.dataSize).asLongBuffer());
    }
    
    /**
     * Gets the data in the buffer.
     * <p>
     * Make sure to bind the buffer first.
     *
     * @return The data in the buffer.
     */
    public FloatBuffer getFloatBuffer()
    {
        return getFloatBuffer(BufferUtils.createByteBuffer(this.dataSize).asFloatBuffer());
    }
    
    /**
     * Gets the data in the buffer.
     * <p>
     * Make sure to bind the buffer first.
     *
     * @return The data in the buffer.
     */
    public DoubleBuffer getDoubleBuffer()
    {
        return getDoubleBuffer(BufferUtils.createByteBuffer(this.dataSize).asDoubleBuffer());
    }
}
