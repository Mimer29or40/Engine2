package engine.event;

@SuppressWarnings("unused")
public class EventWindowVSync extends Event
{
    public EventWindowVSync(Object[] values)
    {
        super(new String[] {"vsync"}, values);
    }
    
    public boolean vsync()
    {
        return (boolean) this.values[0];
    }
}
