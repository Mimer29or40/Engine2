package engine.util;

/**
 * A simple {@code int} tuple.
 */
@SuppressWarnings("unused")
public class TupleI extends Tuple<Integer, Integer, Integer>
{
    /**
     * Creates a new pair with three ints.
     *
     * @param a The first int.
     * @param b The second int.
     * @param c The third int.
     */
    public TupleI(int a, int b, int c)
    {
        super(a, b, c);
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
    
    /**
     * @return The third int value.
     */
    public int c()
    {
        return this.c;
    }
}
