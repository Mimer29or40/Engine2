package engine.util;

/**
 * A simple {@code int} pair.
 */
@SuppressWarnings("unused")
public class PairI extends Pair<Integer, Integer>
{
    public PairI(int a, int b)
    {
        super(a, b);
    }
    
    public int a()
    {
        return this.a;
    }
    
    public int b()
    {
        return this.b;
    }
}
