package engine.color;

import static org.lwjgl.opengl.GL14.*;

/**
 * An object to blend colors in a similar way to OpenGL.
 * The blend functions happens in integer space because I wanted repeatability.
 */
public class Blend implements IBlend
{
    private Func     srcFactor;
    private Func     dstFactor;
    private Equation blendEq;
    
    public Blend()
    {
        blendFunc(Func.SRC_ALPHA, Func.ONE_MINUS_SRC_ALPHA);
        blendEquation(Equation.ADD);
    }
    
    /**
     * @return The current function to modify the source color values.
     */
    public Func sourceFactor()
    {
        return this.srcFactor;
    }
    
    /**
     * @return The current function to modify the destination color values.
     */
    public Func destFactor()
    {
        return this.dstFactor;
    }
    
    /**
     * @param sourceFactor The new function to modify the source color values.
     * @param destFactor   The new function to modify the destination color values.
     * @return This instance for call chaining.
     */
    public Blend blendFunc(Func sourceFactor, Func destFactor)
    {
        this.srcFactor = sourceFactor;
        this.dstFactor = destFactor;
        glBlendFunc(sourceFactor.gl, destFactor.gl);
        return this;
    }
    
    /**
     * @return The current function to blend the two colors.
     */
    public Equation blendEquation()
    {
        return this.blendEq;
    }
    
    /**
     * @param blendEquation The new function to blend the two colors.
     * @return This instance for call chaining.
     */
    public Blend blendEquation(Equation blendEquation)
    {
        this.blendEq = blendEquation;
        glBlendEquation(blendEquation.gl);
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
        int sr = this.srcFactor.func.apply(rs, as, rd, ad);
        int sg = this.srcFactor.func.apply(gs, as, gd, ad);
        int sb = this.srcFactor.func.apply(bs, as, bd, ad);
        int sa = this.srcFactor.func.apply(as, as, ad, ad);
    
        int dr = this.dstFactor.func.apply(rs, as, rd, ad);
        int dg = this.dstFactor.func.apply(gs, as, gd, ad);
        int db = this.dstFactor.func.apply(bs, as, bd, ad);
        int da = this.dstFactor.func.apply(as, as, ad, ad);
    
        return result.r(this.blendEq.func.apply(rs * sr, rd * dr) / 255)
                     .g(this.blendEq.func.apply(gs * sg, gd * dg) / 255)
                     .b(this.blendEq.func.apply(bs * sb, bd * db) / 255)
                     .a(this.blendEq.func.apply(as * sa, ad * da) / 255);
    }
    
    /**
     * Different functions to modified the two colors before they are blended together.
     */
    public enum Func
    {
        ZERO(GL_ZERO, (sourceColor, sourceAlpha, destColor, destAlpha) -> 0),
        ONE(GL_ONE, (sourceColor, sourceAlpha, destColor, destAlpha) -> 255),
        
        SRC_COLOR(GL_SRC_COLOR, (sourceColor, sourceAlpha, destColor, destAlpha) -> sourceColor),
        ONE_MINUS_SRC_COLOR(GL_ONE_MINUS_SRC_COLOR, (sourceColor, sourceAlpha, destColor, destAlpha) -> 255 - sourceColor),
        SRC_ALPHA(GL_SRC_ALPHA, (sourceColor, sourceAlpha, destColor, destAlpha) -> sourceAlpha),
        ONE_MINUS_SRC_ALPHA(GL_ONE_MINUS_SRC_ALPHA, (sourceColor, sourceAlpha, destColor, destAlpha) -> 255 - sourceAlpha),
        
        DST_COLOR(GL_DST_ALPHA, (sourceColor, sourceAlpha, destColor, destAlpha) -> destColor),
        ONE_MINUS_DST_COLOR(GL_ONE_MINUS_DST_COLOR, (sourceColor, sourceAlpha, destColor, destAlpha) -> 255 - destColor),
        DST_ALPHA(GL_DST_ALPHA, (sourceColor, sourceAlpha, destColor, destAlpha) -> destAlpha),
        ONE_MINUS_DST_ALPHA(GL_ONE_MINUS_DST_ALPHA, (sourceColor, sourceAlpha, destColor, destAlpha) -> 255 - destAlpha),
        ;
        
        private final int        gl;
        private final IBlendFunc func;
        
        Func(int gl, IBlendFunc func)
        {
            this.gl   = gl;
            this.func = func;
        }
    }
    
    /**
     * Determines how the two colors are blended together.
     */
    public enum Equation
    {
        ADD(GL_FUNC_ADD, Integer::sum),
        SUBTRACT(GL_FUNC_SUBTRACT, (source, dest) -> source - dest),
        REVERSE_SUBTRACT(GL_FUNC_REVERSE_SUBTRACT, (source, dest) -> dest - source),
        
        MIN(GL_MIN, Math::min),
        MAX(GL_MAX, Math::max),
        ;
        
        private final int            gl;
        private final IBlendEquation func;
        
        Equation(int gl, IBlendEquation func)
        {
            this.gl   = gl;
            this.func = func;
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
