package engine.util;

/**
 * A simple {@code String} tuple.
 */
@SuppressWarnings("unused")
public class TupleS extends Tuple<String, String, String>
{
    /**
     * Creates a new tuple with three Strings.
     *
     * @param a The first String.
     * @param b The second String.
     * @param c The third String.
     */
    public TupleS(String a, String b, String c)
    {
        super(a, b, c);
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
    
    /**
     * @return The third String value.
     */
    public String c()
    {
        return this.c;
    }
}
