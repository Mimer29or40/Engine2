package engine.event;

import engine.input.Mouse;
import org.joml.Vector2d;
import org.joml.Vector2dc;

/**
 * This event is generated when ever a mouse button is held and the mouse is moved, with the
 * held button, the start of the drag, the current mouse position and the relative mouse movement.
 */
@SuppressWarnings("unused")
public class EventMouseButtonDragged extends Event
{
    public EventMouseButtonDragged(Object[] values)
    {
        super(new String[] {"", "dragPos", "pos", "rel"}, values);
    }
    
    public Mouse.Button button()
    {
        return (Mouse.Button) this.values[0];
    }
    
    public Vector2dc dragPos()
    {
        return (Vector2d) this.values[1];
    }
    
    public Vector2dc pos()
    {
        return (Vector2d) this.values[2];
    }
    
    public Vector2dc rel()
    {
        return (Vector2d) this.values[3];
    }
    
    public double dragX()
    {
        return dragPos().x();
    }
    
    public double dragY()
    {
        return dragPos().y();
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
