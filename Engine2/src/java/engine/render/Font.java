package engine.render;

import engine.util.Tuple;
import engine.util.Util;
import org.lwjgl.stb.STBTTAlignedQuad;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBTTPackContext;
import org.lwjgl.stb.STBTTPackedchar;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;

import static org.lwjgl.stb.STBTruetype.*;

/**
 * This class turns a TrueType Font into a texture that can be used to render text to the screen.
 */
@SuppressWarnings("unused")
public class Font
{
    private static final HashMap<String, ByteBuffer> FILE_CACHE = new HashMap<>();
    private static final ArrayList<Font>             FONTS      = new ArrayList<>();
    
    public static void destroyAll()
    {
        for (ByteBuffer buffer : Font.FILE_CACHE.values()) MemoryUtil.memFree(buffer);
        for (Font font : Font.FONTS)
        {
            font.info.free();
            for (STBTTPackedchar.Buffer buffer : font.charDataMap.values()) MemoryUtil.memFree(buffer);
            font.charDataMap.clear();
            font.scaleMap.clear();
            font.descentMap.clear();
            font.lineGapMap.clear();
            for (Texture texture : font.textureMap.values()) texture.destroy();
            font.textureMap.clear();
        }
        Font.FONTS.clear();
    }
    
    private static final String  DEFAULT_FONT      = "BetterPixels.ttf";
    private static final int     DEFAULT_SIZE      = 12;
    private static final boolean DEFAULT_ALIGNMENT = false;
    
    protected String        font;
    protected ByteBuffer    data;
    protected STBTTFontinfo info;
    
    protected int size;
    
    protected boolean pixelAligned;
    
    protected final HashMap<Integer, STBTTPackedchar.Buffer> charDataMap = new HashMap<>();
    
    protected final HashMap<Integer, Double> scaleMap   = new HashMap<>();
    protected final HashMap<Integer, Double> ascentMap  = new HashMap<>();
    protected final HashMap<Integer, Double> descentMap = new HashMap<>();
    protected final HashMap<Integer, Double> lineGapMap = new HashMap<>();
    
    protected final HashMap<Integer, Texture> textureMap = new HashMap<>();
    
    /**
     * Creates a new font from a ttf file.
     *
     * @param font         The path to the file.
     * @param size         The size in pixels to generate the glyphs at.
     * @param pixelAligned If the text should be drawn at pixel coordinates.
     */
    public Font(String font, int size, boolean pixelAligned)
    {
        this.font = font;
        this.data = Font.FILE_CACHE.computeIfAbsent(this.font, Util::resourceToByteBuffer);
        this.info = STBTTFontinfo.malloc();
        if (!stbtt_InitFont(this.info, this.data)) throw new RuntimeException("Font could not be loaded: " + font);
        
        this.size = Math.max(4, size);
        
        this.pixelAligned = pixelAligned;
        
        setup();
        
        Font.FONTS.add(this);
    }
    
    /**
     * Creates a new font from a ttf file.
     *
     * @param font The path to the file.
     * @param size The size in pixels to generate the glyphs at.
     */
    public Font(String font, int size)
    {
        this(font, size, Font.DEFAULT_ALIGNMENT);
    }
    
    /**
     * Creates a new font from a ttf file at the default size.
     *
     * @param font         The path to the file.
     * @param pixelAligned If the text should be drawn at pixel coordinates.
     */
    public Font(String font, boolean pixelAligned)
    {
        this(font, Font.DEFAULT_SIZE, pixelAligned);
    }
    
    /**
     * Creates a new font from the default font.
     *
     * @param size         The size in pixels to generate the glyphs at.
     * @param pixelAligned If the text should be drawn at pixel coordinates.
     */
    public Font(int size, boolean pixelAligned)
    {
        this(Font.DEFAULT_FONT, size, pixelAligned);
    }
    
    /**
     * Creates a new font from a ttf file at the default size.
     *
     * @param font The path to the file.
     */
    public Font(String font)
    {
        this(font, Font.DEFAULT_SIZE, Font.DEFAULT_ALIGNMENT);
    }
    
    /**
     * Creates a new font from the default font.
     *
     * @param size The size in pixels to generate the glyphs at.
     */
    public Font(int size)
    {
        this(Font.DEFAULT_FONT, size, Font.DEFAULT_ALIGNMENT);
    }
    
    /**
     * Creates a new font from the default font at the default size.
     *
     * @param pixelAligned If the text should be drawn at pixel coordinates.
     */
    public Font(boolean pixelAligned)
    {
        this(Font.DEFAULT_FONT, Font.DEFAULT_SIZE, pixelAligned);
    }
    
    /**
     * Creates a new font from the default font at the default size.
     */
    public Font()
    {
        this(Font.DEFAULT_FONT, Font.DEFAULT_SIZE, Font.DEFAULT_ALIGNMENT);
    }
    
    /**
     * Creates a new font from another font.
     */
    @SuppressWarnings("CopyConstructorMissesField")
    public Font(Font other)
    {
        this(other.font, other.size, other.pixelAligned);
    }
    
    /**
     * @return Gets the font path.
     */
    public String getFont()
    {
        return this.font;
    }
    
    /**
     * Sets the font from a ttf file.
     *
     * @param font The new font.
     */
    public void setFont(String font)
    {
        if (this.font.equals(font)) return;
        
        this.font = font;
        this.data = Font.FILE_CACHE.computeIfAbsent(this.font, Util::resourceToByteBuffer);
        this.info = STBTTFontinfo.create();
        if (!stbtt_InitFont(this.info, this.data)) throw new RuntimeException("Font could not be loaded: " + font);
        
        setup();
    }
    
    /**
     * @return Gets the size of the font in pixels.
     */
    public int getSize()
    {
        return this.size;
    }
    
    /**
     * Sets the size in pixels for the font.
     *
     * @param size The new size.
     */
    public void setSize(int size)
    {
        this.size = Math.max(4, size);
        setup();
    }
    
    /**
     * @return If the font will be drawn at integer coordinates
     */
    public boolean isPixelAligned()
    {
        return this.pixelAligned;
    }
    
    /**
     * Sets if the font will be drawn at integer coordinates
     *
     * @param pixelAligned The new value
     */
    public void setPixelAligned(boolean pixelAligned)
    {
        this.pixelAligned = pixelAligned;
    }
    
    /**
     * @return Gets the scale of the font.
     */
    public double getScale()
    {
        return this.scaleMap.get(this.size);
    }
    
    /**
     * @return Gets the ascent of the font.
     */
    public double getAscent()
    {
        return this.ascentMap.get(this.size);
    }
    
    /**
     * @return Gets the descent of the font.
     */
    public double getDescent()
    {
        return this.descentMap.get(this.size);
    }
    
    /**
     * @return Gets the lineGap of the font.
     */
    public double getLineGap()
    {
        return this.lineGapMap.get(this.size);
    }
    
    /**
     * @return Gets the texture map of the font.
     */
    public Texture getTexture()
    {
        return this.textureMap.get(this.size);
    }
    
    /**
     * Calculates the width in pixels of the string. If the string contains line breaks, then it calculates the widest line and returns it.
     *
     * @return The width in pixels of the string.
     */
    public double getStringWidth(String text)
    {
        String[] lines = text.split("\n");
        double   width = 0;
        if (lines.length == 1)
        {
            try (MemoryStack stack = MemoryStack.stackPush())
            {
                IntBuffer cpBuffer      = stack.mallocInt(1);
                IntBuffer advBuffer     = stack.mallocInt(1);
                IntBuffer bearingBuffer = stack.mallocInt(1);
                
                for (int i = 0, n = text.length(); i < n; )
                {
                    i += getCP(text, n, i, cpBuffer);
                    int cp = cpBuffer.get(0);
                    stbtt_GetCodepointHMetrics(this.info, cp, advBuffer, bearingBuffer);
                    width += advBuffer.get(0);
                    
                    if (i < n)
                    {
                        getCP(text, n, i, cpBuffer);
                        width += stbtt_GetCodepointKernAdvance(this.info, cp, cpBuffer.get(0));
                    }
                }
                return width * this.scaleMap.get(this.size);
            }
        }
        else
        {
            for (String line : lines)
            {
                width = Math.max(width, getStringWidth(line));
            }
            return width;
        }
    }
    
    /**
     * Calculates the height in pixels of the string. If the string contains line breaks, then it calculates the total height of all lines.
     *
     * @return The height in pixels of the string.
     */
    public double getStringHeight(String text)
    {
        String[] lines = text.split("\n");
        return lines.length * this.size;
    }
    
    /**
     * Renders the text and return an array of screen coordinates and uv coordinates for each character to send to a renderer.
     * <p>
     * The format is as follows: <p>
     * [x10,y10,x20,y20,u10,v10,u20,v20,x11,y11,x21,y21,u11,v11,u21,v21,...]
     *
     * @param text The text to render.
     * @return The array of coordinates.
     */
    public double[] renderText(String text)
    {
        int n = text.length();
        
        STBTTPackedchar.Buffer charData = this.charDataMap.get(this.size);
        
        int width  = this.textureMap.get(this.size).width();
        int height = this.textureMap.get(this.size).height();
        
        double ascent = this.ascentMap.get(this.size);
        
        double[] vertices = new double[n * 8];
        try (MemoryStack stack = MemoryStack.stackPush())
        {
            FloatBuffer x = stack.mallocFloat(1);
            FloatBuffer y = stack.mallocFloat(1);
            
            STBTTAlignedQuad quad = STBTTAlignedQuad.mallocStack(stack);
            
            int  index;
            char character;
            x.put(0, 0);
            y.put(0, 0);
            for (int i = 0; i < n; i++)
            {
                index     = i * 8;
                character = text.charAt(i);
                stbtt_GetPackedQuad(charData, width, height, character, x, y, quad, this.pixelAligned);
                if (character == ' ') continue;
                vertices[index]     = quad.x0();
                vertices[index + 1] = quad.y0() + ascent;
                vertices[index + 2] = quad.x1();
                vertices[index + 3] = quad.y1() + ascent;
                vertices[index + 4] = quad.s0();
                vertices[index + 5] = quad.t0();
                vertices[index + 6] = quad.s1();
                vertices[index + 7] = quad.t1();
            }
        }
        return vertices;
    }
    
    /**
     * Destroys the Font and free's it memory.
     */
    public void destroy()
    {
        this.info.free();
        for (STBTTPackedchar.Buffer buffer : this.charDataMap.values()) MemoryUtil.memFree(buffer);
        this.charDataMap.clear();
        this.scaleMap.clear();
        this.descentMap.clear();
        this.lineGapMap.clear();
        for (Texture texture : this.textureMap.values()) texture.destroy();
        this.textureMap.clear();
        Font.FONTS.remove(this);
    }
    
    private void setup()
    {
        if (this.charDataMap.containsKey(this.size)) return;
        
        STBTTPackedchar.Buffer charData = STBTTPackedchar.malloc(128);
        this.charDataMap.put(this.size, charData);
        
        double scale = stbtt_ScaleForPixelHeight(this.info, this.size);
        this.scaleMap.put(this.size, scale);
        
        try (MemoryStack stack = MemoryStack.stackPush())
        {
            IntBuffer ascent  = stack.mallocInt(1);
            IntBuffer descent = stack.mallocInt(1);
            IntBuffer lineGap = stack.mallocInt(1);
            
            stbtt_GetFontVMetrics(this.info, ascent, descent, lineGap);
            
            this.ascentMap.put(this.size, ascent.get(0) * scale);
            this.descentMap.put(this.size, descent.get(0) * scale);
            this.lineGapMap.put(this.size, lineGap.get(0) * scale);
        }
        
        Texture texture      = null;
        boolean success      = false;
        int     textureWidth = 32;
        int     sampleSize   = 2;
        while (!success && textureWidth < 1000)
        {
            try (STBTTPackContext pc = STBTTPackContext.malloc())
            {
                texture = new Texture(textureWidth * this.size, this.size * sampleSize, 1);
                stbtt_PackBegin(pc, texture.data(), texture.width(), texture.height(), 0, 2, MemoryUtil.NULL);
                charData.position(32);
                stbtt_PackSetOversampling(pc, sampleSize, sampleSize);
                success = stbtt_PackFontRange(pc, this.data, 0, (float) this.size, 32, charData);
                charData.clear();
                texture.data().clear();
                stbtt_PackEnd(pc);
                textureWidth *= 2;
            }
        }
        texture.bind().upload();
        this.textureMap.put(this.size, texture);
    }
    
    private static int getCP(String text, int to, int i, IntBuffer codePoint)
    {
        char c1 = text.charAt(i);
        if (Character.isHighSurrogate(c1) && i + 1 < to)
        {
            char c2 = text.charAt(i + 1);
            if (Character.isLowSurrogate(c2))
            {
                codePoint.put(0, Character.toCodePoint(c1, c2));
                return 2;
            }
        }
        codePoint.put(0, c1);
        return 1;
    }
    
    public static final class FontTuple extends Tuple<String, Integer, Boolean>
    {
        /**
         * Creates a new tuple with the fonts information
         *
         * @param font The font object.
         */
        public FontTuple(Font font)
        {
            super(font.font, font.size, font.pixelAligned);
        }
        
        public void setFont(Font font)
        {
            font.setFont(font.font);
            font.setSize(font.size);
            font.setPixelAligned(font.pixelAligned);
        }
    }
}
