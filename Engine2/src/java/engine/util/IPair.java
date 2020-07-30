package engine.util;


import java.util.Map;

/**
 * Interface to a read-only view of a Pair.
 */
@SuppressWarnings("unused")
public interface IPair<A, B> extends Map.Entry<A, B>
{
    /**
     * @return The first object in the pair.
     */
    A getA();
    
    /**
     * @return The second object in the pair.
     */
    B getB();
}
