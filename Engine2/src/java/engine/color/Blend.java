package engine.color;

/**
 * An object to blend colors in a similar way to OpenGL.
 * The blend functions happens in integer space because I wanted repeatability.
 */
public class Blend implements IBlend
{
    private Func     sourceFactor  = Func.SRC_ALPHA;
    private Func     destFactor    = Func.ONE_MINUS_SRC_ALPHA;
    private Equation blendEquation = Equation.ADD;
    
    /**
     * @return The current function to modify the source color values.
     */
    public Func sourceFactor()
    {
        return this.sourceFactor;
    }
    
    /**
     * @return The current function to modify the destination color values.
     */
    public Func destFactor()
    {
        return this.destFactor;
    }
    
    /**
     * @param sourceFactor The new function to modify the source color values.
     * @param destFactor   The new function to modify the destination color values.
     * @return This instance for call chaining.
     */
    public Blend blendFunc(Func sourceFactor, Func destFactor)
    {
        this.sourceFactor = sourceFactor;
        this.destFactor   = destFactor;
        return this;
    }
    
    /**
     * @return The current function to blend the two colors.
     */
    public Equation blendEquation()
    {
        return this.blendEquation;
    }
    
    /**
     * @param blendEquation The new function to blend the two colors.
     * @return This instance for call chaining.
     */
    public Blend blendEquation(Equation blendEquation)
    {
        this.blendEquation = blendEquation;
        return this;
    }
    
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
    @Override
    public Color blend(int rs, int gs, int bs, int as, int rd, int gd, int bd, int ad, Color result)
    {
        int sr = this.sourceFactor.apply(rs, as, rd, ad);
        int sg = this.sourceFactor.apply(gs, as, gd, ad);
        int sb = this.sourceFactor.apply(bs, as, bd, ad);
        int sa = this.sourceFactor.apply(as, as, ad, ad);
        
        int dr = this.destFactor.apply(rs, as, rd, ad);
        int dg = this.destFactor.apply(gs, as, gd, ad);
        int db = this.destFactor.apply(bs, as, bd, ad);
        int da = this.destFactor.apply(as, as, ad, ad);
        
        result.r(this.blendEquation.apply(rs * sr, rd * dr) / 255);
        result.g(this.blendEquation.apply(gs * sg, gd * dg) / 255);
        result.b(this.blendEquation.apply(bs * sb, bd * db) / 255);
        result.a(this.blendEquation.apply(as * sa, ad * da) / 255);
        
        return result;
    }
    
    /**
     * Different functions to modified the two colors before they are blended together.
     */
    public enum Func
    {
        ZERO((sourceColor, sourceAlpha, destColor, destAlpha) -> 0),
        ONE((sourceColor, sourceAlpha, destColor, destAlpha) -> 255),
        
        SRC_COLOR((sourceColor, sourceAlpha, destColor, destAlpha) -> sourceColor),
        ONE_MINUS_SRC_COLOR((sourceColor, sourceAlpha, destColor, destAlpha) -> 255 - sourceColor),
        SRC_ALPHA((sourceColor, sourceAlpha, destColor, destAlpha) -> sourceAlpha),
        ONE_MINUS_SRC_ALPHA((sourceColor, sourceAlpha, destColor, destAlpha) -> 255 - sourceAlpha),
        
        DEST_COLOR((sourceColor, sourceAlpha, destColor, destAlpha) -> destColor),
        ONE_MINUS_DEST_COLOR((sourceColor, sourceAlpha, destColor, destAlpha) -> 255 - destColor),
        DEST_ALPHA((sourceColor, sourceAlpha, destColor, destAlpha) -> destAlpha),
        ONE_MINUS_DEST_ALPHA((sourceColor, sourceAlpha, destColor, destAlpha) -> 255 - destAlpha),
        ;
        
        private final IBlendFunc function;
        
        Func(IBlendFunc function)
        {
            this.function = function;
        }
        
        public int apply(int sourceColor, int sourceAlpha, int destColor, int destAlpha)
        {
            return this.function.apply(sourceColor, sourceAlpha, destColor, destAlpha);
        }
    }
    
    /**
     * Determines how the two colors are blended together.
     */
    public enum Equation
    {
        ADD(Integer::sum),
        
        SUBTRACT((source, dest) -> source - dest),
        REVERSE_SUBTRACT((source, dest) -> dest - source),
        
        MIN(Math::min),
        MAX(Math::max),
        ;
        
        private final IBlendEquation function;
        
        Equation(IBlendEquation function)
        {
            this.function = function;
        }
        
        public int apply(int s, int d)
        {
            return this.function.apply(s, d);
        }
    }
    
    private interface IBlendFunc
    {
        int apply(int sourceColor, int sourceAlpha, int destColor, int destAlpha);
    }
    
    private interface IBlendEquation
    {
        int apply(int source, int dest);
    }
}
