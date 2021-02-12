package engine.font;

import engine.render.Texture;
import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBTTAlignedQuad;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBTTPackContext;
import org.lwjgl.stb.STBTTPackedchar;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import rutils.Logger;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Objects;

import static org.lwjgl.stb.STBTruetype.*;
import static rutils.IOUtil.resourceToByteBuffer;

@SuppressWarnings("unused")
public class Font
{
    private static final Logger LOGGER = new Logger();
    
    private static final HashMap<String, Font> CACHE = new HashMap<>();
    
    public static final String  DEFAULT_NAME    = "BetterPixels";
    public static final Weight  DEFAULT_WEIGHT  = Weight.REGULAR;
    public static final boolean DEFAULT_ITALICS = false;
    public static final int     DEFAULT_SIZE    = 24;
    
    static
    {
        register("fonts/BetterPixels-Regular.ttf", Font.DEFAULT_NAME, Font.DEFAULT_WEIGHT, Font.DEFAULT_ITALICS, true);
        register("fonts/Arial-Regular.ttf", "Arial", Font.DEFAULT_WEIGHT, Font.DEFAULT_ITALICS, true);
    }
    
    public static final Font DEFAULT = Font.get(Font.DEFAULT_NAME, Font.DEFAULT_WEIGHT, Font.DEFAULT_ITALICS);
    
    private final String  name;
    private final Weight  weight;
    private final boolean italicized;
    private final boolean kerning;
    
    private final String id;
    
    private final STBTTFontinfo info;
    private final ByteBuffer    fileData;
    
    private final int ascentUnscaled;
    private final int descentUnscaled;
    private final int lineGapUnscaled;
    
    private final HashMap<Integer, CharData> charData;
    private final HashMap<Integer, SizeData> sizeData;
    
    private Font(String filePath, String name, Weight weight, boolean italicized, boolean kerning)
    {
        this.name       = name;
        this.weight     = weight;
        this.italicized = italicized;
        this.kerning    = kerning;
        
        this.id = getID(this.name, this.weight, this.italicized);
        
        this.info     = STBTTFontinfo.create();
        this.fileData = resourceToByteBuffer(filePath);
        
        if (!stbtt_InitFont(this.info, this.fileData)) throw new RuntimeException("Font Data could not be loaded: " + this.id);
        
        
        try (MemoryStack stack = MemoryStack.stackPush())
        {
            IntBuffer ascent  = stack.mallocInt(1);
            IntBuffer descent = stack.mallocInt(1);
            IntBuffer lineGap = stack.mallocInt(1);
            
            stbtt_GetFontVMetrics(this.info, ascent, descent, lineGap);
            
            this.ascentUnscaled  = ascent.get(0);
            this.descentUnscaled = descent.get(0);
            this.lineGapUnscaled = lineGap.get(0);
            
            IntBuffer advanceWidth    = stack.mallocInt(1);
            IntBuffer leftSideBearing = stack.mallocInt(1);
            
            IntBuffer x0 = stack.mallocInt(1);
            IntBuffer y0 = stack.mallocInt(1);
            IntBuffer x1 = stack.mallocInt(1);
            IntBuffer y1 = stack.mallocInt(1);
            
            this.charData = new HashMap<>();
            for (int i = 0; i < 0xFFFF; i++)
            {
                this.charData.put(i, new CharData(i, advanceWidth, leftSideBearing, x0, y0, x1, y1));
            }
        }
        
        this.sizeData = new HashMap<>();
        
        getSizeData(Font.DEFAULT_SIZE);
    }
    
    @Override
    public String toString()
    {
        return "Font{" + this.id + '}';
    }
    
    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Font font = (Font) o;
        return this.id.equals(font.id);
    }
    
    @Override
    public int hashCode()
    {
        return Objects.hash(this.name, this.weight, this.italicized);
    }
    
    /**
     * @return The font name
     */
    public String name()
    {
        return this.name;
    }
    
    /**
     * @return The weight of the font
     */
    public Weight weight()
    {
        return this.weight;
    }
    
    /**
     * @return true if the font is italicized
     */
    public boolean italicized()
    {
        return this.italicized;
    }
    
    /**
     * @return true if font kerning will be respected
     */
    public boolean kerning()
    {
        return this.kerning;
    }
    
    /**
     * @return Gets the scale of the size data.
     */
    public double scale(int size)
    {
        return getSizeData(size).scale;
    }
    
    /**
     * @return Gets the ascent of the size data.
     */
    public double ascent(int size)
    {
        return getSizeData(size).ascent;
    }
    
    /**
     * @return Gets the descent of the size data.
     */
    public double descent(int size)
    {
        return getSizeData(size).descent;
    }
    
    /**
     * @return Gets the lineGap of the size data.
     */
    public double lineGap(int size)
    {
        return getSizeData(size).lineGap;
    }
    
    /**
     * @return Gets the texture map of the size data.
     */
    public Texture texture(int size)
    {
        return getSizeData(size).texture;
    }
    
    /**
     * Method to get the character data for a specific size, generating it if necessary.
     *
     * @param size The size in pixels.
     * @return The size data.
     */
    public SizeData getSizeData(int size)
    {
        return this.sizeData.computeIfAbsent(size, s -> {
            Font.LOGGER.finest("Generating SizeData for font \"%s\" for size \"%s\"", this, s);
            
            return new SizeData(s);
        });
    }
    
    /**
     * Gets a read-only class with the metrics for a specific character
     *
     * @param character The character.
     * @return The character data.
     */
    public CharData getCharData(int character)
    {
        return this.charData.get(character);
    }
    
    /**
     * Calculates the width in pixels of the string. If the string contains line breaks, then it calculates the widest line and returns it.
     *
     * @param text The text.
     * @param size The size of the text.
     * @return The width in pixels of the string.
     */
    public double getTextWidth(String text, int size)
    {
        Font.LOGGER.finest("Getting text width for text \"%s\" with font \"%s\" of size \"%s\"", text, this, size);
        
        SizeData sizeData = getSizeData(size);
        
        double width = 0;
        
        String[] lines = text.split("\n");
        if (lines.length == 1)
        {
            CharData currChar, prevChar = null;
            for (int i = 0, n = text.length(); i < n; i++)
            {
                currChar = getCharData(text.charAt(i));
                width += currChar.advanceWidthUnscaled + getKernAdvance(prevChar, currChar);
                
                prevChar = currChar;
            }
            return width * sizeData.scale;
        }
        else
        {
            for (String line : lines)
            {
                width = Math.max(width, getTextWidth(line, size));
            }
            return width;
        }
    }
    
    /**
     * Calculates the height in pixels of the string. If the string contains line breaks, then it calculates the total height of all lines.
     *
     * @param text The text.
     * @param size The size of the text.
     * @return The height in pixels of the string.
     */
    public double getTextHeight(String text, int size)
    {
        Font.LOGGER.finest("Getting text height for text \"%s\" with font \"%s\" of size \"%s\"", text, this, size);
        
        String[] lines = text.split("\n");
        return lines.length * size;
    }
    
    /**
     * Builds the quad to renderer a character at a particular size and location.
     *
     * @param charData The character to render.
     * @param sizeData The size to render the character at.
     * @param x        The x coordinate of the origin of the character
     * @param y        The y coordinate of the origin of the character
     * @param quad     Character information is stored here.
     */
    public void buildCharQuad(CharData charData, SizeData sizeData, FloatBuffer x, FloatBuffer y, FloatBuffer quad)
    {
        Font.LOGGER.finest("Rendering \"%s\" with \"%s\"", charData, sizeData);
        
        try (MemoryStack stack = MemoryStack.stackPush())
        {
            STBTTAlignedQuad q = STBTTAlignedQuad.mallocStack(stack);
            
            stbtt_GetPackedQuad(sizeData.charData, sizeData.texture.width(), sizeData.texture.height(), charData.character, x, y, q, false);
            
            quad.put(q.x0());
            quad.put(q.y0());
            quad.put(q.x1());
            quad.put(q.y1());
            quad.put(q.s0());
            quad.put(q.t0());
            quad.put(q.s1());
            quad.put(q.t1());
            
            quad.clear();
        }
    }
    
    /**
     * Gets the kerning between two characters. If kerning is disabled then this offset is zero.
     *
     * @param ch1 The first character.
     * @param ch2 The second character.
     * @return The number of pixels to offset ch2 when rendering.
     */
    public int getKernAdvance(CharData ch1, CharData ch2)
    {
        if (ch1 == null) return 0;
        if (ch2 == null) return 0;
        if (!this.kerning) return 0;
        return stbtt_GetGlyphKernAdvance(this.info, ch1.index, ch2.index);
    }
    
    /**
     * Destroys the Font and free's it memory.
     */
    public void destroy()
    {
        Font.LOGGER.fine("Destroying Font: %s", this);
        
        this.sizeData.forEach((i, sizeData) -> sizeData.texture.destroy());
        this.sizeData.clear();
    }
    
    /**
     * Registers a font to be used. All fonts needs to be registered before they can be used. Font instances are owned by this class.
     * <p>
     * There is no checking if the characteristics provided actually match the font.
     *
     * @param filePath   The path to the .ttf file
     * @param name       The registry name of the font.
     * @param weight     The weight of the font.
     * @param italicized If the font is italicized
     * @param kerning    If kerning should be used when rendering.
     */
    public static void register(String filePath, String name, Weight weight, boolean italicized, boolean kerning)
    {
        String fontID = getID(name, weight, italicized);
        
        if (Font.CACHE.containsKey(fontID))
        {
            Font.LOGGER.warning("Font already registered: " + fontID);
            return;
        }
        
        Font.LOGGER.fine("Registering Font: " + fontID);
        
        Font.CACHE.put(fontID, new Font(filePath, name, weight, italicized, kerning));
    }
    
    /**
     * Gets a font with the specified properties.
     *
     * @param name       The font name.
     * @param weight     The weight of the font.
     * @param italicized Whether the font is italic styled or not.
     * @return The font object.
     */
    public static Font get(String name, Weight weight, boolean italicized)
    {
        String fontID = getID(name, weight, italicized);
        
        if (Font.CACHE.containsKey(fontID)) return Font.CACHE.get(fontID);
        
        Font.LOGGER.warning("Font is not registered: " + fontID);
        
        return Font.DEFAULT;
    }
    
    /**
     * Gets a font with the specified properties.
     *
     * @param name   The font name.
     * @param weight The weight of the font.
     * @return The font object.
     */
    public static Font get(String name, Weight weight)
    {
        return get(name, weight, Font.DEFAULT_ITALICS);
    }
    
    /**
     * Gets a font with the specified properties.
     *
     * @param name       The font name.
     * @param italicized Whether the font is italic styled or not.
     * @return The font object.
     */
    public static Font get(String name, boolean italicized)
    {
        return get(name, Font.DEFAULT_WEIGHT, italicized);
    }
    
    /**
     * Gets a font with the specified properties.
     *
     * @param weight     The weight of the font.
     * @param italicized Whether the font is italic styled or not.
     * @return The font object.
     */
    public static Font get(Weight weight, boolean italicized)
    {
        return get(Font.DEFAULT_NAME, weight, italicized);
    }
    
    /**
     * Gets a font with the specified properties.
     *
     * @param name The font name.
     * @return The font object.
     */
    public static Font get(String name)
    {
        return get(name, Font.DEFAULT_WEIGHT, Font.DEFAULT_ITALICS);
    }
    
    /**
     * Gets a font with the specified properties.
     *
     * @param weight The weight of the font.
     * @return The font object.
     */
    public static Font get(Weight weight)
    {
        return get(Font.DEFAULT_NAME, weight, Font.DEFAULT_ITALICS);
    }
    
    /**
     * Gets a font with the specified properties.
     *
     * @param italicized Whether the font is italic styled or not.
     * @return The font object.
     */
    public static Font get(boolean italicized)
    {
        return get(Font.DEFAULT_NAME, Font.DEFAULT_WEIGHT, italicized);
    }
    
    /**
     * Checks if the font is registered.
     *
     * @param name       The font name.
     * @param weight     The weight of the font.
     * @param italicized Whether the font is italic styled or not.
     * @return {@code true} if the font is registered.
     */
    public static boolean exists(String name, Weight weight, boolean italicized)
    {
        return Font.CACHE.containsKey(getID(name, weight, italicized));
    }
    
    /**
     * Builds a font tag with the provided properties.
     *
     * @param name       The font name
     * @param weight     The weight of the font
     * @param italicized If the font is italicized.
     * @return The tag string.
     */
    public static String getID(String name, Weight weight, boolean italicized)
    {
        return name + '_' + weight.tag() + (italicized ? "_italicized" : "");
    }
    
    public class SizeData
    {
        public final int   size;
        public final float scale;
        
        public final float ascent;
        public final float descent;
        public final float lineGap;
        
        private final STBTTPackedchar.Buffer charData;
        
        private final Texture texture;
        
        private SizeData(int size)
        {
            this.size  = size;
            this.scale = stbtt_ScaleForPixelHeight(Font.this.info, this.size);
            
            this.ascent  = Font.this.ascentUnscaled * this.scale;
            this.descent = Font.this.descentUnscaled * this.scale;
            this.lineGap = Font.this.lineGapUnscaled * this.scale;
            
            this.charData = STBTTPackedchar.create(0xFFFF);
            
            int width  = 0;
            int height = 0;
            
            ByteBuffer buffer = null;
            
            boolean success = false;
            
            int textureSize = 32;
            int samples     = 2;
            while (!success && textureSize < 1000)
            {
                width  = this.size * textureSize;
                height = this.size * (textureSize >> 1);
                
                buffer = BufferUtils.createByteBuffer(width * height);
                
                this.charData.position(32);
                try (STBTTPackContext pc = STBTTPackContext.malloc())
                {
                    stbtt_PackBegin(pc, buffer, width, height, 0, 2, MemoryUtil.NULL);
                    stbtt_PackSetOversampling(pc, samples, samples);
                    success = stbtt_PackFontRange(pc, Font.this.fileData, 0, this.size, this.charData.position(), this.charData);
                    stbtt_PackEnd(pc);
                }
                this.charData.clear();
                buffer.clear();
                
                textureSize <<= 1;
            }
            
            this.texture = Texture.loadFromBuffer(width, height, 1, buffer);
        }
        
        @Override
        public String toString()
        {
            return "SizeData{" + this.size + ", font=" + Font.this.toString() + "}";
        }
    }
    
    public class CharData
    {
        public final char character;
        public final int  index;
        
        public final int advanceWidthUnscaled;
        public final int leftSideBearingUnscaled;
        
        public final int x0Unscaled;
        public final int y0Unscaled;
        public final int x1Unscaled;
        public final int y1Unscaled;
        
        private CharData(int character, IntBuffer advanceWidth, IntBuffer leftSideBearing, IntBuffer x0, IntBuffer y0, IntBuffer x1, IntBuffer y1)
        {
            this.character = (char) character;
            this.index     = stbtt_FindGlyphIndex(Font.this.info, this.character);
            
            stbtt_GetGlyphHMetrics(Font.this.info, this.index, advanceWidth, leftSideBearing);
            
            this.advanceWidthUnscaled    = advanceWidth.get(0);
            this.leftSideBearingUnscaled = leftSideBearing.get(0);
            
            stbtt_GetGlyphBox(Font.this.info, this.index, x0, y0, x1, y1);
            
            this.x0Unscaled = x0.get(0);
            this.y0Unscaled = y0.get(0);
            this.x1Unscaled = x1.get(0);
            this.y1Unscaled = y1.get(0);
        }
        
        public double advanceWidth(int size)
        {
            return this.advanceWidthUnscaled * getSizeData(size).scale;
        }
        
        public double leftSideBearing(int size)
        {
            return this.leftSideBearingUnscaled * getSizeData(size).scale;
        }
        
        public double x0(int size)
        {
            return this.x0Unscaled * getSizeData(size).scale;
        }
        
        public double y0(int size)
        {
            return this.y0Unscaled * getSizeData(size).scale;
        }
        
        public double x1(int size)
        {
            return this.x1Unscaled * getSizeData(size).scale;
        }
        
        public double y1(int size)
        {
            return this.y1Unscaled * getSizeData(size).scale;
        }
        
        @Override
        public String toString()
        {
            return "CharData{" + this.character + ", font=" + Font.this.toString() + "}";
        }
    }
}
