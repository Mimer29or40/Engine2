package engine.event;

import engine.Mouse;
import org.joml.Vector2dc;

public interface EventMouseButtonRepeated extends EventInputDeviceInputRepeated, EventMouseButton
{
    final class _EventMouseButtonRepeated extends AbstractEventMouseButton implements EventMouseButtonRepeated
    {
        private _EventMouseButtonRepeated(Mouse.Button button, Vector2dc pos)
        {
            super(button, pos);
        }
    }
    
    static EventMouseButtonRepeated create(Mouse.Button button, Vector2dc pos)
    {
        return new _EventMouseButtonRepeated(button, pos);
    }
}
