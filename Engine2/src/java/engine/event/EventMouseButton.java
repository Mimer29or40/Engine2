package engine.event;

import engine.Mouse;
import org.joml.Vector2dc;

public interface EventMouseButton extends EventInputDeviceInput, EventMouse
{
    @Property(printName = false)
    Mouse.Button button();
    
    @Property
    Vector2dc pos();
    
    default double x()
    {
        return pos().x();
    }
    
    default double y()
    {
        return pos().y();
    }
}
