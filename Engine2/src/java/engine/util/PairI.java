package engine.util;

/**
 * A simple {@code int} pair.
 */
@SuppressWarnings("unused")
public class PairI extends Pair<Integer, Integer>
{
    /**
     * Creates a new pair with two ints.
     *
     * @param a The first int.
     * @param b The second int.
     */
    public PairI(int a, int b)
    {
        super(a, b);
    }
    
    /**
     * @return The first int value.
     */
    public int a()
    {
        return this.a;
    }
    
    /**
     * @return The second int value.
     */
    public int b()
    {
        return this.b;
    }
}
