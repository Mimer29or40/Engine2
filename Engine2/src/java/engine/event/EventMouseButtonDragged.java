package engine.event;

import org.joml.Vector2i;
import org.joml.Vector2ic;
import engine.input.Mouse;

/**
 * This event is generated when ever a mouse button is held and the mouse is moved, with the
 * held button, the start of the drag, the current mouse position and the relative mouse movement.
 */
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
    
    public Vector2ic dragPos()
    {
        return (Vector2i) this.values[1];
    }
    
    public Vector2ic pos()
    {
        return (Vector2i) this.values[2];
    }
    
    public Vector2ic rel()
    {
        return (Vector2i) this.values[3];
    }
    
    public int dragX()
    {
        return dragPos().x();
    }
    
    public int dragY()
    {
        return dragPos().y();
    }
    
    public int x()
    {
        return pos().x();
    }
    
    public int y()
    {
        return pos().y();
    }
    
    public int relX()
    {
        return rel().x();
    }
    
    public int relY()
    {
        return rel().y();
    }
}
