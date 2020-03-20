package engine.util;

import java.util.Objects;

@SuppressWarnings("unused")
public class Pair<A, B> implements IPair<A, B>
{
    public A a;
    public B b;
    
    public Pair(A a, B b)
    {
        this.a = a;
        this.b = b;
    }
    
    @Override
    public A getA()
    {
        return this.a;
    }
    
    @Override
    public B getB()
    {
        return this.b;
    }
    
    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof Pair)) return false;
        Pair<?, ?> pair = (Pair<?, ?>) o;
        return Objects.equals(this.a, pair.a) && Objects.equals(this.b, pair.b);
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
}
