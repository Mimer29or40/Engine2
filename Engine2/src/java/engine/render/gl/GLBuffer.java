package engine.render.gl;

import engine.util.Logger;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL15;

import java.nio.*;
import java.util.Objects;

import static org.lwjgl.opengl.GL45.*;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public class GLBuffer
{
    private static final Logger LOGGER = new Logger();
    
    private final int     id;
    private final GLConst type;
    
    private GLConst usage = GLConst.STATIC_DRAW;
    
    private int bufferSize, dataSize;
    
    public GLBuffer(GLConst type)
    {
        this.id   = glGenBuffers();
        this.type = type;
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
        return "GLBuffer{" + "type=" + this.type + ", id=" + this.id + ", size=" + this.bufferSize + '}';
    }
    
    /**
     * @return The buffer type.
     */
    public GLConst type()
    {
        return this.type;
    }
    
    /**
     * @return The buffer usage.
     */
    public GLConst usage()
    {
        return this.usage;
    }
    
    /**
     * Sets the buffer usage.
     *
     * @return This instance for call chaining.
     */
    public GLBuffer usage(GLConst usage)
    {
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
        GLBuffer.LOGGER.finest("GLBuffer: %s Binding to Base: %s", this.id, index);
    
        if (this.type != GLConst.ATOMIC_COUNTER_BUFFER && this.type != GLConst.TRANSFORM_FEEDBACK_BUFFER && this.type != GLConst.UNIFORM_BUFFER && this.type != GLConst.SHADER_STORAGE_BUFFER)
        {
            GLBuffer.LOGGER.warning("base is not supported for this buffer");
            return this;
        }
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
        GLBuffer.LOGGER.finest("Binding GLBuffer (%s) as %s", this.id, this.type);
        
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
        GLBuffer.LOGGER.finest("Unbinding GLBuffer (%s) as %s", this.id, this.type);
        
        glBindBuffer(this.type.ref(), 0);
        return this;
    }
    
    /**
     * Deletes the contents of the buffer and free's its memory.
     *
     * @return This instance for call chaining.
     */
    public GLBuffer delete()
    {
        GLBuffer.LOGGER.finest("Deleting GLBuffer %s", this.id);
    
        this.bufferSize = 0;
    
        glDeleteBuffers(this.id);
        return this;
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
    
        GLBuffer.LOGGER.finest("Resizing GLBuffer %s to %s", this.id, this.bufferSize);
    
        GL15.glBufferData(this.type.ref(), this.bufferSize, this.usage.ref());
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
        
            GLBuffer.LOGGER.finest("Setting GLBuffer %s with size %s and usage %s", this.id, this.bufferSize, this.usage);
    
            GL15.glBufferData(this.type.ref(), data, this.usage.ref());
        }
        else
        {
            this.dataSize = dataLength;
        
            GLBuffer.LOGGER.finest("Overwriting GLBuffer %s", this.id);
    
            GL15.glBufferSubData(this.type.ref(), 0, data);
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
        
            GLBuffer.LOGGER.finest("Setting GLBuffer %s with size %s and usage %s", this.id, this.bufferSize, this.usage);
    
            GL15.glBufferData(this.type.ref(), data, this.usage.ref());
        }
        else
        {
            this.dataSize = dataLength;
        
            GLBuffer.LOGGER.finest("Overwriting GLBuffer %s", this.id);
    
            GL15.glBufferSubData(this.type.ref(), 0, data);
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
        
            GLBuffer.LOGGER.finest("Setting GLBuffer %s with size %s and usage %s", this.id, this.bufferSize, this.usage);
    
            GL15.glBufferData(this.type.ref(), data, this.usage.ref());
        }
        else
        {
            this.dataSize = dataLength;
        
            GLBuffer.LOGGER.finest("Overwriting GLBuffer %s", this.id);
    
            GL15.glBufferSubData(this.type.ref(), 0, data);
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
        
            GLBuffer.LOGGER.finest("Setting GLBuffer %s with size %s and usage %s", this.id, this.bufferSize, this.usage);
    
            GL15.glBufferData(this.type.ref(), data, this.usage.ref());
        }
        else
        {
            this.dataSize = dataLength;
        
            GLBuffer.LOGGER.finest("Overwriting GLBuffer %s", this.id);
    
            GL15.glBufferSubData(this.type.ref(), 0, data);
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
        
            GLBuffer.LOGGER.finest("Setting GLBuffer %s with size %s and usage %s", this.id, this.bufferSize, this.usage);
    
            GL15.glBufferData(this.type.ref(), data, this.usage.ref());
        }
        else
        {
            this.dataSize = dataLength;
        
            GLBuffer.LOGGER.finest("Overwriting GLBuffer %s", this.id);
    
            GL15.glBufferSubData(this.type.ref(), 0, data);
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
        
            GLBuffer.LOGGER.finest("Setting GLBuffer %s with size %s and usage %s", this.id, this.bufferSize, this.usage);
    
            GL15.glBufferData(this.type.ref(), data, this.usage.ref());
        }
        else
        {
            this.dataSize = dataLength;
        
            GLBuffer.LOGGER.finest("Overwriting GLBuffer %s", this.id);
    
            GL15.glBufferSubData(this.type.ref(), 0, data);
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
        
            GLBuffer.LOGGER.finest("Setting GLBuffer %s with size %s and usage %s", this.id, this.bufferSize, this.usage);
    
            GL15.glBufferData(this.type.ref(), data, this.usage.ref());
        }
        else
        {
            this.dataSize = dataLength;
        
            GLBuffer.LOGGER.finest("Overwriting GLBuffer %s", this.id);
    
            GL15.glBufferSubData(this.type.ref(), 0, data);
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
        
            GLBuffer.LOGGER.finest("Setting GLBuffer %s with size %s and usage %s", this.id, this.bufferSize, this.usage);
    
            GL15.glBufferData(this.type.ref(), data, this.usage.ref());
        }
        else
        {
            this.dataSize = dataLength;
        
            GLBuffer.LOGGER.finest("Overwriting GLBuffer %s", this.id);
    
            GL15.glBufferSubData(this.type.ref(), 0, data);
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
        
            GLBuffer.LOGGER.finest("Setting GLBuffer %s with size %s and usage %s", this.id, this.bufferSize, this.usage);
    
            GL15.glBufferData(this.type.ref(), data, this.usage.ref());
        }
        else
        {
            this.dataSize = dataLength;
        
            GLBuffer.LOGGER.finest("Overwriting GLBuffer %s", this.id);
    
            GL15.glBufferSubData(this.type.ref(), 0, data);
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
        
            GLBuffer.LOGGER.finest("Setting GLBuffer %s with size %s and usage %s", this.id, this.bufferSize, this.usage);
    
            GL15.glBufferData(this.type.ref(), data, this.usage.ref());
        }
        else
        {
            this.dataSize = dataLength;
        
            GLBuffer.LOGGER.finest("Overwriting GLBuffer %s", this.id);
    
            GL15.glBufferSubData(this.type.ref(), 0, data);
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
        
            GLBuffer.LOGGER.finest("Setting GLBuffer %s with size %s and usage %s", this.id, this.bufferSize, this.usage);
    
            GL15.glBufferData(this.type.ref(), data, this.usage.ref());
        }
        else
        {
            this.dataSize = dataLength;
        
            GLBuffer.LOGGER.finest("Overwriting GLBuffer %s", this.id);
    
            GL15.glBufferSubData(this.type.ref(), 0, data);
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
        GLBuffer.LOGGER.finest("Getting GLBuffer %s contents", this.id);
    
        short[] shortData = new short[this.bufferSize / Short.BYTES];
        GL15.glGetBufferSubData(this.type.ref(), 0, shortData);
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
        GLBuffer.LOGGER.finest("Getting GLBuffer %s contents", this.id);
    
        int[] intData = new int[this.bufferSize / Integer.BYTES];
        GL15.glGetBufferSubData(this.type.ref(), 0, intData);
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
        GLBuffer.LOGGER.finest("Getting GLBuffer %s contents", this.id);
    
        long[] longData = new long[this.bufferSize / Long.BYTES];
        GL15.glGetBufferSubData(this.type.ref(), 0, longData);
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
        GLBuffer.LOGGER.finest("Getting GLBuffer %s contents", this.id);
    
        float[] floatData = new float[this.bufferSize / Float.BYTES];
        GL15.glGetBufferSubData(this.type.ref(), 0, floatData);
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
        GLBuffer.LOGGER.finest("Getting GLBuffer %s contents", this.id);
    
        double[] doubleData = new double[this.bufferSize / Double.BYTES];
        GL15.glGetBufferSubData(this.type.ref(), 0, doubleData);
        return doubleData;
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
        GLBuffer.LOGGER.finest("Getting GLBuffer %s contents", this.id);
    
        ByteBuffer buffer = BufferUtils.createByteBuffer(this.bufferSize);
        GL15.glGetBufferSubData(this.type.ref(), 0, buffer);
        return buffer;
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
        GLBuffer.LOGGER.finest("Getting GLBuffer %s contents", this.id);
    
        ShortBuffer buffer = BufferUtils.createShortBuffer(this.bufferSize / Short.BYTES);
        GL15.glGetBufferSubData(this.type.ref(), 0, buffer);
        return buffer;
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
        GLBuffer.LOGGER.finest("Getting GLBuffer %s contents", this.id);
    
        IntBuffer buffer = BufferUtils.createIntBuffer(this.bufferSize / Integer.BYTES);
        GL15.glGetBufferSubData(this.type.ref(), 0, buffer);
        return buffer;
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
        GLBuffer.LOGGER.finest("Getting GLBuffer %s contents", this.id);
    
        LongBuffer buffer = BufferUtils.createLongBuffer(this.bufferSize / Long.BYTES);
        GL15.glGetBufferSubData(this.type.ref(), 0, buffer);
        return buffer;
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
        GLBuffer.LOGGER.finest("Getting GLBuffer %s contents", this.id);
    
        FloatBuffer buffer = BufferUtils.createFloatBuffer(this.bufferSize / Float.BYTES);
        GL15.glGetBufferSubData(this.type.ref(), 0, buffer);
        return buffer;
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
        GLBuffer.LOGGER.finest("Getting GLBuffer %s contents", this.id);
    
        DoubleBuffer buffer = BufferUtils.createDoubleBuffer(this.bufferSize / Double.BYTES);
        GL15.glGetBufferSubData(this.type.ref(), 0, buffer);
        return buffer;
    }
}
