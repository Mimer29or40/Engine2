package engine.render;

import engine.color.Color;
import engine.color.Colorc;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import rutils.IOUtil;
import rutils.Logger;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Objects;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL43.glCopyImageSubData;
import static org.lwjgl.stb.STBImage.*;

public class Texture extends GLTexture
{
    private static final Logger LOGGER = new Logger();
    
    protected final int fbo;
    
    public Texture(int width, int height, int channels, Colorc initial)
    {
        super(width, height, getFormat(channels));
        
        ByteBuffer data = MemoryUtil.memAlloc(width * height * channels);
        
        if (channels == 4)
        {
            int color = initial.toInt();
            for (int i = 0; i < width * height; i++)
            {
                data.putInt(i * channels, color);
            }
        }
        else
        {
            for (int i = 0; i < width * height; i++)
            {
                for (int j = 0; j < channels; j++) data.put(i * channels + j, (byte) initial.getComponent(j));
            }
        }
        
        bind();
        applyTextureSettings();
        set(data);
        unbind();
        
        MemoryUtil.memFree(data);
        
        this.fbo = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, this.fbo);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, this.id, 0);
        
        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) Texture.LOGGER.severe("Could not create FrameBuffer");
        
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }
    
    public Texture(int width, int height, Colorc initial)
    {
        this(width, height, 4, initial);
    }
    
    public Texture(int width, int height)
    {
        this(width, height, 4, Color.BLACK);
    }
    
    public Texture(int width, int height, int channels, ByteBuffer data)
    {
        super(width, height, getFormat(channels));
        
        bind();
        applyTextureSettings();
        set(data);
        unbind();
        
        this.fbo = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, this.fbo);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, this.id, 0);
        
        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) Texture.LOGGER.severe("Could not create FrameBuffer");
        
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        
        Texture.LOGGER.fine("Generated:", this);
    }
    
    public Texture(int width, int height, int channels)
    {
        super(width, height, getFormat(channels));
        
        bind();
        applyTextureSettings();
        allocate();
        unbind();
        
        this.fbo = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, this.fbo);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, this.id, 0);
        
        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) Texture.LOGGER.severe("Could not create FrameBuffer");
        
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        
        Texture.LOGGER.fine("Generated:", this);
    }
    
    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Texture texture = (Texture) o;
        return this.id == texture.id;
    }
    
    @Override
    public int hashCode()
    {
        return Objects.hash(this.id);
    }
    
    @Override
    public String toString()
    {
        return "Texture{" + "id=" + this.id + ", width=" + this.width + ", height=" + this.height + ", format=" + this.format + '}';
    }
    
    /**
     * Binds the framebuffer for OpenGL rendering.
     *
     * @return This instance for call chaining.
     */
    public Texture bindFramebuffer()
    {
        glBindFramebuffer(GL_FRAMEBUFFER, this.fbo);
        glViewport(0, 0, this.width, this.height);
        return this;
    }
    
    /**
     * Unbinds the framebuffer from OpenGL rendering.
     *
     * @return This instance for call chaining.
     */
    public Texture unbindFramebuffer()
    {
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        return this;
    }
    
    /**
     * Creates a new texture that is a sub region of this one.
     *
     * @param x      the left coordinate of the sub region
     * @param y      the top coordinate of the sub region
     * @param width  the width of the sub region
     * @param height the height of the sub region
     * @return the new Texture.
     */
    @Override
    public Texture subTexture(int x, int y, int width, int height)
    {
        if (x + width > this.width) throw new RuntimeException("Sub-Region exceeds texture bounds");
        if (y + height > this.height) throw new RuntimeException("Sub-Region exceeds texture bounds");
    
        Texture other = new Texture(width, height, this.channels);
    
        glCopyImageSubData(this.id, GL_TEXTURE_2D, 0, x, y, 0,
                           other.id, GL_TEXTURE_2D, 0, 0, 0, 0,
                           width, height, this.channels);
    
        return other;
    }
    
    /**
     * Loads a png file from disk.
     *
     * @param filePath The path to the file.
     * @param flip     If the image should be flipped vertically.
     * @return The new texture.
     */
    public static Texture loadImage(String filePath, boolean flip)
    {
        String actualPath = IOUtil.getPath(filePath).toString();
    
        stbi_set_flip_vertically_on_load(flip);
    
        try (MemoryStack stack = MemoryStack.stackPush())
        {
            IntBuffer width    = stack.mallocInt(1);
            IntBuffer height   = stack.mallocInt(1);
            IntBuffer channels = stack.mallocInt(1);
        
            if (stbi_info(actualPath, width, height, channels))
            {
                ByteBuffer data = stbi_load(actualPath, width, height, channels, 0);
            
                return new Texture(width.get(), height.get(), channels.get(), data);
            }
            else
            {
                Texture.LOGGER.severe("Failed to load Texture:", filePath);
            }
        }
    
        stbi_set_flip_vertically_on_load(false);
    
        return new Texture(0, 0, 1);
    }
    
    /**
     * Loads a png file from disk.
     *
     * @param filePath The path to the file.
     * @return The new texture.
     */
    public static Texture loadImage(String filePath)
    {
        return loadImage(filePath, false);
    }
    
    private static GL getFormat(int channels)
    {
        return switch (channels)
                {
                    case 1 -> GL.RED;
                    case 2 -> GL.RG;
                    case 3 -> GL.RGB;
                    default -> GL.RGBA;
                };
    }
}
