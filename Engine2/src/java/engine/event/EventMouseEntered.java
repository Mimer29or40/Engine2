package engine.event;

/**
 * This event is generated when ever the mouse enters or exits the window, with if its in window or not.
 */
public class EventMouseEntered extends Event
{
    public EventMouseEntered(Object[] values)
    {
        super(new String[] {"entered"}, values);
    }
    
    public boolean entered()
    {
        return (boolean) this.values[0];
    }
}
