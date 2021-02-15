package engine.color;

import rutils.Logger;

import java.util.Objects;

import static rutils.NumUtil.clamp;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public class Color implements Colorc
{
    private static final Logger LOGGER = new Logger();
    
    public static final Colorc WHITE      = new Color(255, 255, 255);
    public static final Colorc LIGHT_GREY = new Color(191, 191, 191);
    public static final Colorc GREY       = new Color(127, 127, 127);
    public static final Colorc DARK_GREY  = new Color(63, 63, 63);
    public static final Colorc BLACK      = new Color(0, 0, 0);
    
    public static final Colorc LIGHTEST_RED = new Color(255, 191, 191);
    public static final Colorc LIGHTER_RED  = new Color(255, 127, 127);
    public static final Colorc LIGHT_RED    = new Color(255, 63, 63);
    public static final Colorc RED          = new Color(255, 0, 0);
    public static final Colorc DARK_RED     = new Color(191, 0, 0);
    public static final Colorc DARKER_RED   = new Color(127, 0, 0);
    public static final Colorc DARKEST_RED  = new Color(63, 0, 0);
    
    public static final Colorc LIGHTEST_YELLOW = new Color(255, 255, 191);
    public static final Colorc LIGHTER_YELLOW  = new Color(255, 255, 127);
    public static final Colorc LIGHT_YELLOW    = new Color(255, 255, 63);
    public static final Colorc YELLOW          = new Color(255, 255, 0);
    public static final Colorc DARK_YELLOW     = new Color(191, 191, 0);
    public static final Colorc DARKER_YELLOW   = new Color(127, 127, 0);
    public static final Colorc DARKEST_YELLOW  = new Color(63, 63, 0);
    
    public static final Colorc LIGHTEST_GREEN = new Color(191, 255, 191);
    public static final Colorc LIGHTER_GREEN  = new Color(127, 255, 127);
    public static final Colorc LIGHT_GREEN    = new Color(63, 255, 63);
    public static final Colorc GREEN          = new Color(0, 255, 0);
    public static final Colorc DARK_GREEN     = new Color(0, 191, 0);
    public static final Colorc DARKER_GREEN   = new Color(0, 127, 0);
    public static final Colorc DARKEST_GREEN  = new Color(0, 63, 0);
    
    public static final Colorc LIGHTEST_CYAN = new Color(191, 255, 255);
    public static final Colorc LIGHTER_CYAN  = new Color(127, 255, 255);
    public static final Colorc LIGHT_CYAN    = new Color(63, 255, 255);
    public static final Colorc CYAN          = new Color(0, 255, 255);
    public static final Colorc DARK_CYAN     = new Color(0, 191, 191);
    public static final Colorc DARKER_CYAN   = new Color(0, 127, 127);
    public static final Colorc DARKEST_CYAN  = new Color(0, 63, 63);
    
    public static final Colorc LIGHTEST_BLUE = new Color(191, 191, 255);
    public static final Colorc LIGHTER_BLUE  = new Color(127, 127, 255);
    public static final Colorc LIGHT_BLUE    = new Color(63, 63, 255);
    public static final Colorc BLUE          = new Color(0, 0, 255);
    public static final Colorc DARK_BLUE     = new Color(0, 0, 191);
    public static final Colorc DARKER_BLUE   = new Color(0, 0, 127);
    public static final Colorc DARKEST_BLUE  = new Color(0, 0, 63);
    
    public static final Colorc LIGHTEST_MAGENTA = new Color(255, 191, 255);
    public static final Colorc LIGHTER_MAGENTA  = new Color(255, 127, 255);
    public static final Colorc LIGHT_MAGENTA    = new Color(255, 63, 255);
    public static final Colorc MAGENTA          = new Color(255, 0, 255);
    public static final Colorc DARK_MAGENTA     = new Color(191, 0, 191);
    public static final Colorc DARKER_MAGENTA   = new Color(127, 0, 127);
    public static final Colorc DARKEST_MAGENTA  = new Color(63, 0, 63);
    
    public static final Colorc BLANK = new Color(0, 0, 0, 0);
    
    private static final double PR = 0.299;
    private static final double PG = 0.587;
    private static final double PB = 0.114;
    
    private int r, g, b, a;
    
    private int minInd, midInd, maxInd;
    
    public Color(Number r, Number g, Number b, Number a)
    {
        this.r = toColorInt(r);
        this.g = toColorInt(g);
        this.b = toColorInt(b);
        this.a = toColorInt(a);
        computeIndices();
    }
    
    public Color(Number r, Number g, Number b)
    {
        this(r, g, b, 255);
    }
    
    public Color(Number grey, Number a)
    {
        this(grey, grey, grey, a);
    }
    
    public Color(Number grey)
    {
        this(grey, grey, grey, 255);
    }
    
    public Color(Colorc color)
    {
        this(color.r(), color.g(), color.b(), color.a());
    }
    
    public Color(String hex)
    {
        fromHex(hex);
    }
    
    public Color()
    {
        this(0, 0, 0, 255);
    }
    
    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof Colorc)) return false;
        Colorc color = (Colorc) o;
        return equals(color.r(), color.g(), color.b(), color.a());
    }
    
    @Override
    public int hashCode()
    {
        return Objects.hash(this.r, this.g, this.b, this.a);
    }
    
    @Override
    public String toString()
    {
        return "Color{r=" + this.r + ", g=" + this.g + ", b=" + this.b + ", a=" + this.a + "}";
    }
    
    private Color thisOrNew()
    {
        return this;
    }
    
    private void computeIndices()
    {
        if (this.r >= this.g)
        {
            if (this.r >= this.b)
            {
                if (this.g >= this.b)
                {
                    // Case 3 r >= g >= b
                    this.minInd = 2;
                    this.midInd = 1;
                }
                else
                {
                    // Case 2 r >= b > g
                    this.minInd = 1;
                    this.midInd = 2;
                }
                this.maxInd = 0;
            }
            else
            {
                // Case 4 b > r >= g
                this.minInd = 1;
                this.midInd = 0;
                this.maxInd = 2;
            }
        }
        else
        {
            if (this.r >= this.b)
            {
                // Case 1 g > r >= b
                this.minInd = 2;
                this.midInd = 0;
                this.maxInd = 1;
            }
            else
            {
                this.minInd = 0;
                if (this.g >= this.b)
                {
                    // Case 5 g >= b > r
                    this.midInd = 2;
                    this.maxInd = 1;
                }
                else
                {
                    // Case 0 b > g > r
                    this.midInd = 1;
                    this.maxInd = 2;
                }
            }
        }
    }
    
    /**
     * @return the value of the r component [0..255]
     */
    @Override
    public int r()
    {
        return this.r;
    }
    
    /**
     * @return the value of the r component [0..1]
     */
    @Override
    public float rf()
    {
        return r() / 255F;
    }
    
    public void r(Number r)
    {
        this.r = toColorInt(r);
        computeIndices();
    }
    
    /**
     * @return the value of the g component [0..255]
     */
    @Override
    public int g()
    {
        return this.g;
    }
    
    /**
     * @return the value of the g component [0..1]
     */
    @Override
    public float gf()
    {
        return g() / 255F;
    }
    
    public void g(Number g)
    {
        this.g = toColorInt(g);
        computeIndices();
    }
    
    /**
     * @return the value of the b component [0..255]
     */
    @Override
    public int b()
    {
        return this.b;
    }
    
    /**
     * @return the value of the b component [0..1]
     */
    @Override
    public float bf()
    {
        return b() / 255F;
    }
    
    public void b(Number b)
    {
        this.b = toColorInt(b);
        computeIndices();
    }
    
    /**
     * @return the value of the a component [0..255]
     */
    @Override
    public int a()
    {
        return this.a;
    }
    
    /**
     * @return the value of the a component [0..1]
     */
    @Override
    public float af()
    {
        return a() / 255F;
    }
    
    public void a(Number a)
    {
        this.a = toColorInt(a);
    }
    
    public Color set(Number r, Number g, Number b, Number a)
    {
        this.r = toColorInt(r);
        this.g = toColorInt(g);
        this.b = toColorInt(b);
        this.a = toColorInt(a);
        computeIndices();
    
        return this;
    }
    
    public Color set(Number r, Number g, Number b)
    {
        return set(r, g, b, 255);
    }
    
    public Color set(Number grey, Number a)
    {
        return set(grey, grey, grey, a);
    }
    
    public Color set(Number grey)
    {
        return set(grey, grey, grey, 255);
    }
    
    public Color set(Colorc p)
    {
        return set(p.r(), p.g(), p.b(), p.a());
    }
    
    /**
     * @return 32-bit integer representation (argb) of the color
     */
    @Override
    public int toInt()
    {
        return (this.a << 24) | (this.r << 16) | (this.g << 8) | this.b;
    }
    
    /**
     * Sets this color to the value described by a 32-bit integer (argb).
     *
     * @param x the 32-bit integer
     * @return this
     */
    public Color fromInt(int x)
    {
        return set((x >>> 16) & 0xFF, (x >>> 8) & 0xFF, x & 0xFF, (x >>> 24) & 0xFF);
    }
    
    /**
     * @return The Hex String representation of the color.
     */
    @Override
    public String toHex()
    {
        return String.format("#%02X%02X%02X%02X", this.r, this.g, this.b, this.a);
    }
    
    /**
     * Sets this color to the value described by a hex string (#AARRGGBB or #RRGGBB).
     *
     * @param hex the hex string
     * @return this
     */
    public Color fromHex(String hex)
    {
        if (!hex.startsWith("#")) throw new RuntimeException("Invalid Hex Color String: " + hex);
        if (hex.length() == 7)
        {
            return set(Integer.parseInt(hex.substring(1, 3), 16),
                       Integer.parseInt(hex.substring(3, 5), 16),
                       Integer.parseInt(hex.substring(5, 7), 16),
                       255);
        }
        else if (hex.length() == 9)
        {
            return set(Integer.parseInt(hex.substring(1, 3), 16),
                       Integer.parseInt(hex.substring(3, 5), 16),
                       Integer.parseInt(hex.substring(5, 7), 16),
                       Integer.parseInt(hex.substring(7, 9), 16));
        }
        throw new RuntimeException("Invalid Hex Color String: " + hex);
    }
    
    public Color fromHSB(int h, int s, int b)
    {
        if (s == 0) return set(b, b, b);
        
        h = h * 255 / 359;
        
        int region    = h / 43;
        int remainder = (h - (region * 43)) * 6;
        
        int p = (b * (255 - s)) >> 8;
        int q = (b * (255 - ((s * remainder) >> 8))) >> 8;
        int t = (b * (255 - ((s * (255 - remainder)) >> 8))) >> 8;
        
        switch (region)
        {
            case 0 -> {
                this.r = b;
                this.g = t;
                this.b = p;
            }
            case 1 -> {
                this.r = q;
                this.g = b;
                this.b = p;
            }
            case 2 -> {
                this.r = p;
                this.g = b;
                this.b = t;
            }
            case 3 -> {
                this.r = p;
                this.g = q;
                this.b = b;
            }
            case 4 -> {
                this.r = t;
                this.g = p;
                this.b = b;
            }
            default -> {
                this.r = b;
                this.g = p;
                this.b = q;
            }
        }
        return this;
    }
    
    /**
     * Get the value of the specified component of this color.
     *
     * @param component the component, within <code>[0..3]</code>
     * @return the value
     * @throws IllegalArgumentException if <code>component</code> is not within <code>[0..3]</code>
     */
    @Override
    public int getComponent(int component) throws IllegalArgumentException
    {
        return switch (component)
                {
                    case 0 -> this.r;
                    case 1 -> this.g;
                    case 2 -> this.b;
                    case 3 -> this.a;
                    default -> throw new IllegalArgumentException();
                };
    }
    
    public Color setComponent(int component, Number value) throws IllegalArgumentException
    {
        switch (component)
        {
            case 0 -> this.r = toColorInt(value);
            case 1 -> this.g = toColorInt(value);
            case 2 -> this.b = toColorInt(value);
            case 3 -> this.a = toColorInt(value);
            default -> throw new IllegalArgumentException();
        }
        computeIndices();
        return this;
    }
    
    /**
     * Compare the color components of <code>this</code> color with the given <code>(r, g, b, a)</code>
     * and return whether all of them are equal.
     *
     * @param r the r component to compare to
     * @param g the g component to compare to
     * @param b the b component to compare to
     * @param a the a component to compare to
     * @return <code>true</code> if all the color components are equal
     */
    @Override
    public boolean equals(int r, int g, int b, int a)
    {
        return this.r == r && this.g == g && this.b == b && this.a == a;
    }
    
    /**
     * @return the hue of the color [0..359]
     */
    @Override
    public int hue()
    {
        int max = maxComponent();
        int min = minComponent();
    
        if (max == 0 || max - min == 0) return 0;
    
        int h = switch (this.maxInd)
                {
                    // Red is Max
                    case 0 -> 43 * (this.g - this.b) / (max - min);
                    // Green is Max
                    case 1 -> 85 + 43 * (this.b - this.r) / (max - min);
                    // Blue is Max
                    case 2 -> 171 + 43 * (this.r - this.g) / (max - min);
                    default -> 0;
                };
        return h * 359 / 255;
    }
    
    /**
     * @return the saturation of the color [0..255]
     */
    @Override
    public int saturation()
    {
        int max = maxComponent();
        int min = minComponent();
        
        if (max == 0) return 0;
        
        return (max - min) * 255 / max;
    }
    
    /**
     * @return the luminosity of the color
     */
    @Override
    public int brightness()
    {
        return maxComponent();
    }
    
    /**
     * Determine the component with the smallest (towards zero) absolute value.
     *
     * @return the component, within <code>[0..255]</code>
     */
    @Override
    public int minComponent()
    {
        return getComponent(this.minInd);
    }
    
    /**
     * Determine the component with the middle (towards zero) absolute value.
     *
     * @return the component, within <code>[0..255]</code>
     */
    @Override
    public int midComponent()
    {
        return getComponent(this.midInd);
    }
    
    /**
     * Determine the component with the biggest absolute value.
     *
     * @return the component, within <code>[0..255]</code>
     */
    @Override
    public int maxComponent()
    {
        return getComponent(this.maxInd);
    }
    
    /**
     * Negate this color.
     *
     * @return a color holding the result
     */
    public Color negate()
    {
        return negate(thisOrNew());
    }
    
    /**
     * Negate this color and store the result in <code>dest</code>.
     *
     * @param dest will hold the result
     * @return dest
     */
    @Override
    public Color negate(Color dest)
    {
        dest.r = 255 - this.r;
        dest.g = 255 - this.g;
        dest.b = 255 - this.b;
    
        dest.computeIndices();
    
        return dest;
    }
    
    /**
     * Scales this color in place
     *
     * @param x scale
     * @return this
     */
    public Color scale(double x)
    {
        return scale(x, thisOrNew());
    }
    
    /**
     * Scales this color in place
     *
     * @param x     scale
     * @param alpha flag to scale the alpha (default: false)
     * @return this
     */
    public Color scale(double x, boolean alpha)
    {
        return scale(x, alpha, thisOrNew());
    }
    
    /**
     * Scales this color and stores the result in <code>dest</code>.
     *
     * @param x     scale
     * @param alpha flag to scale the alpha (default: false)
     * @param dest  will hold the result
     * @return dest
     */
    public Color scale(double x, boolean alpha, Color dest)
    {
        dest.r = toColorInt(rf() * x);
        dest.g = toColorInt(gf() * x);
        dest.b = toColorInt(bf() * x);
        if (alpha) dest.a = toColorInt(af() * x);
        
        dest.computeIndices();
        
        return dest;
    }
    
    /**
     * Returns this color that is a factor brighter.
     *
     * @param factor the factor
     * @return this
     */
    public Color brighter(double factor)
    {
        return brighter(factor, thisOrNew());
    }
    
    /**
     * Returns a color that is brighter than this by a factor.
     *
     * @param factor the factor
     * @param dest   will hold the result
     * @return dest
     */
    public Color brighter(double factor, Color dest)
    {
        int i = (int) (1.0 / (1.0 - factor));
        if (this.r == 0 && this.g == 0 && this.b == 0)
        {
            dest.r = i;
            dest.g = i;
            dest.b = i;
        }
        else
        {
            int r = this.r;
            int g = this.g;
            int b = this.b;
            
            if (0 < this.r && this.r < i) r = i;
            if (0 < this.g && this.g < i) g = i;
            if (0 < this.b && this.b < i) b = i;
            
            dest.r = Math.min((int) (r / factor), 255);
            dest.g = Math.min((int) (g / factor), 255);
            dest.b = Math.min((int) (b / factor), 255);
        }
        dest.a = this.a;
        
        dest.computeIndices();
        
        return dest;
    }
    
    /**
     * Returns this color that is a factor darker.
     *
     * @param factor the factor
     * @return this
     */
    public Color darker(double factor)
    {
        return darker(factor, thisOrNew());
    }
    
    /**
     * Returns a color that is darker than this by a factor.
     *
     * @param factor the factor
     * @param dest   will hold the result
     * @return dest
     */
    public Color darker(double factor, Color dest)
    {
        dest.r = Math.max((int) (this.r * factor), 0);
        dest.g = Math.max((int) (this.g * factor), 0);
        dest.b = Math.max((int) (this.b * factor), 0);
        dest.a = this.a;
        
        dest.computeIndices();
        
        return dest;
    }
    
    /**
     * Returns a color that is tinted by the color.
     *
     * @param tint the tint
     * @param dest will hold the result
     * @return dest
     */
    @Override
    public Color tint(Colorc tint, Color dest)
    {
        if (tint.r() < 255) dest.r = this.r * tint.r() / 255;
        if (tint.g() < 255) dest.g = this.g * tint.g() / 255;
        if (tint.b() < 255) dest.b = this.b * tint.b() / 255;
        if (tint.a() < 255) dest.a = this.a * tint.a() / 255;
        
        dest.computeIndices();
        
        return dest;
    }
    
    /**
     * Returns this color that is a tinted.
     *
     * @param tint the tint
     * @return this
     */
    public Color tint(Colorc tint)
    {
        return tint(tint, thisOrNew());
    }
    
    /**
     * Returns a color that is interpolated between this color and other.
     *
     * @param other  the other color
     * @param amount the amount to interpolate
     * @param dest   will hold the result
     * @return dest
     */
    @Override
    public Color interpolate(Colorc other, double amount, Color dest)
    {
        if (amount <= 0)
        {
            dest.r = this.r;
            dest.g = this.g;
            dest.b = this.b;
            dest.a = this.a;
        }
        else if (1 <= amount)
        {
            dest.r = other.r();
            dest.g = other.g();
            dest.b = other.b();
            dest.a = other.a();
        }
        else
        {
            double inverse = 1.0 - amount;
            dest.r = toColorInt(inverse * rf() + amount * other.rf());
            dest.g = toColorInt(inverse * gf() + amount * other.gf());
            dest.b = toColorInt(inverse * bf() + amount * other.bf());
            dest.a = toColorInt(inverse * af() + amount * other.af());
        }
        dest.computeIndices();
        
        return dest;
    }
    
    /**
     * Returns this color that is interpolated between this color and other.
     *
     * @param other  the other color
     * @param amount the amount to interpolate
     * @return this
     */
    public Color interpolate(Colorc other, double amount)
    {
        return interpolate(other, amount, thisOrNew());
    }
    
    private static int toColorInt(Number x)
    {
        if (x instanceof Integer) return clamp((int) x, 0, 255);
        if (x instanceof Double) return clamp((int) ((double) x * 255), 0, 255);
        if (x instanceof Float) return clamp((int) ((float) x * 255), 0, 255);
        if (x instanceof Long) return clamp((int) ((long) x), 0, 255);
        if (x instanceof Short) return clamp((short) x, 0, 255);
        if (x instanceof Byte) return clamp((byte) x, 0, 255);
        throw new RuntimeException("Invalid number type: " + x.getClass());
    }
}
