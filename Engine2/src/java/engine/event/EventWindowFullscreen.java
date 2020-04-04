package engine.event;

/**
 * This event is generated whenever the window's fullscreen setting is toggled, with if its fullscreen or not.
 */
@SuppressWarnings("unused")
public class EventWindowFullscreen extends Event
{
    public EventWindowFullscreen(Object[] values)
    {
        super(new String[] {"fullscreen"}, values);
    }
    
    public boolean fullscreen()
    {
        return (boolean) this.values[0];
    }
}
