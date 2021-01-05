package engine.render.gl;

import engine.color.Color;
import engine.color.Colorc;
import engine.render.Texture;
import engine.util.Logger;
import org.joml.*;
import org.lwjgl.system.MemoryStack;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.regex.Pattern;

import static engine.util.Util.getPath;
import static org.lwjgl.opengl.GL20.*;

/**
 * A shader that can be used to render things.
 */
@SuppressWarnings("unused")
public class GLShader
{
    private static final Logger LOGGER = new Logger();
    
    private final int id;
    
    private final HashMap<GLConst, Integer> shaders  = new HashMap<>();
    private final HashMap<String, Integer>  uniforms = new HashMap<>();
    
    private final Color color = new Color();
    
    /**
     * Creates a new shader.
     */
    public GLShader()
    {
        this.id = glCreateProgram();
        
        GLShader.LOGGER.fine("Generating GLShader{id=%s}", this.id);
    }
    
    @Override
    public String toString()
    {
        return "GLShader{" + "id=" + this.id + '}';
    }
    
    public GLShader loadFile(String file)
    {
        return loadFile(determineShaderType(getPath(file)), file);
    }
    
    public GLShader loadFile(GLConst shaderType, String file)
    {
        try
        {
            GLShader.LOGGER.fine("%s: Loading %s from file '%s'", this, shaderType, file);
            
            Path filePath = getPath(file);
            
            return loadImpl(shaderType, Files.readString(filePath));
        }
        catch (IOException e)
        {
            throw new RuntimeException(shaderType + " could not be read from file: \n" + file);
        }
    }
    
    public GLShader load(GLConst shaderType, String source)
    {
        GLShader.LOGGER.fine("%s: Loading %s from String:%n%s", this, shaderType, source);
        
        return loadImpl(shaderType, source);
    }
    
    /**
     * Validates the shader program.
     *
     * @return This instance for call chaining.
     */
    public GLShader validate()
    {
        GLShader.LOGGER.fine("%s: Validating", this);
        
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
    public GLShader bind()
    {
        GLShader.LOGGER.finer("%s: Binding", this);
        
        glUseProgram(this.id);
        return this;
    }
    
    /**
     * Unbinds the shader from rendering.
     *
     * @return This instance for call chaining.
     */
    public GLShader unbind()
    {
        GLShader.LOGGER.finer("%s: Unbinding", this);
        
        glUseProgram(0);
        return this;
    }
    
    /**
     * Deletes the shader from memory.
     *
     * @return This instance for call chaining.
     */
    public GLShader delete()
    {
        GLShader.LOGGER.fine("%s: Deleting", this);
        
        for (int shader : this.shaders.values()) glDetachShader(this.id, shader);
        glDeleteProgram(this.id);
        
        this.shaders.clear();
        
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
        GLShader.LOGGER.finest("%s: Setting bool Uniform: %s=%s", this, name, value);
        
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
        GLShader.LOGGER.finest("%s: Setting int Uniform: %s=%s", this, name, value);
        
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
        GLShader.LOGGER.finest("%s: Setting float Uniform: %s=%s", this, name, value);
        
        glUniform1f(getUniform(name), (float) value);
    }
    
    /**
     * Sets a vec2 uniform in the shader.
     *
     * @param name The uniform name.
     * @param x    The x value.
     * @param y    The y value.
     */
    public void setUniform(final String name, boolean x, boolean y)
    {
        GLShader.LOGGER.finest("%s: Setting vec2 Uniform: %s=(%s, %s)", this, name, x, y);
        
        glUniform2i(getUniform(name), x ? 1 : 0, y ? 1 : 0);
    }
    
    /**
     * Sets a vec2 uniform in the shader.
     *
     * @param name The uniform name.
     * @param x    The x value.
     * @param y    The y value.
     */
    public void setUniform(final String name, long x, long y)
    {
        GLShader.LOGGER.finest("%s: Setting vec2 Uniform: %s=(%s, %s)", this, name, x, y);
        
        glUniform2i(getUniform(name), (int) x, (int) y);
    }
    
    /**
     * Sets a vec2 uniform in the shader.
     *
     * @param name The uniform name.
     * @param x    The x value.
     * @param y    The y value.
     */
    public void setUniform(final String name, double x, double y)
    {
        GLShader.LOGGER.finest("%s: Setting vec2 Uniform: %s=(%s, %s)", this, name, x, y);
        
        glUniform2f(getUniform(name), (float) x, (float) y);
    }
    
    /**
     * Sets a vec2 uniform in the shader.
     *
     * @param name The uniform name.
     * @param vec  The value.
     */
    public void setUniform(final String name, Vector2ic vec)
    {
        setUniform(name, vec.x(), vec.y());
    }
    
    /**
     * Sets a vec2 uniform in the shader.
     *
     * @param name The uniform name.
     * @param vec  The value.
     */
    public void setUniform(final String name, Vector2fc vec)
    {
        setUniform(name, vec.x(), vec.y());
    }
    
    /**
     * Sets a vec2 uniform in the shader.
     *
     * @param name The uniform name.
     * @param vec  The value.
     */
    public void setUniform(final String name, Vector2dc vec)
    {
        setUniform(name, vec.x(), vec.y());
    }
    
    /**
     * Sets a vec3 uniform in the shader.
     *
     * @param name The uniform name.
     * @param x    The x value.
     * @param y    The y value.
     * @param z    The z value.
     */
    public void setUniform(final String name, boolean x, boolean y, boolean z)
    {
        GLShader.LOGGER.finest("%s: Setting vec3 Uniform: %s=(%s, %s, %s)", this, name, x, y, z);
        
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
    public void setUniform(final String name, long x, long y, long z)
    {
        GLShader.LOGGER.finest("%s: Setting vec3 Uniform: %s=(%s, %s, %s)", this, name, x, y, z);
        
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
    public void setUniform(final String name, double x, double y, double z)
    {
        GLShader.LOGGER.finest("%s: Setting vec3 Uniform: %s=(%s, %s, %s)", this, name, x, y, z);
        
        glUniform3f(getUniform(name), (float) x, (float) y, (float) z);
    }
    
    /**
     * Sets a vec3 uniform in the shader.
     *
     * @param name The uniform name.
     * @param vec  The value.
     */
    public void setUniform(final String name, Vector3ic vec)
    {
        setUniform(name, vec.x(), vec.y(), vec.z());
    }
    
    /**
     * Sets a vec3 uniform in the shader.
     *
     * @param name The uniform name.
     * @param vec  The value.
     */
    public void setUniform(final String name, Vector3fc vec)
    {
        setUniform(name, vec.x(), vec.y(), vec.z());
    }
    
    /**
     * Sets a vec3 uniform in the shader.
     *
     * @param name The uniform name.
     * @param vec  The value.
     */
    public void setUniform(final String name, Vector3dc vec)
    {
        setUniform(name, vec.x(), vec.y(), vec.z());
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
    public void setUniform(final String name, boolean x, boolean y, boolean z, boolean w)
    {
        GLShader.LOGGER.finest("%s: Setting vec3 Uniform: %s=(%s, %s, %s, %s)", this, name, x, y, z, w);
        
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
    public void setUniform(final String name, long x, long y, long z, long w)
    {
        GLShader.LOGGER.finest("%s: Setting vec3 Uniform: %s=(%s, %s, %s, %s)", this, name, x, y, z, w);
        
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
    public void setUniform(final String name, double x, double y, double z, double w)
    {
        GLShader.LOGGER.finest("%s: Setting vec3 Uniform: %s=(%s, %s, %s, %s)", this, name, x, y, z, w);
        
        glUniform4f(getUniform(name), (float) x, (float) y, (float) z, (float) w);
    }
    
    /**
     * Sets a vec4 uniform in the shader.
     *
     * @param name The uniform name.
     * @param vec  The value.
     */
    public void setUniform(final String name, Vector4ic vec)
    {
        setUniform(name, vec.x(), vec.y(), vec.z(), vec.w());
    }
    
    /**
     * Sets a vec4 uniform in the shader.
     *
     * @param name The uniform name.
     * @param vec  The value.
     */
    public void setUniform(final String name, Vector4fc vec)
    {
        setUniform(name, vec.x(), vec.y(), vec.z(), vec.w());
    }
    
    /**
     * Sets a vec4 uniform in the shader.
     *
     * @param name The uniform name.
     * @param vec  The value.
     */
    public void setUniform(final String name, Vector4dc vec)
    {
        setUniform(name, vec.x(), vec.y(), vec.z(), vec.w());
    }
    
    /**
     * Sets a mat2 uniform in the shader.
     *
     * @param name The uniform name.
     * @param mat  The matrix value.
     */
    public void setUniform(final String name, Matrix2fc mat)
    {
        GLShader.LOGGER.finest("%s: Setting mat2 Uniform: %s=%n%s", this, name, mat);
        
        try (MemoryStack stack = MemoryStack.stackPush())
        {
            glUniformMatrix2fv(getUniform(name), false, mat.get(stack.mallocFloat(4)));
        }
    }
    
    /**
     * Sets a mat2 uniform in the shader.
     *
     * @param name The uniform name.
     * @param mat  The matrix value.
     */
    public void setUniform(final String name, Matrix2dc mat)
    {
        GLShader.LOGGER.finest("%s: Setting mat2 Uniform: %s=%n%s", this, name, mat);
        
        float[] arr = {
                (float) mat.m00(),
                (float) mat.m01(),
                (float) mat.m10(),
                (float) mat.m11()
        };
        glUniformMatrix2fv(getUniform(name), false, arr);
    }
    
    /**
     * Sets a mat3 uniform in the shader.
     *
     * @param name The uniform name.
     * @param mat  The matrix value.
     */
    public void setUniform(final String name, Matrix3fc mat)
    {
        GLShader.LOGGER.finest("%s: Setting mat3 Uniform: %s=%n%s", this, name, mat);
        
        try (MemoryStack stack = MemoryStack.stackPush())
        {
            glUniformMatrix3fv(getUniform(name), false, mat.get(stack.mallocFloat(9)));
        }
    }
    
    /**
     * Sets a mat3 uniform in the shader.
     *
     * @param name The uniform name.
     * @param mat  The matrix value.
     */
    public void setUniform(final String name, Matrix3dc mat)
    {
        GLShader.LOGGER.finest("%s: Setting mat3 Uniform: %s=%n%s", this, name, mat);
        
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
    public void setUniform(final String name, Matrix4fc mat)
    {
        GLShader.LOGGER.finest("%s: Setting mat4 Uniform: %s=%n%s", this, name, mat);
        
        try (MemoryStack stack = MemoryStack.stackPush())
        {
            glUniformMatrix4fv(getUniform(name), false, mat.get(stack.mallocFloat(16)));
        }
    }
    
    /**
     * Sets a mat4 uniform in the shader.
     *
     * @param name The uniform name.
     * @param mat  The matrix value.
     */
    public void setUniform(final String name, Matrix4dc mat)
    {
        GLShader.LOGGER.finest("%s: Setting mat4 Uniform: %s=%n%s", this, name, mat);
        
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
    public void setUniform(final String name, Colorc color)
    {
        GLShader.LOGGER.finest("Setting Color (vec4) Uniform: %s=%s", name, color);
        
        glUniform4f(getUniform(name), color.rf(), color.gf(), color.bf(), color.af());
    }
    
    // /**
    //  * Sets a vec4 uniform that represents a color in the shader.
    //  *
    //  * @param name The uniform name.
    //  * @param r    The red value.
    //  * @param g    The green value.
    //  * @param b    The blue value.
    //  * @param a    The alpha value.
    //  */
    // public void setUniform(final String name, Number r, Number g, Number b, Number a)
    // {
    //     setUniform(name, this.color.set(r, g, b, a));
    // }
    //
    // /**
    //  * Sets a vec4 uniform that represents a color in the shader.
    //  *
    //  * @param name The uniform name.
    //  * @param r    The red value.
    //  * @param g    The green value.
    //  * @param b    The blue value.
    //  */
    // public void setUniform(final String name, Number r, Number g, Number b)
    // {
    //     setUniform(name, this.color.set(r, g, b, 255));
    // }
    //
    // /**
    //  * Sets a vec4 uniform that represents a color in the shader.
    //  *
    //  * @param name The uniform name.
    //  * @param grey The red, green, and blue value.
    //  * @param a    The alpha value.
    //  */
    // public void setUniform(final String name, Number grey, Number a)
    // {
    //     setUniform(name, this.color.set(grey, grey, grey, a));
    // }
    //
    // /**
    //  * Sets a vec4 uniform that represents a color in the shader.
    //  *
    //  * @param name The uniform name.
    //  * @param grey The red, green, and blue value.
    //  */
    // public void setUniform(final String name, Number grey)
    // {
    //     setUniform(name, this.color.set(grey, grey, grey, 255));
    // }
    
    public void setTexture(final String name, int textureNum, Texture texture)
    {
        GLShader.LOGGER.finest("Setting Texture Uniform: %s=%s(%s)", name, textureNum, texture);
        
        texture.bindTexture(textureNum);
        glUniform1i(getUniform(name), textureNum);
    }
    
    private int getUniform(String uniform)
    {
        return this.uniforms.computeIfAbsent(uniform, u -> glGetUniformLocation(this.id, u));
    }
    
    private GLShader loadImpl(GLConst shaderType, String source)
    {
        int shader = glCreateShader(shaderType.ref());
        glShaderSource(shader, source);
        glCompileShader(shader);
        
        int result = glGetShaderi(shader, GL_COMPILE_STATUS);
        if (result != GL_TRUE) throw new RuntimeException(shaderType + " compile failure: " + glGetShaderInfoLog(shader));
        this.shaders.put(shaderType, shader);
        glAttachShader(this.id, shader);
        glDeleteShader(shader);
        return this;
    }
    
    private static GLConst determineShaderType(Path filePath)
    {
        String fileName = filePath.getFileName().toString();
        
        if (GLShader.vertPattern.matcher(fileName).matches())
        {
            return GLConst.VERTEX_SHADER;
        }
        else if (GLShader.tescPattern.matcher(fileName).matches())
        {
            return GLConst.TESS_CONTROL_SHADER;
        }
        else if (GLShader.tesePattern.matcher(fileName).matches())
        {
            return GLConst.TESS_EVALUATION_SHADER;
        }
        else if (GLShader.geomPattern.matcher(fileName).matches())
        {
            return GLConst.GEOMETRY_SHADER;
        }
        else if (GLShader.fragPattern.matcher(fileName).matches())
        {
            return GLConst.FRAGMENT_SHADER;
        }
        else if (GLShader.compPattern.matcher(fileName).matches())
        {
            return GLConst.COMPUTE_SHADER;
        }
        throw new RuntimeException(String.format("Invalid GLShader Extension '%s'. Please specify shader type.", filePath.toString()));
    }
    
    private static final Pattern vertPattern = Pattern.compile(".*\\.(?:vert|vs)");
    private static final Pattern tescPattern = Pattern.compile(".*\\.(?:tesc)");
    private static final Pattern tesePattern = Pattern.compile(".*\\.(?:tese)");
    private static final Pattern geomPattern = Pattern.compile(".*\\.(?:geom|gs)");
    private static final Pattern fragPattern = Pattern.compile(".*\\.(?:frag|fs)");
    private static final Pattern compPattern = Pattern.compile(".*\\.(?:comp|cs)");
}
