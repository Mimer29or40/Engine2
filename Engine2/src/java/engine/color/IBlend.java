package engine.color;

/**
 * An interface to provide function to blend two colors together and store the results in a {@code Color} instance.
 */
public interface IBlend
{
    /**
     * Blends two colors and stores the results to {@code dest}
     *
     * @param rs     The source red value.
     * @param gs     The source green value.
     * @param bs     The source blue value.
     * @param as     The source alpha value.
     * @param rd     The destination red value.
     * @param gd     The destination green value.
     * @param bd     The destination blue value.
     * @param ad     The destination alpha value.
     * @param result The color to store the blended color.
     * @return result
     */
    Color blend(int rs, int gs, int bs, int as, int rd, int gd, int bd, int ad, Color result);
    
    /**
     * Blends two colors and stores the results to {@code dest}
     *
     * @param source The source color.
     * @param rd     The destination red value.
     * @param gd     The destination green value.
     * @param bd     The destination blue value.
     * @param ad     The destination alpha value.
     * @param result The color to store the blended color.
     * @return result
     */
    default Color blend(Colorc source, int rd, int gd, int bd, int ad, Color result)
    {
        return blend(source.r(), source.g(), source.b(), source.a(), rd, gd, bd, ad, result);
    }
    
    /**
     * Blends two colors and stores the results to {@code dest}
     *
     * @param rs     The source red value.
     * @param gs     The source green value.
     * @param bs     The source blue value.
     * @param as     The source alpha value.
     * @param dest   The destination color.
     * @param result The color to store the blended color.
     * @return result
     */
    default Color blend(int rs, int gs, int bs, int as, Colorc dest, Color result)
    {
        return blend(rs, gs, bs, as, dest.r(), dest.g(), dest.b(), dest.a(), result);
    }
    
    /**
     * Blends two colors and stores the results to {@code dest}
     *
     * @param source The source color.
     * @param dest   The destination color.
     * @param result The color to store the blended color.
     * @return result
     */
    default Color blend(Colorc source, Colorc dest, Color result)
    {
        return blend(source.r(), source.g(), source.b(), source.a(), dest.r(), dest.g(), dest.b(), dest.a(), result);
    }
}
