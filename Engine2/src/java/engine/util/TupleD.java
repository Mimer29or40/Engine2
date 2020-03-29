package engine.util;

/**
 * A simple {@code double} tuple.
 */
@SuppressWarnings("unused")
public class TupleD extends Tuple<Double, Double, Double>
{
    public TupleD(double a, double b, double c)
    {
        super(a, b, c);
    }
    
    public double a()
    {
        return this.a;
    }
    
    public double b()
    {
        return this.b;
    }
    
    public double c()
    {
        return this.c;
    }
}
