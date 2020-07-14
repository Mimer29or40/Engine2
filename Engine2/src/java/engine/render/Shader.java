package engine.render;

import engine.color.Color;
import engine.color.Colorc;
import engine.util.Logger;
import org.joml.*;
import org.lwjgl.system.MemoryStack;

import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;

import static engine.util.Util.getPath;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL32.GL_GEOMETRY_SHADER;

/**
 * A shader that can be used to render things.
 */
@SuppressWarnings("unused")
public class Shader
{
    private static final Logger LOGGER = new Logger();
    
    private final int id;
    
    private final HashMap<String, Integer> shaders  = new HashMap<>();
    private final HashMap<String, Integer> uniforms = new HashMap<>();
    
    private final Color color = new Color();
    
    /**
     * Creates a new shader.
     */
    public Shader()
    {
        this.id = glCreateProgram();
    }
    
    @Override
    public String toString()
    {
        return "Shader{" + "id=" + this.id + '}';
    }
    
    /**
     * Loads a Vertex Shader from a string and attaches it to the program.
     *
     * @param source The source string
     * @return This instance for call chaining.
     */
    public Shader loadVertex(String source)
    {
        Shader.LOGGER.finer("Loading Vertex Shader from String", source);
        
        return load("Vertex", GL_VERTEX_SHADER, source);
    }
    
    /**
     * Loads a Vertex Shader from a file and attaches it to the program.
     *
     * @param file The path to the file.
     * @return This instance for call chaining.
     */
    public Shader loadVertexFile(String file)
    {
        Shader.LOGGER.finer("Loading Vertex Shader from File", file);
        
        return loadFile("Vertex", GL_VERTEX_SHADER, file);
    }
    
    /**
     * Loads a Geometry Shader from a string and attaches it to the program.
     *
     * @param source The source string
     * @return This instance for call chaining.
     */
    public Shader loadGeometry(String source)
    {
        Shader.LOGGER.finer("Loading Geometry Shader from String", source);
        
        return load("Geometry", GL_GEOMETRY_SHADER, source);
    }
    
    /**
     * Loads a Geometry Shader from a file and attaches it to the program.
     *
     * @param file The path to the file.
     * @return This instance for call chaining.
     */
    public Shader loadGeometryFile(String file)
    {
        Shader.LOGGER.finer("Loading Geometry Shader from File", file);
        
        return loadFile("Geometry", GL_GEOMETRY_SHADER, file);
    }
    
    /**
     * Loads a Fragment Shader from a string and attaches it to the program.
     *
     * @param source The source string
     * @return This instance for call chaining.
     */
    public Shader loadFragment(String source)
    {
        Shader.LOGGER.finer("Loading Fragment Shader from String", source);
        
        return load("Fragment", GL_FRAGMENT_SHADER, source);
    }
    
    /**
     * Loads a Fragment Shader from a file and attaches it to the program.
     *
     * @param file The path to the file.
     * @return This instance for call chaining.
     */
    public Shader loadFragmentFile(String file)
    {
        Shader.LOGGER.finer("Loading Fragment Shader from File", file);
        
        return loadFile("Fragment", GL_FRAGMENT_SHADER, file);
    }
    
    /**
     * Validates the shader program.
     *
     * @return This instance for call chaining.
     */
    public Shader validate()
    {
        Shader.LOGGER.finer("Validating Shader Program", this.id);
        
        glLinkProgram(this.id);
        if (glGetProgrami(this.id, GL_LINK_STATUS) != GL_TRUE) throw new RuntimeException("Link failure: \n" + glGetProgramInfoLog(this.id));
        
        glValidateProgram(this.id);
        if (glGetProgrami(this.id, GL_VALIDATE_STATUS) != GL_TRUE) throw new RuntimeException("Validation failure: \n" + glGetProgramInfoLog(this.id));
        
        return this;
    }
    
    /**
     * Bind the shader for rendering.
     *
     * @return This instance for call chaining.
     */
    public Shader bind()
    {
        Shader.LOGGER.finest("Binding Shader Program", this.id);
        
        glUseProgram(this.id);
        return this;
    }
    
    /**
     * Unbinds the shader from rendering.
     *
     * @return This instance for call chaining.
     */
    public Shader unbind()
    {
        Shader.LOGGER.finest("Unbinding Shader Program", this.id);
        
        glUseProgram(0);
        return this;
    }
    
    /**
     * Deletes the shader from memory.
     *
     * @return This instance for call chaining.
     */
    public Shader delete()
    {
        Shader.LOGGER.finest("Deleting Shader Program", this.id);
        
        for (int shader : this.shaders.values()) glDetachShader(this.id, shader);
        glDeleteProgram(this.id);
        return this;
    }
    
    /**
     * Sets a bool uniform in the shader.
     *
     * @param name  The uniform name.
     * @param value The value.
     */
    public void setUniform(final String name, boolean value)
    {
        Shader.LOGGER.finest("Setting bool Uniform: %s=%s", name, value);
        
        glUniform1i(getUniform(name), value ? 1 : 0);
    }
    
    /**
     * Sets an int uniform in the shader.
     *
     * @param name  The uniform name.
     * @param value The value.
     */
    public void setUniform(final String name, long value)
    {
        Shader.LOGGER.finest("Setting int Uniform: %s=%s", name, value);
        
        glUniform1i(getUniform(name), (int) value);
    }
    
    /**
     * Sets a float uniform in the shader.
     *
     * @param name  The uniform name.
     * @param value The value.
     */
    public void setUniform(final String name, double value)
    {
        Shader.LOGGER.finest("Setting float Uniform: %s=%s", name, value);
        
        glUniform1f(getUniform(name), (float) value);
    }
    
    /**
     * Sets a vec2 uniform in the shader.
     *
     * @param name The uniform name.
     * @param x    The x value.
     * @param y    The y value.
     */
    public void setVec2(final String name, boolean x, boolean y)
    {
        Shader.LOGGER.finest("Setting vec2 Uniform: %s=(%s, %s)", name, x, y);
        
        glUniform2i(getUniform(name), x ? 1 : 0, y ? 1 : 0);
    }
    
    /**
     * Sets a vec2 uniform in the shader.
     *
     * @param name The uniform name.
     * @param x    The x value.
     * @param y    The y value.
     */
    public void setVec2(final String name, long x, long y)
    {
        Shader.LOGGER.finest("Setting vec2 Uniform: %s=(%s, %s)", name, x, y);
        
        glUniform2i(getUniform(name), (int) x, (int) y);
    }
    
    /**
     * Sets a vec2 uniform in the shader.
     *
     * @param name The uniform name.
     * @param x    The x value.
     * @param y    The y value.
     */
    public void setVec2(final String name, double x, double y)
    {
        Shader.LOGGER.finest("Setting vec2 Uniform: %s=(%s, %s)", name, x, y);
        
        glUniform2f(getUniform(name), (float) x, (float) y);
    }
    
    /**
     * Sets a vec2 uniform in the shader.
     *
     * @param name The uniform name.
     * @param vec  The value.
     */
    public void setVec2(final String name, Vector2ic vec)
    {
        setVec2(name, vec.x(), vec.y());
    }
    
    /**
     * Sets a vec2 uniform in the shader.
     *
     * @param name The uniform name.
     * @param vec  The value.
     */
    public void setVec2(final String name, Vector2fc vec)
    {
        setVec2(name, vec.x(), vec.y());
    }
    
    /**
     * Sets a vec2 uniform in the shader.
     *
     * @param name The uniform name.
     * @param vec  The value.
     */
    public void setVec2(final String name, Vector2dc vec)
    {
        setVec2(name, vec.x(), vec.y());
    }
    
    /**
     * Sets a vec3 uniform in the shader.
     *
     * @param name The uniform name.
     * @param x    The x value.
     * @param y    The y value.
     * @param z    The z value.
     */
    public void setVec3(final String name, boolean x, boolean y, boolean z)
    {
        Shader.LOGGER.finest("Setting vec3 Uniform: %s=(%s, %s, %s)", name, x, y, z);
        
        glUniform3i(getUniform(name), x ? 1 : 0, y ? 1 : 0, z ? 1 : 0);
    }
    
    /**
     * Sets a vec3 uniform in the shader.
     *
     * @param name The uniform name.
     * @param x    The x value.
     * @param y    The y value.
     * @param z    The z value.
     */
    public void setVec3(final String name, long x, long y, long z)
    {
        Shader.LOGGER.finest("Setting vec3 Uniform: %s=(%s, %s, %s)", name, x, y, z);
        
        glUniform3i(getUniform(name), (int) x, (int) y, (int) z);
    }
    
    /**
     * Sets a vec3 uniform in the shader.
     *
     * @param name The uniform name.
     * @param x    The x value.
     * @param y    The y value.
     * @param z    The z value.
     */
    public void setVec3(final String name, double x, double y, double z)
    {
        Shader.LOGGER.finest("Setting vec3 Uniform: %s=(%s, %s, %s)", name, x, y, z);
        
        glUniform3f(getUniform(name), (float) x, (float) y, (float) z);
    }
    
    /**
     * Sets a vec3 uniform in the shader.
     *
     * @param name The uniform name.
     * @param vec  The value.
     */
    public void setVec3(final String name, Vector3ic vec)
    {
        setVec3(name, vec.x(), vec.y(), vec.z());
    }
    
    /**
     * Sets a vec3 uniform in the shader.
     *
     * @param name The uniform name.
     * @param vec  The value.
     */
    public void setVec3(final String name, Vector3fc vec)
    {
        setVec3(name, vec.x(), vec.y(), vec.z());
    }
    
    /**
     * Sets a vec3 uniform in the shader.
     *
     * @param name The uniform name.
     * @param vec  The value.
     */
    public void setVec3(final String name, Vector3dc vec)
    {
        setVec3(name, vec.x(), vec.y(), vec.z());
    }
    
    /**
     * Sets a vec4 uniform in the shader.
     *
     * @param name The uniform name.
     * @param x    The x value.
     * @param y    The y value.
     * @param z    The z value.
     * @param w    The w value.
     */
    public void setVec4(final String name, boolean x, boolean y, boolean z, boolean w)
    {
        Shader.LOGGER.finest("Setting vec3 Uniform: %s=(%s, %s, %s, %s)", name, x, y, z, w);
        
        glUniform4i(getUniform(name), x ? 1 : 0, y ? 1 : 0, z ? 1 : 0, w ? 1 : 0);
    }
    
    /**
     * Sets a vec4 uniform in the shader.
     *
     * @param name The uniform name.
     * @param x    The x value.
     * @param y    The y value.
     * @param z    The z value.
     * @param w    The w value.
     */
    public void setVec4(final String name, long x, long y, long z, long w)
    {
        Shader.LOGGER.finest("Setting vec3 Uniform: %s=(%s, %s, %s, %s)", name, x, y, z, w);
        
        glUniform4i(getUniform(name), (int) x, (int) y, (int) z, (int) w);
    }
    
    /**
     * Sets a vec4 uniform in the shader.
     *
     * @param name The uniform name.
     * @param x    The x value.
     * @param y    The y value.
     * @param z    The z value.
     * @param w    The w value.
     */
    public void setVec4(final String name, double x, double y, double z, double w)
    {
        Shader.LOGGER.finest("Setting vec3 Uniform: %s=(%s, %s, %s, %s)", name, x, y, z, w);
        
        glUniform4f(getUniform(name), (float) x, (float) y, (float) z, (float) w);
    }
    
    /**
     * Sets a vec4 uniform in the shader.
     *
     * @param name The uniform name.
     * @param vec  The value.
     */
    public void setVec4(final String name, Vector4ic vec)
    {
        setVec4(name, vec.x(), vec.y(), vec.z(), vec.w());
    }
    
    /**
     * Sets a vec4 uniform in the shader.
     *
     * @param name The uniform name.
     * @param vec  The value.
     */
    public void setVec4(final String name, Vector4fc vec)
    {
        setVec4(name, vec.x(), vec.y(), vec.z(), vec.w());
    }
    
    /**
     * Sets a vec4 uniform in the shader.
     *
     * @param name The uniform name.
     * @param vec  The value.
     */
    public void setVec4(final String name, Vector4dc vec)
    {
        setVec4(name, vec.x(), vec.y(), vec.z(), vec.w());
    }
    
    /**
     * Sets a mat2 uniform in the shader.
     *
     * @param name The uniform name.
     * @param mat  The matrix value.
     */
    public void setMat2(final String name, Matrix2fc mat)
    {
        Shader.LOGGER.finest("Setting mat2 Uniform: %s=%s", name, mat);
        
        try (MemoryStack stack = MemoryStack.stackPush())
        {
            glUniformMatrix2fv(getUniform(name), false, mat.get(stack.mallocFloat(4)));
        }
    }
    
    /**
     * Sets a mat3 uniform in the shader.
     *
     * @param name The uniform name.
     * @param mat  The matrix value.
     */
    public void setMat3(final String name, Matrix3fc mat)
    {
        Shader.LOGGER.finest("Setting mat3 Uniform: %s=%s", name, mat);
        
        try (MemoryStack stack = MemoryStack.stackPush())
        {
            glUniformMatrix3fv(getUniform(name), false, mat.get(stack.mallocFloat(9)));
        }
    }
    
    /**
     * Sets a mat4 uniform in the shader.
     *
     * @param name The uniform name.
     * @param mat  The matrix value.
     */
    public void setMat4(final String name, Matrix4fc mat)
    {
        Shader.LOGGER.finest("Setting mat4 Uniform: %s=%s", name, mat);
        
        try (MemoryStack stack = MemoryStack.stackPush())
        {
            glUniformMatrix4fv(getUniform(name), false, mat.get(stack.mallocFloat(16)));
        }
    }
    
    /**
     * Sets a vec4 uniform that represents a color in the shader.
     *
     * @param name  The uniform name.
     * @param color The color value.
     */
    public void setColor(final String name, Colorc color)
    {
        Shader.LOGGER.finest("Setting Color (vec4) Uniform: %s=%s", name, color);
        
        glUniform4f(getUniform(name), color.rf(), color.gf(), color.bf(), color.af());
    }
    
    /**
     * Sets a vec4 uniform that represents a color in the shader.
     *
     * @param name The uniform name.
     * @param r    The red value.
     * @param g    The green value.
     * @param b    The blue value.
     * @param a    The alpha value.
     */
    public void setColor(final String name, Number r, Number g, Number b, Number a)
    {
        setColor(name, this.color.set(r, g, b, a));
    }
    
    /**
     * Sets a vec4 uniform that represents a color in the shader.
     *
     * @param name The uniform name.
     * @param r    The red value.
     * @param g    The green value.
     * @param b    The blue value.
     */
    public void setColor(final String name, Number r, Number g, Number b)
    {
        setColor(name, this.color.set(r, g, b, 255));
    }
    
    /**
     * Sets a vec4 uniform that represents a color in the shader.
     *
     * @param name The uniform name.
     * @param grey The red, green, and blue value.
     * @param a    The alpha value.
     */
    public void setColor(final String name, Number grey, Number a)
    {
        setColor(name, this.color.set(grey, grey, grey, a));
    }
    
    /**
     * Sets a vec4 uniform that represents a color in the shader.
     *
     * @param name The uniform name.
     * @param grey The red, green, and blue value.
     */
    public void setColor(final String name, Number grey)
    {
        setColor(name, this.color.set(grey, grey, grey, 255));
    }
    
    public void setTexture(final String name, int textureNum, Texture texture)
    {
        Shader.LOGGER.finest("Setting Texture Uniform: %s=%s(%s)", name, textureNum, texture);
    
        texture.bindTexture(textureNum);
        glUniform1i(getUniform(name), textureNum);
    }
    
    private int getUniform(String uniform)
    {
        return this.uniforms.computeIfAbsent(uniform, u -> glGetUniformLocation(this.id, u));
    }
    
    private Shader loadFile(String shaderName, int shaderType, String file)
    {
        try
        {
            return load(shaderName, shaderType, Files.readString(getPath(file)));
        }
        catch (IOException e)
        {
            throw new RuntimeException("Vertex Shader could not be read from file: \n" + file);
        }
    }
    
    private Shader load(String shaderName, int shaderType, String source)
    {
        int shader = glCreateShader(shaderType);
        glShaderSource(shader, source);
        glCompileShader(shader);
        
        int result = glGetShaderi(shader, GL_COMPILE_STATUS);
        if (result != GL_TRUE) throw new RuntimeException(shaderName + " Shader compile failure: " + glGetShaderInfoLog(shader));
        this.shaders.put(shaderName, shader);
        glAttachShader(this.id, shader);
        glDeleteShader(shader);
        return this;
    }
}
