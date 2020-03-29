package engine.util;

/**
 * A simple {@code long} pair.
 */
@SuppressWarnings("unused")
public class PairL extends Pair<Long, Long>
{
    public PairL(long a, long b)
    {
        super(a, b);
    }
    
    public long a()
    {
        return this.a;
    }
    
    public long b()
    {
        return this.b;
    }
}
