package engine.util;

/**
 * A simple {@code long} pair.
 */
@SuppressWarnings("unused")
public class PairL extends Pair<Long, Long>
{
    /**
     * Creates a new pair with two long.
     *
     * @param a The first long.
     * @param b The second long.
     */
    public PairL(long a, long b)
    {
        super(a, b);
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
}
