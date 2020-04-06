package engine.render;

import engine.util.Logger;

import java.nio.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import static engine.util.Util.sum;
import static org.lwjgl.opengl.GL30.*;

/**
 * A wrapper class for OpenGL's vertex arrays. This class adds helper functions that make it easy to send data to the buffers.
 */
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class VertexArray
{
    private static final Logger LOGGER = new Logger();
    
    private final int id;
    
    private final ArrayList<GLBuffer> vertexBuffers = new ArrayList<>();
    
    private GLBuffer indexBuffer = null;
    
    private final ArrayList<ArrayList<Integer>> attributes = new ArrayList<>();
    
    private int vertexCount;
    
    /**
     * Creates a new VertexArray.
     */
    public VertexArray()
    {
        this.id = glGenVertexArrays();
    }
    
    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VertexArray that = (VertexArray) o;
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
        return "VertexArray{" + "id=" + this.id + ", VBOs=" + this.vertexBuffers + ", EBO=" + this.indexBuffer + '}';
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
    public GLBuffer getBuffer(int index)
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
        VertexArray.LOGGER.finest("Binding VertexArray: %s", this.id);
        
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
        VertexArray.LOGGER.finest("Unbinding VertexArray: %s", this.id);
        
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
        VertexArray.LOGGER.finest("Deleting VertexArray: %s", this.id);
        
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
        VertexArray.LOGGER.finest("Resetting VertexArray: %s", this.id);
        
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
        VertexArray.LOGGER.finest("Resizing VertexArray: %s", this.id);
        
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
            VertexArray.LOGGER.finest("Drawing indices for VertexArray: %s", this.id);
            
            if (this.indexBuffer.size() > 0) glDrawElements(mode, this.indexBuffer.size(), GL_UNSIGNED_INT, 0);
        }
        else
        {
            VertexArray.LOGGER.finest("Drawing vertices for VertexArray: %s", this.id);
            
            if (this.vertexCount > 0) glDrawArrays(mode, 0, this.vertexCount);
        }
        return this;
    }
    
    /**
     * Adds an element array buffer to the Vertex Array. The buffer object will be managed by the VertexArray object.
     * <p>
     * Make sure to bind the vertex array first.
     *
     * @param buffer The index array buffer
     * @return This instance for call chaining.
     */
    public VertexArray addEBO(GLBuffer buffer)
    {
        VertexArray.LOGGER.finest("Adding EBO (%s) into VertexArray: %s", buffer, this.id);
        
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
    public VertexArray addEBO(int[] data, int usage)
    {
        return addEBO(new GLBuffer(GL_ELEMENT_ARRAY_BUFFER).bind().set(data, usage));
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
    public VertexArray addEBO(IntBuffer data, int usage)
    {
        return addEBO(new GLBuffer(GL_ELEMENT_ARRAY_BUFFER).bind().set(data, usage));
    }
    
    /**
     * Adds a buffer object with any number of attributes to the Vertex Array. The VertexArray object will manage the buffer.
     * <p>
     * Make sure to bind the vertex array first.
     *
     * @param buffer  The buffer object;
     * @param formats The type and size pairs for how the buffer is organized.
     * @return This instance for call chaining.
     */
    public VertexArray add(GLBuffer buffer, int... formats)
    {
        int n = formats.length;
        if (n == 0) throw new RuntimeException("Invalid vertex format: Must have at least one type/size pair");
        if ((n & 1) == 1) throw new RuntimeException("Invalid vertex format: A type/size pair is missing a value");
        
        n >>= 1;
        int[] types = new int[n];
        int[] bytes = new int[n];
        int[] sizes = new int[n];
        int stride = 0;
        for (int i = 0, index; i < n; i++)
        {
            index = i << 1;
            if (invalidType(formats[index])) throw new RuntimeException("Invalid vertex format: Not recognized OpenGL type: " + formats[index]);
            types[i] = formats[index];
            bytes[i] = getBytes(formats[index]);
            sizes[i] = formats[index + 1];
            stride += bytes[i] * sizes[i];
        }
        
        VertexArray.LOGGER.finest("Adding VBO (%s) of types %s into VertexArray: %s", buffer, Arrays.toString(types), this.id);
        
        int bufferAttributesSize = sum(sizes);
        if (bufferAttributesSize == 0) throw new RuntimeException("Invalid vertex format: Vertex length must be > 0");
        
        this.vertexCount = Math.min(this.vertexCount > 0 ? this.vertexCount : Integer.MAX_VALUE, buffer.size() / bufferAttributesSize);
        
        ArrayList<Integer> bufferAttributes = new ArrayList<>();
        
        buffer.bind();
        int attributeCount = attributeCount(), offset = 0;
        for (int i = 0; i < n; i++)
        {
            int type = types[i];
            int size = sizes[i];
            bufferAttributes.add(size);
            glVertexAttribPointer(attributeCount, size, type, false, stride, offset);
            glEnableVertexAttribArray(attributeCount++);
            offset += size * bytes[i];
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
        return add(new GLBuffer(GL_ARRAY_BUFFER).bind().set(data, usage), getFormatArray(sizes, GL_SHORT));
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
        return add(new GLBuffer(GL_ARRAY_BUFFER).bind().set(data, usage), getFormatArray(sizes, GL_INT));
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
        return add(new GLBuffer(GL_ARRAY_BUFFER).bind().set(data, usage), getFormatArray(sizes, GL_FLOAT));
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
        return add(new GLBuffer(GL_ARRAY_BUFFER).bind().set(data, usage), getFormatArray(sizes, GL_DOUBLE));
    }
    
    /**
     * Adds a buffer with any number of attributes to the Vertex Array.
     * <p>
     * Make sure to bind the vertex array first.
     *
     * @param data  The data
     * @param usage How the data should be used.
     * @param formats The type and size pairs for how the buffer is organized.
     * @return This instance for call chaining.
     */
    public VertexArray add(ByteBuffer data, int usage, int... formats)
    {
        return add(new GLBuffer(GL_ARRAY_BUFFER).bind().set(data, usage), formats);
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
        return add(new GLBuffer(GL_ARRAY_BUFFER).bind().set(data, usage), getFormatArray(sizes, GL_UNSIGNED_SHORT));
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
        return add(new GLBuffer(GL_ARRAY_BUFFER).bind().set(data, usage), getFormatArray(sizes, GL_UNSIGNED_INT));
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
        return add(new GLBuffer(GL_ARRAY_BUFFER).bind().set(data, usage), getFormatArray(sizes, GL_FLOAT));
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
        return add(new GLBuffer(GL_ARRAY_BUFFER).bind().set(data, usage), getFormatArray(sizes, GL_DOUBLE));
    }
    
    private int[] getFormatArray(int[] sizes, int type)
    {
        int   n      = sizes.length;
        int[] format = new int[n << 1];
        for (int i = 0, index = 0; i < n; i++)
        {
            format[index++] = type;
            format[index++] = sizes[i];
        }
        return format;
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
    
    private boolean invalidType(int type)
    {
        switch (type)
        {
            case GL_UNSIGNED_BYTE:
            case GL_BYTE:
            case GL_UNSIGNED_SHORT:
            case GL_SHORT:
            case GL_UNSIGNED_INT:
            case GL_INT:
            case GL_FLOAT:
            case GL_DOUBLE:
                return false;
            default:
                return true;
        }
    }
}
