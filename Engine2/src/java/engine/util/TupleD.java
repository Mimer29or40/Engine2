package engine.util;

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
