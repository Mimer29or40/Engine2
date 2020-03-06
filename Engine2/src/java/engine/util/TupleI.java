package engine.util;

@SuppressWarnings("unused")
public class TupleI extends Tuple<Integer, Integer, Integer>
{
    public TupleI(int a, int b, int c)
    {
        super(a, b, c);
    }
    
    public int a()
    {
        return this.a;
    }
    
    public int b()
    {
        return this.b;
    }
    
    public int c()
    {
        return this.c;
    }
}
