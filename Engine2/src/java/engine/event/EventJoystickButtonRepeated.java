package engine.event;

import engine.Joystick;

public interface EventJoystickButtonRepeated extends EventInputDeviceInputRepeated, EventJoystickButton
{
    final class _EventJoystickButtonRepeated extends AbstractEventJoystickButton implements EventJoystickButtonRepeated
    {
        private _EventJoystickButtonRepeated(Joystick joystick, int button)
        {
            super(joystick, button);
        }
    }
    
    static EventJoystickButtonRepeated create(Joystick joystick, int button)
    {
        return new _EventJoystickButtonRepeated(joystick, button);
    }
}
