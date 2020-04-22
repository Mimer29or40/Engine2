package engine.util;

/**
 * A simple {@code String} pair.
 */
@SuppressWarnings("unused")
public class PairS extends Pair<String, String>
{
    /**
     * Creates a new pair with two Strings.
     *
     * @param a The first String.
     * @param b The second String.
     */
    public PairS(String a, String b)
    {
        super(a, b);
    }
    
    /**
     * @return The first String value.
     */
    public String a()
    {
        return this.a;
    }
    
    /**
     * @return The second String value.
     */
    public String b()
    {
        return this.b;
    }
}
