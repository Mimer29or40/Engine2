package engine.util;


/**
 * Interface to a read-only view of a Pair.
 */
public interface IPair<A, B>
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
