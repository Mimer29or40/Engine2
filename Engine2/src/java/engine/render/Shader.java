package engine.render;

import engine.color.Color;
import engine.color.Colorc;
import org.joml.*;
import org.lwjgl.BufferUtils;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.util.HashMap;

import static engine.util.Util.getPath;
import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL32.GL_GEOMETRY_SHADER;

public class Shader
{
    private final int id;
    
    private final HashMap<String, Integer> shaders  = new HashMap<>();
    private final HashMap<String, Integer> uniforms = new HashMap<>();
    
    private final Color       color = new Color();
    private final FloatBuffer m2Buf = BufferUtils.createFloatBuffer(4);
    private final FloatBuffer m3Buf = BufferUtils.createFloatBuffer(9);
    private final FloatBuffer m4Buf = BufferUtils.createFloatBuffer(16);
    
    public Shader()
    {
        this.id = glCreateProgram();
    }
    
    public Shader loadVertex(String source)
    {
        return load("Vertex", GL_VERTEX_SHADER, source);
    }
    
    public Shader loadVertexFile(String file)
    {
        return loadFile("Vertex", GL_VERTEX_SHADER, file);
    }
    
    public Shader loadGeometry(String source)
    {
        return load("Geometry", GL_GEOMETRY_SHADER, source);
    }
    
    public Shader loadGeometryFile(String file)
    {
        return loadFile("Geometry", GL_GEOMETRY_SHADER, file);
    }
    
    public Shader loadFragment(String source)
    {
        return load("Fragment", GL_FRAGMENT_SHADER, source);
    }
    
    public Shader loadFragmentFile(String file)
    {
        return loadFile("Fragment", GL_FRAGMENT_SHADER, file);
    }
    
    public Shader validate()
    {
        glLinkProgram(this.id);
        if (glGetProgrami(this.id, GL_LINK_STATUS) != GL_TRUE) throw new RuntimeException("Link failure: \n" + glGetProgramInfoLog(this.id));
        
        glValidateProgram(this.id);
        if (glGetProgrami(this.id, GL_VALIDATE_STATUS) != GL_TRUE) throw new RuntimeException("Validation failure: \n" + glGetProgramInfoLog(this.id));
        
        return this;
    }
    
    public Shader bind()
    {
        glUseProgram(this.id);
        return this;
    }
    
    public Shader unbind()
    {
        glUseProgram(0);
        return this;
    }
    
    public Shader delete()
    {
        for (int shader : this.shaders.values()) glDetachShader(this.id, shader);
        glDeleteProgram(this.id);
        return this;
    }
    
    public void setInt(final String name, int value)
    {
        glUniform1i(getUniform(name), value);
    }
    
    public void setBool(final String name, boolean value)
    {
        glUniform1i(getUniform(name), value ? 1 : 0);
    }
    
    public void setFloat(final String name, float value)
    {
        glUniform1f(getUniform(name), value);
    }
    
    public void setVec2(final String name, float x, float y)
    {
        glUniform2f(getUniform(name), x, y);
    }
    
    public void setVec2(final String name, Vector2fc vec)
    {
        setVec2(name, vec.x(), vec.y());
    }
    
    public void setVec3(final String name, float x, float y, float z)
    {
        glUniform3f(getUniform(name), x, y, z);
    }
    
    public void setVec3(final String name, Vector3fc vec)
    {
        setVec3(name, vec.x(), vec.y(), vec.z());
    }
    
    public void setVec4(final String name, float x, float y, float z, float w)
    {
        glUniform4f(getUniform(name), x, y, z, w);
    }
    
    public void setVec4(final String name, Vector4fc vec)
    {
        setVec4(name, vec.x(), vec.y(), vec.z(), vec.w());
    }
    
    public void setColor(final String name, Colorc color)
    {
        glUniform4f(getUniform(name), color.rf(), color.gf(), color.bf(), color.af());
    }
    
    public void setColor(final String name, Number r, Number g, Number b, Number a)
    {
        setColor(name, this.color.set(r, g, b, a));
    }
    
    public void setColor(final String name, Number r, Number g, Number b)
    {
        setColor(name, this.color.set(r, g, b, 255));
    }
    
    public void setColor(final String name, Number grey, Number a)
    {
        setColor(name, this.color.set(grey, grey, grey, a));
    }
    
    public void setColor(final String name, Number grey)
    {
        setColor(name, this.color.set(grey, grey, grey, 255));
    }
    
    public void setMat2(final String name, Matrix2fc mat)
    {
        glUniformMatrix2fv(getUniform(name), false, mat.get(this.m2Buf));
    }
    
    public void setMat3(final String name, Matrix3fc mat)
    {
        glUniformMatrix3fv(getUniform(name), false, mat.get(this.m3Buf));
    }
    
    public void setMat4(final String name, Matrix4fc mat)
    {
        glUniformMatrix4fv(getUniform(name), false, mat.get(this.m4Buf));
    }
    
    private int getUniform(String uniform)
    {
        return this.uniforms.computeIfAbsent(uniform, (u) -> glGetUniformLocation(this.id, u));
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
