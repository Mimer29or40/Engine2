package engine.render;

import engine.util.Logger;

import java.nio.*;
import java.util.Objects;

import static org.lwjgl.opengl.GL43.*;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public class GLBuffer
{
    private static final Logger LOGGER = new Logger();
    
    private final int id, type;
    
    private int size;
    
    private short[]  shortData;
    private int[]    intData;
    private long[]   longData;
    private float[]  floatData;
    private double[] doubleData;
    
    public GLBuffer(int type)
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
        return "GLBuffer{" + "type=" + this.type + ", id=" + this.id + ", size=" + this.size + '}';
    }
    
    /**
     * @return The buffer type.
     */
    public int type()
    {
        return this.type;
    }
    
    /**
     * @return The size of the buffer.
     */
    public int size()
    {
        return this.size;
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
        
        if (this.type != GL_ATOMIC_COUNTER_BUFFER && this.type != GL_TRANSFORM_FEEDBACK_BUFFER && this.type != GL_UNIFORM_BUFFER && this.type != GL_SHADER_STORAGE_BUFFER)
        {
            GLBuffer.LOGGER.warning("base is not supported for this buffer");
            return this;
        }
        glBindBufferBase(this.type, index, this.id);
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
        
        glBindBuffer(this.type, this.id);
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
        
        glBindBuffer(this.type, 0);
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
        
        glDeleteBuffers(this.id);
        return this;
    }
    
    /**
     * Resizes the buffer.
     * <p>
     * Make sure to bind the buffer first.
     *
     * @param size  The new size
     * @param usage How the data will be used.
     * @return This instance for call chaining.
     */
    public GLBuffer resize(int size, int usage)
    {
        this.size = size;
        
        GLBuffer.LOGGER.finest("Resizing GLBuffer %s to %s", this.id, this.size);
        
        resizeArrays();
        glBufferData(this.type, this.size, usage);
        return this;
    }
    
    /**
     * Changes the contents of the buffer.
     * <p>
     * Make sure to bind the buffer first.
     *
     * @param data  The data.
     * @param usage How the data will be used.
     * @return This instance for call chaining.
     */
    public GLBuffer set(short[] data, int usage)
    {
        this.size = data.length;
        
        GLBuffer.LOGGER.finest("Setting GLBuffer %s with size %s and usage", this.id, this.size, usage);
        
        resizeArrays();
        glBufferData(this.type, data, usage);
        return this;
    }
    
    /**
     * Changes the contents of the buffer.
     * <p>
     * Make sure to bind the buffer first.
     *
     * @param data  The data.
     * @param usage How the data will be used.
     * @return This instance for call chaining.
     */
    public GLBuffer set(int[] data, int usage)
    {
        this.size = data.length;
        
        GLBuffer.LOGGER.finest("Setting GLBuffer %s with size %s and usage", this.id, this.size, usage);
        
        resizeArrays();
        glBufferData(this.type, data, usage);
        return this;
    }
    
    /**
     * Changes the contents of the buffer.
     * <p>
     * Make sure to bind the buffer first.
     *
     * @param data  The data.
     * @param usage How the data will be used.
     * @return This instance for call chaining.
     */
    public GLBuffer set(long[] data, int usage)
    {
        this.size = data.length;
        
        GLBuffer.LOGGER.finest("Setting GLBuffer %s with size %s and usage", this.id, this.size, usage);
        
        resizeArrays();
        glBufferData(this.type, data, usage);
        return this;
    }
    
    /**
     * Changes the contents of the buffer.
     * <p>
     * Make sure to bind the buffer first.
     *
     * @param data  The data.
     * @param usage How the data will be used.
     * @return This instance for call chaining.
     */
    public GLBuffer set(float[] data, int usage)
    {
        this.size = data.length;
        
        GLBuffer.LOGGER.finest("Setting GLBuffer %s with size %s and usage", this.id, this.size, usage);
        
        resizeArrays();
        glBufferData(this.type, data, usage);
        return this;
    }
    
    /**
     * Changes the contents of the buffer.
     * <p>
     * Make sure to bind the buffer first.
     *
     * @param data  The data.
     * @param usage How the data will be used.
     * @return This instance for call chaining.
     */
    public GLBuffer set(double[] data, int usage)
    {
        this.size = data.length;
        
        GLBuffer.LOGGER.finest("Setting GLBuffer %s with size %s and usage", this.id, this.size, usage);
        
        resizeArrays();
        glBufferData(this.type, data, usage);
        return this;
    }
    
    /**
     * Changes the contents of the buffer.
     * <p>
     * Make sure to bind the buffer first.
     *
     * @param data  The data.
     * @param usage How the data will be used.
     * @return This instance for call chaining.
     */
    public GLBuffer set(ByteBuffer data, int usage)
    {
        this.size = data.remaining();
        
        GLBuffer.LOGGER.finest("Setting GLBuffer %s with size %s and usage", this.id, this.size, usage);
        
        resizeArrays();
        glBufferData(this.type, data, usage);
        return this;
    }
    
    /**
     * Changes the contents of the buffer.
     * <p>
     * Make sure to bind the buffer first.
     *
     * @param data  The data.
     * @param usage How the data will be used.
     * @return This instance for call chaining.
     */
    public GLBuffer set(ShortBuffer data, int usage)
    {
        this.size = data.remaining();
        
        GLBuffer.LOGGER.finest("Setting GLBuffer %s with size %s and usage", this.id, this.size, usage);
        
        resizeArrays();
        glBufferData(this.type, data, usage);
        return this;
    }
    
    /**
     * Changes the contents of the buffer.
     * <p>
     * Make sure to bind the buffer first.
     *
     * @param data  The data.
     * @param usage How the data will be used.
     * @return This instance for call chaining.
     */
    public GLBuffer set(IntBuffer data, int usage)
    {
        this.size = data.remaining();
        
        GLBuffer.LOGGER.finest("Setting GLBuffer %s with size %s and usage", this.id, this.size, usage);
        
        resizeArrays();
        glBufferData(this.type, data, usage);
        return this;
    }
    
    /**
     * Changes the contents of the buffer.
     * <p>
     * Make sure to bind the buffer first.
     *
     * @param data  The data.
     * @param usage How the data will be used.
     * @return This instance for call chaining.
     */
    public GLBuffer set(LongBuffer data, int usage)
    {
        this.size = data.remaining();
        
        GLBuffer.LOGGER.finest("Setting GLBuffer %s with size %s and usage", this.id, this.size, usage);
        
        resizeArrays();
        glBufferData(this.type, data, usage);
        return this;
    }
    
    /**
     * Changes the contents of the buffer.
     * <p>
     * Make sure to bind the buffer first.
     *
     * @param data  The data.
     * @param usage How the data will be used.
     * @return This instance for call chaining.
     */
    public GLBuffer set(FloatBuffer data, int usage)
    {
        this.size = data.remaining();
        
        GLBuffer.LOGGER.finest("Setting GLBuffer %s with size %s and usage", this.id, this.size, usage);
        
        resizeArrays();
        glBufferData(this.type, data, usage);
        return this;
    }
    
    /**
     * Changes the contents of the buffer.
     * <p>
     * Make sure to bind the buffer first.
     *
     * @param data  The data.
     * @param usage How the data will be used.
     * @return This instance for call chaining.
     */
    public GLBuffer set(DoubleBuffer data, int usage)
    {
        this.size = data.remaining();
        
        GLBuffer.LOGGER.finest("Setting GLBuffer %s with size %s and usage", this.id, this.size, usage);
        
        resizeArrays();
        glBufferData(this.type, data, usage);
        return this;
    }
    
    // /**
    //  * Changes the contents of the buffer. The size of the data must match the buffer.
    //  * <p>
    //  * Make sure to bind the buffer first.
    //  *
    //  * @param data The data.
    //  * @return This instance for call chaining.
    //  */
    // public GLBuffer set(short[] data)
    // {
    //     GLBuffer.LOGGER.finest("Overwriting GLBuffer %s", this.id);
    //
    //     if (data.length != this.size) throw new RuntimeException("Data length does not match the buffer length");
    //     glBufferSubData(this.type, 0, data);
    //     return this;
    // }
    //
    // /**
    //  * Changes the contents of the buffer. The size of the data must match the buffer.
    //  * <p>
    //  * Make sure to bind the buffer first.
    //  *
    //  * @param data The data.
    //  * @return This instance for call chaining.
    //  */
    // public GLBuffer set(int[] data)
    // {
    //     GLBuffer.LOGGER.finest("Overwriting GLBuffer %s", this.id);
    //
    //     if (data.length != this.size) throw new RuntimeException("Data length does not match the buffer length");
    //     glBufferSubData(this.type, 0, data);
    //     return this;
    // }
    //
    // /**
    //  * Changes the contents of the buffer. The size of the data must match the buffer.
    //  * <p>
    //  * Make sure to bind the buffer first.
    //  *
    //  * @param data The data.
    //  * @return This instance for call chaining.
    //  */
    // public GLBuffer set(long[] data)
    // {
    //     GLBuffer.LOGGER.finest("Overwriting GLBuffer %s", this.id);
    //
    //     if (data.length != this.size) throw new RuntimeException("Data length does not match the buffer length");
    //     glBufferSubData(this.type, 0, data);
    //     return this;
    // }
    //
    // /**
    //  * Changes the contents of the buffer. The size of the data must match the buffer.
    //  * <p>
    //  * Make sure to bind the buffer first.
    //  *
    //  * @param data The data.
    //  * @return This instance for call chaining.
    //  */
    // public GLBuffer set(float[] data)
    // {
    //     GLBuffer.LOGGER.finest("Overwriting GLBuffer %s", this.id);
    //
    //     if (data.length != this.size) throw new RuntimeException("Data length does not match the buffer length");
    //     glBufferSubData(this.type, 0, data);
    //     return this;
    // }
    //
    // /**
    //  * Changes the contents of the buffer. The size of the data must match the buffer.
    //  * <p>
    //  * Make sure to bind the buffer first.
    //  *
    //  * @param data The data.
    //  * @return This instance for call chaining.
    //  */
    // public GLBuffer set(double[] data)
    // {
    //     GLBuffer.LOGGER.finest("Overwriting GLBuffer %s", this.id);
    //
    //     if (data.length != this.size) throw new RuntimeException("Data length does not match the buffer length");
    //     glBufferSubData(this.type, 0, data);
    //     return this;
    // }
    //
    // /**
    //  * Changes the contents of the buffer. The size of the data must match the buffer.
    //  * <p>
    //  * Make sure to bind the buffer first.
    //  *
    //  * @param data The data.
    //  * @return This instance for call chaining.
    //  */
    // public GLBuffer set(ByteBuffer data)
    // {
    //     GLBuffer.LOGGER.finest("Overwriting GLBuffer %s", this.id);
    //
    //     if (data.remaining() != this.size) throw new RuntimeException("Data length does not match the buffer length");
    //     glBufferSubData(this.type, 0, data);
    //     return this;
    // }
    //
    // /**
    //  * Changes the contents of the buffer. The size of the data must match the buffer.
    //  * <p>
    //  * Make sure to bind the buffer first.
    //  *
    //  * @param data The data.
    //  * @return This instance for call chaining.
    //  */
    // public GLBuffer set(ShortBuffer data)
    // {
    //     GLBuffer.LOGGER.finest("Overwriting GLBuffer %s", this.id);
    //
    //     if (data.remaining() != this.size) throw new RuntimeException("Data length does not match the buffer length");
    //     glBufferSubData(this.type, 0, data);
    //     return this;
    // }
    //
    // /**
    //  * Changes the contents of the buffer. The size of the data must match the buffer.
    //  * <p>
    //  * Make sure to bind the buffer first.
    //  *
    //  * @param data The data.
    //  * @return This instance for call chaining.
    //  */
    // public GLBuffer set(IntBuffer data)
    // {
    //     GLBuffer.LOGGER.finest("Overwriting GLBuffer %s", this.id);
    //
    //     if (data.remaining() != this.size) throw new RuntimeException("Data length does not match the buffer length");
    //     glBufferSubData(this.type, 0, data);
    //     return this;
    // }
    //
    // /**
    //  * Changes the contents of the buffer. The size of the data must match the buffer.
    //  * <p>
    //  * Make sure to bind the buffer first.
    //  *
    //  * @param data The data.
    //  * @return This instance for call chaining.
    //  */
    // public GLBuffer set(LongBuffer data)
    // {
    //     GLBuffer.LOGGER.finest("Overwriting GLBuffer %s", this.id);
    //
    //     if (data.remaining() != this.size) throw new RuntimeException("Data length does not match the buffer length");
    //     glBufferSubData(this.type, 0, data);
    //     return this;
    // }
    //
    // /**
    //  * Changes the contents of the buffer. The size of the data must match the buffer.
    //  * <p>
    //  * Make sure to bind the buffer first.
    //  *
    //  * @param data The data.
    //  * @return This instance for call chaining.
    //  */
    // public GLBuffer set(FloatBuffer data)
    // {
    //     GLBuffer.LOGGER.finest("Overwriting GLBuffer %s", this.id);
    //
    //     if (data.remaining() != this.size) throw new RuntimeException("Data length does not match the buffer length");
    //     glBufferSubData(this.type, 0, data);
    //     return this;
    // }
    //
    // /**
    //  * Changes the contents of the buffer. The size of the data must match the buffer.
    //  * <p>
    //  * Make sure to bind the buffer first.
    //  *
    //  * @param data The data.
    //  * @return This instance for call chaining.
    //  */
    // public GLBuffer set(DoubleBuffer data)
    // {
    //     GLBuffer.LOGGER.finest("Overwriting GLBuffer %s", this.id);
    //
    //     if (data.remaining() != this.size) throw new RuntimeException("Data length does not match the buffer length");
    //     glBufferSubData(this.type, 0, data);
    //     return this;
    // }
    
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
        
        if (this.shortData != null) glGetBufferSubData(this.type, 0, this.shortData);
        return this.shortData;
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
        
        if (this.intData != null) glGetBufferSubData(this.type, 0, this.intData);
        return this.intData;
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
        
        if (this.longData != null) glGetBufferSubData(this.type, 0, this.longData);
        return this.longData;
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
        
        if (this.floatData != null) glGetBufferSubData(this.type, 0, this.floatData);
        return this.floatData;
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
        
        if (this.doubleData != null) glGetBufferSubData(this.type, 0, this.doubleData);
        return this.doubleData;
    }
    
    private void resizeArrays()
    {
        if (this.shortData == null || this.shortData.length != this.size) this.shortData = new short[this.size];
        if (this.intData == null || this.intData.length != this.size) this.intData = new int[this.size];
        if (this.longData == null || this.longData.length != this.size) this.longData = new long[this.size];
        if (this.floatData == null || this.floatData.length != this.size) this.floatData = new float[this.size];
        if (this.doubleData == null || this.doubleData.length != this.size) this.doubleData = new double[this.size];
    }
}
