package engine.event;

import org.joml.Vector2d;
import org.joml.Vector2dc;

/**
 * This event is generated when ever the mouse is moved, with the current mouse position and the relative mouse move.
 */
@SuppressWarnings("unused")
public class EventMouseMoved extends Event
{
    public EventMouseMoved(Object[] values)
    {
        super(new String[] {"pos", "rel"}, values);
    }
    
    public Vector2dc pos()
    {
        return (Vector2d) this.values[0];
    }
    
    public Vector2dc rel()
    {
        return (Vector2d) this.values[1];
    }
    
    public double x()
    {
        return pos().x();
    }
    
    public double y()
    {
        return pos().y();
    }
    
    public double relX()
    {
        return rel().x();
    }
    
    public double relY()
    {
        return rel().y();
    }
}
