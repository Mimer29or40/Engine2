package engine.event;

import engine.Joystick;

public interface EventJoystickButtonUp extends EventInputDeviceInputUp, EventJoystickButton
{
    final class _EventJoystickButtonUp extends AbstractEventJoystickButton implements EventJoystickButtonUp
    {
        private _EventJoystickButtonUp(Joystick joystick, int button)
        {
            super(joystick, button);
        }
    }
    
    static EventJoystickButtonUp create(Joystick joystick, int button)
    {
        return new _EventJoystickButtonUp(joystick, button);
    }
}
