package engine.color;

import engine.util.Logger;
import engine.util.Pair;
import engine.util.Tuple;

import static org.lwjgl.opengl.GL46.*;

/**
 * An object to blend colors in a similar way to OpenGL.
 * The blend functions happens in integer space because I wanted repeatability.
 */
@SuppressWarnings("unused")
public class Blend implements IBlend
{
    private static final Logger LOGGER = new Logger();
    
    private boolean enabled;
    
    private Func     srcFactor;
    private Func     dstFactor;
    private Equation equation;
    
    public Blend()
    {
        enabled(false);
        blendFunc(Func.SRC_ALPHA, Func.ONE_MINUS_SRC_ALPHA);
        blendEquation(Equation.ADD);
    }
    
    @Override
    public String toString()
    {
        return "Blend{" + "srcFactor=" + this.srcFactor + ", dstFactor=" + this.dstFactor + ", equation=" + this.equation + '}';
    }
    
    /**
     * @return If blend is enabled.
     */
    public boolean enabled()
    {
        return this.enabled;
    }
    
    /**
     * Sets if blend is enabled.
     *
     * @param enabled The new enabled state.
     * @return This instance for call chaining.
     */
    public Blend enabled(boolean enabled)
    {
        this.enabled = enabled;
        if (this.enabled) glEnable(GL_BLEND);
        if (!this.enabled) glDisable(GL_BLEND);
        return this;
    }
    
    /**
     * Toggles if the blend is enabled or not.
     *
     * @return This instance for call chaining.
     */
    public Blend toggle()
    {
        return enabled(!enabled());
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
     * @param srcFactor The new function to modify the source color values.
     * @param dstFactor The new function to modify the destination color values.
     * @return This instance for call chaining.
     */
    public Blend blendFunc(Func srcFactor, Func dstFactor)
    {
        Blend.LOGGER.finest("Setting Blend Functions:", srcFactor, dstFactor);
        
        this.srcFactor = srcFactor;
        this.dstFactor = dstFactor;
        glBlendFunc(srcFactor.gl, dstFactor.gl);
        return this;
    }
    
    /**
     * @return The current function to blend the two colors.
     */
    public Equation blendEquation()
    {
        return this.equation;
    }
    
    /**
     * @param blendEquation The new function to blend the two colors.
     * @return This instance for call chaining.
     */
    public Blend blendEquation(Equation blendEquation)
    {
        Blend.LOGGER.finest("Setting Blend Equation:", blendEquation);
        
        this.equation = blendEquation;
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
        if (this.enabled)
        {
            int sr = this.srcFactor.func.apply(rs, as, rd, ad);
            int sg = this.srcFactor.func.apply(gs, as, gd, ad);
            int sb = this.srcFactor.func.apply(bs, as, bd, ad);
            int sa = this.srcFactor.func.apply(as, as, ad, ad);
            
            int dr = this.dstFactor.func.apply(rs, as, rd, ad);
            int dg = this.dstFactor.func.apply(gs, as, gd, ad);
            int db = this.dstFactor.func.apply(bs, as, bd, ad);
            int da = this.dstFactor.func.apply(as, as, ad, ad);
            
            return result.r(this.equation.func.apply(rs * sr, rd * dr) / 255)
                         .g(this.equation.func.apply(gs * sg, gd * dg) / 255)
                         .b(this.equation.func.apply(bs * sb, bd * db) / 255)
                         .a(this.equation.func.apply(as * sa, ad * da) / 255);
        }
        return result.set(rs, gs, bs, as);
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
        
        private final int   gl;
        private final IFunc func;
        
        Func(int gl, IFunc func)
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
        
        private final int       gl;
        private final IEquation func;
        
        Equation(int gl, IEquation func)
        {
            this.gl   = gl;
            this.func = func;
        }
    }
    
    private interface IFunc
    {
        int apply(int sourceColor, int sourceAlpha, int destColor, int destAlpha);
    }
    
    private interface IEquation
    {
        int apply(int source, int dest);
    }
    
    public static final class BTuple extends Tuple<Boolean, Pair<Func, Func>, Equation>
    {
        /**
         * Creates a new tuple with the blends information
         *
         * @param blend The Blend object.
         */
        public BTuple(Blend blend)
        {
            super(blend.enabled, new Pair<>(blend.srcFactor, blend.dstFactor), blend.equation);
        }
        
        public Blend setBlend(Blend blend)
        {
            blend.enabled(this.a);
            blend.blendFunc(this.b.a, this.b.b);
            blend.blendEquation(this.c);
            return blend;
        }
    }
}
