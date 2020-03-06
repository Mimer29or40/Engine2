package engine.event;

import org.joml.Vector2i;
import org.joml.Vector2ic;
import engine.input.Mouse;

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
