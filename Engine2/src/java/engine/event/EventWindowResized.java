package engine.event;

import org.joml.Vector2i;
import org.joml.Vector2ic;

/**
 * This event is generated whenever the window is resized, with the current window size.
 */
@SuppressWarnings("unused")
public class EventWindowResized extends Event
{
    public EventWindowResized(Object[] values)
    {
        super(new String[] {"size"}, values);
    }
    
    public Vector2ic size()
    {
        return (Vector2i) this.values[0];
    }
    
    public int width()
    {
        return size().x();
    }
    
    public int height()
    {
        return size().y();
    }
}
