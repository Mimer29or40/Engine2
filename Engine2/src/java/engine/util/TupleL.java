package engine.util;

/**
 * A simple {@code long} tuple.
 */
@SuppressWarnings("unused")
public class TupleL extends Tuple<Long, Long, Long>
{
    public TupleL(long a, long b, long c)
    {
        super(a, b, c);
    }
    
    public long a()
    {
        return this.a;
    }
    
    public long b()
    {
        return this.b;
    }
    
    public long c()
    {
        return this.c;
    }
}
