package engine.util;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

/**
 * A Generic pair of two objects. The objects can be modified or completely replaced.
 */
@SuppressWarnings("unused")
public class Pair<A, B> implements IPair<A, B>, Map.Entry<A, B>, Comparable<IPair<A, B>>, Serializable
{
    public A a;
    public B b;
    
    /**
     * Creates a new pair with two objects.
     *
     * @param a The first object.
     * @param b The second object.
     */
    public Pair(A a, B b)
    {
        this.a = a;
        this.b = b;
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
    
    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof Map.Entry<?, ?>)) return false;
        Map.Entry<?, ?> entry = (Map.Entry<?, ?>) o;
        return Objects.equals(getKey(), entry.getKey()) && Objects.equals(getValue(), entry.getValue());
    }
    
    @Override
    public int hashCode()
    {
        return Objects.hash(this.a, this.b);
    }
    
    @Override
    public String toString()
    {
        return getClass().getName() + '{' + this.a + ", " + this.b + '}';
    }
    
    /**
     * <p>Gets the key from this pair.</p>
     *
     * <p>This method implements the {@code Map.Entry} interface returning the
     * a element as the key.</p>
     *
     * @return the a element as the key, may be null
     */
    @Override
    public A getKey()
    {
        return this.a;
    }
    
    /**
     * <p>Gets the value from this pair.</p>
     *
     * <p>This method implements the {@code Map.Entry} interface returning the
     * b element as the value.</p>
     *
     * @return the b element as the value, may be null
     */
    @Override
    public B getValue()
    {
        return this.b;
    }
    
    /**
     * Sets the {@code Map.Entry} value.
     * This sets the b element of the pair.
     *
     * @param value the b value to set, not null
     * @return the old value for the b element
     */
    @Override
    public B setValue(B value)
    {
        final B result = this.b;
        this.b = value;
        return result;
    }
    
    @Override
    public int compareTo(IPair<A, B> o)
    {
        int comparison;
        
        if (getA() != o.getA())
        {
            if (getA() == null)
            {
                return -1;
            }
            if (o.getA() == null)
            {
                return 1;
            }
            @SuppressWarnings("unchecked") // assume this can be done; if not throw CCE as per Javadoc
            final Comparable<Object> comparable = (Comparable<Object>) getA();
            if ((comparison = comparable.compareTo(o.getA())) != 0) return comparison;
        }
        
        if (getB() != o.getB())
        {
            if (getB() == null)
            {
                return -1;
            }
            if (o.getB() == null)
            {
                return 1;
            }
            @SuppressWarnings("unchecked") // assume this can be done; if not throw CCE as per Javadoc
            final Comparable<Object> comparable = (Comparable<Object>) getB();
            if ((comparison = comparable.compareTo(o.getB())) != 0) return comparison;
        }
        
        return 0;
    }
}
