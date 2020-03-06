package engine.util;

import java.util.Objects;

@SuppressWarnings("unused")
public class Tuple<A, B, C>
{
    public final A a;
    public final B b;
    public final C c;
    
    public Tuple(A a, B b, C c)
    {
        this.a = a;
        this.b = b;
        this.c = c;
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
