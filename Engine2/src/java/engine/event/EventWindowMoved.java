package engine.event;

public class EventWindowMoved extends Event
{
    public EventWindowMoved(Object[] values)
    {
        super(new String[] {"x", "y"}, values);
    }
    
    public int x()
    {
        return (int) this.values[0];
    }
    
    public int y()
    {
        return (int) this.values[1];
    }
}
