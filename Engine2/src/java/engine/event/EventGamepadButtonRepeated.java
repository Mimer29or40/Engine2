package engine.event;

import engine.Gamepad;

public interface EventGamepadButtonRepeated extends EventInputDeviceInputRepeated, EventJoystickButtonRepeated, EventGamepadButton
{
    final class _EventGamepadButtonRepeated extends AbstractEventGamepadButton implements EventGamepadButtonRepeated
    {
        private _EventGamepadButtonRepeated(Gamepad gamepad, Gamepad.Button button)
        {
            super(gamepad, button);
        }
    }
    
    static EventGamepadButtonRepeated create(Gamepad gamepad, Gamepad.Button button)
    {
        return new _EventGamepadButtonRepeated(gamepad, button);
    }
}
