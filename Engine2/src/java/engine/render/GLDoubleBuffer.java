package engine.render;

import static org.lwjgl.opengl.GL46.*;

/**
 * A double buffer that can send and retrieve data from the GPU
 */
@SuppressWarnings("unused")
public class GLDoubleBuffer
{
    private final int id;
    private final int type;
    
    private double[] data;
    
    public GLDoubleBuffer(int type)
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
    public GLDoubleBuffer base(int index)
    {
        glBindBufferBase(this.type, index, this.id);
        return this;
    }
    
    /**
     * Binds the buffer.
     *
     * @return This instance for call chaining.
     */
    public GLDoubleBuffer bind()
    {
        glBindBuffer(this.type, this.id);
        return this;
    }
    
    /**
     * Unbinds the buffer.
     *
     * @return This instance for call chaining.
     */
    public GLDoubleBuffer unbind()
    {
        glBindBuffer(this.type, 0);
        return this;
    }
    
    /**
     * @return The data in the buffer.
     */
    public double[] get()
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
    public GLDoubleBuffer set(int usage, double[] data)
    {
        this.data = data;
        glBufferData(this.type, data, usage);
        return this;
    }
}
