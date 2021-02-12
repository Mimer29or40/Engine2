package engine.event;

import engine.Gamepad;

public interface EventGamepadButtonPressed extends EventInputDeviceInputPressed, EventJoystickButtonPressed, EventGamepadButton
{
    final class _EventJoystickButtonPressed extends AbstractEventGamepadButton implements EventGamepadButtonPressed
    {
        private final boolean doublePressed;
    
        private _EventJoystickButtonPressed(Gamepad gamepad, Gamepad.Button button, boolean doublePressed)
        {
            super(gamepad, button);
            
            this.doublePressed = doublePressed;
        }
        
        @Override
        public boolean doublePressed()
        {
            return this.doublePressed;
        }
    }
    
    static EventGamepadButtonPressed create(Gamepad gamepad, Gamepad.Button button, boolean doublePressed)
    {
        return new _EventJoystickButtonPressed(gamepad, button, doublePressed);
    }
}
