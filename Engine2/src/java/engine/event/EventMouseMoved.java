package engine.event;

import org.joml.Vector2i;
import org.joml.Vector2ic;

@SuppressWarnings("unused")
public class EventMouseMoved extends Event
{
    public EventMouseMoved(Object[] values)
    {
        super(new String[] {"pos", "rel"}, values);
    }
    
    public Vector2ic pos()
    {
        return (Vector2i) this.values[0];
    }
    
    public Vector2ic rel()
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
    
    public int relX()
    {
        return rel().x();
    }
    
    public int relY()
    {
        return rel().y();
    }
}
