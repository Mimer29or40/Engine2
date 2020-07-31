package engine.util;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

/**
 * A Generic pair of two objects. The objects can be modified or completely replaced.
 */
@SuppressWarnings("unused")
public class Pair<A, B> implements IPair<A, B>, Comparable<Map.Entry<A, B>>, Serializable
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
        return getClass().getSimpleName() + '{' + this.a + ", " + this.b + '}';
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
    public int compareTo(Map.Entry<A, B> o)
    {
        int comparison;
        
        if (getA() != o.getKey())
        {
            if (getA() == null)
            {
                return -1;
            }
            if (o.getKey() == null)
            {
                return 1;
            }
            @SuppressWarnings("unchecked") // assume this can be done; if not throw CCE as per Javadoc
            final Comparable<Object> comparable = (Comparable<Object>) getA();
            if ((comparison = comparable.compareTo(o.getKey())) != 0) return comparison;
        }
        
        if (getB() != o.getValue())
        {
            if (getB() == null)
            {
                return -1;
            }
            if (o.getValue() == null)
            {
                return 1;
            }
            @SuppressWarnings("unchecked") // assume this can be done; if not throw CCE as per Javadoc
            final Comparable<Object> comparable = (Comparable<Object>) getB();
            if ((comparison = comparable.compareTo(o.getValue())) != 0) return comparison;
        }
        
        return 0;
    }
    
    /**
     * A simple {@code int} pair.
     */
    public static class I extends Pair<Integer, Integer>
    {
        /**
         * Creates a new pair with two ints.
         *
         * @param a The first int.
         * @param b The second int.
         */
        public I(int a, int b)
        {
            super(a, b);
        }
        
        /**
         * @return The first int value.
         */
        public int a()
        {
            return this.a;
        }
        
        /**
         * @return The second int value.
         */
        public int b()
        {
            return this.b;
        }
    }
    
    /**
     * A simple {@code long} pair.
     */
    public static class L extends Pair<Long, Long>
    {
        /**
         * Creates a new pair with two long.
         *
         * @param a The first long.
         * @param b The second long.
         */
        public L(long a, long b)
        {
            super(a, b);
        }
        
        /**
         * @return The first long value.
         */
        public long a()
        {
            return this.a;
        }
        
        /**
         * @return The second long value.
         */
        public long b()
        {
            return this.b;
        }
    }
    
    /**
     * A simple {@code float} pair.
     */
    public static class F extends Pair<Float, Float>
    {
        /**
         * Creates a new pair with two floats.
         *
         * @param a The first float.
         * @param b The second float.
         */
        public F(float a, float b)
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
    
    /**
     * A simple {@code double} pair.
     */
    public static class D extends Pair<Double, Double>
    {
        /**
         * Creates a new pair with two doubles.
         *
         * @param a The first double.
         * @param b The second double.
         */
        public D(double a, double b)
        {
            super(a, b);
        }
        
        /**
         * @return The first double value.
         */
        public double a()
        {
            return this.a;
        }
        
        /**
         * @return The second double value.
         */
        public double b()
        {
            return this.b;
        }
    }
    
    /**
     * A simple {@code String} pair.
     */
    public static class S extends Pair<String, String>
    {
        /**
         * Creates a new pair with two Strings.
         *
         * @param a The first String.
         * @param b The second String.
         */
        public S(String a, String b)
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
}
