package engine.color;

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
     * @return 32-bit integer representation of the color
     */
    int toInt();
    
    /**
     * @return The Hex String representation of the color.
     */
    String toHex();
    
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
     * Determine the component with the smallest (towards zero) absolute value.
     *
     * @return the component, within <code>[0..255]</code>
     */
    int minComponent();
    
    /**
     * Determine the component with the middle (towards zero) absolute value.
     *
     * @return the component, within <code>[0..255]</code>
     */
    int midComponent();
    
    /**
     * Determine the component with the biggest absolute value.
     *
     * @return the component, within <code>[0..255]</code>
     */
    int maxComponent();
    
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
     * @param x    scale
     * @param dest will hold the result
     * @return dest
     */
    default Color scale(double x, Color dest)
    {
        return scale(x, false, dest);
    }
    
    /**
     * Scales this color and stores the result in <code>result</code>.
     *
     * @param x     scale
     * @param alpha flag to scale the alpha (default: false)
     * @param dest  will hold the result
     * @return dest
     */
    Color scale(double x, boolean alpha, Color dest);
    
    /**
     * Returns a color that is brighter than this by a factor.
     *
     * @param factor the factor
     * @param dest   will hold the result
     * @return dest
     */
    Color brighter(double factor, Color dest);
    
    /**
     * Returns a color that is darker than this by a factor.
     *
     * @param factor the factor
     * @param dest   will hold the result
     * @return dest
     */
    Color darker(double factor, Color dest);
    
    /**
     * Returns a color that is tinted by the color.
     *
     * @param tint the tint
     * @param dest will hold the result
     * @return dest
     */
    Color tint(Colorc tint, Color dest);
    
    /**
     * Returns a color that is interpolated between this color and other.
     *
     * @param other  the other color
     * @param amount the amount to interpolate
     * @param dest   will hold the result
     * @return dest
     */
    Color interpolate(Colorc other, double amount, Color dest);
}
