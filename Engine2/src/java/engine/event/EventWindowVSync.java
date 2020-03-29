package engine.event;

/**
 * This event is generated whenever the window's vsync setting is toggled, with if its vsync or not.
 */
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
