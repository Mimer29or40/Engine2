package engine.color;

import engine.Engine;

/**
 * Interface to a read-only view of a Color.
 *
 * @author Ryan Smith
 */
@SuppressWarnings("unused")
public interface Colorc
{
    /**
     * @return the value of the r component [0..255]
     */
    int r();
    
    /**
     * @return the value of the r component [0..1]
     */
    float rf();
    
    /**
     * @return the value of the g component [0..255]
     */
    int g();
    
    /**
     * @return the value of the g component [0..1]
     */
    float gf();
    
    /**
     * @return the value of the b component [0..255]
     */
    int b();
    
    /**
     * @return the value of the b component [0..1]
     */
    float bf();
    
    /**
     * @return the value of the a component [0..255]
     */
    int a();
    
    /**
     * @return the value of the a component [0..1]
     */
    float af();
    
    /**
     * Get the value of the specified component of this color.
     *
     * @param component the component, within <code>[0..3]</code>
     * @return the value
     * @throws IllegalArgumentException if <code>component</code> is not within <code>[0..3]</code>
     */
    int getComponent(int component) throws IllegalArgumentException;
    
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
    boolean equals(int r, int g, int b, int a);
    
    /**
     * Compare the color components of <code>this</code> color with the given <code>(r, g, b, 255)</code>
     * and return whether all of them are equal.
     *
     * @param r the r component to compare to
     * @param g the g component to compare to
     * @param b the b component to compare to
     * @return <code>true</code> if all the color components are equal
     */
    default boolean equals(int r, int g, int b)
    {
        return equals(r, g, b, 255);
    }
    
    /**
     * Compare the color components of <code>this</code> color with the given <code>(g, g, g, a)</code>
     * and return whether all of them are equal.
     *
     * @param grey the r, g, and b component to compare to
     * @param a    the a component to compare to
     * @return <code>true</code> if all the color components are equal
     */
    default boolean equals(int grey, int a)
    {
        return equals(grey, grey, grey, a);
    }
    
    /**
     * Compare the color components of <code>this</code> color with the given <code>(g, g, g, 255)</code>
     * and return whether all of them are equal.
     *
     * @param grey the r, g, and b component to compare to
     * @return <code>true</code> if all the color components are equal
     */
    default boolean equals(int grey)
    {
        return equals(grey, grey, grey, 255);
    }
    
    /**
     * Blends the supplied <code>(r, g, b, a)</code> (source) with <code>this</code> (backdrop) according
     * to the blend function and stores these values in <code>result</code>.
     *
     * @param r      the r component of source
     * @param g      the g component of source
     * @param b      the b component of source
     * @param a      the a component of source
     * @param func   the function to blend the colors
     * @param result will hold the result
     * @return result
     */
    Color blend(int r, int g, int b, int a, IBlend func, Color result);
    
    /**
     * Blends the supplied <code>(r, g, b, 255)</code> (source) with <code>this</code> (backdrop) according
     * to the blend function and stores these values in <code>result</code>. Source a is assumed to
     * be 255.
     *
     * @param r      the r component of source
     * @param g      the g component of source
     * @param b      the b component of source
     * @param func   the function to blend the colors
     * @param result will hold the result
     * @return result
     */
    default Color blend(int r, int g, int b, IBlend func, Color result)
    {
        return blend(r, g, b, 255, func, result);
    }
    
    /**
     * Blends the supplied <code>(g, g, g, a)</code> (source) with <code>this</code> (backdrop)
     * according to the blend function and stores these values in <code>result</code>. Source a is assumed to
     * be 255.
     *
     * @param grey   the r, g, and b component of source
     * @param a      the a component of source
     * @param func   the function to blend the colors
     * @param result will hold the result
     * @return result
     */
    default Color blend(int grey, int a, IBlend func, Color result)
    {
        return blend(grey, grey, grey, a, func, result);
    }
    
    /**
     * Blends the supplied <code>(g, g, g, 255)</code> (source) with <code>this</code> (backdrop)
     * according to the blend function and stores these values in <code>result</code>. Source a is assumed to
     * be 255.
     *
     * @param grey   the r, g, and b component of source
     * @param func   the function to blend the colors
     * @param result will hold the result
     * @return result
     */
    default Color blend(int grey, IBlend func, Color result)
    {
        return blend(grey, grey, grey, 255, func, result);
    }
    
    /**
     * Blends the supplied color (source) with <code>this</code> (backdrop) according to the blend
     * function and stores these values in <code>result</code>.
     *
     * @param source the source color
     * @param func   the function to blend the colors
     * @param result will hold the result
     * @return result
     */
    default Color blend(Colorc source, IBlend func, Color result)
    {
        return blend(source.r(), source.g(), source.b(), source.a(), func, result);
    }
    
    /**
     * @return 32-bit integer representation of the color
     */
    int toInt();
    
    /**
     * @return The Hex String representation of the color.
     */
    String toHex();
    
    /**
     * @return the hue of the color [0..359]
     */
    int hue();
    
    /**
     * @return the saturation of the color [0..255]
     */
    int saturation();
    
    /**
     * @return the brightness of the color [0..255]
     */
    int brightness();
    
    /**
     * Determine the component with the biggest absolute value.
     *
     * @return the component, within <code>[0..255]</code>
     */
    int maxComponent();
    
    /**
     * Determine the component with the middle (towards zero) absolute value.
     *
     * @return the component, within <code>[0..255]</code>
     */
    int midComponent();
    
    /**
     * Determine the component with the smallest (towards zero) absolute value.
     *
     * @return the component, within <code>[0..255]</code>
     */
    int minComponent();
    
    /**
     * Determine the component with the biggest absolute value.
     *
     * @return the component index, within <code>[0..2]</code>
     */
    int maxComponentIndex();
    
    /**
     * Determine the component with the middle (towards zero) absolute value.
     *
     * @return the component index, within <code>[0..2]</code>
     */
    int midComponentIndex();
    
    /**
     * Determine the component with the smallest (towards zero) absolute value.
     *
     * @return the component index, within <code>[0..2]</code>
     */
    int minComponentIndex();
    
    /**
     * Negate this color and store the result in <code>dest</code>.
     *
     * @param dest will hold the result
     * @return dest
     */
    Color negate(Color dest);
    
    /**
     * Scales this color and stores the result in <code>result</code>.
     *
     * @param x      scale
     * @param result will hold the result
     * @return result
     */
    default Color scale(double x, Color result)
    {
        return scale(x, false, result);
    }
    
    /**
     * Scales this color and stores the result in <code>result</code>.
     *
     * @param x      scale
     * @param alpha  flag to scale the alpha (default: false)
     * @param result will hold the result
     * @return result
     */
    Color scale(double x, boolean alpha, Color result);
    
    /**
     * Blend this color with another color and store the result in <code>result</code>.
     *
     * @param other  the other color
     * @param result the result
     * @return result
     */
    default Color blend(Color other, Color result)
    {
        return blend(other, Engine.blend(), result);
    }
    
    /**
     * Negate this color and store the result in <code>result</code>.
     *
     * @param other  the other color
     * @param func   the function that will blend the two colors
     * @param result the result
     * @return result
     */
    Color blend(Color other, IBlend func, Color result);
    
    /**
     * Returns a color that is brighter than this by a factor.
     *
     * @param factor the factor
     * @param result the result
     * @return result
     */
    Color brighter(double factor, Color result);
    
    /**
     * Returns a color that is darker than this by a factor.
     *
     * @param factor the factor
     * @param result the result
     * @return result
     */
    Color darker(double factor, Color result);
}
