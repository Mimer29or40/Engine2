package engine.render;

import static org.lwjgl.opengl.GL15.glGetBufferSubData;
import static org.lwjgl.opengl.GL46.*;

/**
 * A float buffer that can send and retrieve data from the GPU
 */
@SuppressWarnings("unused")
public class GLFloatBuffer
{
    private final int id;
    private final int type;
    
    private float[] data;
    
    public GLFloatBuffer(int type)
    {
        this.id   = glGenBuffers();
        this.type = type;
    }
    
    /**
     * Sets the base for the buffer.
     *
     * @param index The index.
     * @return This instance for call chaining.
     */
    public GLFloatBuffer base(int index)
    {
        glBindBufferBase(this.type, index, this.id);
        return this;
    }
    
    /**
     * Binds the buffer.
     *
     * @return This instance for call chaining.
     */
    public GLFloatBuffer bind()
    {
        glBindBuffer(this.type, this.id);
        return this;
    }
    
    /**
     * Unbinds the buffer.
     *
     * @return This instance for call chaining.
     */
    public GLFloatBuffer unbind()
    {
        glBindBuffer(this.type, 0);
        return this;
    }
    
    /**
     * @return The data in the buffer.
     */
    public float[] get()
    {
        if (this.data != null) glGetBufferSubData(this.type, 0, this.data);
        return this.data;
    }
    
    /**
     * Sets the data in the buffer.
     *
     * @param data  The data.
     * @param usage How the data will be used.
     * @return This instance for call chaining.
     */
    public GLFloatBuffer set(float[] data, int usage)
    {
        this.data = data;
        glBufferData(this.type, data, usage);
        return this;
    }
    
    /**
     * Sets the data in the buffer.
     *
     * @param data The data.
     * @return This instance for call chaining.
     */
    public GLFloatBuffer set(float[] data)
    {
        return set(data, GL_STATIC_DRAW);
    }
}
