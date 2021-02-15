package engine.render;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import rutils.IOUtil;
import rutils.Logger;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Objects;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL43.glCopyImageSubData;
import static org.lwjgl.stb.STBImage.*;
import static org.lwjgl.stb.STBImageWrite.stbi_write_png;

public class GLTexture
{
    private static final Logger LOGGER = new Logger();
    
    protected final int id;
    
    protected final int width;
    protected final int height;
    protected final GL  format;
    protected final GL  internalFormat;
    protected final GL  dataType;
    
    protected final int channels;
    
    protected GL wrapS     = GL.CLAMP_TO_EDGE;
    protected GL wrapT     = GL.CLAMP_TO_EDGE;
    protected GL minFilter = GL.NEAREST;
    protected GL magFilter = GL.NEAREST;
    
    /**
     * Creates an OpenGL texture.
     *
     * @param width          The width of the texture.
     * @param height         The height of the texture.
     * @param internalFormat The GL internal format of the texture.
     */
    public GLTexture(int width, int height, GL internalFormat)
    {
        this.id = glGenTextures();
        
        this.width          = width;
        this.height         = height;
        this.internalFormat = internalFormat;
        this.format         = getFormat(internalFormat);
        this.dataType       = getDataType(internalFormat);
        
        this.channels = getChannels(this.format);
        
        GLTexture.LOGGER.fine("Generated:", this);
    }
    
    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GLTexture texture = (GLTexture) o;
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
        return "GLTexture{" + "id=" + this.id + ", width=" + this.width + ", height=" + this.height + ", format=" + this.format + '}';
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
     * Sets the wrap mode for the texture.
     * <p>
     * Make sure to bind the texture first.
     *
     * @param wrapS The new wrapS mode.
     * @param wrapT The new wrapT mode.
     * @return This instance for call chaining.
     */
    public GLTexture wrapMode(GL wrapS, GL wrapT)
    {
        this.wrapS = wrapS;
        this.wrapT = wrapT;
        
        return applyWrapMode();
    }
    
    /**
     * Sets the filter mode for the texture.
     * <p>
     * Make sure to bind the texture first.
     *
     * @param minFilter The new minFilter mode.
     * @param magFilter The new magFilter mode.
     * @return This instance for call chaining.
     */
    public GLTexture filterMode(GL minFilter, GL magFilter)
    {
        this.minFilter = minFilter;
        this.magFilter = magFilter;
        
        return applyFilterMode();
    }
    
    /**
     * Applies the wrap mode to the texture.
     * <p>
     * Make sure to bind the texture first.
     *
     * @return This instance for call chaining.
     */
    public GLTexture applyWrapMode()
    {
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, this.wrapS.ref());
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, this.wrapT.ref());
        
        return this;
    }
    
    /**
     * Applies the filter mode to the texture.
     * <p>
     * Make sure to bind the texture first.
     *
     * @return This instance for call chaining.
     */
    public GLTexture applyFilterMode()
    {
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, this.minFilter.ref());
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, this.magFilter.ref());
        
        return this;
    }
    
    /**
     * Applies the pixel alignment to the texture.
     * <p>
     * Make sure to bind the texture first.
     *
     * @return This instance for call chaining.
     */
    public GLTexture applyPixelAlignment()
    {
        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
        
        return this;
    }
    
    /**
     * Applies the following texture settings: applyWrapModem applyFilterMode, applyPixelAlignment
     * <p>
     * Make sure to bind the texture first.
     *
     * @return This instance for call chaining.
     */
    public GLTexture applyTextureSettings()
    {
        return applyWrapMode().applyFilterMode().applyPixelAlignment();
    }
    
    /**
     * Binds the texture for OpenGL rendering to a texture index.
     *
     * @param textureNumber The texture index.
     * @return This instance for call chaining.
     */
    public GLTexture bind(int textureNumber)
    {
        glActiveTexture(GL_TEXTURE0 + textureNumber);
        glBindTexture(GL_TEXTURE_2D, this.id);
        
        return this;
    }
    
    /**
     * Binds the texture for OpenGL rendering to the 0 texture.
     *
     * @return This instance for call chaining.
     */
    public GLTexture bind()
    {
        return bind(0);
    }
    
    /**
     * Unbinds the texture from OpenGL rendering.
     *
     * @return This instance for call chaining.
     */
    public GLTexture unbind()
    {
        glBindTexture(GL_TEXTURE_2D, 0);
        
        return this;
    }
    
    /**
     * Destroys the texture and free's it memory.
     *
     * @return This instance for call chaining.
     */
    public GLTexture destroy()
    {
        glDeleteTextures(this.id);
        
        return this;
    }
    
    /**
     * @return A new copy of this texture.
     */
    public GLTexture copy()
    {
        GLTexture other = new GLTexture(this.width, this.height, this.format);
        
        other.wrapS = this.wrapS;
        other.wrapT = this.wrapT;
        
        other.minFilter = this.minFilter;
        other.magFilter = this.magFilter;
        
        return copy(other.bind().applyTextureSettings().allocate().unbind());
    }
    
    /**
     * Copies this texture into the other texture.
     *
     * @param other The other texture.
     * @return The other texture.
     */
    public GLTexture copy(GLTexture other)
    {
        if (this.width != other.width || this.height != other.height) throw new RuntimeException(String.format("Textures are not same size. (%s, %s) != (%s, %s)", this.width, this.height, other.width, other.height));
        if (this.format != other.format) throw new RuntimeException(String.format("Textures are not same format. %s != %s", this.format, other.format));
        
        glCopyImageSubData(this.id, GL_TEXTURE_2D, 0, 0, 0, 0,
                           other.id, GL_TEXTURE_2D, 0, 0, 0, 0,
                           this.width, this.height, this.channels);
        
        return other;
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
    public GLTexture subTexture(int x, int y, int width, int height)
    {
        if (x + width > this.width) throw new RuntimeException("Sub-Region exceeds texture bounds");
        if (y + height > this.height) throw new RuntimeException("Sub-Region exceeds texture bounds");
        
        GLTexture other = new GLTexture(width, height, this.internalFormat);
        other.bind().applyTextureSettings().allocate().unbind();
        
        glCopyImageSubData(this.id, GL_TEXTURE_2D, 0, x, y, 0,
                           other.id, GL_TEXTURE_2D, 0, 0, 0, 0,
                           width, height, this.channels);
        
        return other;
    }
    
    /**
     * Allocates the memory required to store this texture in v-ram.
     * <p>
     * Make sure to bind the texture first.
     *
     * @return This instance for call chaining
     */
    public GLTexture allocate()
    {
        glTexImage2D(GL_TEXTURE_2D, 0, this.format.ref(), this.width, this.height, 0, this.format.ref(), this.dataType.ref(), 0L);
        
        return this;
    }
    
    /**
     * Downloads the texture from the GPU and stores it into the buffer.
     * <p>
     * Make sure to bind the texture first.
     *
     * @return This buffer containing the data.
     */
    public ByteBuffer get(ByteBuffer data)
    {
        int size = this.width * this.height * this.channels;
        if (data.remaining() != size) throw new RuntimeException("Array size mismatch: " + data.remaining() + " != " + size);
        
        glGetTexImage(GL_TEXTURE_2D, 0, this.format.ref(), GL_UNSIGNED_BYTE, data);
        
        return data;
    }
    
    /**
     * Downloads the texture from the GPU and stores it into the buffer.
     * <p>
     * Make sure to bind the texture first.
     *
     * @return This buffer containing the data.
     */
    public int[] get(int[] data)
    {
        int size = this.width * this.height * this.channels;
        if (data.length != size) throw new RuntimeException("Array size mismatch: " + data.length + " != " + size);
        
        glGetTexImage(GL_TEXTURE_2D, 0, this.format.ref(), GL_UNSIGNED_BYTE, data);
        
        return data;
    }
    
    /**
     * Downloads the texture from the GPU and stores it into the array.
     * <p>
     * Make sure to bind the texture first.
     *
     * @return This array containing the data.
     */
    public int[] get()
    {
        return get(new int[this.width * this.height * this.channels]);
    }
    
    /**
     * Uploads the data in the byte buffer to the GPU
     * <p>
     * Make sure to bind the texture first.
     *
     * @param data The texture data.
     * @return This instance for call chaining
     */
    public GLTexture set(ByteBuffer data)
    {
        int size = this.width * this.height * this.channels;
        if (data.remaining() != size) throw new RuntimeException("Array size mismatch: " + data.remaining() + " != " + size);
        
        glTexImage2D(GL_TEXTURE_2D, 0, this.format.ref(), this.width, this.height, 0, this.format.ref(), GL_UNSIGNED_BYTE, data);
        
        return this;
    }
    
    /**
     * Uploads the data in the byte buffer to the GPU
     * <p>
     * Make sure to bind the texture first.
     *
     * @param data The texture data.
     * @return This instance for call chaining
     */
    public GLTexture set(int[] data)
    {
        int size = this.width * this.height * this.channels;
        if (data.length != size) throw new RuntimeException("Array size mismatch: " + data.length + " != " + size);
        
        glTexImage2D(GL_TEXTURE_2D, 0, this.format.ref(), this.width, this.height, 0, this.format.ref(), GL_UNSIGNED_BYTE, data);
        
        return this;
    }
    
    /**
     * Generates the mipmap for the texture.
     * <p>
     * Make sure to bind the texture first.
     *
     * @return This instance for call chaining
     */
    public GLTexture generateMipmap()
    {
        glGenerateMipmap(GL_TEXTURE_2D);
        
        return this;
    }
    
    /**
     * Saves the texture as a png to disk.
     *
     * @param filePath The path to the file.
     * @return This instance for call chaining.
     */
    public GLTexture saveImage(String filePath)
    {
        if (!filePath.endsWith(".png")) filePath += ".png";
        
        ByteBuffer data = get(MemoryUtil.memAlloc(this.width * this.height * this.channels));
        
        if (!stbi_write_png(filePath, this.width, this.height, this.channels, data, this.width * this.channels))
        {
            GLTexture.LOGGER.severe("Image could not be saved:", filePath);
        }
        MemoryUtil.memFree(data);
        
        return this;
    }
    
    /**
     * Loads a png file from disk.
     *
     * @param filePath The path to the file.
     * @param flip     If the image should be flipped vertically.
     * @return The new texture.
     */
    public static GLTexture loadImage(String filePath, boolean flip)
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
                
                return new GLTexture(width.get(), height.get(), getFormat(channels.get())).bind().set(data).generateMipmap().unbind();
            }
            else
            {
                GLTexture.LOGGER.severe("Failed to load Texture:", filePath);
            }
        }
        
        stbi_set_flip_vertically_on_load(false);
        
        return new GLTexture(0, 0, GL.RED);
    }
    
    /**
     * Loads a png file from disk.
     *
     * @param filePath The path to the file.
     * @return The new texture.
     */
    public static GLTexture loadImage(String filePath)
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
    
    private static GL getFormat(GL internalFormat)
    {
        return switch (internalFormat)
                {
                    case RED,
                            R8,
                            R8_SNORM,
                            R16,
                            R16_SNORM,
                            R16F,
                            R32F,
                            R8I,
                            R8UI,
                            R16I,
                            R16UI,
                            R32I,
                            R32UI,
                            COMPRESSED_RED_RGTC1,
                            COMPRESSED_SIGNED_RED_RGTC1,
                            COMPRESSED_RED -> GL.RED;
                    case RG,
                            RG8,
                            RG8_SNORM,
                            RG16,
                            RG16_SNORM,
                            RG16F,
                            RG32F,
                            RG8I,
                            RG8UI,
                            RG16I,
                            RG16UI,
                            RG32I,
                            RG32UI,
                            COMPRESSED_RG,
                            COMPRESSED_RG_RGTC2,
                            COMPRESSED_SIGNED_RG_RGTC2 -> GL.RG;
                    case RGB,
                            R3_G3_B2,
                            RGB4,
                            RGB5,
                            RGB8,
                            RGB8_SNORM,
                            RGB10,
                            RGB12,
                            RGB16_SNORM,
                            RGBA2,
                            RGBA4,
                            SRGB8,
                            RGB16F,
                            RGB32F,
                            R11F_G11F_B10F,
                            RGB9_E5,
                            RGB8I,
                            RGB8UI,
                            RGB16I,
                            RGB16UI,
                            RGB32I,
                            RGB32UI,
                            COMPRESSED_RGB,
                            COMPRESSED_SRGB,
                            COMPRESSED_RGB_BPTC_SIGNED_FLOAT,
                            COMPRESSED_RGB_BPTC_UNSIGNED_FLOAT -> GL.RGB;
                    case RGBA,
                            RGB5_A1,
                            RGBA8,
                            RGBA8_SNORM,
                            RGB10_A2,
                            RGB10_A2UI,
                            RGBA12,
                            RGBA16,
                            RGBA16F,
                            RGBA32F,
                            RGBA8I,
                            RGBA8UI,
                            RGBA16I,
                            RGBA16UI,
                            RGBA32I,
                            RGBA32UI,
                            COMPRESSED_RGBA,
                            COMPRESSED_SRGB_ALPHA,
                            COMPRESSED_RGBA_BPTC_UNORM,
                            COMPRESSED_SRGB_ALPHA_BPTC_UNORM -> GL.RGBA;
                    case DEPTH_COMPONENT,
                            DEPTH_COMPONENT16,
                            DEPTH_COMPONENT24,
                            DEPTH_COMPONENT32F -> GL.DEPTH_COMPONENT;
                    case DEPTH_STENCIL,
                            DEPTH32F_STENCIL8,
                            DEPTH24_STENCIL8 -> GL.DEPTH_STENCIL;
                    default -> throw new RuntimeException("Invalid Internal Format: " + internalFormat);
                };
    }
    
    @SuppressWarnings("DuplicateBranchesInSwitch")
    private static GL getDataType(GL internalFormat)
    {
        return switch (internalFormat)
                {
                    case RED -> GL.UNSIGNED_BYTE;
                    case R8 -> GL.UNSIGNED_BYTE;
                    case R8_SNORM -> GL.BYTE;
                    case R16 -> GL.UNSIGNED_SHORT;
                    case R16_SNORM -> GL.SHORT;
                    case R16F -> GL.FLOAT;
                    case R32F -> GL.DOUBLE;
                    case R8I -> GL.BYTE;
                    case R8UI -> GL.UNSIGNED_BYTE;
                    case R16I -> GL.SHORT;
                    case R16UI -> GL.UNSIGNED_SHORT;
                    case R32I -> GL.INT;
                    case R32UI -> GL.UNSIGNED_INT;
                    case COMPRESSED_RED_RGTC1 -> GL.UNSIGNED_BYTE;
                    case COMPRESSED_SIGNED_RED_RGTC1 -> GL.BYTE;
                    case COMPRESSED_RED -> GL.UNSIGNED_BYTE;
                    case RG -> GL.UNSIGNED_BYTE;
                    case RG8 -> GL.UNSIGNED_BYTE;
                    case RG8_SNORM -> GL.BYTE;
                    case RG16 -> GL.UNSIGNED_SHORT;
                    case RG16_SNORM -> GL.SHORT;
                    case RG16F -> GL.FLOAT;
                    case RG32F -> GL.DOUBLE;
                    case RG8I -> GL.BYTE;
                    case RG8UI -> GL.UNSIGNED_BYTE;
                    case RG16I -> GL.SHORT;
                    case RG16UI -> GL.UNSIGNED_SHORT;
                    case RG32I -> GL.INT;
                    case RG32UI -> GL.UNSIGNED_INT;
                    case COMPRESSED_RG -> GL.UNSIGNED_BYTE;
                    case COMPRESSED_RG_RGTC2 -> GL.UNSIGNED_BYTE;
                    case COMPRESSED_SIGNED_RG_RGTC2 -> GL.UNSIGNED_BYTE;
                    case RGB -> GL.UNSIGNED_BYTE;
                    case R3_G3_B2 -> GL.UNSIGNED_BYTE_3_3_2;
                    case RGB4 -> GL._4_BYTES;
                    case RGB5 -> GL.UNSIGNED_BYTE;
                    case RGB8 -> GL.UNSIGNED_BYTE;
                    case RGB8_SNORM -> GL.BYTE;
                    case RGB10 -> GL.UNSIGNED_INT_10_10_10_2;
                    case RGB12 -> GL.UNSIGNED_BYTE;
                    case RGB16_SNORM -> GL.UNSIGNED_BYTE;
                    case RGBA2 -> GL.UNSIGNED_BYTE;
                    case RGBA4 -> GL.UNSIGNED_BYTE;
                    case SRGB8 -> GL.UNSIGNED_BYTE;
                    case RGB16F -> GL.UNSIGNED_BYTE;
                    case RGB32F -> GL.UNSIGNED_BYTE;
                    case R11F_G11F_B10F -> GL.UNSIGNED_INT_10F_11F_11F_REV;
                    case RGB9_E5 -> GL.UNSIGNED_INT_5_9_9_9_REV;
                    case RGB8I -> GL.BYTE;
                    case RGB8UI -> GL.UNSIGNED_BYTE;
                    case RGB16I -> GL.SHORT;
                    case RGB16UI -> GL.UNSIGNED_SHORT;
                    case RGB32I -> GL.INT;
                    case RGB32UI -> GL.UNSIGNED_INT;
                    case COMPRESSED_RGB -> GL.UNSIGNED_BYTE;
                    case COMPRESSED_SRGB -> GL.BYTE;
                    case COMPRESSED_RGB_BPTC_SIGNED_FLOAT -> GL.UNSIGNED_BYTE;
                    case COMPRESSED_RGB_BPTC_UNSIGNED_FLOAT -> GL.UNSIGNED_BYTE;
                    case RGBA -> GL.UNSIGNED_BYTE;
                    case RGB5_A1 -> GL.UNSIGNED_SHORT_5_5_5_1;
                    case RGBA8 -> GL.UNSIGNED_BYTE;
                    case RGBA8_SNORM -> GL.BYTE;
                    case RGB10_A2 -> GL.UNSIGNED_INT_10_10_10_2;
                    case RGB10_A2UI -> GL.UNSIGNED_INT_10_10_10_2;
                    case RGBA12 -> GL.UNSIGNED_SHORT;
                    case RGBA16 -> GL.UNSIGNED_SHORT;
                    case RGBA16F -> GL.FLOAT;
                    case RGBA32F -> GL.DOUBLE;
                    case RGBA8I -> GL.BYTE;
                    case RGBA8UI -> GL.UNSIGNED_BYTE;
                    case RGBA16I -> GL.SHORT;
                    case RGBA16UI -> GL.UNSIGNED_SHORT;
                    case RGBA32I -> GL.INT;
                    case RGBA32UI -> GL.UNSIGNED_INT;
                    case COMPRESSED_RGBA -> GL.UNSIGNED_BYTE;
                    case COMPRESSED_SRGB_ALPHA -> GL.UNSIGNED_BYTE;
                    case COMPRESSED_RGBA_BPTC_UNORM -> GL.UNSIGNED_BYTE;
                    case COMPRESSED_SRGB_ALPHA_BPTC_UNORM -> GL.UNSIGNED_BYTE;
                    case DEPTH_COMPONENT -> GL.UNSIGNED_BYTE;
                    case DEPTH_COMPONENT16 -> GL.UNSIGNED_BYTE;
                    case DEPTH_COMPONENT24 -> GL.UNSIGNED_BYTE;
                    case DEPTH_COMPONENT32F -> GL.FLOAT;
                    case DEPTH_STENCIL -> GL.UNSIGNED_BYTE;
                    case DEPTH24_STENCIL8 -> GL.UNSIGNED_INT_24_8;
                    case DEPTH32F_STENCIL8 -> GL.FLOAT_32_UNSIGNED_INT_24_8_REV;
                    default -> throw new RuntimeException("Invalid Texture Format: " + internalFormat);
                };
        // GL_R8		GL_RED	8
        // GL_R8_SNORM	GL_RED	s8
        // GL_R16		GL_RED	16
        // GL_R16_SNORM	GL_RED	s16
        // GL_R16F		GL_RED	f16
        // GL_R32F		GL_RED	f32
        // GL_R8I		GL_RED	i8
        // GL_R8UI		GL_RED	ui8
        // GL_R16I		GL_RED	i16
        // GL_R16UI		GL_RED	ui16
        // GL_R32I		GL_RED	i32
        // GL_R32UI		GL_RED	ui32
        
        // GL_RG8			GL_RG	8		8
        // GL_RG8_SNORM		GL_RG	s8		s8
        // GL_RG16			GL_RG	16		16
        // GL_RG16_SNORM	GL_RG	s16		s16
        // GL_RG16F			GL_RG	f16		f16
        // GL_RG32F			GL_RG	f32		f32
        // GL_RG8I			GL_RG	i8		i8
        // GL_RG8UI			GL_RG	ui8		ui8
        // GL_RG16I			GL_RG	i16		i16
        // GL_RG16UI		GL_RG	ui16	ui16
        // GL_RG32I			GL_RG	i32		i32
        // GL_RG32UI		GL_RG	ui32	ui32
        
        // GL_R3_G3_B2			GL_RGB	3		3		2
        // GL_RGB4				GL_RGB	4		4		4
        // GL_RGB5				GL_RGB	5		5		5
        // GL_RGB8				GL_RGB	8		8		8
        // GL_RGB8_SNORM		GL_RGB	s8		s8		s8
        // GL_RGB10				GL_RGB	10		10		10
        // GL_RGB12				GL_RGB	12		12		12
        // GL_RGB16_SNORM		GL_RGB	16		16		16
        // GL_RGBA2				GL_RGB	2		2		2		2
        // GL_RGBA4				GL_RGB	4		4		4		4
        // GL_SRGB8				GL_RGB	8		8		8
        // GL_RGB16F			GL_RGB	f16		f16		f16
        // GL_RGB32F			GL_RGB	f32		f32		f32
        // GL_R11F_G11F_B10F	GL_RGB	f11		f11		f10
        // GL_RGB9_E5			GL_RGB	9		9		9		5
        // GL_RGB8I				GL_RGB	i8		i8		i8
        // GL_RGB8UI			GL_RGB	ui8		ui8		ui8
        // GL_RGB16I			GL_RGB	i16		i16		i16
        // GL_RGB16UI			GL_RGB	ui16	ui16	ui16
        // GL_RGB32I			GL_RGB	i32		i32		i32
        // GL_RGB32UI			GL_RGB	ui32	ui32	ui32
        
        // GL_RGB5_A1		GL_RGBA	5		5		5		1
        // GL_RGBA8			GL_RGBA	8		8		8		8
        // GL_RGBA8_SNORM	GL_RGBA	s8		s8		s8		s8
        // GL_RGB10_A2		GL_RGBA	10		10		10		2
        // GL_RGB10_A2UI	GL_RGBA	ui10	ui10	ui10	ui2
        // GL_RGBA12		GL_RGBA	12		12		12		12
        // GL_RGBA16		GL_RGBA	16		16		16		16
        // GL_SRGB8_ALPHA8	GL_RGBA	8		8		8		8
        // GL_RGBA16F		GL_RGBA	f16		f16		f16		f16
        // GL_RGBA32F		GL_RGBA	f32		f32		f32		f32
        // GL_RGBA8I		GL_RGBA	i8		i8		i8		i8
        // GL_RGBA8UI		GL_RGBA	ui8		ui8		ui8		ui8
        // GL_RGBA16I		GL_RGBA	i16		i16		i16		i16
        // GL_RGBA16UI		GL_RGBA	ui16	ui16	ui16	ui16
        // GL_RGBA32I		GL_RGBA	i32		i32		i32		i32
        // GL_RGBA32UI		GL_RGBA	ui32	ui32	ui32	ui32
    }
    
    private static int getChannels(GL gl)
    {
        return switch (gl)
                {
                    case RED, ALPHA, RED_INTEGER, STENCIL_INDEX, DEPTH_COMPONENT -> 1;
                    case RG, RG_INTEGER, DEPTH_STENCIL -> 2;
                    case RGB, BGR, RGB_INTEGER, BGR_INTEGER -> 3;
                    case RGBA, BGRA, RGBA_INTEGER, BGRA_INTEGER -> 4;
                    default -> throw new RuntimeException("Invalid Texture Format: " + gl);
                };
    }
}
