package engine.event;

/**
 * This event is generated whenever the window loosed or gains focus, with if its focused or not.
 */
public class EventWindowFocused extends Event
{
    public EventWindowFocused(Object[] values)
    {
        super(new String[] {"focused"}, values);
    }
    
    public boolean focused()
    {
        return (boolean) this.values[0];
    }
}
