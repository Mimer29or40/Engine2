package engine.render;

import engine.color.Color;
import engine.color.Colorc;
import engine.util.Logger;
import org.lwjgl.BufferUtils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import static engine.util.Util.getPath;
import static org.lwjgl.BufferUtils.zeroBuffer;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.stb.STBImage.*;
import static org.lwjgl.stb.STBImageWrite.stbi_write_png;

/**
 * A texture that can be drawn to or used to draw to another texture. These can only be created after a window is created.
 */
@SuppressWarnings({"UnusedReturnValue", "unused"})
public class Texture
{
    protected static final Logger LOGGER = new Logger();
    
    protected final Color tempColor = new Color();
    
    protected final int id;
    
    protected final int width;
    protected final int height;
    protected final int channels;
    protected final int format;
    
    protected final ByteBuffer data;
    
    protected int wrapS     = GL_CLAMP_TO_EDGE;
    protected int wrapT     = GL_CLAMP_TO_EDGE;
    protected int minFilter = GL_NEAREST;
    protected int magFilter = GL_NEAREST;
    
    private boolean firstUpload = true;
    
    /**
     * Creates a texture from an existing buffer. This is only used internally by {@link #loadImage} and {@link #loadTexture}
     *
     * @param width    The width of the texture.
     * @param height   The height of the texture.
     * @param channels The number of channels in the texture.
     * @param data     The color data.
     */
    protected Texture(int width, int height, int channels, ByteBuffer data)
    {
        if (channels < 1 || 4 < channels) throw new RuntimeException("Sprites can only have 1-4 channels");
        
        this.id = glGenTextures();
        
        this.width    = width;
        this.height   = height;
        this.channels = channels;
        this.format   = getFormat(channels);
        
        this.data = data;
    }
    
    /**
     * Creates a texture from a width, height and number of channels with an initial color.
     *
     * @param width    The width of the texture.
     * @param height   The height of the texture.
     * @param channels The number of channels in the texture.
     * @param initial  The initial color.
     */
    public Texture(int width, int height, int channels, Colorc initial)
    {
        this(width, height, channels, BufferUtils.createByteBuffer(width * height * channels));
        
        if (this.channels == 4)
        {
            int color = initial.toInt();
            for (int i = 0; i < this.width * this.height; i++)
            {
                this.data.putInt(i * this.channels, color);
            }
        }
        else
        {
            for (int i = 0; i < width * height; i++)
            {
                for (int j = 0; j < this.channels; j++) this.data.put(i * channels + j, (byte) initial.getComponent(j));
            }
        }
    }
    
    /**
     * Creates a black texture from a width, height and number of channels
     *
     * @param width    The width of the texture.
     * @param height   The height of the texture.
     * @param channels The number of channels in the texture.
     */
    public Texture(int width, int height, int channels)
    {
        this(width, height, channels, Color.BLACK);
    }
    
    /**
     * Creates a texture from a width, height and 4 channels with an initial color.
     *
     * @param width   The width of the texture.
     * @param height  The height of the texture.
     * @param initial The initial color.
     */
    public Texture(int width, int height, Colorc initial)
    {
        this(width, height, 4, initial);
    }
    
    /**
     * Creates a black texture from a width, and height.
     *
     * @param width  The width of the texture.
     * @param height The height of the texture.
     */
    public Texture(int width, int height)
    {
        this(width, height, 4, Color.BLACK);
    }
    
    /**
     * @return The OpenGL texture id.
     */
    public int id()
    {
        return this.id;
    }
    
    /**
     * @return The width in pixels.
     */
    public int width()
    {
        return this.width;
    }
    
    /**
     * @return The height in pixels.
     */
    public int height()
    {
        return this.height;
    }
    
    /**
     * @return The number of color channels
     */
    public int channels()
    {
        return this.channels;
    }
    
    /**
     * @return The color data.
     */
    public ByteBuffer data()
    {
        return this.data;
    }
    
    /**
     * Sets the wrap mode for the texture.
     *
     * @param wrapS The new wrapS mode.
     * @param wrapT The new wrapT mode.
     * @return This instance for call chaining.
     */
    public Texture wrapMode(int wrapS, int wrapT)
    {
        this.wrapS = wrapS;
        this.wrapT = wrapT;
        return this;
    }
    
    /**
     * Sets the filter mode for the texture.
     *
     * @param minFilter The new minFilter mode.
     * @param magFilter The new magFilter mode.
     * @return This instance for call chaining.
     */
    public Texture filterMode(int minFilter, int magFilter)
    {
        this.minFilter = minFilter;
        this.magFilter = magFilter;
        return this;
    }
    
    /**
     * Copies this texture into the other texture.
     *
     * @param other The other texture.
     */
    public void copy(Texture other)
    {
        if (this.width != other.width || this.height != other.height || this.channels != other.channels) throw new RuntimeException("Sprites are not same size.");
        
        if (this.channels == 4)
        {
            for (int i = 0; i < this.width * this.height; i++)
            {
                other.data.putInt(i * this.channels, this.data.getInt(i * this.channels));
            }
        }
        else
        {
            for (int i = 0, n = this.width * this.height * this.channels; i < n; i++) other.data.put(i, this.data.get(i));
        }
    }
    
    /**
     * Gets the color data of a pixel. If the coordinate is out of bound, then a blank color is returned.
     * <p>
     * If the color does not have 4 channels, then the color data will be blank for any channel not included.
     *
     * @param x The x coordinate of the pixel.
     * @param y The y coordinate of the pixel.
     * @return The color data.
     */
    public Colorc getPixel(int x, int y)
    {
        if (0 <= x && x < this.width && 0 <= y && y < this.height)
        {
            int index = this.channels * (y * this.width + x);
            if (this.channels == 4)
            {
                this.tempColor.fromInt(this.data.getInt(index));
            }
            else
            {
                this.tempColor.set(0, 0);
                for (int i = 0; i < this.channels; i++) this.tempColor.setComponent(i, this.data.get(index + i));
            }
            return this.tempColor;
        }
        else
        {
            return this.tempColor.set(0, 0);
        }
    }
    
    /**
     * Sets the color data of a pixel. If the coordinate is out of bound, then nothing is written.
     * <p>
     * If the color does not have 4 channels, then only the actual color data with be written.
     *
     * @param x     The x coordinate of the pixel.
     * @param y     The y coordinate of the pixel.
     * @param color The color data.
     */
    public void setPixel(int x, int y, Colorc color)
    {
        if (0 <= x && x < this.width && 0 <= y && y < this.height)
        {
            int index = this.channels * (y * this.width + x);
            if (this.channels == 4)
            {
                this.data.putInt(index, color.toInt());
            }
            else
            {
                for (int i = 0; i < this.channels; i++) this.data.put(index + i, (byte) color.getComponent(i));
            }
        }
    }
    
    /**
     * Gets the color that is at the uv coordinate specified.
     *
     * @param u The u coordinate.
     * @param v The v coordinate.
     * @return The color at the coordinate.
     */
    public Colorc sample(float u, float v)
    {
        int sx = Math.max(0, (Math.min((int) (u * (float) this.width), this.width - 1)));
        int sy = Math.max(0, (Math.min((int) (v * (float) this.height), this.height - 1)));
        
        return getPixel(sx, sy);
    }
    
    public Colorc sampleBL(float u, float v)
    {
        u = u * this.width - 0.5f;
        v = v * this.height - 0.5f;
        
        int x = (int) Math.floor(u);
        int y = (int) Math.floor(v);
        
        float uRat = u - x;
        float vRat = v - y;
        float uOpp = 1 - uRat;
        float vOpp = 1 - vRat;
        
        Color p1 = new Color(getPixel(Math.max(x, 0), Math.max(y, 0)));
        Color p2 = new Color(getPixel(Math.min(x + 1, this.width - 1), Math.max(y, 0)));
        Color p3 = new Color(getPixel(Math.max(x, 0), Math.min(y + 1, this.height - 1)));
        Color p4 = new Color(getPixel(Math.min(x + 1, this.width - 1), Math.min(y + 1, this.height - 1)));
        
        return this.tempColor.set((p1.r() * uOpp + p2.r() * uRat) * vOpp + (p3.r() * uOpp + p4.r() * uRat) * vRat,
                                  (p1.g() * uOpp + p2.g() * uRat) * vOpp + (p3.g() * uOpp + p4.g() * uRat) * vRat,
                                  (p1.b() * uOpp + p2.b() * uRat) * vOpp + (p3.b() * uOpp + p4.b() * uRat) * vRat);
        
    }
    
    /**
     * Clears all the color data from the texture.
     */
    public void clear()
    {
        if (this.data != null)
        {
            zeroBuffer(this.data);
            this.data.clear();
        }
    }
    
    /**
     * Clears the texture to the specified color.
     *
     * @param color The color.
     */
    public void clear(Colorc color)
    {
        if (this.channels == 4)
        {
            int colorInt = color.toInt();
            for (int i = 0; i < this.width * this.height; i++)
            {
                this.data.putInt(i * this.channels, colorInt);
            }
        }
        else
        {
            for (int i = 0; i < this.width * this.height; i++)
            {
                for (int j = 0; j < this.channels; j++) this.data.put(i * this.channels + j, (byte) color.getComponent(j));
            }
        }
    }
    
    /**
     * Binds the texture for OpenGL rendering.
     *
     * @return This instance for call chaining.
     */
    public Texture bind()
    {
        glBindTexture(GL_TEXTURE_2D, this.id);
        return this;
    }
    
    /**
     * Unbinds the texture from OpenGL rendering.
     *
     * @return This instance for call chaining.
     */
    public Texture unbind()
    {
        glBindTexture(GL_TEXTURE_2D, 0);
        return this;
    }
    
    /**
     * Uploads the color data to the GPU.
     *
     * @return This instance for call chaining.
     */
    public Texture upload()
    {
        if (this.data != null)
        {
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, this.wrapS);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, this.wrapT);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, this.minFilter);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, this.magFilter);
            glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
            
            if (this.firstUpload)
            {
                glTexImage2D(GL_TEXTURE_2D, 0, this.format, this.width, this.height, 0, this.format, GL_UNSIGNED_BYTE, this.data);
                
                this.firstUpload = false;
            }
            else
            {
                glTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, this.width, this.height, this.format, GL_UNSIGNED_BYTE, this.data);
            }
            // glGenerateMipmap(GL_TEXTURE_2D);
        }
        return this;
    }
    
    /**
     * Downloads the texture from the GPU and stores it into the buffer.
     *
     * @return This instance for call chaining.
     */
    public Texture download()
    {
        if (this.data != null) glGetTexImage(GL_TEXTURE_2D, 0, this.format, GL_UNSIGNED_BYTE, this.data);
        
        return this;
    }
    
    /**
     * Saves the texture in a custom format to disk. This is only useful for loading the texture from disk at another run time.
     *
     * @param filePath THe path to the file.
     */
    public void saveTexture(String filePath)
    {
        if (this.data == null) return;
        
        try (FileOutputStream out = new FileOutputStream(filePath))
        {
            out.write(this.width);
            out.write(this.height);
            out.write(this.channels);
            for (int i = 0; i < this.width * this.height * this.channels; i++) out.write(this.data.get(i));
        }
        catch (IOException e)
        {
            Texture.LOGGER.severe("Texture could not be saved: " + filePath);
        }
    }
    
    /**
     * Saves the texture as a png to disk.
     *
     * @param filePath THe path to the file.
     */
    public void saveImage(String filePath)
    {
        if (this.data == null) return;
        
        if (!filePath.endsWith(".png")) filePath += ".png";
        
        if (!stbi_write_png(filePath, this.width, this.height, this.channels, this.data, this.width * this.channels))
        {
            Texture.LOGGER.severe("Image could not be saved: " + filePath);
        }
    }
    
    /**
     * Loads a texture from disk in the custom format.
     *
     * @param filePath The path to the file.
     * @return The new texture.
     */
    public static Texture loadTexture(String filePath)
    {
        try (FileInputStream in = new FileInputStream(getPath(filePath).toString()))
        {
            int width    = in.read();
            int height   = in.read();
            int channels = in.read();
            
            ByteBuffer data = BufferUtils.createByteBuffer(width * height * channels);
            
            for (int i = 0; in.available() > 0; i++) data.put(i, (byte) in.read());
            
            return new Texture(width, height, channels, data);
        }
        catch (IOException e)
        {
            Texture.LOGGER.severe("Texture could not be loaded: " + filePath);
        }
        
        return new Texture(0, 0, 0, (ByteBuffer) null);
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
        String actualPath = getPath(filePath).toString();
        
        stbi_set_flip_vertically_on_load(flip);
        
        int[] width    = new int[1];
        int[] height   = new int[1];
        int[] channels = new int[1];
        
        if (stbi_info(actualPath, width, height, channels))
        {
            return new Texture(width[0], height[0], channels[0], stbi_load(actualPath, width, height, channels, 0));
        }
        else
        {
            Texture.LOGGER.severe("Failed to load Texture: " + filePath);
        }
        
        stbi_set_flip_vertically_on_load(false);
        
        return new Texture(0, 0, 0, (ByteBuffer) null);
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
    
    private static int getFormat(int channels)
    {
        switch (channels)
        {
            case 1:
                return GL_RED;
            case 2:
                return GL_RG;
            case 3:
                return GL_RGB;
            default:
                return GL_RGBA;
        }
    }
}
