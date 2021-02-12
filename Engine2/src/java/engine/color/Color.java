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
    
    public static final Colorc BACKGROUND_GREY = new Color(51, 51, 51);
    
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
    
    public Color(Number r, Number g, Number b, Number a)
    {
        r(r).g(g).b(b).a(a);
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
    
    private Color thisOrNew()
    {
        return this;
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
        return (float) r() / 255F;
    }
    
    public Color r(Number r)
    {
        this.r = toColorInt(r);
        return this;
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
        return (float) g() / 255F;
    }
    
    public Color g(Number g)
    {
        this.g = toColorInt(g);
        return this;
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
        return (float) b() / 255F;
    }
    
    public Color b(Number b)
    {
        this.b = toColorInt(b);
        return this;
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
        return (float) a() / 255F;
    }
    
    public Color a(Number a)
    {
        this.a = toColorInt(a);
        return this;
    }
    
    public Color set(Number r, Number g, Number b, Number a)
    {
        return r(r).g(g).b(b).a(a);
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
    
    public Color fromHSB(int h, int s, int b)
    {
        if (s == 0) return set(b, b, b);
    
        h = h * 255 / 359;
    
        int region    = h / 43;
        int remainder = (h - (region * 43)) * 6;
    
        int p = (b * (255 - s)) >> 8;
        int q = (b * (255 - ((s * remainder) >> 8))) >> 8;
        int t = (b * (255 - ((s * (255 - remainder)) >> 8))) >> 8;
    
        return switch (region)
                {
                    case 0 -> set(b, t, p);
                    case 1 -> set(q, b, p);
                    case 2 -> set(p, b, t);
                    case 3 -> set(p, q, b);
                    case 4 -> set(t, p, b);
                    default -> set(b, p, q);
                };
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
        return switch (component)
                {
                    case 0 -> r(value);
                    case 1 -> g(value);
                    case 2 -> b(value);
                    case 3 -> a(value);
                    default -> throw new IllegalArgumentException();
                };
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
        return r() == r && g() == g && b() == b && a() == a;
    }
    
    /**
     * Blends the supplied <code>(r, g, b, a)</code> (source) with <code>this</code> (backdrop) according
     * to the blend function and stores these values in <code>dest</code>.
     *
     * @param r      the r component of source
     * @param g      the g component of source
     * @param b      the b component of source
     * @param a      the a component of source
     * @param func   the function to blend the colors
     * @param result will hold the result
     * @return dest
     */
    @Override
    public Color blend(int r, int g, int b, int a, IBlend func, Color result)
    {
        return func.blend(this, r, g, b, a, result);
    }
    
    /**
     * @return 32-bit integer representation of the color
     */
    @Override
    public int toInt()
    {
        return r() | (g() << 8) | (b() << 16) | (a() << 24);
    }
    
    /**
     * @return The Hex String representation of the color.
     */
    @Override
    public String toHex()
    {
        return String.format("#%02X%02X%02X%02X", r(), g(), b(), a());
    }
    
    /**
     * @return the hue of the color [0..359]
     */
    public int hue()
    {
        int max = maxComponent();
        int min = minComponent();
    
        if (max == 0 || max - min == 0) return 0;
    
        int h = switch (maxComponentIndex())
                {
                    // Red is Max
                    case 0 -> 43 * (g() - b()) / (max - min);
                    // Green is Max
                    case 1 -> 85 + 43 * (b() - r()) / (max - min);
                    // Blue is Max
                    case 2 -> 171 + 43 * (r() - g()) / (max - min);
                    default -> 0;
                };
        return h * 359 / 255;
    }
    
    /**
     * @return the saturation of the color [0..255]
     */
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
     * Determine the component with the biggest absolute value.
     *
     * @return the component, within <code>[0..255]</code>
     */
    @Override
    public int maxComponent()
    {
        return getComponent(maxComponentIndex());
    }
    
    /**
     * Determine the component with the middle (towards zero) absolute value.
     *
     * @return the component, within <code>[0..255]</code>
     */
    public int midComponent()
    {
        return getComponent(midComponentIndex());
    }
    
    /**
     * Determine the component with the smallest (towards zero) absolute value.
     *
     * @return the component, within <code>[0..255]</code>
     */
    @Override
    public int minComponent()
    {
        return getComponent(minComponentIndex());
    }
    
    /**
     * Determine the component with the biggest absolute value.
     *
     * @return the component index, within <code>[0..2]</code>
     */
    @Override
    public int maxComponentIndex()
    {
        if (r() >= g() && r() >= b()) return 0;
        if (g() >= r() && g() >= b()) return 1;
        if (b() >= r() && b() >= g()) return 2;
        return 0;
    }
    
    /**
     * Determine the component with the middle (towards zero) absolute value.
     *
     * @return the component index, within <code>[0..2]</code>
     */
    public int midComponentIndex()
    {
        int min = minComponentIndex();
        int max = maxComponentIndex();
        if (min == 0) return max == 1 ? 2 : 1;
        if (min == 1) return max == 0 ? 2 : 0;
        if (min == 2) return max == 0 ? 1 : 0;
        return 0;
    }
    
    /**
     * Determine the component with the smallest (towards zero) absolute value.
     *
     * @return the component index, within <code>[0..2]</code>
     */
    @Override
    public int minComponentIndex()
    {
        if (r() <= g() && r() <= b()) return 0;
        if (g() <= r() && g() <= b()) return 1;
        if (b() <= r() && b() <= g()) return 2;
        return 0;
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
        dest.r(255 - r());
        dest.g(255 - g());
        dest.b(255 - b());
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
     * @param x      scale
     * @param alpha  flag to scale the alpha (default: false)
     * @param result will hold the result
     * @return dest
     */
    public Color scale(double x, boolean alpha, Color result)
    {
        result.r((int) (r() * x));
        result.g((int) (g() * x));
        result.b((int) (b() * x));
        if (alpha) result.a((int) (a() * x));
        return result;
    }
    
    /**
     * Blend this color with another color in place
     *
     * @param other the other color
     * @return this
     */
    public Color blend(Color other)
    {
        return blend(other, thisOrNew());
    }
    
    /**
     * Negate this color and store the result in <code>dest</code>.
     *
     * @param other the other color
     * @param func  the function that will blend the two colors
     * @return this
     */
    public Color blend(Color other, IBlend func)
    {
        return blend(other, func, thisOrNew());
    }
    
    /**
     * Negate this color and store the result in <code>dest</code>.
     *
     * @param other  the other color
     * @param func   the function that will blend the two colors
     * @param result will hold the result
     * @return result
     */
    public Color blend(Color other, IBlend func, Color result)
    {
        return func.blend(this, other, result);
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
     * @param result the dest
     * @return dest
     */
    public Color brighter(double factor, Color result)
    {
        int r = r();
        int g = g();
        int b = b();
        int a = a();
        
        int i = (int) (1.0 / (1.0 - factor));
        if (r == 0 && g == 0 && b == 0) return result.set(i, i, i, a);
        
        if (0 < r && r < i) r = i;
        if (0 < g && g < i) g = i;
        if (0 < b && b < i) b = i;
        
        return result.set(Math.min((int) (r / factor), 255), Math.min((int) (g / factor), 255), Math.min((int) (b / factor), 255), a);
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
     * @param result the dest
     * @return dest
     */
    public Color darker(double factor, Color result)
    {
        return result.set(Math.max((int) (r() * factor), 0), Math.max((int) (g() * factor), 0), Math.max((int) (b() * factor), 0), a());
    }
    
    /**
     * Returns a color that is tinted by the color.
     *
     * @param tint   the tint
     * @param result the result
     * @return result
     */
    @Override
    public Color tint(Colorc tint, Color result)
    {
        if (tint.r() < 255) result.r(r() * tint.r() / 255);
        if (tint.g() < 255) result.g(g() * tint.g() / 255);
        if (tint.b() < 255) result.b(b() * tint.b() / 255);
        if (tint.a() < 255) result.a(a() * tint.a() / 255);
        return result;
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
     * @param result the result
     * @return result
     */
    @Override
    public Color interpolate(Colorc other, double amount, Color result)
    {
        if (amount <= 0) return result.set(this);
        if (1 <= amount) return result.set(other);
        double inverse = 1 - amount;
        return result.r((int) (inverse * r() + amount * other.r()))
                     .g((int) (inverse * g() + amount * other.g()))
                     .b((int) (inverse * b() + amount * other.b()))
                     .a((int) (inverse * a() + amount * other.a()));
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
    
    /**
     * Sets this color to the value described by a 32-bit integer.
     *
     * @param x the 32-bit integer
     * @return this
     */
    public Color fromInt(int x)
    {
        long l = x & 0x00000000FFFFFFFFL;
        return set(l & 0xFF, l >> 8 & 0xFF, l >> 16 & 0xFF, l >> 24 & 0xFF);
    }
    
    public Color fromHex(String hex)
    {
        if (!hex.startsWith("#")) throw new RuntimeException("Invalid Hex Color String: " + hex);
        if (hex.length() == 7)
        {
            a(255);
            r(Integer.parseInt(hex.substring(1, 3), 16));
            g(Integer.parseInt(hex.substring(3, 5), 16));
            b(Integer.parseInt(hex.substring(5, 7), 16));
            return this;
        }
        else if (hex.length() == 9)
        {
            a(Integer.parseInt(hex.substring(1, 3), 16));
            r(Integer.parseInt(hex.substring(3, 5), 16));
            g(Integer.parseInt(hex.substring(5, 7), 16));
            b(Integer.parseInt(hex.substring(7, 9), 16));
            return this;
        }
        throw new RuntimeException("Invalid Hex Color String: " + hex);
    }
    
    private static int toColorInt(Number x)
    {
        return clamp(x instanceof Float ? (int) ((float) x * 255) :
                     x instanceof Double ? (int) ((double) x * 255) :
                     x instanceof Long ? (int) (long) x :
                     x instanceof Short ? (short) x :
                     x instanceof Byte ? (byte) x :
                     (int) x, 0, 255);
    }
}
