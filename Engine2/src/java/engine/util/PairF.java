package engine.util;

/**
 * A simple {@code float} pair.
 */
@SuppressWarnings("unused")
public class PairF extends Pair<Float, Float>
{
    /**
     * Creates a new pair with two floats.
     *
     * @param a The first float.
     * @param b The second float.
     */
    public PairF(float a, float b)
    {
        super(a, b);
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
}
