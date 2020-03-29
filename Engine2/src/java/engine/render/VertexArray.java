package engine.render;

import engine.util.Util;

import java.util.ArrayList;
import java.util.Arrays;

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
    private       int                ebo;
    
    private int vertexCount, indexCount;
    
    /**
     * Creates a new VertexArray.
     */
    public VertexArray()
    {
        this.vao = glGenVertexArrays();
    }
    
    /**
     * @return The number of vertices in the vertex array.
     */
    public int getVertexCount()
    {
        return this.vertexCount;
    }
    
    /**
     * @return The number of indices in the vertex array.
     */
    public int getIndexCount()
    {
        return this.indexCount;
    }
    
    /**
     * Bind the VertexArray for use.
     *
     * @return This instance for call chaining.
     */
    public VertexArray bind()
    {
        glBindVertexArray(this.vao);
        for (int vbo : this.vboList) glBindBuffer(GL_ARRAY_BUFFER, vbo);
        if (this.ebo > 0) glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, this.ebo);
        return this;
    }
    
    /**
     * Unbind the VertexArray from use.
     *
     * @return This instance for call chaining.
     */
    public VertexArray unbind()
    {
        glBindVertexArray(0);
        for (int vbo : this.vboList) glBindBuffer(GL_ARRAY_BUFFER, 0);
        if (this.ebo > 0) glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
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
        return reset();
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
        if (this.ebo > 0) glDeleteBuffers(this.ebo);
        return this;
    }
    
    /**
     * Draws the array in the specified mode. If an element buffer is available, it used it.
     *
     * @param mode The primitive type.
     * @return This instance for call chaining.
     */
    public VertexArray draw(int mode)
    {
        if (this.ebo > 0)
        {
            glDrawElements(mode, this.indexCount, GL_UNSIGNED_INT, 0);
        }
        else
        {
            glDrawArrays(mode, 0, this.vertexCount);
        }
        return this;
    }
    
    /**
     * Adds an index array to the Vertex Array
     *
     * @param data  The index array
     * @param usage How the data should be used.
     * @return This instance for call chaining.
     */
    public VertexArray addIndices(int[] data, int usage)
    {
        if (this.ebo > 0) glDeleteBuffers(this.ebo);
        this.ebo        = glGenBuffers();
        this.indexCount = data.length;
        
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, this.ebo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, data, usage);
        
        return this;
    }
    
    /**
     * Adds an index array to the Vertex Array
     *
     * @param data The index array
     * @return This instance for call chaining.
     */
    public VertexArray addIndices(int[] data)
    {
        return addIndices(data, GL_STATIC_DRAW);
    }
    
    /**
     * Adds a float buffer with many attributes to the Vertex Array.
     *
     * @param usage How the data should be used.
     * @param data The data
     * @param sizes The attributes lengths
     * @return This instance for call chaining.
     */
    public VertexArray add(int usage, float[] data, int... sizes)
    {
        if (sizes.length == 0) throw new RuntimeException("Invalid vertex size: Must have at least one size");
        int vertexSize = Util.sum(sizes);
        if (vertexSize == 0) throw new RuntimeException("Invalid vertex size: Vertex length must be > 0");
        
        int count = data.length / vertexSize;
        if (this.vertexCount > 0 && this.vertexCount != count) throw new RuntimeException(String.format("Vertex Array Mismatch: Array Len: %s Provide Array Len: %s", this.vertexCount, count));
        this.vertexCount = count;
    
        int vbo = glGenBuffers();
    
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, data, usage);
    
        int offset = 0, stride = vertexSize * Float.BYTES;
        for (int i = 0, n = sizes.length; i < n; i++)
        {
            int size = sizes[i];
            glVertexAttribPointer(this.vboList.size() + i, size, GL_FLOAT, false, stride, offset);
            glEnableVertexAttribArray(this.vboList.size() + i);
            offset += size * Float.BYTES;
        }
    
        this.vboList.add(vbo);
        return this;
    }
    
    /**
     * Adds a float buffer with many attributes to the Vertex Array.
     *
     * @param data  The data
     * @param sizes The attributes lengths
     * @return This instance for call chaining.
     */
    public VertexArray add(float[] data, int... sizes)
    {
        return add(GL_STATIC_DRAW, data, sizes);
    }
    
    /**
     * Adds an int buffer with many attributes to the Vertex Array.
     *
     * @param usage How the data should be used.
     * @param data The data
     * @param sizes The attributes lengths
     * @return This instance for call chaining.
     */
    public VertexArray add(int usage, int[] data, int... sizes)
    {
        if (sizes.length == 0) throw new RuntimeException("Invalid vertex size: Must have at least one size");
        int vertexSize = Util.sum(sizes);
        if (vertexSize == 0) throw new RuntimeException("Invalid vertex size: Vertex length must be > 0");
        
        int count = data.length / vertexSize;
        if (this.vertexCount > 0 && this.vertexCount != count) throw new RuntimeException(String.format("Vertex Array Mismatch: Array Len: %s Provide Array Len: %s", this.vertexCount, count));
        this.vertexCount = count;
    
        int vbo = glGenBuffers();
    
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, data, usage);
    
        int offset = 0, stride = vertexSize * Float.BYTES;
        for (int i = 0, n = sizes.length; i < n; i++)
        {
            int size = sizes[i];
            glVertexAttribPointer(this.vboList.size() + i, size, GL_FLOAT, false, stride, offset);
            glEnableVertexAttribArray(this.vboList.size() + i);
            offset += size * Float.BYTES;
        }
    
        this.vboList.add(vbo);
        return this;
    }
    
    /**
     * Adds an int buffer with many attributes to the Vertex Array.
     *
     * @param data  The data
     * @param sizes The attributes lengths
     * @return This instance for call chaining.
     */
    public VertexArray add(int[] data, int... sizes)
    {
        return add(GL_STATIC_DRAW, data, sizes);
    }
}
