package engine.util;

/**
 * A simple {@code double} tuple.
 */
@SuppressWarnings("unused")
public class TupleD extends Tuple<Double, Double, Double>
{
    /**
     * Creates a new pair with three doubles.
     *
     * @param a The first double.
     * @param b The second double.
     * @param c The third double.
     */
    public TupleD(double a, double b, double c)
    {
        super(a, b, c);
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
    
    /**
     * @return The third double value.
     */
    public double c()
    {
        return this.c;
    }
}
