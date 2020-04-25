package engine.event;

import java.util.Arrays;

/**
 * Generic {@link Event} that represents a group of information related to something happening with the engine.
 * <p>
 * To use an event Sub-Class this class and add getters to cast the values to the actual types.
 * <p>
 * To post an event call {@link Events#post} with the event class and parameters.
 */
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
    
    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof Event)) return false;
        Event event = (Event) o;
        return Arrays.equals(this.keys, event.keys) && Arrays.equals(this.values, event.values);
    }
    
    @Override
    public int hashCode()
    {
        return 31 * Arrays.hashCode(this.keys) + Arrays.hashCode(this.values);
    }
}
