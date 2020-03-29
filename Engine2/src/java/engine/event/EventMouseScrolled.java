package engine.event;

import org.joml.Vector2i;
import org.joml.Vector2ic;

/**
 * This event is generated when ever the mouse is scrolled, with the x and y direction and magnitude.
 */
public class EventMouseScrolled extends Event
{
    public EventMouseScrolled(Object[] values)
    {
        super(new String[] {"scroll"}, values);
    }
    
    public Vector2ic scroll()
    {
        return (Vector2i) this.values[0];
    }
    
    public int x()
    {
        return scroll().x();
    }
    
    public int y()
    {
        return scroll().y();
    }
}
