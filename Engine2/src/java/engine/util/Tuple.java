package engine.util;

import java.io.Serializable;
import java.util.Objects;

/**
 * A Generic tuple of three objects. The objects can be modified or completely replaced.
 */
@SuppressWarnings("unused")
public class Tuple<A, B, C> implements ITuple<A, B, C>, Comparable<ITuple<A, B, C>>, Serializable
{
    public A a;
    public B b;
    public C c;
    
    /**
     * Creates a new pair with three objects.
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
        return getClass().getSimpleName() + '{' + this.a + ", " + this.b + ", " + this.c + '}';
    }
    @Override
    public int compareTo(ITuple<A, B, C> o)
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
    
        if (getC() != o.getC())
        {
            if (getC() == null)
            {
                return -1;
            }
            if (o.getC() == null)
            {
                return 1;
            }
            @SuppressWarnings("unchecked") // assume this can be done; if not throw CCE as per Javadoc
            final Comparable<Object> comparable = (Comparable<Object>) getC();
            if ((comparison = comparable.compareTo(o.getC())) != 0) return comparison;
        }
    
        return 0;
    }
    
    /**
     * A simple {@code int} tuple.
     */
    public static class I extends Tuple<Integer, Integer, Integer>
    {
        /**
         * Creates a new pair with three ints.
         *
         * @param a The first int.
         * @param b The second int.
         * @param c The third int.
         */
        public I(int a, int b, int c)
        {
            super(a, b, c);
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
        
        /**
         * @return The third int value.
         */
        public int c()
        {
            return this.c;
        }
    }
    
    /**
     * A simple {@code long} tuple.
     */
    public static class L extends Tuple<Long, Long, Long>
    {
        /**
         * Creates a new pair with three longs.
         *
         * @param a The first long.
         * @param b The second long.
         * @param c The third long.
         */
        public L(long a, long b, long c)
        {
            super(a, b, c);
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
        
        /**
         * @return The third long value.
         */
        public long c()
        {
            return this.c;
        }
    }
    
    /**
     * A simple {@code double} float.
     */
    public static class F extends Tuple<Float, Float, Float>
    {
        /**
         * Creates a new pair with three floats.
         *
         * @param a The first float.
         * @param b The second float.
         * @param c The third float.
         */
        public F(float a, float b, float c)
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
    
    /**
     * A simple {@code double} tuple.
     */
    public static class D extends Tuple<Double, Double, Double>
    {
        /**
         * Creates a new pair with three doubles.
         *
         * @param a The first double.
         * @param b The second double.
         * @param c The third double.
         */
        public D(double a, double b, double c)
        {
            super(a, b, c);
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
        
        /**
         * @return The third double value.
         */
        public double c()
        {
            return this.c;
        }
    }
    
    /**
     * A simple {@code String} tuple.
     */
    public static class S extends Tuple<String, String, String>
    {
        /**
         * Creates a new tuple with three Strings.
         *
         * @param a The first String.
         * @param b The second String.
         * @param c The third String.
         */
        public S(String a, String b, String c)
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
}
