package engine.event;

import engine.input.Mouse;
import org.joml.Vector2i;
import org.joml.Vector2ic;

/**
 * This event is generated when ever a mouse button is held, with the mouse button that was held and the position of the mouse.
 */
@SuppressWarnings("unused")
public class EventMouseButtonHeld extends Event
{
    public EventMouseButtonHeld(Object[] values)
    {
        super(new String[] {"", "pos"}, values);
    }
    
    public Mouse.Button button()
    {
        return (Mouse.Button) this.values[0];
    }
    
    public Vector2ic pos()
    {
        return (Vector2i) this.values[1];
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
