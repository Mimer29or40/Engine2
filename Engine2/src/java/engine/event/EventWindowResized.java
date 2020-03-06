package engine.event;

public class EventWindowResized extends Event
{
    public EventWindowResized(Object[] values)
    {
        super(new String[] {"width", "height"}, values);
    }
    
    public int width()
    {
        return (int) this.values[0];
    }
    
    public int height()
    {
        return (int) this.values[1];
    }
}
