package engine.event;

import engine.Mouse;
import org.joml.Vector2dc;

public interface EventMouseButtonUp extends EventInputDeviceInputUp, EventMouseButton
{
    final class _EventMouseButtonUp extends AbstractEventMouseButton implements EventMouseButtonUp
    {
        private _EventMouseButtonUp(Mouse.Button button, Vector2dc pos)
        {
            super(button, pos);
        }
    }
    
    static EventMouseButtonUp create(Mouse.Button button, Vector2dc pos)
    {
        return new _EventMouseButtonUp(button, pos);
    }
}
