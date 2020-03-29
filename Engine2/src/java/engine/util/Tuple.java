package engine.util;

import java.util.Objects;

/**
 * A Generic tuple of three objects. The objects can be modified or completely replaced.
 */
@SuppressWarnings("unused")
public class Tuple<A, B, C> implements ITuple<A, B, C>
{
    public A a;
    public B b;
    public C c;
    
    /**
     * Creates a new pair with two objects.
     *
     * @param a The first object.
     * @param b The second object.
     * @param c The third object.
     */
    public Tuple(A a, B b, C c)
    {
        this.a = a;
        this.b = b;
        this.c = c;
    }
    
    /**
     * @return The first object in the pair.
     */
    @Override
    public A getA()
    {
        return this.a;
    }
    
    /**
     * @return The second object in the pair.
     */
    @Override
    public B getB()
    {
        return this.b;
    }
    
    /**
     * @return The third object in the pair.
     */
    @Override
    public C getC()
    {
        return this.c;
    }
    
    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof Tuple)) return false;
        Tuple<?, ?, ?> tuple = (Tuple<?, ?, ?>) o;
        return Objects.equals(this.a, tuple.a) && Objects.equals(this.b, tuple.b) && Objects.equals(this.c, tuple.c);
    }
    
    @Override
    public int hashCode()
    {
        return Objects.hash(this.a, this.b, this.c);
    }
    
    @Override
    public String toString()
    {
        return getClass().getName() + '{' + this.a + ", " + this.b + ", " + this.c + '}';
    }
}
