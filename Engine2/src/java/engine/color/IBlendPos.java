package engine.color;

public interface IBlendPos extends IBlend
{
    Color blend(int x, int y, int rs, int gs, int bs, int as, int rd, int gd, int bd, int ad, Color result);
    
    default Color blend(int x, int y, Colorc source, int rd, int gd, int bd, int ad, Color result)
    {
        return blend(x, y, source.r(), source.g(), source.b(), source.a(), rd, gd, bd, ad, result);
    }
    
    default Color blend(int x, int y, int rs, int gs, int bs, int as, Colorc dest, Color result)
    {
        return blend(x, y, rs, gs, bs, as, dest.r(), dest.g(), dest.b(), dest.a(), result);
    }
    
    default Color blend(int x, int y, Colorc source, Colorc dest, Color result)
    {
        return blend(x, y, source.r(), source.g(), source.b(), source.a(), dest.r(), dest.g(), dest.b(), dest.a(), result);
    }
    
    default Color blend(int rs, int gs, int bs, int as, int rd, int gd, int bd, int ad, Color dest)
    {
        return blend(0, 0, rs, gs, bs, as, rd, gd, bd, ad, dest);
    }
}
