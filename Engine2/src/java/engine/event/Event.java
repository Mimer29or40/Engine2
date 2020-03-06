package engine.event;

public class Event
{
    protected final String[] keys;
    protected final Object[] values;
    
    public Event(String[] keys, Object[] values)
    {
        this.keys   = keys;
        this.values = values;
    }
    
    @Override
    public String toString()
    {
        StringBuilder s = new StringBuilder(getClass().getSimpleName()).append("[");
        for (int i = 0, n = this.values.length; i < n; i++)
        {
            if (!this.keys[i].equals("")) s.append(this.keys[i]).append("=");
            s.append(this.values[i].toString());
            if (i + 1 < n) s.append(" ");
        }
        return s.append("]").toString();
    }
}
