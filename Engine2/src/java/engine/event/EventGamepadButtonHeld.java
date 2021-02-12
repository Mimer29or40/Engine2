package engine.event;

import engine.Gamepad;

public interface EventGamepadButtonHeld extends EventInputDeviceInputHeld, EventJoystickButtonHeld, EventGamepadButton
{
    final class _EventGamepadButtonHeld extends AbstractEventGamepadButton implements EventGamepadButtonHeld
    {
        private _EventGamepadButtonHeld(Gamepad gamepad, Gamepad.Button button)
        {
            super(gamepad, button);
        }
    }
    
    static EventGamepadButtonHeld create(Gamepad gamepad, Gamepad.Button button)
    {
        return new _EventGamepadButtonHeld(gamepad, button);
    }
}
