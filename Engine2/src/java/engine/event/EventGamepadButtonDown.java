package engine.event;

import engine.Gamepad;

public interface EventGamepadButtonDown extends EventInputDeviceInputDown, EventJoystickButtonDown, EventGamepadButton
{
    final class _EventGamepadButtonDown extends AbstractEventGamepadButton implements EventGamepadButtonDown
    {
        private _EventGamepadButtonDown(Gamepad gamepad, Gamepad.Button button)
        {
            super(gamepad, button);
        }
    }
    
    static EventGamepadButtonDown create(Gamepad gamepad, Gamepad.Button button)
    {
        return new _EventGamepadButtonDown(gamepad, button);
    }
}
