package engine.event;

import org.joml.Vector2d;
import org.joml.Vector2dc;

/**
 * This event is generated when ever the mouse is scrolled, with the x and y direction and magnitude.
 */
@SuppressWarnings("unused")
public class EventMouseScrolled extends Event
{
    public EventMouseScrolled(Object[] values)
    {
        super(new String[] {"dir"}, values);
    }
    
    public Vector2dc dir()
    {
        return (Vector2d) this.values[0];
    }
    
    public double x()
    {
        return dir().x();
    }
    
    public double y()
    {
        return dir().y();
    }
}
