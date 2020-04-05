package engine.render;

import engine.util.Util;

import java.nio.*;
import java.util.ArrayList;

import static org.lwjgl.opengl.GL30.*;

/**
 * A wrapper class for OpenGL's vertex arrays. This class adds helper functions that make it easy to send data to the buffers.
 */
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class VertexArray
{
    private final int id;
    
    private final ArrayList<GLBuffer>           vertexBuffers = new ArrayList<>();
    private       GLBuffer                      indexBuffer   = null;
    private final ArrayList<ArrayList<Integer>> attributes    = new ArrayList<>();
    
    private int vertexCount;
    
    /**
     * Creates a new VertexArray.
     */
    public VertexArray()
    {
        this.id = glGenVertexArrays();
    }
    
    /**
     * Gets the GLBuffer that holds the indices bound to the VertexArray. Can be null.
     *
     * @return The index GLBuffer
     */
    public GLBuffer getIndexBuffer()
    {
        return this.indexBuffer;
    }
    
    /**
     * Gets the GLBuffer that has been bound to the VertexArray.
     *
     * @param index The index.
     * @return The GLBuffer
     */
    public GLBuffer getVertexBuffer(int index)
    {
        return this.vertexBuffers.get(index);
    }
    
    /**
     * @return The number of attributes in the vertex array.
     */
    public int attributeCount()
    {
        int count = 0;
        for (ArrayList<Integer> bufferAttributes : this.attributes) count += bufferAttributes.size();
        return count;
    }
    
    /**
     * @return The number of vertices in the vertex array.
     */
    public int vertexCount()
    {
        return this.vertexCount;
    }
    
    /**
     * @return The number of indices in the vertex array.
     */
    public int indexCount()
    {
        return this.indexBuffer != null ? this.indexBuffer.size() : 0;
    }
    
    /**
     * Bind the VertexArray for use.
     *
     * @return This instance for call chaining.
     */
    public VertexArray bind()
    {
        glBindVertexArray(this.id);
        if (this.indexBuffer != null) this.indexBuffer.bind();
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
        if (this.indexBuffer != null) this.indexBuffer.unbind();
        return this;
    }
    
    /**
     * Deletes the VertexArray and Buffers.
     *
     * @return This instance for call chaining.
     */
    public VertexArray delete()
    {
        glDeleteVertexArrays(this.id);
        return reset();
    }
    
    /**
     * Resets the VertexArray by deleting all buffers and attributes.
     * <p>
     * Make sure to bind the vertex array first.
     *
     * @return This instance for call chaining.
     */
    public VertexArray reset()
    {
        for (GLBuffer vbo : this.vertexBuffers) vbo.delete();
        this.vertexBuffers.clear();
        if (this.indexBuffer != null) this.indexBuffer.delete();
    
        int i = 0;
        for (ArrayList<Integer> bufferAttributes : this.attributes)
        {
            for (int attribute : bufferAttributes) glDisableVertexAttribArray(i++);
            bufferAttributes.clear();
        }
        this.attributes.clear();
        this.vertexCount = 0;
        
        return this;
    }
    
    /**
     * Recalculates the vertexCount.
     *
     * @return This instance for call chaining.
     */
    public VertexArray resize()
    {
        this.vertexCount = Integer.MAX_VALUE;
        for (int i = 0, n = this.vertexBuffers.size(); i < n; i++)
        {
            int bufferAttributesSize = 0;
            for (int v : this.attributes.get(i)) bufferAttributesSize += v;
            this.vertexCount = Math.min(this.vertexCount, this.vertexBuffers.get(i).size() / bufferAttributesSize);
        }
        return this;
    }
    
    /**
     * Draws the array in the specified mode. If an element buffer is available, it used it.
     * <p>
     * Make sure to bind the vertex array first.
     *
     * @param mode The primitive type.
     * @return This instance for call chaining.
     */
    public VertexArray draw(int mode)
    {
        if (this.indexBuffer != null)
        {
            if (this.indexBuffer.size() > 0) glDrawElements(mode, this.indexBuffer.size(), GL_UNSIGNED_INT, 0);
        }
        else
        {
            if (this.vertexCount > 0) glDrawArrays(mode, 0, this.vertexCount);
        }
        return this;
    }
    
    /**
     * Adds an index array buffer to the Vertex Array. The buffer object will be managed by the VertexArray object.
     * <p>
     * Make sure to bind the vertex array first.
     *
     * @param buffer The index array buffer
     * @return This instance for call chaining.
     */
    public VertexArray addIndices(GLBuffer buffer)
    {
        if (this.indexBuffer != null) this.indexBuffer.delete();
        this.indexBuffer = buffer;
        return this;
    }
    
    /**
     * Adds an index array to the Vertex Array
     * <p>
     * Make sure to bind the vertex array first.
     *
     * @param data  The index array
     * @param usage How the data should be used.
     * @return This instance for call chaining.
     */
    public VertexArray addIndices(int[] data, int usage)
    {
        return addIndices(new GLBuffer(GL_ELEMENT_ARRAY_BUFFER).bind().set(data, usage));
    }
    
    /**
     * Adds an index array to the Vertex Array
     * <p>
     * Make sure to bind the vertex array first.
     *
     * @param data  The index array
     * @param usage How the data should be used.
     * @return This instance for call chaining.
     */
    public VertexArray addIndices(IntBuffer data, int usage)
    {
        return addIndices(new GLBuffer(GL_ELEMENT_ARRAY_BUFFER).bind().set(data, usage));
    }
    
    /**
     * Adds a buffer object with any number of attributes to the Vertex Array. The VertexArray object will manage the buffer.
     * <p>
     * Make sure to bind the vertex array first.
     *
     * @param type   The OpenGL data type
     * @param buffer Teh buffer object;
     * @param sizes  The attributes lengths
     * @return This instance for call chaining.
     */
    public VertexArray add(int type, GLBuffer buffer, int... sizes)
    {
        if (sizes.length == 0) throw new RuntimeException("Invalid vertex size: Must have at least one size");
        int bufferAttributesSize = Util.sum(sizes);
        if (bufferAttributesSize == 0) throw new RuntimeException("Invalid vertex size: Vertex length must be > 0");
        
        this.vertexCount = Math.min(this.vertexCount > 0 ? this.vertexCount : Integer.MAX_VALUE, buffer.size() / bufferAttributesSize);
        
        ArrayList<Integer> bufferAttributes = new ArrayList<>();
        
        buffer.bind();
        int attributeCount = attributeCount(), offset = 0, bytes = getBytes(type), stride = bufferAttributesSize * bytes;
        for (int size : sizes)
        {
            bufferAttributes.add(size);
            glVertexAttribPointer(attributeCount, size, type, false, stride, offset);
            glEnableVertexAttribArray(attributeCount++);
            offset += size * bytes;
        }
        this.vertexBuffers.add(buffer.unbind());
        this.attributes.add(bufferAttributes);
        return this;
    }
    
    /**
     * Adds a buffer with any number of attributes to the Vertex Array.
     * <p>
     * Make sure to bind the vertex array first.
     *
     * @param data  The data
     * @param usage How the data should be used.
     * @param sizes The attributes lengths
     * @return This instance for call chaining.
     */
    public VertexArray add(short[] data, int usage, int... sizes)
    {
        return add(GL_UNSIGNED_SHORT, new GLBuffer(GL_ARRAY_BUFFER).bind().set(data, usage), sizes);
    }
    
    /**
     * Adds a buffer with any number of attributes to the Vertex Array.
     * <p>
     * Make sure to bind the vertex array first.
     *
     * @param data  The data
     * @param usage How the data should be used.
     * @param sizes The attributes lengths
     * @return This instance for call chaining.
     */
    public VertexArray add(int[] data, int usage, int... sizes)
    {
        return add(GL_UNSIGNED_INT, new GLBuffer(GL_ARRAY_BUFFER).bind().set(data, usage), sizes);
    }
    
    /**
     * Adds a buffer with any number of attributes to the Vertex Array.
     * <p>
     * Make sure to bind the vertex array first.
     *
     * @param data  The data
     * @param usage How the data should be used.
     * @param sizes The attributes lengths
     * @return This instance for call chaining.
     */
    public VertexArray add(float[] data, int usage, int... sizes)
    {
        return add(GL_FLOAT, new GLBuffer(GL_ARRAY_BUFFER).bind().set(data, usage), sizes);
    }
    
    /**
     * Adds a buffer with any number of attributes to the Vertex Array.
     * <p>
     * Make sure to bind the vertex array first.
     *
     * @param data  The data
     * @param usage How the data should be used.
     * @param sizes The attributes lengths
     * @return This instance for call chaining.
     */
    public VertexArray add(double[] data, int usage, int... sizes)
    {
        return add(GL_DOUBLE, new GLBuffer(GL_ARRAY_BUFFER).bind().set(data, usage), sizes);
    }
    
    /**
     * Adds a buffer with any number of attributes to the Vertex Array.
     * <p>
     * Make sure to bind the vertex array first.
     *
     * @param data  The data
     * @param usage How the data should be used.
     * @param sizes The attributes lengths
     * @return This instance for call chaining.
     */
    public VertexArray add(ByteBuffer data, int usage, int... sizes)
    {
        return add(GL_UNSIGNED_BYTE, new GLBuffer(GL_ARRAY_BUFFER).bind().set(data, usage), sizes);
    }
    
    /**
     * Adds a buffer with any number of attributes to the Vertex Array.
     * <p>
     * Make sure to bind the vertex array first.
     *
     * @param data  The data
     * @param usage How the data should be used.
     * @param sizes The attributes lengths
     * @return This instance for call chaining.
     */
    public VertexArray add(ShortBuffer data, int usage, int... sizes)
    {
        return add(GL_UNSIGNED_SHORT, new GLBuffer(GL_ARRAY_BUFFER).bind().set(data, usage), sizes);
    }
    
    /**
     * Adds a buffer with any number of attributes to the Vertex Array.
     * <p>
     * Make sure to bind the vertex array first.
     *
     * @param data  The data
     * @param usage How the data should be used.
     * @param sizes The attributes lengths
     * @return This instance for call chaining.
     */
    public VertexArray add(IntBuffer data, int usage, int... sizes)
    {
        return add(GL_UNSIGNED_INT, new GLBuffer(GL_ARRAY_BUFFER).bind().set(data, usage), sizes);
    }
    
    /**
     * Adds a buffer with any number of attributes to the Vertex Array.
     * <p>
     * Make sure to bind the vertex array first.
     *
     * @param data  The data
     * @param usage How the data should be used.
     * @param sizes The attributes lengths
     * @return This instance for call chaining.
     */
    public VertexArray add(FloatBuffer data, int usage, int... sizes)
    {
        return add(GL_FLOAT, new GLBuffer(GL_ARRAY_BUFFER).bind().set(data, usage), sizes);
    }
    
    /**
     * Adds a buffer with any number of attributes to the Vertex Array.
     * <p>
     * Make sure to bind the vertex array first.
     *
     * @param data  The data
     * @param usage How the data should be used.
     * @param sizes The attributes lengths
     * @return This instance for call chaining.
     */
    public VertexArray add(DoubleBuffer data, int usage, int... sizes)
    {
        return add(GL_DOUBLE, new GLBuffer(GL_ARRAY_BUFFER).bind().set(data, usage), sizes);
    }
    
    private int getBytes(int type)
    {
        switch (type)
        {
            case GL_UNSIGNED_BYTE:
            case GL_BYTE:
            default:
                return Byte.BYTES;
            case GL_UNSIGNED_SHORT:
            case GL_SHORT:
                return Short.BYTES;
            case GL_UNSIGNED_INT:
            case GL_INT:
                return Integer.BYTES;
            case GL_FLOAT:
                return Float.BYTES;
            case GL_DOUBLE:
                return Double.BYTES;
        }
    }
}
