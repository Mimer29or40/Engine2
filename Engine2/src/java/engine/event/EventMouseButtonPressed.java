package engine.event;

import engine.Mouse;
import org.joml.Vector2dc;

public interface EventMouseButtonPressed extends EventInputDeviceInputPressed, EventMouseButton
{
    final class _EventMouseButtonPressed extends AbstractEventMouseButton implements EventMouseButtonPressed
    {
        private final boolean doublePressed;
        
        private _EventMouseButtonPressed(Mouse.Button button, Vector2dc pos, boolean doublePressed)
        {
            super(button, pos);
            
            this.doublePressed = doublePressed;
        }
        
        @Override
        public boolean doublePressed()
        {
            return this.doublePressed;
        }
    }
    
    static EventMouseButtonPressed create(Mouse.Button button, Vector2dc pos, boolean doublePressed)
    {
        return new _EventMouseButtonPressed(button, pos, doublePressed);
    }
}
