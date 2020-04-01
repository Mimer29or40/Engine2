package engine.util;

/**
 * A simple {@code double} float.
 */
@SuppressWarnings("unused")
public class TupleF extends Tuple<Float, Float, Float>
{
    /**
     * Creates a new pair with three floats.
     *
     * @param a The first float.
     * @param b The second float.
     * @param c The third float.
     */
    public TupleF(float a, float b, float c)
    {
        super(a, b, c);
    }
    
    /**
     * @return The first float value.
     */
    public float a()
    {
        return this.a;
    }
    
    /**
     * @return The second float value.
     */
    public float b()
    {
        return this.b;
    }
    
    /**
     * @return The third float value.
     */
    public float c()
    {
        return this.c;
    }
}
