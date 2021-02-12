package engine.render.gl;

import rutils.Logger;

import java.nio.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.*;

/**
 * A wrapper class for OpenGL's vertex arrays. This class adds helper functions that make it easy to send data to the buffers.
 */
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class GLVertexArray
{
    private static final Logger LOGGER = new Logger();
    
    private final int id;
    
    private final ArrayList<GLBuffer> vertexBuffers = new ArrayList<>();
    
    private GLBuffer indexBuffer = null;
    
    private final ArrayList<ArrayList<Attribute>> attributes = new ArrayList<>();
    
    private int vertexCount;
    
    /**
     * Creates a new GLVertexArray.
     */
    public GLVertexArray()
    {
        this.id = glGenVertexArrays();
        
        GLVertexArray.LOGGER.fine("Generating GLVertexArray{id=%s}", this.id);
    }
    
    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GLVertexArray that = (GLVertexArray) o;
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
        return "GLVertexArray{" + "id=" + this.id + ", vertex=" + printVertex() + ", vertexCount=" + this.vertexCount + (indexCount() > 0 ? ", indexCount=" + indexCount() : "") + '}';
    }
    
    public String printArrays()
    {
        return "[VBOs=" + this.vertexBuffers + ", EBO=" + this.indexBuffer + ']';
    }
    
    public String printVertex()
    {
        ArrayList<Attribute> vertex = new ArrayList<>();
        for (ArrayList<Attribute> bufferAttributes : this.attributes) vertex.addAll(bufferAttributes);
        return vertex.toString();
    }
    
    /**
     * Gets the GLBuffer that holds the indices bound to the GLVertexArray. Can be null.
     *
     * @return The index GLBuffer
     */
    public GLBuffer getIndexBuffer()
    {
        return this.indexBuffer;
    }
    
    /**
     * Gets the GLBuffer that has been bound to the GLVertexArray.
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
    public int attributeAmount()
    {
        int count = 0;
        for (ArrayList<Attribute> bufferAttributes : this.attributes)
        {
            count += bufferAttributes.size();
        }
        return count;
    }
    
    /**
     * @return The count of elements in a vertex.
     */
    public int attributeCount()
    {
        int count = 0;
        for (ArrayList<Attribute> bufferAttributes : this.attributes)
        {
            for (Attribute attribute : bufferAttributes)
            {
                count += attribute.count;
            }
        }
        return count;
    }
    
    /**
     * @return The size in bytes of a vertex.
     */
    public int attributeSize()
    {
        int size = 0;
        for (ArrayList<Attribute> bufferAttributes : this.attributes)
        {
            for (Attribute attribute : bufferAttributes)
            {
                size += attribute.size;
            }
        }
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
        return this.indexBuffer != null ? this.indexBuffer.dataSize() / Integer.BYTES : 0;
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
     * Bind the GLVertexArray for use.
     *
     * @return This instance for call chaining.
     */
    public GLVertexArray bind()
    {
        GLVertexArray.LOGGER.finer("%s: Binding", this);
        
        glBindVertexArray(this.id);
        
        if (this.indexBuffer != null) this.indexBuffer.bind();
        
        return this;
    }
    
    /**
     * Unbind the GLVertexArray from use.
     *
     * @return This instance for call chaining.
     */
    public GLVertexArray unbind()
    {
        GLVertexArray.LOGGER.finer("%s: Unbinding", this);
        
        glBindVertexArray(0);
        
        if (this.indexBuffer != null) this.indexBuffer.unbind();
        
        return this;
    }
    
    /**
     * Deletes the GLVertexArray and Buffers.
     *
     * @return This instance for call chaining.
     */
    public GLVertexArray delete()
    {
        GLVertexArray.LOGGER.fine("%s: Deleting", this);
        
        glDeleteVertexArrays(this.id);
        
        return reset();
    }
    
    /**
     * Resets the GLVertexArray by deleting all buffers and attributes.
     * <p>
     * Make sure to bind the vertex array first.
     *
     * @return This instance for call chaining.
     */
    public GLVertexArray reset()
    {
        GLVertexArray.LOGGER.fine("%s: Resetting", this);
        
        for (GLBuffer vbo : this.vertexBuffers) vbo.delete();
        this.vertexBuffers.clear();
        if (this.indexBuffer != null) this.indexBuffer.delete();
        
        int i = 0;
        for (ArrayList<Attribute> bufferAttributes : this.attributes)
        {
            for (Attribute attribute : bufferAttributes)
            {
                glDisableVertexAttribArray(i++);
            }
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
    public GLVertexArray resize()
    {
        GLVertexArray.LOGGER.finest("%s: Resizing", this);
        
        this.vertexCount = Integer.MAX_VALUE;
        for (int i = 0, n = this.vertexBuffers.size(); i < n; i++)
        {
            int bufferAttributesSize = 0;
            for (Attribute attribute : this.attributes.get(i)) bufferAttributesSize += attribute.size;
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
    public GLVertexArray draw(GLConst mode, int size)
    {
        if (this.indexBuffer != null)
        {
            GLVertexArray.LOGGER.finer("%s: Drawing Elements size=%s", this, size);
            
            if (indexCount() > 0) glDrawElements(mode.ref(), size, GLConst.UNSIGNED_INT.ref(), 0);
        }
        else
        {
            GLVertexArray.LOGGER.finer("%s: Drawing Array size=%s", this, size);
            
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
    public GLVertexArray draw(GLConst mode)
    {
        return draw(mode, this.indexBuffer != null ? indexCount() : this.vertexCount);
    }
    
    /**
     * Adds an element array buffer to the Vertex Array, if one is already present then is deletes and replaces it. The buffer object will be managed by the GLVertexArray object.
     * <p>
     * Make sure to bind the vertex array first.
     *
     * @param buffer The index array buffer
     * @return This instance for call chaining.
     */
    public GLVertexArray addEBO(GLBuffer buffer)
    {
        GLVertexArray.LOGGER.fine("%s: Adding EBO %s", this, buffer);
        
        if (this.indexBuffer != null) this.indexBuffer.delete();
        
        this.indexBuffer = buffer.unbind();
        
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
    public GLVertexArray addEBO(int[] data, GLConst usage)
    {
        return addEBO(new GLBuffer(GLConst.ELEMENT_ARRAY_BUFFER).usage(usage).bind().set(data));
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
    public GLVertexArray addEBO(IntBuffer data, GLConst usage)
    {
        return addEBO(new GLBuffer(GLConst.ELEMENT_ARRAY_BUFFER).usage(usage).bind().set(data));
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
    public GLVertexArray setEBO(GLConst usage, int... data)
    {
        GLVertexArray.LOGGER.finer("%s: Setting EBO date from int array with usage %s", this, usage);
        
        if (this.indexBuffer == null) this.indexBuffer = new GLBuffer(GLConst.ELEMENT_ARRAY_BUFFER);
        
        this.indexBuffer.usage(usage).bind().set(data).unbind();
        
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
    public GLVertexArray setEBO(GLConst usage, IntBuffer data)
    {
        GLVertexArray.LOGGER.finer("%s: Setting EBO date from IntBuffer with usage %s", this, usage);
        
        if (this.indexBuffer == null) this.indexBuffer = new GLBuffer(GLConst.ELEMENT_ARRAY_BUFFER);
        
        this.indexBuffer.usage(usage).bind().set(data).unbind();
        
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
    public GLVertexArray setEBO(int... data)
    {
        GLVertexArray.LOGGER.finer("%s: Setting EBO date from int array", this);
        
        if (this.indexBuffer == null) this.indexBuffer = new GLBuffer(GLConst.ELEMENT_ARRAY_BUFFER);
        
        this.indexBuffer.bind().set(data).unbind();
        
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
    public GLVertexArray setEBO(IntBuffer data)
    {
        GLVertexArray.LOGGER.finer("%s: Setting EBO date from IntBuffer", this);
        
        if (this.indexBuffer == null) this.indexBuffer = new GLBuffer(GLConst.ELEMENT_ARRAY_BUFFER);
        
        this.indexBuffer.bind().set(data).unbind();
        
        return this;
    }
    
    /**
     * Adds a buffer object with any number of attributes to the Vertex Array. The GLVertexArray object will manage the buffer.
     * <p>
     * Make sure to bind the vertex array first.
     *
     * @param buffer  The buffer object;
     * @param formats The type and size pairs for how the buffer is organized.
     * @return This instance for call chaining.
     */
    public GLVertexArray add(GLBuffer buffer, Object... formats)
    {
        int n = formats.length;
        if (n == 0) throw new RuntimeException("Invalid vertex format: Must have at least one type/size pair");
        if ((n & 1) == 1) throw new RuntimeException("Invalid vertex format: A type/size pair is missing a value");
        
        n >>= 1;
        
        ArrayList<Attribute> bufferAttributes = new ArrayList<>(n);
        
        int stride = 0;
        for (int i = 0, index; i < n; i++)
        {
            index = i << 1;
            if (!(formats[index] instanceof GLConst)) throw new RuntimeException("Invalid vertex format: Not recognized OpenGL type: " + formats[index]);
            Attribute attribute = getAttribute((GLConst) formats[index], (int) formats[index + 1]);
            bufferAttributes.add(attribute);
            stride += attribute.size;
        }
        
        GLVertexArray.LOGGER.finest("%s: Adding VBO %s of structure %s", this, buffer, bufferAttributes);
        
        this.vertexCount = Math.min(this.vertexCount > 0 ? this.vertexCount : Integer.MAX_VALUE, buffer.dataSize() / stride);
        
        buffer.bind();
        for (int i = 0, size = bufferAttributes.size(), attributeCount = attributeAmount(), offset = 0; i < size; i++)
        {
            Attribute attribute = bufferAttributes.get(i);
            glVertexAttribPointer(attributeCount, attribute.count, attribute.type.ref(), false, stride, offset);
            glEnableVertexAttribArray(attributeCount++);
            offset += attribute.size;
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
    public GLVertexArray add(int size, GLConst usage, Object... formats)
    {
        return add(new GLBuffer(GLConst.ARRAY_BUFFER).usage(usage).bind().set(size), formats);
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
    public GLVertexArray add(short[] data, GLConst usage, int... sizes)
    {
        return add(new GLBuffer(GLConst.ARRAY_BUFFER).usage(usage).bind().set(data), getFormatArray(sizes, GLConst.SHORT));
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
    public GLVertexArray add(int[] data, GLConst usage, int... sizes)
    {
        return add(new GLBuffer(GLConst.ARRAY_BUFFER).usage(usage).bind().set(data), getFormatArray(sizes, GLConst.INT));
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
    public GLVertexArray add(float[] data, GLConst usage, int... sizes)
    {
        return add(new GLBuffer(GLConst.ARRAY_BUFFER).usage(usage).bind().set(data), getFormatArray(sizes, GLConst.FLOAT));
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
    public GLVertexArray add(double[] data, GLConst usage, int... sizes)
    {
        return add(new GLBuffer(GLConst.ARRAY_BUFFER).usage(usage).bind().set(data), getFormatArray(sizes, GLConst.DOUBLE));
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
    public GLVertexArray add(ByteBuffer data, GLConst usage, Object... formats)
    {
        return add(new GLBuffer(GLConst.ARRAY_BUFFER).usage(usage).bind().set(data), formats);
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
    public GLVertexArray add(ShortBuffer data, GLConst usage, int... sizes)
    {
        return add(new GLBuffer(GLConst.ARRAY_BUFFER).usage(usage).bind().set(data), getFormatArray(sizes, GLConst.UNSIGNED_SHORT));
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
    public GLVertexArray add(IntBuffer data, GLConst usage, int... sizes)
    {
        return add(new GLBuffer(GLConst.ARRAY_BUFFER).usage(usage).bind().set(data), getFormatArray(sizes, GLConst.UNSIGNED_INT));
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
    public GLVertexArray add(FloatBuffer data, GLConst usage, int... sizes)
    {
        return add(new GLBuffer(GLConst.ARRAY_BUFFER).usage(usage).bind().set(data), getFormatArray(sizes, GLConst.FLOAT));
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
    public GLVertexArray add(DoubleBuffer data, GLConst usage, int... sizes)
    {
        return add(new GLBuffer(GLConst.ARRAY_BUFFER).usage(usage).bind().set(data), getFormatArray(sizes, GLConst.DOUBLE));
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
    public GLVertexArray set(int buffer, GLConst usage, short... data)
    {
        GLVertexArray.LOGGER.finer("%s: Setting Buffer '%s' data from short array with usage %s", this, buffer, usage);
        
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
    public GLVertexArray set(GLConst usage, short... data)
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
    public GLVertexArray set(int buffer, GLConst usage, int... data)
    {
        GLVertexArray.LOGGER.finer("%s: Setting Buffer '%s' data from int array with usage %s", this, buffer, usage);
        
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
    public GLVertexArray set(GLConst usage, int... data)
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
    public GLVertexArray set(int buffer, GLConst usage, long... data)
    {
        GLVertexArray.LOGGER.finer("%s: Setting Buffer '%s' data from long array with usage %s", this, buffer, usage);
        
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
    public GLVertexArray set(GLConst usage, long... data)
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
    public GLVertexArray set(int buffer, GLConst usage, float... data)
    {
        GLVertexArray.LOGGER.finer("%s: Setting Buffer '%s' data from float array with usage %s", this, buffer, usage);
        
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
    public GLVertexArray set(GLConst usage, float... data)
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
    public GLVertexArray set(int buffer, GLConst usage, double... data)
    {
        GLVertexArray.LOGGER.finer("%s: Setting Buffer '%s' data from double array with usage %s", this, buffer, usage);
        
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
    public GLVertexArray set(GLConst usage, double... data)
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
    public GLVertexArray set(int buffer, GLConst usage, ByteBuffer data)
    {
        GLVertexArray.LOGGER.finer("%s: Setting Buffer '%s' data from ByteBuffer with usage %s", this, buffer, usage);
        
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
    public GLVertexArray set(GLConst usage, ByteBuffer data)
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
    public GLVertexArray set(int buffer, GLConst usage, ShortBuffer data)
    {
        GLVertexArray.LOGGER.finer("%s: Setting Buffer '%s' data from ShortBuffer with usage %s", this, buffer, usage);
        
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
    public GLVertexArray set(GLConst usage, ShortBuffer data)
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
    public GLVertexArray set(int buffer, GLConst usage, IntBuffer data)
    {
        GLVertexArray.LOGGER.finer("%s: Setting Buffer '%s' data from IntBuffer with usage %s", this, buffer, usage);
        
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
    public GLVertexArray set(GLConst usage, IntBuffer data)
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
    public GLVertexArray set(int buffer, GLConst usage, LongBuffer data)
    {
        GLVertexArray.LOGGER.finer("%s: Setting Buffer '%s' data from LongBuffer with usage %s", this, buffer, usage);
        
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
    public GLVertexArray set(GLConst usage, LongBuffer data)
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
    public GLVertexArray set(int buffer, GLConst usage, FloatBuffer data)
    {
        GLVertexArray.LOGGER.finer("%s: Setting Buffer '%s' data from FloatBuffer with usage %s", this, buffer, usage);
        
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
    public GLVertexArray set(GLConst usage, FloatBuffer data)
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
    public GLVertexArray set(int buffer, GLConst usage, DoubleBuffer data)
    {
        GLVertexArray.LOGGER.finer("%s: Setting Buffer '%s' data from DoubleBuffer with usage %s", this, buffer, usage);
        
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
    public GLVertexArray set(GLConst usage, DoubleBuffer data)
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
    public GLVertexArray set(int buffer, short... data)
    {
        GLVertexArray.LOGGER.finer("%s: Setting Buffer '%s' data from short array", this, buffer);
        
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
    public GLVertexArray set(short... data)
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
    public GLVertexArray set(int buffer, int... data)
    {
        GLVertexArray.LOGGER.finer("%s: Setting Buffer '%s' data from int array", this, buffer);
        
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
    public GLVertexArray set(int... data)
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
    public GLVertexArray set(int buffer, long... data)
    {
        GLVertexArray.LOGGER.finer("%s: Setting Buffer '%s' data from long array", this, buffer);
        
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
    public GLVertexArray set(long... data)
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
    public GLVertexArray set(int buffer, float... data)
    {
        GLVertexArray.LOGGER.finer("%s: Setting Buffer '%s' data from float array", this, buffer);
        
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
    public GLVertexArray set(float... data)
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
    public GLVertexArray set(int buffer, double... data)
    {
        GLVertexArray.LOGGER.finer("%s: Setting Buffer '%s' data from double array", this, buffer);
        
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
    public GLVertexArray set(double... data)
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
    public GLVertexArray set(int buffer, ByteBuffer data)
    {
        GLVertexArray.LOGGER.finer("%s: Setting Buffer '%s' data from ByteBuffer", this, buffer);
        
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
    public GLVertexArray set(ByteBuffer data)
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
    public GLVertexArray set(int buffer, ShortBuffer data)
    {
        GLVertexArray.LOGGER.finer("%s: Setting Buffer '%s' data from ShortBuffer", this, buffer);
        
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
    public GLVertexArray set(ShortBuffer data)
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
    public GLVertexArray set(int buffer, IntBuffer data)
    {
        GLVertexArray.LOGGER.finer("%s: Setting Buffer '%s' data from IntBuffer", this, buffer);
        
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
    public GLVertexArray set(IntBuffer data)
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
    public GLVertexArray set(int buffer, LongBuffer data)
    {
        GLVertexArray.LOGGER.finer("%s: Setting Buffer '%s' data from LongBuffer", this, buffer);
        
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
    public GLVertexArray set(LongBuffer data)
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
    public GLVertexArray set(int buffer, FloatBuffer data)
    {
        GLVertexArray.LOGGER.finer("%s: Setting Buffer '%s' data from FloatBuffer", this, buffer);
        
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
    public GLVertexArray set(FloatBuffer data)
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
    public GLVertexArray set(int buffer, DoubleBuffer data)
    {
        GLVertexArray.LOGGER.finer("%s: Setting Buffer '%s' data from DoubleBuffer", this, buffer);
        
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
    public GLVertexArray set(DoubleBuffer data)
    {
        return set(0, data);
    }
    
    private static Object[] getFormatArray(int[] sizes, GLConst type)
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
    
    private static int getBytes(GLConst type)
    {
        return switch (type)
                {
                    default -> Byte.BYTES;
                    case UNSIGNED_SHORT, SHORT -> Short.BYTES;
                    case UNSIGNED_INT, INT -> Integer.BYTES;
                    case FLOAT -> Float.BYTES;
                    case DOUBLE -> Double.BYTES;
                };
    }
    
    private static final HashMap<Integer, Attribute> ATTRIBUTE_CACHE = new HashMap<>();
    
    private static Attribute getAttribute(GLConst type, int count)
    {
        int hash = Objects.hash(type, count);
        
        if (!GLVertexArray.ATTRIBUTE_CACHE.containsKey(hash))
        {
            Attribute attribute = new Attribute(type, count);
            
            GLVertexArray.ATTRIBUTE_CACHE.put(hash, attribute);
        }
        
        return GLVertexArray.ATTRIBUTE_CACHE.get(hash);
    }
    
    public static class Attribute
    {
        public final GLConst type;
        public final int     count;
        public final int     bytes;
        public final int     size;
        
        private Attribute(GLConst type, int count)
        {
            this.type  = type;
            this.count = count;
            this.bytes = getBytes(type);
            this.size  = this.count * this.bytes;
        }
        
        @Override
        public boolean equals(Object o)
        {
            if (this == o) return true;
            if (!(o instanceof Attribute)) return false;
            Attribute attribute = (Attribute) o;
            return this.type == attribute.type && this.count == attribute.count;
        }
        
        @Override
        public int hashCode()
        {
            return Objects.hash(this.type, this.count);
        }
        
        @Override
        public String toString()
        {
            return this.type + "x" + this.count;
        }
    }
}
