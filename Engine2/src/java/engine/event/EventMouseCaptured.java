package engine.event;

/**
 * This event is generated when ever the mouse enters or exits the window, with if its in window or not.
 */
public class EventMouseCaptured extends Event
{
    public EventMouseCaptured(Object[] values)
    {
        super(new String[] {"captured"}, values);
    }
    
    public boolean captured()
    {
        return (boolean) this.values[0];
    }
}
