package engine.render;

import engine.color.Colorc;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.*;
import org.lwjgl.system.MemoryStack;
import rutils.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.regex.Pattern;

import static org.lwjgl.opengl.GL20.*;
import static rutils.IOUtil.getPath;

/**
 * A shader that can be used to render things.
 */
@SuppressWarnings("unused")
public class GLShader
{
    private static final Logger LOGGER = new Logger();
    
    private final int id;
    
    private final HashMap<GL, Integer>     shaders  = new HashMap<>();
    private final HashMap<GL, String>      sources  = new HashMap<>();
    private final HashMap<String, Integer> uniforms = new HashMap<>();
    
    /**
     * Creates a new shader.
     */
    public GLShader()
    {
        this.id = glCreateProgram();
        
        GLShader.LOGGER.fine("%s: Generated", this);
    }
    
    @Override
    public @NotNull String toString()
    {
        return "GLShader{" + "id=" + this.id + '}';
    }
    
    public @NotNull GLShader loadFile(@NotNull String file)
    {
        return loadFile(determineShaderType(getPath(file)), file);
    }
    
    public @NotNull GLShader loadFile(@NotNull GL shaderType, @NotNull String file)
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
    
    public @NotNull GLShader load(@NotNull GL shaderType, @NotNull String source)
    {
        GLShader.LOGGER.fine("%s: Loading %s from String:%n%s", this, shaderType, source);
        
        return loadImpl(shaderType, source);
    }
    
    /**
     * Validates the shader program.
     *
     * @return This instance for call chaining.
     */
    public @NotNull GLShader validate()
    {
        glLinkProgram(this.id);
        if (glGetProgrami(this.id, GL_LINK_STATUS) != GL_TRUE) throw new RuntimeException(this + " Link failure: \n" + glGetProgramInfoLog(this.id));
        
        GLShader.LOGGER.fine("%s: Linked", this);
        
        glValidateProgram(this.id);
        if (glGetProgrami(this.id, GL_VALIDATE_STATUS) != GL_TRUE) throw new RuntimeException(this + " Validation failure: \n" + glGetProgramInfoLog(this.id));
        
        GLShader.LOGGER.fine("%s: Validated", this);
        
        return this;
    }
    
    /**
     * Gets the source that is associated with shader type provided.
     *
     * @param shaderType The type to query.
     * @return The source string.
     */
    public @Nullable String source(GL shaderType)
    {
        String source = this.sources.get(shaderType);
        if (source != null) return source;
        GLShader.LOGGER.warning("%s does not have a shader associated with %s", this, shaderType);
        return null;
    }
    
    /**
     * Bind the shader for rendering.
     *
     * @return This instance for call chaining.
     */
    public @NotNull GLShader bind()
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
    public @NotNull GLShader unbind()
    {
        GLShader.LOGGER.finer("%s: Unbinding", this);
        
        glUseProgram(0);
        
        return this;
    }
    
    /**
     * Deletes the shader from memory.
     */
    public void delete()
    {
        GLShader.LOGGER.fine("%s: Deleting", this);
        
        for (int shader : this.shaders.values()) glDetachShader(this.id, shader);
        glDeleteProgram(this.id);
        
        this.shaders.clear();
        this.sources.clear();
    }
    
    /**
     * Sets a bool uniform in the shader.
     *
     * @param name  The uniform name.
     * @param value The value.
     */
    public void setUniform(@NotNull final String name, boolean value)
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
    public void setUniform(@NotNull final String name, long value)
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
    public void setUniform(@NotNull final String name, double value)
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
    public void setUniform(@NotNull final String name, boolean x, boolean y)
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
    public void setUniform(@NotNull final String name, long x, long y)
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
    public void setUniform(@NotNull final String name, double x, double y)
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
    public void setUniform(@NotNull final String name, @NotNull final Vector2ic vec)
    {
        setUniform(name, vec.x(), vec.y());
    }
    
    /**
     * Sets a vec2 uniform in the shader.
     *
     * @param name The uniform name.
     * @param vec  The value.
     */
    public void setUniform(@NotNull final String name, @NotNull final Vector2fc vec)
    {
        setUniform(name, vec.x(), vec.y());
    }
    
    /**
     * Sets a vec2 uniform in the shader.
     *
     * @param name The uniform name.
     * @param vec  The value.
     */
    public void setUniform(@NotNull final String name, @NotNull final Vector2dc vec)
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
    public void setUniform(@NotNull final String name, boolean x, boolean y, boolean z)
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
    public void setUniform(@NotNull final String name, long x, long y, long z)
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
    public void setUniform(@NotNull final String name, double x, double y, double z)
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
    public void setUniform(@NotNull final String name, @NotNull final Vector3ic vec)
    {
        setUniform(name, vec.x(), vec.y(), vec.z());
    }
    
    /**
     * Sets a vec3 uniform in the shader.
     *
     * @param name The uniform name.
     * @param vec  The value.
     */
    public void setUniform(@NotNull final String name, @NotNull final Vector3fc vec)
    {
        setUniform(name, vec.x(), vec.y(), vec.z());
    }
    
    /**
     * Sets a vec3 uniform in the shader.
     *
     * @param name The uniform name.
     * @param vec  The value.
     */
    public void setUniform(@NotNull final String name, @NotNull final Vector3dc vec)
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
    public void setUniform(@NotNull final String name, boolean x, boolean y, boolean z, boolean w)
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
    public void setUniform(@NotNull final String name, long x, long y, long z, long w)
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
    public void setUniform(@NotNull final String name, double x, double y, double z, double w)
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
    public void setUniform(@NotNull final String name, @NotNull final Vector4ic vec)
    {
        setUniform(name, vec.x(), vec.y(), vec.z(), vec.w());
    }
    
    /**
     * Sets a vec4 uniform in the shader.
     *
     * @param name The uniform name.
     * @param vec  The value.
     */
    public void setUniform(@NotNull final String name, @NotNull final Vector4fc vec)
    {
        setUniform(name, vec.x(), vec.y(), vec.z(), vec.w());
    }
    
    /**
     * Sets a vec4 uniform in the shader.
     *
     * @param name The uniform name.
     * @param vec  The value.
     */
    public void setUniform(@NotNull final String name, @NotNull final Vector4dc vec)
    {
        setUniform(name, vec.x(), vec.y(), vec.z(), vec.w());
    }
    
    /**
     * Sets a mat2 uniform in the shader.
     *
     * @param name The uniform name.
     * @param mat  The matrix value.
     */
    public void setUniform(@NotNull final String name, @NotNull final Matrix2fc mat)
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
    public void setUniform(@NotNull final String name, @NotNull final Matrix2dc mat)
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
    public void setUniform(@NotNull final String name, @NotNull final Matrix3fc mat)
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
    public void setUniform(@NotNull final String name, @NotNull final Matrix3dc mat)
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
    public void setUniform(@NotNull final String name, @NotNull final Matrix4fc mat)
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
    public void setUniform(@NotNull final String name, @NotNull final Matrix4dc mat)
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
    public void setUniform(@NotNull final String name, @NotNull final Colorc color)
    {
        GLShader.LOGGER.finest("Setting Color (vec4) Uniform: %s=%s", name, color);
        
        glUniform4f(getUniform(name), color.rf(), color.gf(), color.bf(), color.af());
    }
    
    private int getUniform(String uniform)
    {
        int value = this.uniforms.computeIfAbsent(uniform, u -> glGetUniformLocation(this.id, u));
        if (value < 0) GLShader.LOGGER.warning("Could not find uniform '%s' in %s", uniform, this);
        return value;
    }
    
    private GLShader loadImpl(GL shaderType, String source)
    {
        int shader = glCreateShader(shaderType.ref());
        glShaderSource(shader, source);
        glCompileShader(shader);
        
        int result = glGetShaderi(shader, GL_COMPILE_STATUS);
        if (result != GL_TRUE) throw new RuntimeException(shaderType + " compile failure: " + glGetShaderInfoLog(shader));
        this.shaders.put(shaderType, shader);
        this.sources.put(shaderType, source);
        glAttachShader(this.id, shader);
        glDeleteShader(shader);
        return this;
    }
    
    private static GL determineShaderType(Path filePath)
    {
        String fileName = filePath.getFileName().toString();
        
        if (GLShader.vertPattern.matcher(fileName).matches())
        {
            return GL.VERTEX_SHADER;
        }
        else if (GLShader.tescPattern.matcher(fileName).matches())
        {
            return GL.TESS_CONTROL_SHADER;
        }
        else if (GLShader.tesePattern.matcher(fileName).matches())
        {
            return GL.TESS_EVALUATION_SHADER;
        }
        else if (GLShader.geomPattern.matcher(fileName).matches())
        {
            return GL.GEOMETRY_SHADER;
        }
        else if (GLShader.fragPattern.matcher(fileName).matches())
        {
            return GL.FRAGMENT_SHADER;
        }
        else if (GLShader.compPattern.matcher(fileName).matches())
        {
            return GL.COMPUTE_SHADER;
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
