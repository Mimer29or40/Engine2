package engine.util;

/**
 * A simple {@code float} pair.
 */
@SuppressWarnings("unused")
public class PairF extends Pair<Float, Float>
{
    public PairF(float a, float b)
    {
        super(a, b);
    }
    
    public float a()
    {
        return this.a;
    }
    
    public float b()
    {
        return this.b;
    }
}
