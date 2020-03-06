package engine.event;

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
