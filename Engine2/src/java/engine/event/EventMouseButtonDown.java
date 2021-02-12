package engine.event;

import engine.Mouse;
import org.joml.Vector2dc;

public interface EventMouseButtonDown extends EventInputDeviceInputDown, EventMouseButton
{
    final class _EventMouseButtonDown extends AbstractEventMouseButton implements EventMouseButtonDown
    {
        private _EventMouseButtonDown(Mouse.Button button, Vector2dc pos)
        {
            super(button, pos);
        }
    }
    
    static EventMouseButtonDown create(Mouse.Button button, Vector2dc pos)
    {
        return new _EventMouseButtonDown(button, pos);
    }
}
