package engine.util;

/**
 * A simple {@code double} pair.
 */
@SuppressWarnings("unused")
public class PairD extends Pair<Double, Double>
{
    /**
     * Creates a new pair with two doubles.
     *
     * @param a The first double.
     * @param b The second double.
     */
    public PairD(double a, double b)
    {
        super(a, b);
    }
    
    /**
     * @return The first double value.
     */
    public double a()
    {
        return this.a;
    }
    
    /**
     * @return The second double value.
     */
    public double b()
    {
        return this.b;
    }
}
