package engine.color;

public interface IBlend
{
    Color blend(int rs, int gs, int bs, int as, int rd, int gd, int bd, int ad, Color dest);
    
    default Color blend(Colorc source, int rd, int gd, int bd, int ad, Color result)
    {
        return blend(source.r(), source.g(), source.b(), source.a(), rd, gd, bd, ad, result);
    }
    
    default Color blend(int rs, int gs, int bs, int as, Colorc dest, Color result)
    {
        return blend(rs, gs, bs, as, dest.r(), dest.g(), dest.b(), dest.a(), result);
    }
    
    default Color blend(Colorc source, Colorc dest, Color result)
    {
        return blend(source.r(), source.g(), source.b(), source.a(), dest.r(), dest.g(), dest.b(), dest.a(), result);
    }
}
