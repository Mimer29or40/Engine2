package engine.event;

import engine.Mouse;
import org.joml.Vector2dc;

public interface EventMouseButtonHeld extends EventInputDeviceInputHeld, EventMouseButton
{
    final class _EventMouseButtonHeld extends AbstractEventMouseButton implements EventMouseButtonHeld
    {
        private _EventMouseButtonHeld(Mouse.Button button, Vector2dc pos)
        {
            super(button, pos);
        }
    }
    
    static EventMouseButtonHeld create(Mouse.Button button, Vector2dc pos)
    {
        return new _EventMouseButtonHeld(button, pos);
    }
}
