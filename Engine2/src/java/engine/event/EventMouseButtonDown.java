package engine.event;

import engine.input.Mouse;
import org.joml.Vector2d;
import org.joml.Vector2dc;

/**
 * This event is generated when ever a mouse button is pressed, with the mouse button that was pressed and the position of the mouse.
 */
@SuppressWarnings("unused")
public class EventMouseButtonDown extends Event
{
    public EventMouseButtonDown(Object[] values)
    {
        super(new String[] {"", "pos"}, values);
    }
    
    public Mouse.Button button()
    {
        return (Mouse.Button) this.values[0];
    }
    
    public Vector2dc pos()
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
}
