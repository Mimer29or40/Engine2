package engine.util;

/**
 * A simple {@code long} tuple.
 */
@SuppressWarnings("unused")
public class TupleL extends Tuple<Long, Long, Long>
{
    /**
     * Creates a new pair with three longs.
     *
     * @param a The first long.
     * @param b The second long.
     * @param c The third long.
     */
    public TupleL(long a, long b, long c)
    {
        super(a, b, c);
    }
    
    /**
     * @return The first long value.
     */
    public long a()
    {
        return this.a;
    }
    
    /**
     * @return The second long value.
     */
    public long b()
    {
        return this.b;
    }
    
    /**
     * @return The third long value.
     */
    public long c()
    {
        return this.c;
    }
}
