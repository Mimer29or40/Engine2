package engine.event;

import org.joml.Vector2i;
import org.joml.Vector2ic;

/**
 * This event is generated whenever the window's frame buffer is resized, with the current frame buffer size.
 */
public class EventFrameBufferResized extends Event
{
    public EventFrameBufferResized(Object[] values)
    {
        super(new String[] {"size"}, values);
    }
    
    public Vector2ic size()
    {
        return (Vector2i) this.values[0];
    }
    
    public int width()
    {
        return size().x();
    }
    
    public int height()
    {
        return size().y();
    }
}
