package engine.render;

import static org.lwjgl.opengl.GL46.*;

/**
 * A int buffer that can send and retrieve data from the GPU
 */
@SuppressWarnings("unused")
public class GLIntBuffer
{
    private final int id;
    private final int type;
    
    private int[] data;
    
    public GLIntBuffer(int type)
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
    public GLIntBuffer base(int index)
    {
        glBindBufferBase(this.type, index, this.id);
        return this;
    }
    
    /**
     * Binds the buffer.
     *
     * @return This instance for call chaining.
     */
    public GLIntBuffer bind()
    {
        glBindBuffer(this.type, this.id);
        return this;
    }
    
    /**
     * Unbinds the buffer.
     *
     * @return This instance for call chaining.
     */
    public GLIntBuffer unbind()
    {
        glBindBuffer(this.type, 0);
        return this;
    }
    
    /**
     * @return The data in the buffer.
     */
    public int[] get()
    {
        if (this.data != null) glGetBufferSubData(this.type, 0, this.data);
        return this.data;
    }
    
    /**
     * Sets the data in the buffer.
     *
     * @param usage How the data will be used.
     * @param data  The data.
     * @return This instance for call chaining.
     */
    public GLIntBuffer set(int usage, int[] data)
    {
        this.data = data;
        glBufferData(this.type, data, usage);
        return this;
    }
}
