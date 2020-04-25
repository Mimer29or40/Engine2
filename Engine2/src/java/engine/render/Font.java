package engine.render;

import engine.util.Logger;
import org.lwjgl.stb.STBTTAlignedQuad;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBTTPackContext;
import org.lwjgl.stb.STBTTPackedchar;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;

import static engine.util.Util.resourceToByteBuffer;
import static org.lwjgl.stb.STBTruetype.*;

public class Font
{
    private static final Logger LOGGER = new Logger();
    
    private static final HashMap<String, String[]>   FONT_PATHS = new HashMap<>();
    private static final HashMap<String, Font>       FONT_CACHE = new HashMap<>();
    private static final HashMap<String, ByteBuffer> FILE_CACHE = new HashMap<>();
    
    private static final String DEFAULT_FONT_FAMILY = "default";
    private static final int    DEFAULT_FONT_SIZE   = 12;
    
    static
    {
        registerFont(Font.DEFAULT_FONT_FAMILY, "fonts/BetterPixels.ttf", null, null, null);
    }
    
    public static final Font DEFAULT_FONT = Font.getFont(Font.DEFAULT_FONT_FAMILY, Font.DEFAULT_FONT_SIZE, false, false);
    
    private final String name;
    private final int    size;
    
    private final boolean bold;
    private final boolean italic;
    
    private final double scale;
    private final double ascent;
    private final double descent;
    private final double lineGap;
    
    private Texture texture;
    
    private final STBTTFontinfo          info;
    private final STBTTPackedchar.Buffer charData;
    
    private Font(String name, String filePath, int size, boolean bold, boolean italic)
    {
        this.name = name;
        this.size = size;
        
        this.bold   = bold;
        this.italic = italic;
        
        if (!Font.FILE_CACHE.containsKey(filePath))
        {
            Font.LOGGER.finer("Loading new Font file:", filePath);
            Font.FILE_CACHE.put(filePath, resourceToByteBuffer(filePath));
        }
        ByteBuffer data = Font.FILE_CACHE.get(filePath);
        
        this.info = STBTTFontinfo.create();
        if (!stbtt_InitFont(this.info, data)) throw new RuntimeException("Font could not be loaded: " + getFontID(name, size, bold, italic));
        
        this.charData = STBTTPackedchar.create(128);
        
        Font.LOGGER.finer("Generating new font size for size=%s", this.size);
        
        this.scale = stbtt_ScaleForPixelHeight(this.info, this.size);
        
        try (MemoryStack stack = MemoryStack.stackPush())
        {
            IntBuffer ascent  = stack.mallocInt(1);
            IntBuffer descent = stack.mallocInt(1);
            IntBuffer lineGap = stack.mallocInt(1);
            
            stbtt_GetFontVMetrics(this.info, ascent, descent, lineGap);
            
            this.ascent  = ascent.get(0) * this.scale;
            this.descent = descent.get(0) * this.scale;
            this.lineGap = lineGap.get(0) * this.scale;
        }
        
        boolean success = false;
        
        int width   = 32;
        int samples = 2;
        while (!success && width < 1000)
        {
            try (STBTTPackContext pc = STBTTPackContext.malloc())
            {
                this.texture = new Texture(width * this.size, this.size * samples, 1);
                stbtt_PackBegin(pc, this.texture.data(), this.texture.width(), this.texture.height(), 0, 2, MemoryUtil.NULL);
                this.charData.position(32);
                stbtt_PackSetOversampling(pc, samples, samples);
                success = stbtt_PackFontRange(pc, data, 0, (float) this.size, this.charData.position(), this.charData);
                this.charData.clear();
                this.texture.data().clear();
                stbtt_PackEnd(pc);
                width *= 2;
            }
        }
        texture.bindTexture().upload();
    }
    
    @Override
    public String toString()
    {
        return "Font{" + getFontID(this.name, this.size, this.bold, this.italic) + '}';
    }
    
    /**
     * @return The name of the font family
     */
    public String name()
    {
        return this.name;
    }
    
    /**
     * @return The size of the font
     */
    public int size()
    {
        return this.size;
    }
    
    /**
     * @return If the font is bolded
     */
    public boolean bold()
    {
        return this.bold;
    }
    
    /**
     * @return If the font is italicized
     */
    public boolean italic()
    {
        return this.italic;
    }
    
    /**
     * @return Gets the scale of the font.
     */
    public double scale()
    {
        return this.scale;
    }
    
    /**
     * @return Gets the ascent of the font.
     */
    public double ascent()
    {
        return this.ascent;
    }
    
    /**
     * @return Gets the descent of the font.
     */
    public double descent()
    {
        return this.descent;
    }
    
    /**
     * @return Gets the lineGap of the font.
     */
    public double lineGap()
    {
        return this.lineGap;
    }
    
    /**
     * @return Gets the texture map of the font.
     */
    public Texture texture()
    {
        return this.texture;
    }
    
    /**
     * Calculates the width in pixels of the string. If the string contains line breaks, then it calculates the widest line and returns it.
     *
     * @param text The text.
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
                IntBuffer codePoint = stack.mallocInt(1);
                IntBuffer advance   = stack.mallocInt(1);
                IntBuffer bearing   = stack.mallocInt(1);
                
                for (int i = 0, n = text.length(); i < n; )
                {
                    i += getCP(text, n, i, codePoint);
                    int cp = codePoint.get(0);
                    stbtt_GetCodepointHMetrics(this.info, cp, advance, bearing);
                    width += advance.get(0);
                    
                    if (i < n)
                    {
                        getCP(text, n, i, codePoint);
                        width += stbtt_GetCodepointKernAdvance(this.info, cp, codePoint.get(0));
                    }
                }
                return width * this.scale;
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
     * @param text The text.
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
                stbtt_GetPackedQuad(this.charData, this.texture.width(), this.texture.height(), character, x, y, quad, false);
                if (character == ' ') continue;
                vertices[index]     = quad.x0();
                vertices[index + 1] = quad.y0() + this.ascent;
                vertices[index + 2] = quad.x1();
                vertices[index + 3] = quad.y1() + this.ascent;
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
        this.texture.destroy();
        this.texture = null;
    }
    
    public static void registerFont(String fontName, String fontPath, String boldPath, String italicPath, String boldItalicPath)
    {
        if (Font.FONT_PATHS.containsKey(fontName))
        {
            Font.LOGGER.info("Font already registered: " + fontName);
            return;
        }
        
        if (boldPath == null) boldPath = fontPath;
        if (italicPath == null) italicPath = fontPath;
        if (boldItalicPath == null) boldItalicPath = fontPath;
        
        Font.FONT_PATHS.put(fontName, new String[] {fontPath, boldPath, italicPath, boldItalicPath});
    }
    
    public static String getFontID(String name, int size, boolean bold, boolean italic)
    {
        StringBuilder id = new StringBuilder(name).append('_').append(size).append('_');
        if (bold && italic)
        {
            id.append("bold_italic");
        }
        else if (italic)
        {
            id.append("italic");
        }
        else if (bold)
        {
            id.append("bold");
        }
        else
        {
            id.append("regular");
        }
        return id.toString();
    }
    
    public static Font getFont(String name, int size, boolean bold, boolean italic)
    {
        String fontID = getFontID(name, size, bold, italic);
        
        if (Font.FONT_CACHE.containsKey(fontID)) return Font.FONT_CACHE.get(fontID);
        
        if (!Font.FONT_PATHS.containsKey(name))
        {
            Font.LOGGER.warning("Font not registered: " + name);
            return Font.DEFAULT_FONT;
        }
        
        String[] fontPaths = Font.FONT_PATHS.get(name);
        
        String filePath;
        if (bold && italic)
        {
            filePath = fontPaths[3];
        }
        else if (italic)
        {
            filePath = fontPaths[2];
        }
        else if (bold)
        {
            filePath = fontPaths[1];
        }
        else
        {
            filePath = fontPaths[0];
        }
        
        Font.FONT_CACHE.put(fontID, new Font(name, filePath, size, bold, italic));
        
        return Font.FONT_CACHE.get(fontID);
    }
    
    public static Font getFont(String name, int size)
    {
        return getFont(name, size, false, false);
    }
    
    public static Font getFont(String name)
    {
        return getFont(name, Font.DEFAULT_FONT_SIZE, false, false);
    }
    
    public static Font getFont(int size)
    {
        return getFont(Font.DEFAULT_FONT_FAMILY, size, false, false);
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
