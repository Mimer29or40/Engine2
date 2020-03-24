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
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.GL_RG;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;
import static org.lwjgl.stb.STBImage.*;
import static org.lwjgl.stb.STBImageWrite.stbi_write_png;

public class Texture
{
    protected static final Logger LOGGER = new Logger();
    
    protected final Color tempColor = new Color();
    
    protected final int id;
    
    protected final int width;
    protected final int height;
    protected final int channels;
    
    protected final ByteBuffer data;
    
    protected int wrapS     = GL_REPEAT;
    protected int wrapT     = GL_REPEAT;
    protected int minFilter = GL_NEAREST;
    protected int magFilter = GL_NEAREST;
    
    private boolean firstUpload = true;
    
    protected Texture(int width, int height, int channels, ByteBuffer data)
    {
        if (channels < 1 || 4 < channels) throw new RuntimeException("Sprites can only have 1-4 channels");
        
        this.id = glGenTextures();
        
        this.width    = width;
        this.height   = height;
        this.channels = channels;
        
        this.data = data;
    }
    
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
    
    public Texture(int width, int height, int channels)
    {
        this(width, height, channels, Color.BLACK);
    }
    
    public Texture(int width, int height, Colorc initial)
    {
        this(width, height, 4, initial);
    }
    
    public Texture(int width, int height)
    {
        this(width, height, 4, Color.BLACK);
    }
    
    public int id()
    {
        return this.id;
    }
    
    public int width()
    {
        return this.width;
    }
    
    public int height()
    {
        return this.height;
    }
    
    public int channels()
    {
        return this.channels;
    }
    
    public ByteBuffer data()
    {
        return this.data;
    }
    
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
                for (int i = 0; i < this.channels; i++) this.tempColor.setComponent(i, this.data.get(index + i));
            }
            return this.tempColor;
        }
        else
        {
            return this.tempColor.set(0, 0);
        }
    }
    
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
    
    public Colorc sample(float x, float y)
    {
        int sx = Math.min((int) (x * (float) this.width), this.width - 1);
        int sy = Math.min((int) (y * (float) this.height), this.height - 1);
        
        return getPixel(sx, sy);
    }
    
    public Colorc sampleBL(float u, float v)
    {
        u = u * width - 0.5f;
        v = v * height - 0.5f;
        
        int x = (int) Math.floor(u);
        int y = (int) Math.floor(v);
        
        float u_ratio    = u - x;
        float v_ratio    = v - y;
        float u_opposite = 1 - u_ratio;
        float v_opposite = 1 - v_ratio;
        
        Color p1 = new Color(getPixel(Math.max(x, 0), Math.max(y, 0)));
        Color p2 = new Color(getPixel(Math.min(x + 1, this.width - 1), Math.max(y, 0)));
        Color p3 = new Color(getPixel(Math.max(x, 0), Math.min(y + 1, this.height - 1)));
        Color p4 = new Color(getPixel(Math.min(x + 1, this.width - 1), Math.min(y + 1, this.height - 1)));
        
        return this.tempColor.set((p1.r() * u_opposite + p2.r() * u_ratio) * v_opposite + (p3.r() * u_opposite + p4.r() * u_ratio) * v_ratio,
                                  (p1.g() * u_opposite + p2.g() * u_ratio) * v_opposite + (p3.g() * u_opposite + p4.g() * u_ratio) * v_ratio,
                                  (p1.b() * u_opposite + p2.b() * u_ratio) * v_opposite + (p3.b() * u_opposite + p4.b() * u_ratio) * v_ratio);
        
    }
    
    public void clear()
    {
        if (this.data != null)
        {
            this.data.clear();
            zeroBuffer(this.data);
        }
    }
    
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
    
    public Texture bind()
    {
        glBindTexture(GL_TEXTURE_2D, this.id);
        return this;
    }
    
    public Texture unbind()
    {
        glBindTexture(GL_TEXTURE_2D, 0);
        return this;
    }
    
    public Texture upload()
    {
        bind();
        
        if (this.data != null)
        {
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, this.wrapS);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, this.wrapT);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, this.minFilter);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, this.magFilter);
            glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
            
            if (this.firstUpload)
            {
                glTexImage2D(GL_TEXTURE_2D, 0, getFormat(channels), this.width, this.height, 0, getFormat(channels), GL_UNSIGNED_BYTE, this.data);
                
                this.firstUpload = false;
            }
            else
            {
                glTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, this.width, this.height, getFormat(channels), GL_UNSIGNED_BYTE, this.data);
            }
            glGenerateMipmap(GL_TEXTURE_2D);
        }
        return this;
    }
    
    public Texture download()
    {
        bind();
        
        if (this.data != null) glGetTexImage(GL_TEXTURE_2D, 0, getFormat(channels), GL_UNSIGNED_BYTE, this.data);
        
        return this;
    }
    
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
            Texture.LOGGER.error("Texture could not be saved: " + filePath);
        }
    }
    
    public void saveImage(String imagePath)
    {
        if (this.data == null) return;
        
        if (!imagePath.endsWith(".png")) imagePath += ".png";
        
        if (!stbi_write_png(imagePath, this.width, this.height, this.channels, this.data, this.width * 4))
        {
            Texture.LOGGER.error("Image could not be saved: " + imagePath);
        }
    }
    
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
            Texture.LOGGER.error("Texture could not be loaded: " + filePath);
        }
        
        return new Texture(0, 0, 0, (ByteBuffer) null);
    }
    
    public static Texture loadImage(String imagePath, boolean flip)
    {
        String actualPath = getPath(imagePath).toString();
        
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
            Texture.LOGGER.error("Failed to load Texture: " + imagePath);
        }
        
        stbi_set_flip_vertically_on_load(false);
        
        return new Texture(0, 0, 0, (ByteBuffer) null);
    }
    
    public static Texture loadImage(String imagePath)
    {
        return loadImage(imagePath, false);
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
