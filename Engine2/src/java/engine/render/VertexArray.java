package engine.render;

import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_INT;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

/**
 * A wrapper class for OpenGL's vertex arrays. This class adds helper functions that make it easy to send data to the buffers.
 */
public class VertexArray
{
    private final int                vao;
    private final ArrayList<Integer> vboList = new ArrayList<>();
    
    /**
     * Creates a new VertexArray.
     */
    public VertexArray()
    {
        this.vao = glGenVertexArrays();
    }
    
    /**
     * Bind the VertexArray for use.
     *
     * @return This instance for call chaining.
     */
    public VertexArray bind()
    {
        glBindVertexArray(this.vao);
        for (int i = 0, n = this.vboList.size(); i < n; i++) glEnableVertexAttribArray(i);
        return this;
    }
    
    /**
     * Unbind the VertexArray from use.
     *
     * @return This instance for call chaining.
     */
    public VertexArray unbind()
    {
        glBindVertexArray(this.vao);
        for (int i = 0, n = this.vboList.size(); i < n; i++) glDisableVertexAttribArray(i);
        glBindVertexArray(0);
        return this;
    }
    
    /**
     * Deletes the VertexArray and Buffers.
     *
     * @return This instance for call chaining.
     */
    public VertexArray delete()
    {
        glDeleteVertexArrays(this.vao);
        for (int vbo : this.vboList) glDeleteBuffers(vbo);
        return this;
    }
    
    /**
     * Deletes the Buffers.
     *
     * @return This instance for call chaining.
     */
    public VertexArray reset()
    {
        for (int vbo : this.vboList) glDeleteBuffers(vbo);
        this.vboList.clear();
        return this;
    }
    
    /**
     * Adds an int buffer to the Vertex Array
     *
     * @param size  The size of the buffer.
     * @param data  The data
     * @param usage How the data should be used.
     * @return This instance for call chaining.
     */
    public VertexArray add(int size, int[] data, int usage)
    {
        int vbo = glGenBuffers();
        
        glBindVertexArray(this.vao);
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, data, usage);
        
        glVertexAttribPointer(this.vboList.size(), size, GL_INT, false, 0, 0);
        
        this.vboList.add(vbo);
        
        glBindVertexArray(0);
        
        return this;
    }
    
    /**
     * Adds an int buffer to the Vertex Array
     *
     * @param size The size of the buffer.
     * @param data The data
     * @return This instance for call chaining.
     */
    public VertexArray add(int size, int[] data)
    {
        return add(size, data, GL_STATIC_DRAW);
    }
    
    /**
     * Adds a float buffer to the Vertex Array
     *
     * @param size  The size of the buffer.
     * @param data  The data
     * @param usage How the data should be used.
     * @return This instance for call chaining.
     */
    public VertexArray add(int size, float[] data, int usage)
    {
        int vbo = glGenBuffers();
        
        glBindVertexArray(this.vao);
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, data, usage);
        
        glVertexAttribPointer(this.vboList.size(), size, GL_FLOAT, false, 0, 0);
        
        this.vboList.add(vbo);
        
        glBindVertexArray(0);
        
        return this;
    }
    
    /**
     * Adds a float buffer to the Vertex Array
     *
     * @param size The size of the buffer.
     * @param data The data
     * @return This instance for call chaining.
     */
    public VertexArray add(int size, float[] data)
    {
        return add(size, data, GL_STATIC_DRAW);
    }
}
