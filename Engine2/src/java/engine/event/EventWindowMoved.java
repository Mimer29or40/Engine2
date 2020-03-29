package engine.event;

import org.joml.Vector2i;
import org.joml.Vector2ic;

/**
 * This event is generated whenever the window is moved, with the current window position.
 */
public class EventWindowMoved extends Event
{
    public EventWindowMoved(Object[] values)
    {
        super(new String[] {"pos"}, values);
    }
    
    public Vector2ic pos()
    {
        return (Vector2i) this.values[0];
    }
    
    public int x()
    {
        return pos().x();
    }
    
    public int y()
    {
        return pos().y();
    }
}
