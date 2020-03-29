package engine.util;

/**
 * A simple {@code double} pair.
 */
@SuppressWarnings("unused")
public class PairD extends Pair<Double, Double>
{
    public PairD(double a, double b)
    {
        super(a, b);
    }
    
    public double a()
    {
        return this.a;
    }
    
    public double b()
    {
        return this.b;
    }
}
