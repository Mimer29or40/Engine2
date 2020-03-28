package engine.render;

import org.lwjgl.stb.STBTTAlignedQuad;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBTTPackContext;
import org.lwjgl.stb.STBTTPackedchar;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;

import static engine.util.Util.resourceToByteBuffer;
import static org.lwjgl.stb.STBTruetype.*;

public class Font
{
    private static final String  DEFAULT_FONT      = "BetterPixels.ttf";
    private static final int     DEFAULT_SIZE      = 12;
    private static final boolean DEFAULT_ALIGNMENT = false;
    
    protected final String        font;
    protected final ByteBuffer    data;
    protected final STBTTFontinfo info;
    
    private final STBTTAlignedQuad quad;
    
    private final FloatBuffer x;
    private final FloatBuffer y;
    
    private final IntBuffer cpBuffer;
    private final IntBuffer advancedBuffer;
    private final IntBuffer bearingBuffer;
    
    private final IntBuffer ascentIntBuffer;
    private final IntBuffer descentIntBuffer;
    private final IntBuffer lineGapIntBuffer;
    
    protected int size;
    
    protected boolean pixelAligned;
    
    protected final HashMap<Integer, STBTTPackedchar.Buffer> charDataMap = new HashMap<>();
    
    protected final HashMap<Integer, Double> scaleMap   = new HashMap<>();
    protected final HashMap<Integer, Double> ascentMap  = new HashMap<>();
    protected final HashMap<Integer, Double> descentMap = new HashMap<>();
    protected final HashMap<Integer, Double> lineGapMap = new HashMap<>();
    
    protected final HashMap<Integer, Texture> bitmapMap = new HashMap<>();
    
    public Font(String font, int size, boolean pixelAligned)
    {
        this.font = font;
        this.data = resourceToByteBuffer(font);
        this.info = STBTTFontinfo.create();
        if (!stbtt_InitFont(info, this.data)) throw new RuntimeException("Font could not be loaded: " + font);
        
        this.quad = STBTTAlignedQuad.malloc();
        
        this.x = MemoryUtil.memAllocFloat(1);
        this.y = MemoryUtil.memAllocFloat(1);
        
        this.cpBuffer       = MemoryUtil.memAllocInt(1);
        this.advancedBuffer = MemoryUtil.memAllocInt(1);
        this.bearingBuffer  = MemoryUtil.memAllocInt(1);
        
        this.ascentIntBuffer  = MemoryUtil.memAllocInt(1);
        this.descentIntBuffer = MemoryUtil.memAllocInt(1);
        this.lineGapIntBuffer = MemoryUtil.memAllocInt(1);
        
        this.size = Math.max(4, size);
        
        this.pixelAligned = pixelAligned;
        
        setup();
    }
    
    public Font(String font, int size)
    {
        this(font, size, Font.DEFAULT_ALIGNMENT);
    }
    
    public Font(String font, boolean pixelAligned)
    {
        this(font, Font.DEFAULT_SIZE, pixelAligned);
    }
    
    public Font(int size, boolean pixelAligned)
    {
        this(Font.DEFAULT_FONT, size, pixelAligned);
    }
    
    public Font(String font)
    {
        this(font, Font.DEFAULT_SIZE, Font.DEFAULT_ALIGNMENT);
    }
    
    public Font(int size)
    {
        this(Font.DEFAULT_FONT, size, Font.DEFAULT_ALIGNMENT);
    }
    
    public Font(boolean pixelAligned)
    {
        this(Font.DEFAULT_FONT, Font.DEFAULT_SIZE, pixelAligned);
    }
    
    public Font()
    {
        this(Font.DEFAULT_FONT, Font.DEFAULT_SIZE, Font.DEFAULT_ALIGNMENT);
    }
    
    @SuppressWarnings("CopyConstructorMissesField")
    public Font(Font other)
    {
        this(other.font, other.size, other.pixelAligned);
    }
    
    public int getSize()
    {
        return this.size;
    }
    
    public void setSize(int size)
    {
        this.size = Math.max(4, size);
        setup();
    }
    
    public boolean isPixelAligned()
    {
        return this.pixelAligned;
    }
    
    public void setPixelAligned(boolean pixelAligned)
    {
        this.pixelAligned = pixelAligned;
    }
    
    public double getScale()
    {
        return this.scaleMap.get(this.size);
    }
    
    public double getAscent()
    {
        return this.ascentMap.get(this.size);
    }
    
    public double getDescent()
    {
        return this.descentMap.get(this.size);
    }
    
    public double getLineGap()
    {
        return this.lineGapMap.get(this.size);
    }
    
    public Texture getBitmap()
    {
        return this.bitmapMap.get(this.size);
    }
    
    public double getStringWidth(String text)
    {
        String[] lines = text.split("\n");
        double   width = 0;
        if (lines.length == 1)
        {
            for (int i = 0, n = text.length(); i < n; )
            {
                i += getCP(text, n, i, this.cpBuffer);
                int cp = this.cpBuffer.get(0);
                stbtt_GetCodepointHMetrics(info, cp, this.advancedBuffer, this.bearingBuffer);
                width += this.advancedBuffer.get(0);
                
                if (i < n)
                {
                    getCP(text, n, i, this.cpBuffer);
                    width += stbtt_GetCodepointKernAdvance(info, cp, this.cpBuffer.get(0));
                }
            }
            return width * this.scaleMap.get(this.size);
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
    
    public double getStringHeight(String text)
    {
        String[] lines = text.split("\n");
        return lines.length * this.size;
    }
    
    public double[] renderText(String text)
    {
        int n = text.length();
        
        STBTTPackedchar.Buffer charData = this.charDataMap.get(this.size);
        
        int width  = this.bitmapMap.get(this.size).width();
        int height = this.bitmapMap.get(this.size).height();
        
        double ascent = this.ascentMap.get(this.size);
        
        double[] vertices = new double[n * 8];
        
        int line = 0;
        
        int  index;
        char character;
        this.x.put(0, 0);
        this.y.put(0, line);
        for (int i = 0; i < n; i++)
        {
            // TODO - Handle next line. This should probably go into the render
            index     = i * 8;
            character = text.charAt(i);
            stbtt_GetPackedQuad(charData, width, height, character, this.x, this.y, this.quad, this.pixelAligned);
            if (character == ' ') continue;
            if (character == '\n')
            {
                this.x.put(0, 0);
                this.y.put(0, ++line * (float) this.size);
                continue;
            }
            vertices[index]     = this.quad.x0();
            vertices[index + 1] = this.quad.y0() + ascent;
            vertices[index + 2] = this.quad.x1() - this.quad.x0();
            vertices[index + 3] = this.quad.y1() - this.quad.y0();
            vertices[index + 4] = this.quad.s0();
            vertices[index + 5] = this.quad.t0();
            vertices[index + 6] = this.quad.s1() - this.quad.s0();
            vertices[index + 7] = this.quad.t1() - this.quad.t0();
        }
        return vertices;
    }
    
    private void setup()
    {
        if (this.charDataMap.containsKey(this.size)) return;
        
        STBTTPackedchar.Buffer charData = STBTTPackedchar.malloc(128);
        this.charDataMap.put(this.size, charData);
        
        double scale = stbtt_ScaleForPixelHeight(this.info, this.size);
        this.scaleMap.put(this.size, scale);
        
        stbtt_GetFontVMetrics(this.info, this.ascentIntBuffer, this.descentIntBuffer, this.lineGapIntBuffer);
        
        this.ascentMap.put(this.size, this.ascentIntBuffer.get(0) * scale);
        this.descentMap.put(this.size, this.descentIntBuffer.get(0) * scale);
        this.lineGapMap.put(this.size, this.lineGapIntBuffer.get(0) * scale);
        
        Texture bitmap       = null;
        boolean success      = false;
        int     textureWidth = 32;
        int     sampleSize   = 2;
        while (!success && textureWidth < 1000)
        {
            try (STBTTPackContext pc = STBTTPackContext.malloc())
            {
                bitmap = new Texture(textureWidth * this.size, this.size * sampleSize, 1);
                stbtt_PackBegin(pc, bitmap.data(), bitmap.width(), bitmap.height(), 0, 2, MemoryUtil.NULL);
                charData.position(32);
                stbtt_PackSetOversampling(pc, sampleSize, sampleSize);
                success = stbtt_PackFontRange(pc, this.data, 0, (float) this.size, 32, charData);
                charData.clear();
                bitmap.data().clear();
                stbtt_PackEnd(pc);
                textureWidth *= 2;
            }
        }
        bitmap.upload();
        this.bitmapMap.put(this.size, bitmap);
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
}