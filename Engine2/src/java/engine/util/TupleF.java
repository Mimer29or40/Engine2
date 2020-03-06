package engine.util;

@SuppressWarnings("unused")
public class TupleF extends Tuple<Float, Float, Float>
{
    public TupleF(float a, float b, float c)
    {
        super(a, b, c);
    }
    
    public float a()
    {
        return this.a;
    }
    
    public float b()
    {
        return this.b;
    }
    
    public float c()
    {
        return this.c;
    }
}
