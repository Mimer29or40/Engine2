package engine.render;

import engine.util.Logger;
import engine.util.Pair;

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
    
    private final ArrayList<ArrayList<Pair.I>> attributes = new ArrayList<>();
    
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
        for (ArrayList<Pair.I> bufferAttributes : this.attributes) count += bufferAttributes.size();
        return count;
    }
    
    /**
     * @return The size in bytes of the attributes.
     */
    public int attributesSize()
    {
        int size = 0;
        for (ArrayList<Pair.I> bufferAttributes : this.attributes) for (Pair.I attribute : bufferAttributes) size += attribute.a * attribute.b;
        return size;
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
        return this.indexBuffer != null ? this.indexBuffer.bufferSize() : 0;
    }
    
    /**
     * Gets the size of the buffer at the index specified.
     *
     * @param buffer The buffer index.
     * @return The size of the buffer in bytes
     */
    public int bufferSize(int buffer)
    {
        return this.vertexBuffers.get(buffer).bufferSize();
    }
    
    /**
     * Gets the size of the buffer at the index specified.
     *
     * @return The size of the buffer in bytes
     */
    public int bufferSize()
    {
        return bufferSize(0);
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
        for (ArrayList<Pair.I> bufferAttributes : this.attributes)
        {
            for (Pair.I attribute : bufferAttributes) glDisableVertexAttribArray(i++);
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
            for (Pair.I v : this.attributes.get(i)) bufferAttributesSize += v.a * v.b;
            this.vertexCount = Math.min(this.vertexCount, this.vertexBuffers.get(i).dataSize() / bufferAttributesSize);
        }
        return this;
    }
    
    /**
     * Draws the array in the specified mode. If an element buffer is available, it used it.
     * <p>
     * Make sure to bind the vertex array first.
     *
     * @param mode The primitive type.
     * @param size The size of the buffer to draw.
     * @return This instance for call chaining.
     */
    public VertexArray draw(GL mode, int size)
    {
        if (this.indexBuffer != null)
        {
            VertexArray.LOGGER.finest("Drawing indices for VertexArray: %s", this.id);
            
            if (this.indexBuffer.dataSize() > 0) glDrawElements(mode.ref(), size, GL.UNSIGNED_INT.ref(), 0);
        }
        else
        {
            VertexArray.LOGGER.finest("Drawing vertices for VertexArray: %s", this.id);
            
            if (this.vertexCount > 0) glDrawArrays(mode.ref(), 0, size);
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
    public VertexArray draw(GL mode)
    {
        return draw(mode, this.indexBuffer != null ? this.indexBuffer.dataSize() : this.vertexCount);
    }
    
    /**
     * Adds an element array buffer to the Vertex Array, if one is already present then is deletes and replaces it. The buffer object will be managed by the VertexArray object.
     * <p>
     * Make sure to bind the vertex array first.
     *
     * @param buffer The index array buffer
     * @return This instance for call chaining.
     */
    public VertexArray addEBO(GLBuffer buffer, GL usage)
    {
        VertexArray.LOGGER.finest("Adding EBO (%s) into VertexArray: %s", buffer, this.id);
    
        if (this.indexBuffer != null) this.indexBuffer.delete();
        this.indexBuffer = buffer.usage(usage);
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
    public VertexArray addEBO(int[] data, GL usage)
    {
        return addEBO(new GLBuffer(GL.ELEMENT_ARRAY_BUFFER).bind().set(data), usage);
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
    public VertexArray addEBO(IntBuffer data, GL usage)
    {
        return addEBO(new GLBuffer(GL.ELEMENT_ARRAY_BUFFER).bind().set(data), usage);
    }
    
    /**
     * Sets the index array in the Vertex Array. It creates one if it does not have one.
     * <p>
     * Make sure to bind the vertex array first.
     *
     * @param data  The index array
     * @param usage How the data should be used.
     * @return This instance for call chaining.
     */
    public VertexArray setEBO(int[] data, GL usage)
    {
        if (this.indexBuffer == null) this.indexBuffer = new GLBuffer(GL.ELEMENT_ARRAY_BUFFER);
        this.indexBuffer.usage(usage).bind().set(data);
        return this;
    }
    
    /**
     * Sets the index array in the Vertex Array. It creates one if it does not have one.
     * <p>
     * Make sure to bind the vertex array first.
     *
     * @param data  The index array
     * @param usage How the data should be used.
     * @return This instance for call chaining.
     */
    public VertexArray setEBO(IntBuffer data, GL usage)
    {
        if (this.indexBuffer == null) this.indexBuffer = new GLBuffer(GL.ELEMENT_ARRAY_BUFFER);
        this.indexBuffer.usage(usage).bind().set(data);
        return this;
    }
    
    /**
     * Sets the index array in the Vertex Array. This method will not create one.
     * <p>
     * Make sure to bind the vertex array first.
     *
     * @param data The index array
     * @return This instance for call chaining.
     */
    public VertexArray setEBO(int[] data)
    {
        this.indexBuffer.bind().set(data);
        return this;
    }
    
    /**
     * Sets the index array in the Vertex Array. This method will not create one.
     * <p>
     * Make sure to bind the vertex array first.
     *
     * @param data The index array
     * @return This instance for call chaining.
     */
    public VertexArray setEBO(IntBuffer data)
    {
        this.indexBuffer.bind().set(data);
        return this;
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
    public VertexArray add(GLBuffer buffer, Object... formats)
    {
        int n = formats.length;
        if (n == 0) throw new RuntimeException("Invalid vertex format: Must have at least one type/size pair");
        if ((n & 1) == 1) throw new RuntimeException("Invalid vertex format: A type/size pair is missing a value");
        
        n >>= 1;
        int[] types  = new int[n];
        int[] bytes  = new int[n];
        int[] sizes  = new int[n];
        int   stride = 0;
        for (int i = 0, index; i < n; i++)
        {
            index = i << 1;
            if (!(formats[index] instanceof GL)) throw new RuntimeException("Invalid vertex format: Not recognized OpenGL type: " + formats[index]);
            types[i] = ((GL) formats[index]).ref();
            bytes[i] = getBytes(types[i]);
            sizes[i] = (int) formats[index + 1];
            stride += bytes[i] * sizes[i];
        }
        
        VertexArray.LOGGER.finest("Adding VBO (%s) of types %s into VertexArray: %s", buffer, Arrays.toString(types), this.id);
        
        int bufferAttributesSize = sum(sizes);
        if (bufferAttributesSize == 0) throw new RuntimeException("Invalid vertex format: Vertex length must be > 0");
    
        this.vertexCount = Math.min(this.vertexCount > 0 ? this.vertexCount : Integer.MAX_VALUE, buffer.bufferSize() / bufferAttributesSize);
    
        ArrayList<Pair.I> bufferAttributes = new ArrayList<>();
        
        buffer.bind();
        int attributeCount = attributeCount(), offset = 0;
        for (int i = 0; i < n; i++)
        {
            int type = types[i];
            int size = sizes[i];
            bufferAttributes.add(new Pair.I(size, bytes[i]));
            glVertexAttribPointer(attributeCount, size, type, false, stride, offset);
            glEnableVertexAttribArray(attributeCount++);
            offset += size * bytes[i];
        }
        this.vertexBuffers.add(buffer.unbind());
        this.attributes.add(bufferAttributes);
        return this;
    }
    
    /**
     * Allocates a buffer with a certain size with any number of attributes to the Vertex Array.
     * <p>
     * Make sure to bind the vertex array first.
     *
     * @param size    The size of the data in bytes
     * @param usage   How the data should be used.
     * @param formats The type and size pairs for how the buffer is organized.
     * @return This instance for call chaining.
     */
    public VertexArray add(int size, GL usage, Object... formats)
    {
        return add(new GLBuffer(GL.ARRAY_BUFFER).usage(usage).bind().set(size).unbind(), formats);
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
    public VertexArray add(short[] data, GL usage, int... sizes)
    {
        return add(new GLBuffer(GL.ARRAY_BUFFER).usage(usage).bind().set(data), getFormatArray(sizes, GL.SHORT));
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
    public VertexArray add(int[] data, GL usage, int... sizes)
    {
        return add(new GLBuffer(GL.ARRAY_BUFFER).usage(usage).bind().set(data), getFormatArray(sizes, GL.INT));
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
    public VertexArray add(float[] data, GL usage, int... sizes)
    {
        return add(new GLBuffer(GL.ARRAY_BUFFER).usage(usage).bind().set(data), getFormatArray(sizes, GL.FLOAT));
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
    public VertexArray add(double[] data, GL usage, int... sizes)
    {
        return add(new GLBuffer(GL.ARRAY_BUFFER).usage(usage).bind().set(data), getFormatArray(sizes, GL.DOUBLE));
    }
    
    /**
     * Adds a buffer with any number of attributes to the Vertex Array.
     * <p>
     * Make sure to bind the vertex array first.
     *
     * @param data    The data
     * @param usage   How the data should be used.
     * @param formats The type and size pairs for how the buffer is organized.
     * @return This instance for call chaining.
     */
    public VertexArray add(ByteBuffer data, GL usage, Object... formats)
    {
        return add(new GLBuffer(GL.ARRAY_BUFFER).usage(usage).bind().set(data), formats);
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
    public VertexArray add(ShortBuffer data, GL usage, int... sizes)
    {
        return add(new GLBuffer(GL.ARRAY_BUFFER).usage(usage).bind().set(data), getFormatArray(sizes, GL.UNSIGNED_SHORT));
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
    public VertexArray add(IntBuffer data, GL usage, int... sizes)
    {
        return add(new GLBuffer(GL.ARRAY_BUFFER).usage(usage).bind().set(data), getFormatArray(sizes, GL.UNSIGNED_INT));
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
    public VertexArray add(FloatBuffer data, GL usage, int... sizes)
    {
        return add(new GLBuffer(GL.ARRAY_BUFFER).usage(usage).bind().set(data), getFormatArray(sizes, GL.FLOAT));
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
    public VertexArray add(DoubleBuffer data, GL usage, int... sizes)
    {
        return add(new GLBuffer(GL.ARRAY_BUFFER).usage(usage).bind().set(data), getFormatArray(sizes, GL.DOUBLE));
    }
    
    /**
     * Changes the contents of the buffer at index.
     * <p>
     * Make sure to bind the vertex array first.
     *
     * @param buffer The buffer index.
     * @param usage  How the data will be used.
     * @param data   The data.
     * @return This instance for call chaining.
     */
    public VertexArray set(int buffer, GL usage, short... data)
    {
        this.vertexBuffers.get(buffer).usage(usage).bind().set(data).unbind();
        return this;
    }
    
    /**
     * Changes the contents of the buffer at index.
     * <p>
     * Make sure to bind the vertex array first.
     *
     * @param usage How the data will be used.
     * @param data  The data.
     * @return This instance for call chaining.
     */
    public VertexArray set(GL usage, short... data)
    {
        return set(0, usage, data);
    }
    
    /**
     * Changes the contents of the buffer at index.
     * <p>
     * Make sure to bind the vertex array first.
     *
     * @param buffer The buffer index.
     * @param usage  How the data will be used.
     * @param data   The data.
     * @return This instance for call chaining.
     */
    public VertexArray set(int buffer, GL usage, int... data)
    {
        this.vertexBuffers.get(buffer).usage(usage).bind().set(data).unbind();
        return this;
    }
    
    /**
     * Changes the contents of the buffer at index.
     * <p>
     * Make sure to bind the vertex array first.
     *
     * @param usage How the data will be used.
     * @param data  The data.
     * @return This instance for call chaining.
     */
    public VertexArray set(GL usage, int... data)
    {
        return set(0, usage, data);
    }
    
    /**
     * Changes the contents of the buffer at index.
     * <p>
     * Make sure to bind the vertex array first.
     *
     * @param buffer The buffer index.
     * @param usage  How the data will be used.
     * @param data   The data.
     * @return This instance for call chaining.
     */
    public VertexArray set(int buffer, GL usage, long... data)
    {
        this.vertexBuffers.get(buffer).usage(usage).bind().set(data).unbind();
        return this;
    }
    
    /**
     * Changes the contents of the buffer at index.
     * <p>
     * Make sure to bind the vertex array first.
     *
     * @param usage How the data will be used.
     * @param data  The data.
     * @return This instance for call chaining.
     */
    public VertexArray set(GL usage, long... data)
    {
        return set(0, usage, data);
    }
    
    /**
     * Changes the contents of the buffer at index.
     * <p>
     * Make sure to bind the vertex array first.
     *
     * @param buffer The buffer index.
     * @param usage  How the data will be used.
     * @param data   The data.
     * @return This instance for call chaining.
     */
    public VertexArray set(int buffer, GL usage, float... data)
    {
        this.vertexBuffers.get(buffer).usage(usage).bind().set(data).unbind();
        return this;
    }
    
    /**
     * Changes the contents of the buffer at index.
     * <p>
     * Make sure to bind the vertex array first.
     *
     * @param usage How the data will be used.
     * @param data  The data.
     * @return This instance for call chaining.
     */
    public VertexArray set(GL usage, float... data)
    {
        return set(0, usage, data);
    }
    
    /**
     * Changes the contents of the buffer at index.
     * <p>
     * Make sure to bind the vertex array first.
     *
     * @param buffer The buffer index.
     * @param usage  How the data will be used.
     * @param data   The data.
     * @return This instance for call chaining.
     */
    public VertexArray set(int buffer, GL usage, double... data)
    {
        this.vertexBuffers.get(buffer).usage(usage).bind().set(data).unbind();
        return this;
    }
    
    /**
     * Changes the contents of the buffer at index.
     * <p>
     * Make sure to bind the vertex array first.
     *
     * @param usage How the data will be used.
     * @param data  The data.
     * @return This instance for call chaining.
     */
    public VertexArray set(GL usage, double... data)
    {
        return set(0, usage, data);
    }
    
    /**
     * Changes the contents of the buffer at index.
     * <p>
     * Make sure to bind the vertex array first.
     *
     * @param buffer The buffer index.
     * @param usage  How the data will be used.
     * @param data   The data.
     * @return This instance for call chaining.
     */
    public VertexArray set(int buffer, GL usage, ByteBuffer data)
    {
        this.vertexBuffers.get(buffer).usage(usage).bind().set(data).unbind();
        return this;
    }
    
    /**
     * Changes the contents of the buffer at index.
     * <p>
     * Make sure to bind the vertex array first.
     *
     * @param usage How the data will be used.
     * @param data  The data.
     * @return This instance for call chaining.
     */
    public VertexArray set(GL usage, ByteBuffer data)
    {
        return set(0, usage, data);
    }
    
    /**
     * Changes the contents of the buffer at index.
     * <p>
     * Make sure to bind the vertex array first.
     *
     * @param buffer The buffer index.
     * @param usage  How the data will be used.
     * @param data   The data.
     * @return This instance for call chaining.
     */
    public VertexArray set(int buffer, GL usage, ShortBuffer data)
    {
        this.vertexBuffers.get(buffer).usage(usage).bind().set(data).unbind();
        return this;
    }
    
    /**
     * Changes the contents of the buffer at index.
     * <p>
     * Make sure to bind the vertex array first.
     *
     * @param usage How the data will be used.
     * @param data  The data.
     * @return This instance for call chaining.
     */
    public VertexArray set(GL usage, ShortBuffer data)
    {
        return set(0, usage, data);
    }
    
    /**
     * Changes the contents of the buffer at index.
     * <p>
     * Make sure to bind the vertex array first.
     *
     * @param buffer The buffer index.
     * @param usage  How the data will be used.
     * @param data   The data.
     * @return This instance for call chaining.
     */
    public VertexArray set(int buffer, GL usage, IntBuffer data)
    {
        this.vertexBuffers.get(buffer).usage(usage).bind().set(data).unbind();
        return this;
    }
    
    /**
     * Changes the contents of the buffer at index.
     * <p>
     * Make sure to bind the vertex array first.
     *
     * @param usage How the data will be used.
     * @param data  The data.
     * @return This instance for call chaining.
     */
    public VertexArray set(GL usage, IntBuffer data)
    {
        return set(0, usage, data);
    }
    
    /**
     * Changes the contents of the buffer at index.
     * <p>
     * Make sure to bind the vertex array first.
     *
     * @param buffer The buffer index.
     * @param usage  How the data will be used.
     * @param data   The data.
     * @return This instance for call chaining.
     */
    public VertexArray set(int buffer, GL usage, LongBuffer data)
    {
        this.vertexBuffers.get(buffer).usage(usage).bind().set(data).unbind();
        return this;
    }
    
    /**
     * Changes the contents of the buffer at index.
     * <p>
     * Make sure to bind the vertex array first.
     *
     * @param usage How the data will be used.
     * @param data  The data.
     * @return This instance for call chaining.
     */
    public VertexArray set(GL usage, LongBuffer data)
    {
        return set(0, usage, data);
    }
    
    /**
     * Changes the contents of the buffer at index.
     * <p>
     * Make sure to bind the vertex array first.
     *
     * @param buffer The buffer index.
     * @param usage  How the data will be used.
     * @param data   The data.
     * @return This instance for call chaining.
     */
    public VertexArray set(int buffer, GL usage, FloatBuffer data)
    {
        this.vertexBuffers.get(buffer).usage(usage).bind().set(data).unbind();
        return this;
    }
    
    /**
     * Changes the contents of the buffer at index.
     * <p>
     * Make sure to bind the vertex array first.
     *
     * @param usage How the data will be used.
     * @param data  The data.
     * @return This instance for call chaining.
     */
    public VertexArray set(GL usage, FloatBuffer data)
    {
        return set(0, usage, data);
    }
    
    /**
     * Changes the contents of the buffer at index.
     * <p>
     * Make sure to bind the vertex array first.
     *
     * @param buffer The buffer index.
     * @param usage  How the data will be used.
     * @param data   The data.
     * @return This instance for call chaining.
     */
    public VertexArray set(int buffer, GL usage, DoubleBuffer data)
    {
        this.vertexBuffers.get(buffer).usage(usage).bind().set(data).unbind();
        return this;
    }
    
    /**
     * Changes the contents of the buffer at index.
     * <p>
     * Make sure to bind the vertex array first.
     *
     * @param usage How the data will be used.
     * @param data  The data.
     * @return This instance for call chaining.
     */
    public VertexArray set(GL usage, DoubleBuffer data)
    {
        return set(0, usage, data);
    }
    
    /**
     * Changes the contents of the buffer at the index. The size of the data must match the buffer.
     * <p>
     * Make sure to bind the buffer first.
     *
     * @param buffer The buffer index.
     * @param data   The data.
     * @return This instance for call chaining.
     */
    public VertexArray set(int buffer, short... data)
    {
        this.vertexBuffers.get(buffer).bind().set(data).unbind();
        return this;
    }
    
    /**
     * Changes the contents of the buffer at the index. The size of the data must match the buffer.
     * <p>
     * Make sure to bind the buffer first.
     *
     * @param data The data.
     * @return This instance for call chaining.
     */
    public VertexArray set(short... data)
    {
        return set(0, data);
    }
    
    /**
     * Changes the contents of the buffer at the index. The size of the data must match the buffer.
     * <p>
     * Make sure to bind the buffer first.
     *
     * @param buffer The buffer index.
     * @param data   The data.
     * @return This instance for call chaining.
     */
    public VertexArray set(int buffer, int... data)
    {
        this.vertexBuffers.get(buffer).bind().set(data).unbind();
        return this;
    }
    
    /**
     * Changes the contents of the buffer at the index. The size of the data must match the buffer.
     * <p>
     * Make sure to bind the buffer first.
     *
     * @param data The data.
     * @return This instance for call chaining.
     */
    public VertexArray set(int... data)
    {
        return set(0, data);
    }
    
    /**
     * Changes the contents of the buffer at the index. The size of the data must match the buffer.
     * <p>
     * Make sure to bind the buffer first.
     *
     * @param buffer The buffer index.
     * @param data   The data.
     * @return This instance for call chaining.
     */
    public VertexArray set(int buffer, long... data)
    {
        this.vertexBuffers.get(buffer).bind().set(data).unbind();
        return this;
    }
    
    /**
     * Changes the contents of the buffer at the index. The size of the data must match the buffer.
     * <p>
     * Make sure to bind the buffer first.
     *
     * @param data The data.
     * @return This instance for call chaining.
     */
    public VertexArray set(long... data)
    {
        return set(0, data);
    }
    
    /**
     * Changes the contents of the buffer at the index. The size of the data must match the buffer.
     * <p>
     * Make sure to bind the buffer first.
     *
     * @param buffer The buffer index.
     * @param data   The data.
     * @return This instance for call chaining.
     */
    public VertexArray set(int buffer, float... data)
    {
        this.vertexBuffers.get(buffer).bind().set(data).unbind();
        return this;
    }
    
    /**
     * Changes the contents of the buffer at the index. The size of the data must match the buffer.
     * <p>
     * Make sure to bind the buffer first.
     *
     * @param data The data.
     * @return This instance for call chaining.
     */
    public VertexArray set(float... data)
    {
        return set(0, data);
    }
    
    /**
     * Changes the contents of the buffer at the index. The size of the data must match the buffer.
     * <p>
     * Make sure to bind the buffer first.
     *
     * @param buffer The buffer index.
     * @param data   The data.
     * @return This instance for call chaining.
     */
    public VertexArray set(int buffer, double... data)
    {
        this.vertexBuffers.get(buffer).bind().set(data).unbind();
        return this;
    }
    
    /**
     * Changes the contents of the buffer at the index. The size of the data must match the buffer.
     * <p>
     * Make sure to bind the buffer first.
     *
     * @param data The data.
     * @return This instance for call chaining.
     */
    public VertexArray set(double... data)
    {
        return set(0, data);
    }
    
    /**
     * Changes the contents of the buffer at the index. The size of the data must match the buffer.
     * <p>
     * Make sure to bind the buffer first.
     *
     * @param buffer The buffer index.
     * @param data   The data.
     * @return This instance for call chaining.
     */
    public VertexArray set(int buffer, ByteBuffer data)
    {
        this.vertexBuffers.get(buffer).bind().set(data).unbind();
        return this;
    }
    
    /**
     * Changes the contents of the buffer at the index. The size of the data must match the buffer.
     * <p>
     * Make sure to bind the buffer first.
     *
     * @param data The data.
     * @return This instance for call chaining.
     */
    public VertexArray set(ByteBuffer data)
    {
        return set(0, data);
    }
    
    /**
     * Changes the contents of the buffer at the index. The size of the data must match the buffer.
     * <p>
     * Make sure to bind the buffer first.
     *
     * @param buffer The buffer index.
     * @param data   The data.
     * @return This instance for call chaining.
     */
    public VertexArray set(int buffer, ShortBuffer data)
    {
        this.vertexBuffers.get(buffer).bind().set(data).unbind();
        return this;
    }
    
    /**
     * Changes the contents of the buffer at the index. The size of the data must match the buffer.
     * <p>
     * Make sure to bind the buffer first.
     *
     * @param data The data.
     * @return This instance for call chaining.
     */
    public VertexArray set(ShortBuffer data)
    {
        return set(0, data);
    }
    
    /**
     * Changes the contents of the buffer at the index. The size of the data must match the buffer.
     * <p>
     * Make sure to bind the buffer first.
     *
     * @param buffer The buffer index.
     * @param data   The data.
     * @return This instance for call chaining.
     */
    public VertexArray set(int buffer, IntBuffer data)
    {
        this.vertexBuffers.get(buffer).bind().set(data).unbind();
        return this;
    }
    
    /**
     * Changes the contents of the buffer at the index. The size of the data must match the buffer.
     * <p>
     * Make sure to bind the buffer first.
     *
     * @param data The data.
     * @return This instance for call chaining.
     */
    public VertexArray set(IntBuffer data)
    {
        return set(0, data);
    }
    
    /**
     * Changes the contents of the buffer at the index. The size of the data must match the buffer.
     * <p>
     * Make sure to bind the buffer first.
     *
     * @param buffer The buffer index.
     * @param data   The data.
     * @return This instance for call chaining.
     */
    public VertexArray set(int buffer, LongBuffer data)
    {
        this.vertexBuffers.get(buffer).bind().set(data).unbind();
        return this;
    }
    
    /**
     * Changes the contents of the buffer at the index. The size of the data must match the buffer.
     * <p>
     * Make sure to bind the buffer first.
     *
     * @param data The data.
     * @return This instance for call chaining.
     */
    public VertexArray set(LongBuffer data)
    {
        return set(0, data);
    }
    
    /**
     * Changes the contents of the buffer at the index. The size of the data must match the buffer.
     * <p>
     * Make sure to bind the buffer first.
     *
     * @param buffer The buffer index.
     * @param data   The data.
     * @return This instance for call chaining.
     */
    public VertexArray set(int buffer, FloatBuffer data)
    {
        this.vertexBuffers.get(buffer).bind().set(data).unbind();
        return this;
    }
    
    /**
     * Changes the contents of the buffer at the index. The size of the data must match the buffer.
     * <p>
     * Make sure to bind the buffer first.
     *
     * @param data The data.
     * @return This instance for call chaining.
     */
    public VertexArray set(FloatBuffer data)
    {
        return set(0, data);
    }
    
    /**
     * Changes the contents of the buffer at the index. The size of the data must match the buffer.
     * <p>
     * Make sure to bind the buffer first.
     *
     * @param buffer The buffer index.
     * @param data   The data.
     * @return This instance for call chaining.
     */
    public VertexArray set(int buffer, DoubleBuffer data)
    {
        this.vertexBuffers.get(buffer).bind().set(data).unbind();
        return this;
    }
    
    /**
     * Changes the contents of the buffer at the index. The size of the data must match the buffer.
     * <p>
     * Make sure to bind the buffer first.
     *
     * @param data The data.
     * @return This instance for call chaining.
     */
    public VertexArray set(DoubleBuffer data)
    {
        return set(0, data);
    }
    
    private Object[] getFormatArray(int[] sizes, GL type)
    {
        int      n      = sizes.length;
        Object[] format = new Object[n << 1];
        for (int i = 0, index = 0; i < n; i++)
        {
            format[index++] = type;
            format[index++] = sizes[i];
        }
        return format;
    }
    
    private int getBytes(int type)
    {
        return switch (type)
                {
                    default -> Byte.BYTES;
                    case GL_UNSIGNED_SHORT, GL_SHORT -> Short.BYTES;
                    case GL_UNSIGNED_INT, GL_INT -> Integer.BYTES;
                    case GL_FLOAT -> Float.BYTES;
                    case GL_DOUBLE -> Double.BYTES;
                };
    }
}
