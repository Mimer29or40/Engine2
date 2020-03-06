package engine.color;

public class Blend implements IBlend
{
    private Func     sourceFactor = Func.SRC_ALPHA;
    private Func     destFactor   = Func.ONE_MINUS_SRC_ALPHA;
    private Equation equation     = Equation.ADD;
    
    public Func sourceFactor()
    {
        return this.sourceFactor;
    }
    
    public Func destFactor()
    {
        return this.destFactor;
    }
    
    public Blend blendFunc(Func sourceFactor, Func destFactor)
    {
        this.sourceFactor = sourceFactor;
        this.destFactor   = destFactor;
        return this;
    }
    
    public Equation blendEquation()
    {
        return this.equation;
    }
    
    public Blend blendEquation(Equation equation)
    {
        this.equation = equation;
        return this;
    }
    
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
        
        result.r(this.equation.apply(rs * sr, rd * dr) / 255);
        result.g(this.equation.apply(gs * sg, gd * dg) / 255);
        result.b(this.equation.apply(bs * sb, bd * db) / 255);
        result.a(this.equation.apply(as * sa, ad * da) / 255);
        
        return result;
    }
    
    public enum Func
    {
        ZERO((cs, as, cd, ad) -> 0),
        ONE((cs, as, cd, ad) -> 255),
        
        SRC_COLOR((cs, as, cd, ad) -> cs),
        ONE_MINUS_SRC_COLOR((cs, as, cd, ad) -> 255 - cs),
        SRC_ALPHA((cs, as, cd, ad) -> as),
        ONE_MINUS_SRC_ALPHA((cs, as, cd, ad) -> 255 - as),
        
        DEST_COLOR((cs, as, cd, ad) -> cd),
        ONE_MINUS_DEST_COLOR((cs, as, cd, ad) -> 255 - cd),
        DEST_ALPHA((cs, as, cd, ad) -> ad),
        ONE_MINUS_DEST_ALPHA((cs, as, cd, ad) -> 255 - ad),
        ;
        
        private final IBlendFunc function;
        
        Func(IBlendFunc function)
        {
            this.function = function;
        }
        
        public int apply(int cs, int as, int cd, int ad)
        {
            return this.function.apply(cs, as, cd, ad);
        }
        
        private interface IBlendFunc
        {
            int apply(int cs, int as, int cd, int ad);
        }
    }
    
    public enum Equation
    {
        ADD(Integer::sum),
        
        SUBTRACT((s, d) -> s - d),
        REVERSE_SUBTRACT((s, d) -> d - s),
        
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
        
        private interface IBlendEquation
        {
            int apply(int s, int d);
        }
    }
}
