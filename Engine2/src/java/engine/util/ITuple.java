package engine.util;

/**
 * Interface to a read-only view of a Tuple.
 */
@SuppressWarnings("unused")
public interface ITuple<A, B, C>
{
    /**
     * @return The first object in the pair.
     */
    A getA();
    
    /**
     * @return The second object in the pair.
     */
    B getB();
    
    /**
     * @return The third object in the pair.
     */
    C getC();
}
