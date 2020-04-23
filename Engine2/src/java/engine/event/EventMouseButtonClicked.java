package engine.event;

import engine.input.Mouse;
import org.joml.Vector2d;
import org.joml.Vector2dc;

/**
 * This event is generated when ever a mouse button is pressed then released, with the mouse button that was clicked and the position of the mouse.
 */
@SuppressWarnings("unused")
public class EventMouseButtonClicked extends Event
{
    public EventMouseButtonClicked(Object[] values)
    {
        super(new String[] {"", "pos", "double"}, values);
    }
    
    public Mouse.Button button()
    {
        return (Mouse.Button) this.values[0];
    }
    
    public Vector2dc pos()
    {
        return (Vector2d) this.values[1];
    }
    
    public boolean doubleClicked()
    {
        return (boolean) this.values[2];
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
